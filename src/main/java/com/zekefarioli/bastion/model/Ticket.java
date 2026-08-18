package com.zekefarioli.bastion.model;

import java.time.Instant;

public class Ticket {
    private final Long id;
    private String title;
    private String description;
    private TicketStatus status;
    private User owner;
    private Instant createdAt;

    public Ticket(Long id, String title, String description, TicketStatus status, User owner, Instant createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.owner = owner;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public User getOwner() {
        return owner;
    }

    public Instant getCreatedAt() {
        return createdAt;   
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }
}
