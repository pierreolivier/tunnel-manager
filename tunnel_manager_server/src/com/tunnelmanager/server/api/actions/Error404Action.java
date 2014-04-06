package com.tunnelmanager.server.api.actions;

import com.tunnelmanager.server.api.JsonFactory;
import com.tunnelmanager.server.api.WebServerAction;
import com.tunnelmanager.server.api.WebServerHandler;

import java.sql.SQLException;

/**
 * Class
 *
 * @author Pierre-Olivier on 06/04/2014.
 */
public class Error404Action extends WebServerAction {
    /**
     * Default constructor
     *
     * @param handler web server handler
     */
    public Error404Action(WebServerHandler handler) {
        super(handler);
    }

    @Override
    public String onExecute() throws SQLException, ClassNotFoundException, InterruptedException {
        return JsonFactory.error("error_404", "page_not_found");
    }
}
