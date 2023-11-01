package com.logos.device;

import com.logos.ticket_module.Command;

public class DeviceCommand extends Command {

    public DeviceCommand(String commandString) {
        super(commandString);
    }

    public DeviceCommand(byte[] command) {
        super(command);
    }

    // @Override
    public Command fromBytes(byte[] bytes) {
        return new DeviceCommand(bytes);
    }
}
