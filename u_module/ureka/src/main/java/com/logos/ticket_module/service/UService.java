package com.logos.ticket_module.service;

import com.logos.ticket_module.Ticket;

import org.fidoalliance.fdo.protocol.MessagingService;

public abstract class UService extends MessagingService {
    public abstract Ticket signTicket(Ticket ticket);

    protected abstract boolean needKeyExchange();
}
