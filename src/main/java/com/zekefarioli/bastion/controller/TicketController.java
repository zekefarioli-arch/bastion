package com.zekefarioli.bastion.controller;

import org.springframework.web.bind.annotation.RestController;
import com.zekefarioli.bastion.service.TicketService;

@RestController
public class TicketController {
    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }
}