package com.logos.user;

import java.security.PublicKey;

import org.fidoalliance.fdo.protocol.Composite;

import com.logos.ticket_module.Command;
import com.logos.ticket_module.Communication;
import com.logos.ticket_module.Ticket;
import com.logos.ticket_module.TicketModule;
import com.logos.ticket_module.module_interface.UserModuleInterface;
import com.logos.ticket_module.service.UService;
import com.logos.ticket_module.storage.UStorage;
// import org.openjdk.jmh.annotations.Benchmark;

public class User {
    private UserModuleInterface UModule;

    public User(UStorage storage, UService service, Communication communication) {
        this.UModule = new TicketModule(communication, service, storage);
    }

    public void applyTicket(PublicKey devicePublicKey) {
        UModule.applyTicket(devicePublicKey);
    }

    public Ticket issueUTicket(PublicKey holderPublicKey, PublicKey devicePublicKey, Command command) {
        // return this.UModule.issueInitTicket(holderPublicKey, devicePublicKey);
        return this.UModule.issueTicket(holderPublicKey, devicePublicKey, command);
    }

    public PublicKey getPublicKey() {
        return UModule.getPublicKey();
    }

    public void applyTicket(Ticket ticket) {
        UModule.applyTicket(ticket);
    }

    public void applyTicket(Composite ticketCose) {
        UModule.applyTicket(ticketCose);
    }
}
