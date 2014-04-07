package com.tunnelmanager.commands;

import com.tunnelmanager.handlers.ClientSideHandler;

/**
 * Class ServerCommand
 * Server to Client
 *
 * @author Pierre-Olivier on 02/04/2014.
 */
public abstract class ServerCommand extends Command {
    public ServerCommand(int ackId) {
        super(ackId);
    }

    /**
     * Execute command (on client)
     * @param handler client handler
     * @return command to send to the server
     */
    public abstract ClientCommand execute(ClientSideHandler handler);
}
