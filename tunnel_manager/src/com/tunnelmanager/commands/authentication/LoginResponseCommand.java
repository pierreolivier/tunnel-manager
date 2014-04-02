package com.tunnelmanager.commands.authentication;

import com.tunnelmanager.commands.ClientCommand;
import com.tunnelmanager.commands.Command;
import com.tunnelmanager.commands.ServerCommand;
import com.tunnelmanager.handlers.ClientSideHandler;
import com.tunnelmanager.handlers.ServerSideHandler;

/**
 * Class LoginResponseCommand
 * Send login response to the client
 *
 * @author Pierre-Olivier on 02/04/2014.
 */
public class LoginResponseCommand extends ServerCommand {
    public transient final static int ERROR = 0;
    public transient final static int CONNECTED = 1;

    /**
     * Login status
     */
    private int loginStatus;

    public LoginResponseCommand(ServerSideHandler handler, int loginStatus) {
        super(handler);

        this.loginStatus = loginStatus;
    }

    @Override
    public ClientCommand execute(ClientSideHandler handler) {
        return null;
    }

    @Override
    public String toString() {
        return "LoginResponseCommand{" +
                "loginStatus=" + loginStatus +
                '}';
    }
}
