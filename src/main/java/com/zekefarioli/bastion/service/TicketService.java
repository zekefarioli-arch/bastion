package com.zekefarioli.bastion.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import com.zekefarioli.bastion.model.Ticket;
import com.zekefarioli.bastion.model.TicketStatus;
import com.zekefarioli.bastion.model.User;

@Service
public class TicketService {
    private final List<Ticket> tickets = new ArrayList<>();
    private long nextId = 1;
    public Ticket creaTicket(String title, String description, User owner) {
        Ticket ticket = new Ticket(nextId, title, description, TicketStatus.OPEN, owner, Instant.now());
        nextId = nextId + 1;
        tickets.add(ticket);
        return ticket;
    }
}

