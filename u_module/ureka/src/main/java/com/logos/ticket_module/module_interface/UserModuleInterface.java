package com.logos.ticket_module.module_interface;

import java.security.PublicKey;

import org.fidoalliance.fdo.protocol.Composite;

import com.logos.ticket_module.Command;
import com.logos.ticket_module.Ticket;

public interface UserModuleInterface {
    public abstract void applyTicket(PublicKey devicePublicKey);

    public abstract void applyTicket(Ticket ticket);

    public abstract void applyTicket(Composite ticketCose);

    public abstract void applyEncryptedPackage();

    public abstract Ticket issueTicket(
            PublicKey holderPublicKey,
            PublicKey devicePublicKey,
            Command command);

    public abstract Ticket issueInitTicket(
            PublicKey holderPublicKey,
            PublicKey devicePublicKey);

    public abstract Ticket issueMangementTicket(
            PublicKey holderPublicKey,
            PublicKey devicePublicKey);

    public abstract void UploadTicket(Ticket ticket);

    public abstract Ticket DownloadTicket();

    public abstract PublicKey getPublicKey();
}
