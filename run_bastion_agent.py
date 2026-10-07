#!/usr/bin/env python3
"""
Agente que le pide tareas a un modelo servido por colibrì, escribe el
código resultante sobre una copia del repo Bastion, y SOLO hace commit
si el proyecto compila y los tests pasan. Nunca hace git push.

Uso:
    python3 run_bastion_agent.py

Requisitos:
    - Correr DENTRO de la carpeta del repo (ej: ~/Projects/bastion-auto)
    - El repo debe estar en una rama separada (nunca main/master)
    - colibrì debe estar corriendo y accesible en COLIBRI_URL
"""

import subprocess
import requests
import re
import os
import sys
import time
from datetime import datetime
from pathlib import Path

# ---------- CONFIG ----------
# Where the local model answers. Set COLIBRI_URL to point at another machine; no address of my network is written here.
COLIBRI_URL = os.environ.get("COLIBRI_URL", "http://localhost:8090/v1/chat/completions")
MODEL = "deepseek-v4-colibri"
MAX_TOKENS = 4000
TEMPERATURE = 0.3   # bajo, priorizamos consistencia sobre creatividad para código
LOG_FILE = "agent_log.txt"
SAFE_BRANCH_PREFIX = "bastion-auto"  # el script se niega a correr si la rama actual no empieza asi

# Tareas pendientes, en orden. Cada una define:
#   - descripcion: lo que le pedimos al modelo
#   - contexto_paths: archivos existentes que le mandamos como referencia
#   - archivos_esperados: nombres exactos que el modelo debe generar con los delimitadores
TASKS = [
    {
        "id": "user_controller",
        "descripcion": (
            "Crea la clase UserController en el paquete com.zekefarioli.bastion.controller. "
            "Debe exponer un endpoint POST /api/users que reciba un CreateUserRequest (un record "
            "DTO en el paquete com.zekefarioli.bastion.dto con los campos necesarios segun la "
            "entidad User adjunta) y delegue en UserService para crear el usuario. Devuelve el "
            "usuario creado con status 201. Segui las convenciones que ya usa el proyecto "
            "(constructor injection, @RestController, @PostMapping, @RequestBody)."
        ),
        "contexto_paths": [
            "src/main/java/com/zekefarioli/bastion/model/User.java",
            "src/main/java/com/zekefarioli/bastion/service/UserService.java",
        ],
        "archivos_esperados": [
            "src/main/java/com/zekefarioli/bastion/dto/CreateUserRequest.java",
            "src/main/java/com/zekefarioli/bastion/controller/UserController.java",
        ],
    },
    {
        "id": "wire_ticket_owner",
        "descripcion": (
            "Modifica TicketController para que, al crear un ticket, resuelva el owner real "
            "llamando a UserService.findById con el id de usuario recibido en el request, en vez "
            "de dejarlo null. Si el usuario no existe, debe responder 404. Adjunto el "
            "TicketController y el UserService actuales; devolve el archivo TicketController "
            "completo y actualizado."
        ),
        "contexto_paths": [
            "src/main/java/com/zekefarioli/bastion/controller/TicketController.java",
            "src/main/java/com/zekefarioli/bastion/service/UserService.java",
            "src/main/java/com/zekefarioli/bastion/model/Ticket.java",
        ],
        "archivos_esperados": [
            "src/main/java/com/zekefarioli/bastion/controller/TicketController.java",
        ],
    },
]

DELIM_INSTRUCTIONS = (
    "IMPORTANTE: marca cada archivo exactamente asi, sin nada mas en esas lineas:\n"
    "===ARCHIVO: ruta/completa/Archivo.java===\n"
    "(contenido completo del archivo)\n"
    "===FIN===\n"
    "No agregues explicaciones fuera de los bloques ===ARCHIVO=== / ===FIN===."
)


def log(msg):
    timestamp = datetime.now().strftime("%H:%M:%S")
    line = f"[{timestamp}] {msg}"
    print(line, flush=True)
    with open(LOG_FILE, "a") as f:
        f.write(line + "\n")


def check_safe_branch():
    result = subprocess.run(
        ["git", "branch", "--show-current"], capture_output=True, text=True
    )
    branch = result.stdout.strip()
    if not branch.startswith(SAFE_BRANCH_PREFIX):
        log(f"ABORTANDO: la rama actual es '{branch}', no empieza con "
            f"'{SAFE_BRANCH_PREFIX}'. El script se niega a correr fuera de una rama segura.")
        sys.exit(1)
    log(f"Rama segura confirmada: {branch}")


def read_context(paths):
    parts = []
    for p in paths:
        full = Path(p)
        if full.exists():
            parts.append(f"--- {p} ---\n{full.read_text()}")
        else:
            parts.append(f"--- {p} (no existe todavia) ---")
    return "\n\n".join(parts)


def call_colibri(prompt):
    payload = {
        "model": MODEL,
        "messages": [{"role": "user", "content": prompt}],
        "max_tokens": MAX_TOKENS,
        "temperature": TEMPERATURE,
    }
    resp = requests.post(COLIBRI_URL, json=payload, timeout=1800)
    resp.raise_for_status()
    data = resp.json()
    return data["choices"][0]["message"]["content"]


def extract_files(text):
    pattern = r"===ARCHIVO:\s*(\S+)===\s*(.*?)\s*===FIN==="
    return re.findall(pattern, text, re.DOTALL)


def write_files(matches):
    written = []
    for filename, content in matches:
        path = Path(filename)
        path.parent.mkdir(parents=True, exist_ok=True)
        path.write_text(content.strip() + "\n")
        written.append(filename)
    return written


def run_maven(goal):
    result = subprocess.run(
        ["./mvnw", "-q", goal],
        capture_output=True, text=True, timeout=600
    )
    return result.returncode == 0, result.stdout + result.stderr


def git_commit(task_id, files):
    subprocess.run(["git", "add"] + files, check=True)
    subprocess.run(
        ["git", "commit", "-m", f"feat: {task_id} (colibri auto)"],
        check=True
    )
    result = subprocess.run(
        ["git", "rev-parse", "--short", "HEAD"], capture_output=True, text=True
    )
    return result.stdout.strip()


def run_task(task):
    log(f"=== Tarea: {task['id']} ===")
    context = read_context(task["contexto_paths"])
    prompt = (
        f"{task['descripcion']}\n\n"
        f"Codigo existente para referencia:\n{context}\n\n"
        f"{DELIM_INSTRUCTIONS}"
    )

    log("Enviando prompt a colibri, esperando respuesta (puede tardar varios minutos)...")
    start = time.time()
    try:
        response_text = call_colibri(prompt)
    except Exception as e:
        log(f"ERROR llamando a colibri: {e}")
        return
    elapsed = time.time() - start
    log(f"Respuesta recibida en {elapsed:.1f}s")

    matches = extract_files(response_text)
    if not matches:
        log("SIN DELIMITADORES detectados en la respuesta. Guardando raw para revision manual.")
        Path(f"agent_raw_{task['id']}.txt").write_text(response_text)
        return

    written = write_files(matches)
    log(f"Archivos escritos: {written}")

    ok_compile, out_compile = run_maven("compile")
    if not ok_compile:
        log(f"NO COMPILA. Sin commit. Detalle guardado en agent_error_{task['id']}.log")
        Path(f"agent_error_{task['id']}.log").write_text(out_compile)
        return
    log("COMPILA correctamente.")

    ok_test, out_test = run_maven("test")
    if not ok_test:
        log(f"TESTS FALLAN. Sin commit. Detalle guardado en agent_error_{task['id']}.log")
        Path(f"agent_error_{task['id']}.log").write_text(out_test)
        return
    log("TESTS OK.")

    commit_hash = git_commit(task["id"], written)
    log(f"COMMIT realizado: {commit_hash} (rama local, sin push)")


def main():
    check_safe_branch()
    log(f"Iniciando agente. {len(TASKS)} tareas en cola. Modelo: {MODEL}")
    for task in TASKS:
        run_task(task)
    log("=== Todas las tareas procesadas. Revisa agent_log.txt y los commits con 'git log'. ===")


if __name__ == "__main__":
    main()

