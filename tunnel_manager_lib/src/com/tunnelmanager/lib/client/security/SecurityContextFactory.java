package com.tunnelmanager.lib.client.security;

import com.tunnelmanager.security.SecurityConfiguration;
import com.tunnelmanager.security.SecurityTrustManagerFactory;

import javax.net.ssl.SSLContext;
import java.security.Security;

/**
 * Class SecurityContextFactory
 *
 * @author Pierre-Olivier on 06/04/2014.
 */
public class SecurityContextFactory {
    private static SSLContext context;

    static {
        String algorithm = Security.getProperty("ssl.KeyManagerFactory.algorithm");
        if (algorithm == null) {
            algorithm = "SunX509";
        }

        try {
            context = SSLContext.getInstance(SecurityConfiguration.protocol);
            context.init(null, SecurityTrustManagerFactory.getTrustManagers(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static SSLContext getContext() {
        return context;
    }
}
