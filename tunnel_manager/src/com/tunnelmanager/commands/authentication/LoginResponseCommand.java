package com.tunnelmanager.commands.authentication;

import com.tunnelmanager.commands.ClientCommand;
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

    public LoginResponseCommand(int ackId, int loginStatus) {
        super(ackId);

        this.loginStatus = loginStatus;
    }

    @Override
    public ClientCommand execute(ClientSideHandler handler) {
        handler.onLoginResponse(this.loginStatus);

        return null;
    }

    @Override
    public String toString() {
        return "LoginResponseCommand{" +
                "loginStatus=" + loginStatus +
                '}';
    }
}
