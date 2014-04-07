package com.tunnelmanager.lib;

/**
 * Class TunnelManagerHandler
 * Library handler
 *
 * @author Pierre-Olivier on 01/04/2014.
 */
public abstract class TunnelManagerHandler {
    /**
     * On login response callback
     * @param status status
     */
    public abstract void onLoginResponse(int status);
}
