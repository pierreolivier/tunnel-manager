package com.tunnelmanager.handlers;

import com.tunnelmanager.commands.authentication.LoginCommand;
import com.tunnelmanager.commands.tunnel.CreateTunnelResponseCommand;

/**
 * Class ServerSideHandler
 * server handler
 *
 * @author Pierre-Olivier on 02/04/2014.
 */
public interface ServerSideHandler extends Handler {
    /**
     * Login method
     * Must be called in first
     * @param command Command
     * @return true if connected else false
     */
    public boolean login(LoginCommand command);

    public void portBound(CreateTunnelResponseCommand command);

    public void releasePort(CreateTunnelResponseCommand command);
}
