package com.sampleproject;


import com.tunnelmanager.lib.TunnelManager;
import com.tunnelmanager.utils.Log;

/**
 * Class
 *
 * @author Pierre-Olivier on 01/04/2014.
 */
public class SampleLauncher {
    public static void main(String[] args) {
        Log.v("client initialization...");

        TunnelHandler handler = new TunnelHandler();

        TunnelManager tunnelManager = new TunnelManager("127.0.0.1", 12000);
        tunnelManager = new TunnelManager("robots-war.com", 12000);
        tunnelManager.setTunnelManagerHandler(handler);

        tunnelManager.initialize();


    }
}
