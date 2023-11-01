package com.logos.ticket_module.storage;

import java.security.PrivateKey;
import java.security.PublicKey;

import com.logos.ticket_module.Ticket;

import org.fidoalliance.fdo.protocol.Composite;
import org.fidoalliance.fdo.protocol.StorageEvents;

public interface UStorage extends StorageEvents {
    public abstract Composite getUTicket();

    public abstract Composite getUTicket(PublicKey devicePublicKey);

    public abstract String getCipherSuiteName();

    public abstract String getKexSuiteName();

    public abstract PrivateKey getPrivateKey();

    public abstract PublicKey getOwnerPublicKey();

    public abstract int getSessionNum();

    public abstract void addSessionNum();

    public abstract void storeTicket(Ticket ticket);

    public abstract void storeTicket(Composite ticketCose);

    public abstract PublicKey getPublicKey();
}
