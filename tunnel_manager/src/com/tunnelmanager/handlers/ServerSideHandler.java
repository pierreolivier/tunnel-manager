package com.tunnelmanager.handlers;

import com.tunnelmanager.commands.LoginCommand;

/**
 * Class
 *
 * @author Pierre-Olivier on 02/04/2014.
 */
public interface ServerSideHandler {
    public boolean login(LoginCommand command);
}
