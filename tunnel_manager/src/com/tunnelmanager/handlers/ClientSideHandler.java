package com.tunnelmanager.handlers;

/**
 * Interface ClientSideHandler
 * Client handler
 *
 * @author Pierre-Olivier on 02/04/2014.
 */
public interface ClientSideHandler extends Handler {
    /**
     * On login response callback
     * @param status login status
     */
    public void onLoginResponse(int status);

    public String getPrivateKeyPath();
}
