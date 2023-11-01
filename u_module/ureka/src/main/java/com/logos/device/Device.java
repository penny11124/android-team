package com.logos.device;

import java.security.PublicKey;

import com.logos.ticket_module.Communication;
import com.logos.ticket_module.TicketModule;
import com.logos.ticket_module.module_interface.DeviceModuleInterface;
import com.logos.ticket_module.service.UDeviceService;
import com.logos.ticket_module.storage.UStorage;

public class Device {
    private DeviceModuleInterface UModule;
    Communication communication;
    UStorage storage;
    UDeviceService service;

    public Device(Communication communication, UStorage storage, UDeviceService service) {
        this.UModule = new TicketModule(communication, service, storage);
    }

    public Device(UStorage storage, UDeviceService service) {
        this.storage = storage;
        this.service = service;
    }

    public void up() {
        this.UModule = new TicketModule(this.communication, this.service, this.storage);
    }

    public void setCommunication(Communication communication) {
        this.communication = communication;
    }

    public boolean receivePackage() {
        return UModule.receivePackage();
    }

    public PublicKey getPublicKey() {
        return UModule.getPublicKey();
    }

    public void getRTicket() {
        UModule.getRTicket();
    }

}
