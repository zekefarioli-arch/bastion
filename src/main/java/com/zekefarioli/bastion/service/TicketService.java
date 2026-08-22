package com.zekefarioli.bastion.service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import com.zekefarioli.bastion.model.Ticket;
import com.zekefarioli.bastion.model.TicketStatus;
import com.zekefarioli.bastion.model.User;
import com.zekefarioli.bastion.repository.TicketRepository;

@Service
public class TicketService {
    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public Ticket createTicket(String title, String description, User owner) {
        Ticket ticket = new Ticket(null, title, description, TicketStatus.OPEN, owner, Instant.now());
        return ticketRepository.save(ticket);
    }

    public List<Ticket> findAll() {
        return ticketRepository.findAll();
    }

    public Ticket updateStatus(long id, TicketStatus newStatus) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Ticket not found: " + id));
        ticket.setStatus(newStatus);
        return ticketRepository.save(ticket);
    }
}
