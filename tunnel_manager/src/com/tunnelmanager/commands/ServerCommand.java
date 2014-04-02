package com.tunnelmanager.commands;

import com.tunnelmanager.handlers.ClientSideHandler;

/**
 * Class ServerCommand
 *
 * @author Pierre-Olivier on 02/04/2014.
 */
public abstract class ServerCommand extends Command {
    public abstract Command execute(ClientSideHandler handler);
}
