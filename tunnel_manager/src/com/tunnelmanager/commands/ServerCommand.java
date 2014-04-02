package com.tunnelmanager.commands;

import com.tunnelmanager.handlers.ClientSideHandler;
import com.tunnelmanager.handlers.ServerSideHandler;

/**
 * Class ServerCommand
 * Server to Client
 * @author Pierre-Olivier on 02/04/2014.
 */
public abstract class ServerCommand extends Command {
    public ServerCommand(ServerSideHandler handler) {
        super(handler.nextAckId());
    }

    public abstract ClientCommand execute(ClientSideHandler handler);
}
