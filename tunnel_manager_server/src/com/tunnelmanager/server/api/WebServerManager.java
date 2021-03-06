package com.tunnelmanager.server.api;

import com.tunnelmanager.server.api.actions.CreateTunnelAction;
import com.tunnelmanager.server.api.actions.Error404Action;
import com.tunnelmanager.server.api.actions.RefreshTunnelAction;

/**
 * Class WebServerManager
 * Manage the api server
 *
 * @author Pierre-Olivier on 06/04/2014.
 */
public class WebServerManager {
    /**
     * Return the action thanks to the url
     * @param handler android handler (action constructor)
     * @return android action
     */
    public static WebServerAction getAction(WebServerHandler handler) {
        String page = handler.getPage();

        if(page.equals("/create_tunnel")) {
            return new CreateTunnelAction(handler);
        } else if(page.equals("/refresh_tunnel")) {
            return new RefreshTunnelAction(handler);
        } else {
            return new Error404Action(handler);
        }
    }
}
