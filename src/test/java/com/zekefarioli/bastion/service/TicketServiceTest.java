package com.zekefarioli.bastion.service;

import com.zekefarioli.bastion.model.Ticket;
import com.zekefarioli.bastion.model.TicketStatus;
import com.zekefarioli.bastion.repository.TicketRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.NoSuchElementException;

@DataJpaTest
class TicketServiceTest {
    @Autowired
    private TicketRepository ticketRepository;

    @Test
    void createTicket_setsStatusToOpen() {
        TicketService service = new TicketService(ticketRepository);

        Ticket ticket = service.createTicket("Test title", "Test description", null);

        assertEquals(TicketStatus.OPEN, ticket.getStatus());
    }

    @Test
    void findAll_returnsAllCreatedTickets() {
        // Arrange: preparar
        TicketService service = new TicketService(ticketRepository);

        // Act: actuar
        service.createTicket("First ticket", "First description", null);
        service.createTicket("Second ticket", "Second description", null);
        service.createTicket("Third ticket", "Third description", null);

        // Assert: verificar
        List<Ticket> allTickets = service.findAll();
        assertEquals(3, allTickets.size());
    }

    @Test
    void updateStatus_changesTicketStatus() {
        // Arrange
        TicketService service = new TicketService(ticketRepository);
        Ticket ticket = service.createTicket("Test title", "Test description", null);

        // Act
        Ticket updated = service.updateStatus(ticket.getId(), TicketStatus.IN_PROGRESS);

        // Assert
        assertEquals(TicketStatus.IN_PROGRESS, updated.getStatus());
    }

    @Test
    void updateStatus_throwsWhenTicketNotFound() {
        // Arrange
        TicketService service = new TicketService(ticketRepository);

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> {
            service.updateStatus(999L, TicketStatus.CLOSED);
        });
    }
}