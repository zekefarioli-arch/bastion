package com.zekefarioli.bastion.service;

import com.zekefarioli.bastion.model.Ticket;
import com.zekefarioli.bastion.model.TicketStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TicketServiceTest {

    @Test
    void createTicket_setsStatusToOpen() {
        TicketService service = new TicketService();

        Ticket ticket = service.createTicket("Test title", "Test description", null);

        assertEquals(TicketStatus.OPEN, ticket.getStatus());
    }
}