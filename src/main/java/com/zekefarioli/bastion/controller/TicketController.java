package com.zekefarioli.bastion.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.zekefarioli.bastion.dto.CreateTicketRequest;
import com.zekefarioli.bastion.model.Ticket;
import com.zekefarioli.bastion.service.TicketService;

@RestController
public class TicketController {
    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/tickets")
    public List<Ticket> getAllTickets() {
        return ticketService.findAll();
    }

    @PostMapping("/tickets")
    public Ticket createTicket(@RequestBody CreateTicketRequest ticketDTO) {
        Ticket ticket = ticketService.createTicket(ticketDTO.title(), ticketDTO.description(), null);
        return ticket;
    }

}