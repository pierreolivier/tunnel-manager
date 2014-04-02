package com.tunnelmanager.commands.authentication;

import com.tunnelmanager.commands.ClientCommand;
import com.tunnelmanager.commands.Command;
import com.tunnelmanager.commands.ServerCommand;
import com.tunnelmanager.handlers.ClientSideHandler;
import com.tunnelmanager.handlers.ServerSideHandler;

/**
 * Class LoginCommand
 * Send ssh public key and api key to the server
 *
 * @author Pierre-Olivier on 02/04/2014.
 */
public class LoginCommand extends ClientCommand {
    /**
     * SSH public key
     */
    private final String sshPublicKey;

    /**
     * Api Key
     */
    private final String apiKey;

    /**
     * Default Constructor
     * @param sshPublicKey ssh public key used for the login
     * @param apiKey api key used for the login
     */
    public LoginCommand(ClientSideHandler handler, String sshPublicKey, String apiKey) {
        super(handler);

        this.sshPublicKey = sshPublicKey;
        this.apiKey = apiKey;
    }

    @Override
    public ServerCommand execute(ServerSideHandler handler) {
        LoginResponseCommand loginResponseCommand;

        if(handler.login(this)) {
            loginResponseCommand = new LoginResponseCommand(handler, LoginResponseCommand.CONNECTED);
        } else {
            loginResponseCommand = new LoginResponseCommand(handler, LoginResponseCommand.ERROR);
        }

        return loginResponseCommand;
    }

    public String getSshPublicKey() {
        return sshPublicKey;
    }

    public String getApiKey() {
        return apiKey;
    }

    @Override
    public String toString() {
        return "LoginCommand{" +
                "ackId='" + ackId + '\'' +
                ", sshPublicKey='" + sshPublicKey + '\'' +
                ", apiKey='" + apiKey + '\'' +
                '}';
    }
}
