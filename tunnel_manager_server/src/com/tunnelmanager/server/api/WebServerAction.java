package com.tunnelmanager.server.api;

import com.tunnelmanager.server.database.Database;

import java.sql.SQLException;

/**
 * Class
 *
 * @author Pierre-Olivier on 06/04/2014.
 */
public abstract class WebServerAction {
    /**
     * Netty handler
     */
    protected final WebServerHandler handler;

    /**
     * Database instance
     */
    protected final Database database;

    /**
     * Default constructor
     * @param handler web server handler
     */
    public WebServerAction(WebServerHandler handler) {
        this.handler = handler;
        this.database = new Database();
    }

    /**
     * execute method
     * Command Pattern
     * @return json string
     */
    public String execute() throws SQLException, ClassNotFoundException, InterruptedException {
        String json = onExecute();

        this.database.clean();

        return json;
    }

    /**
     * onExecute method
     * Command Pattern
     * @return json string
     */
    public abstract String onExecute() throws SQLException, ClassNotFoundException, InterruptedException;

    /**
     * Check if post names exist
     * @param args list of post name
     * @return
     */
    public boolean checkPost(String... args) {
        if(this.handler.getPost() == null) {
            return false;
        }

        for(String post : args) {
            if(this.handler.getPost().get(post) == null) {
                return false;
            }
        }

        return true;
    }

    /**
     * Return post value
     * Must be checked with checkPost
     * @param arg post name
     * @return post value
     */
    public String getPost(String arg) {
        return this.handler.getPost().get(arg).get(0);
    }
}
