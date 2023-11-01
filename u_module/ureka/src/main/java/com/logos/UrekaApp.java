package com.logos;

import com.logos.device.Device;
import com.logos.device.DeviceCommand;
import com.logos.device.DeviceExecution;
import com.logos.device.DeviceStorage;
import com.logos.ticket_module.Command;
import com.logos.ticket_module.Communication;
import com.logos.ticket_module.Execution;
import com.logos.ticket_module.Ticket;
import com.logos.ticket_module.service.UDeviceService;
import com.logos.ticket_module.service.UUserService;
import com.logos.ticket_module.storage.UserStorageInterface;
import com.logos.user.User;
import com.logos.user.UserStorage;

/**
 * Hello world!
 *
 */
public class UrekaApp {
    public static void main(String[] args) {
        try {
            UserStorageInterface userStorage = new UserStorage();
            UUserService userService = new UUserService(userStorage);

            DeviceStorage deviceStorage = new DeviceStorage();
            Execution execution = new DeviceExecution();
            UDeviceService deviceService = new UDeviceService(deviceStorage, execution);
            Device device = new Device(deviceStorage, deviceService);

            Communication communication = new DummyCommunication(device);

            User user = new User(userStorage, userService, communication);

            Command command = new DeviceCommand("open");

            Ticket ticket = user.issueUTicket(user.getPublicKey(), device.getPublicKey(), command);

            System.out.println("start");

            user.applyTicket(ticket);
            device.getRTicket();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
