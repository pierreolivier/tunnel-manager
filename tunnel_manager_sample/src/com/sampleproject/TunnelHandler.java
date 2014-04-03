package com.sampleproject;

import com.tunnelmanager.commands.authentication.LoginResponseCommand;
import com.tunnelmanager.lib.TunnelManagerHandler;
import com.tunnelmanager.utils.Log;

/**
 * Class
 *
 * @author Pierre-Olivier on 03/04/2014.
 */
public class TunnelHandler extends TunnelManagerHandler {
    @Override
    public void onLoginResponse(int status) {
        if(status == LoginResponseCommand.CONNECTED) {
            Log.v("connected");
        } else if (status == LoginResponseCommand.ERROR) {
            Log.v("connection error, check your ssh public key and api key.");
        }
    }
}
