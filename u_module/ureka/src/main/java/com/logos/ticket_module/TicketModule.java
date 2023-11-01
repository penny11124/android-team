package com.logos.ticket_module;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

import com.logos.ticket_module.module_interface.AuditPlatformInterface;
import com.logos.ticket_module.module_interface.DeviceModuleInterface;
import com.logos.ticket_module.module_interface.UserModuleInterface;
import com.logos.ticket_module.module_interface.OwnerModuleInterface;
import com.logos.ticket_module.service.UService;
import com.logos.ticket_module.storage.UStorage;

import org.fidoalliance.fdo.protocol.Composite;
import org.fidoalliance.fdo.protocol.Const;
import org.fidoalliance.fdo.protocol.DispatchResult;

public class TicketModule
        implements OwnerModuleInterface, AuditPlatformInterface, DeviceModuleInterface, UserModuleInterface {
    private Communication communication;
    private UService service;
    private UStorage storage;

    public TicketModule(
            Communication communication,
            UService service) {
        this.communication = communication;
        this.service = service;
    }

    public TicketModule(
            Communication communication,
            UService service,
            UStorage storage) {
        this.communication = communication;
        this.service = service;
        this.storage = storage;
    }

    public TicketModule() {
    }

    public void sign() {

    }

    public void verify(PublicKey publicKey) {

    }

    public void issue() {

    }

    public void addTicketType() {

    }

    public void addHolderPubKey() {

    }

    public void addValidCondition() {

    }

    public void addData() {

    }

    public void addInfo() {

    }

    public void addCommand() {
        // TODO Auto-generated method stub

    }

    public void addHash() {
        // TODO Auto-generated method stub

    }

    public void addHash2() {
        // TODO Auto-generated method stub

    }

    public Ticket getTicket() {
        Ticket foo = new UTicket(1, "", "");
        return foo;
        // return null;
    }

    public void verifyTicket(PublicKey publicKey) {
    }

    public void verifyEncryptedPackage() {
        // TODO Auto-generated method stub

    }

    /**
     * apply ticket to execute command or create session(store in service storage).
     * Hide the detail of CR & KEX in service.dispatch
     */
    public void applyTicket(PublicKey devicePublicKey) {
        // create request composite form ticket
        Composite ticket = this.storage.getUTicket(devicePublicKey);
        this.applyTicket(new UTicket(ticket));
    }

    public void applyTicket(Ticket ticket) {
        Composite tickeCose = ticket.toCoseComposite();
        Composite request = Composite.newArray().set(Const.SM_LENGTH, Const.DEFAULT).set(Const.SM_MSG_ID, Const.DEFAULT)
                .set(Const.SM_PROTOCOL_VERSION, ticket.getProtocolVersion())
                .set(Const.SM_PROTOCOL_INFO, Composite.fromObject(Const.EMPTY_BYTE)).set(Const.SM_BODY, tickeCose);

        DispatchResult requestDR = new DispatchResult(request, false);

        // sent ticket and start challenge response & key exchange
        while (!requestDR.isDone()) {
            DispatchResult responseDR = this.communication.sendMessage(requestDR.getReply());

            request = Composite.newArray().set(Const.SM_LENGTH, Const.DEFAULT).set(Const.SM_MSG_ID, Const.DEFAULT)
                    .set(Const.SM_PROTOCOL_VERSION, requestDR.getReply().get(Const.SM_PROTOCOL_VERSION))
                    .set(Const.SM_PROTOCOL_INFO, requestDR.getReply().get(Const.SM_PROTOCOL_INFO))
                    .set(Const.SM_BODY, Const.EMPTY_MESSAGE);

            // generate next request message(dispatch result) with the response
            boolean isDone = this.service.dispatch(responseDR.getReply(), request);

            requestDR = new DispatchResult(request, isDone);
        }

    }

    public void applyTicket(Composite tickeCose) {

        Composite request = Composite.newArray().set(Const.SM_LENGTH, Const.DEFAULT).set(Const.SM_MSG_ID, Const.DEFAULT)
                .set(Const.SM_PROTOCOL_VERSION, Constants.PROTOCOL_VERSION.getBytes())
                .set(Const.SM_PROTOCOL_INFO, Composite.fromObject(Const.EMPTY_BYTE)).set(Const.SM_BODY, tickeCose);

        DispatchResult requestDR = new DispatchResult(request, false);

        // sent ticket and start challenge response & key exchange
        while (!requestDR.isDone()) {
            DispatchResult responseDR = this.communication.sendMessage(requestDR.getReply());
            request = Composite.newArray().set(Const.SM_LENGTH, Const.DEFAULT).set(Const.SM_MSG_ID, Const.DEFAULT)
                    .set(Const.SM_PROTOCOL_VERSION, requestDR.getReply().get(Const.SM_PROTOCOL_VERSION))
                    .set(Const.SM_PROTOCOL_INFO, requestDR.getReply().get(Const.SM_PROTOCOL_INFO))
                    .set(Const.SM_BODY, Const.EMPTY_MESSAGE);

            // generate next request message(dispatch result) with the response
            boolean isDone = this.service.dispatch(responseDR.getReply(), request);

            requestDR = new DispatchResult(request, isDone);
        }
    }

    // public void applyTicket(Composite tickeCose) {
    // Composite request = Composite.newArray().set(Const.SM_LENGTH,
    // Const.DEFAULT).set(Const.SM_MSG_ID, Const.DEFAULT)
    // .set(Const.SM_PROTOCOL_VERSION, Constants.PROTOCOL_VERSION.getBytes())
    // .set(Const.SM_PROTOCOL_INFO,
    // Composite.fromObject(Const.EMPTY_BYTE)).set(Const.SM_BODY, tickeCose);

    // DispatchResult requestDR = new DispatchResult(request, false);

    // // sent ticket and start challenge response & key exchange
    // while (!requestDR.isDone()) {
    // DispatchResult responseDR =
    // this.communication.sendMessage(requestDR.getReply());
    // request = Composite.newArray().set(Const.SM_LENGTH,
    // Const.DEFAULT).set(Const.SM_MSG_ID, Const.DEFAULT)
    // .set(Const.SM_PROTOCOL_VERSION,
    // requestDR.getReply().get(Const.SM_PROTOCOL_VERSION))
    // .set(Const.SM_PROTOCOL_INFO,
    // requestDR.getReply().get(Const.SM_PROTOCOL_INFO))
    // .set(Const.SM_BODY, Const.EMPTY_MESSAGE);

    // // generate next request message(dispatch result) with the response
    // boolean isDone = this.service.dispatch(responseDR.getReply(), request);

    // requestDR = new DispatchResult(request, isDone);
    // }
    // }

    /********** Device **********/

    /**
     * Recive package and check if the package is a ticket or session
     *
     * @return
     */
    public boolean receivePackage() {
        DispatchResult requestDR = this.communication.receiveMessage();
        DispatchResult responseDR;
        Composite response;
        Composite request = requestDR.getReply();
        // System.out.println(request.getAsNumber(Const.SM_MSG_ID).intValue());
        // System.out.println(requestDR.isDone());

        // if (requestDR.isDone()) {
        // System.out.println("is done");
        // // check if pending
        // if (request.getAsNumber(Const.SM_MSG_ID).intValue() == Constants.PENDING) {
        // return true;
        // }

        // // response R-ticket
        // // this.communication.responseMessage(rTicket);
        // // this.getRTicket();
        // return true;
        // }

        response = Composite.newArray().set(Const.SM_LENGTH, Const.DEFAULT).set(Const.SM_MSG_ID, Const.DEFAULT)
                .set(Const.SM_PROTOCOL_VERSION, request.get(Const.SM_PROTOCOL_VERSION))
                .set(Const.SM_PROTOCOL_INFO, request.get(Const.SM_PROTOCOL_INFO))
                .set(Const.SM_BODY, Const.EMPTY_MESSAGE);

        boolean isDone = this.service.dispatch(request, response);
        responseDR = new DispatchResult(response, isDone);
        this.communication.responseMessage(responseDR);
        return isDone;
    }

    @Override
    public void getRTicket() {
        Composite ticket = this.storage.getUTicket();
        MessageDigest digest;
        byte[] hash = new byte[0];
        try {
            digest = MessageDigest.getInstance("SHA-256");
            hash = digest.digest(ticket.toBytes());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        RTicket rTicket = new RTicket(storage.getSessionNum(), storage.getPublicKey());
        rTicket.setHash1(hash);

        Composite response = Composite.newArray().set(Const.SM_LENGTH, Const.DEFAULT)
                .set(Const.SM_MSG_ID, Constants.DONE)
                .set(Const.SM_PROTOCOL_VERSION, rTicket.getProtocolVersion())
                .set(Const.SM_PROTOCOL_INFO, Composite.fromObject(Const.EMPTY_BYTE))
                .set(Const.SM_BODY, rTicket.toCoseComposite());

        DispatchResult responseDR = new DispatchResult(response, true);

        this.communication.responseMessage(responseDR);
    }

    public Command acceptTicket() {
        // TODO Auto-generated method stub
        return null;
    }

    public void doCommand() {
        // TODO Auto-generated method stub

    }

    public void applyTicket() {
        // TODO Auto-generated method stub

    }

    public void applyEncryptedPackage() {
        // TODO Auto-generated method stub

    }

    public Ticket issue(PublicKey holderPublicKey, PublicKey devicePublicKey) {
        // TODO Auto-generated method stub
        return null;
    }

    public Ticket issueTicket(PublicKey holderPublicKey, PublicKey devicePublicKey, Command command) {
        UTicket UTicket = new UTicket(
                storage.getSessionNum()+1, devicePublicKey, holderPublicKey);
        UTicket.setCommand(command);
        UTicket.setTicketType(TicketType.ACCESS);
        // UTicket.setTicketType(TicketType.ACCESS_SESSION);
        UTicket.setValidCondition("validCondition".getBytes());
        Ticket ticket = this.service.signTicket(UTicket);
        storage.storeTicket(ticket);
        return ticket;
    }

    public Ticket issueInitTicket(PublicKey holderPublicKey, PublicKey devicePublicKey) {
        UTicket UTicket = new UTicket(
                storage.getSessionNum(), devicePublicKey, holderPublicKey);
        UTicket.setCommand(null);
        UTicket.setTicketType(TicketType.INIT);
        UTicket.setValidCondition("validCondition".getBytes());
        Ticket ticket = this.service.signTicket(UTicket);
        storage.storeTicket(ticket);
        return ticket;
    }

    public Ticket issueMangementTicket(PublicKey holderPublicKey, PublicKey devicePublicKey) {
        UTicket UTicket = new UTicket(
                storage.getSessionNum(), devicePublicKey, holderPublicKey);
        UTicket.setCommand(null);
        UTicket.setTicketType(TicketType.INIT);
        UTicket.setValidCondition("validCondition".getBytes());
        Ticket ticket = this.service.signTicket(UTicket);
        storage.storeTicket(ticket);
        return ticket;
    }

    public void UploadTicket(Ticket ticket) {
        // TODO Auto-generated method stub

    }

    public Ticket DownloadTicket() {
        // TODO Auto-generated method stub
        return null;
    }

    public PublicKey getPublicKey() {
        return storage.getPublicKey();
    }
}
