package com.logos.ticket_module.module_interface;

import java.security.PublicKey;

import com.logos.ticket_module.Ticket;

public interface OwnerModuleInterface {
    public abstract void sign();

    public abstract Ticket issue(PublicKey holderPublicKey, PublicKey devicePublicKey);

    public abstract void applyTicket();

    public abstract void applyTicket(PublicKey devicePublicKey);

    public abstract void applyEncryptedPackage();

    public abstract void addTicketType();

    public abstract void addHolderPubKey();

    public abstract void addValidCondition();

    public abstract void addCommand();

    public abstract Ticket getTicket();

}
