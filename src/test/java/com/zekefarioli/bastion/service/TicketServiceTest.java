package com.zekefarioli.bastion.service;

import com.zekefarioli.bastion.model.Ticket;
import com.zekefarioli.bastion.model.TicketStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

class TicketServiceTest {

    @Test
    void createTicket_setsStatusToOpen() {
        TicketService service = new TicketService();

        Ticket ticket = service.createTicket("Test title", "Test description", null);

        assertEquals(TicketStatus.OPEN, ticket.getStatus());
    }

    @Test
    void findAll_returnsAllCreatedTickets() {
        // Arrange: preparar
        TicketService service = new TicketService();

        // Act: actuar
        service.createTicket("First ticket", "First description", null);
        service.createTicket("Second ticket", "Second description", null);
        service.createTicket("Third ticket", "Third description", null);

        // Assert: verificar
        List<Ticket> allTickets = service.findAll();
        assertEquals(3, allTickets.size());
    }
}