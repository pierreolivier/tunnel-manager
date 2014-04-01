package com.tunnelmanager.server;

import com.tunnelmanager.utils.Log;

/**
 * Class ServerLauncher
 * Java entry point
 *
 * @author Pierre-Olivier on 01/04/2014.
 */
public class ServerLauncher {
    public static void main(String[] args) {
        Log.v("server initialization...");

        ServerMain serverMain = new ServerMain();
        serverMain.init(args);
    }
}
