package com.tunnelmanager.commands;

import com.tunnelmanager.handlers.ServerSideHandler;

/**
 * Class LoginCommand
 * Login command, send ssh public key to the server
 *
 * @author Pierre-Olivier on 02/04/2014.
 */
public class LoginCommand extends ClientCommand {
    /**
     * SSH public key
     */
    private final String sshPublicKey;

    /**
     * Default Constructor
     * @param sshPublicKey ssh public key used for the login
     */
    public LoginCommand(String sshPublicKey) {
        this.sshPublicKey = sshPublicKey;
    }

    @Override
    public Command execute(ServerSideHandler handler) {
        if(handler.login(this)) {

        } else {

        }

        return null;
    }

    public String getSshPublicKey() {
        return sshPublicKey;
    }
}
