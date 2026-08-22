package com.zekefarioli.bastion.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zekefarioli.bastion.model.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
}
