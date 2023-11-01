package com.example.ureka_android.ureka;

import com.logos.ticket_module.Command;

public class DeviceCommand extends Command {
    private CommandType commandType;

    public DeviceCommand(String commandString) {
        super(commandString);
    }

    public DeviceCommand(byte[] command) {
        super(command);
    }

    public DeviceCommand(CommandType commandType) {
        super(commandType.name().getBytes());
    }

    @Override
    public byte[] getByte() {
        return super.getByte();
    }
}
