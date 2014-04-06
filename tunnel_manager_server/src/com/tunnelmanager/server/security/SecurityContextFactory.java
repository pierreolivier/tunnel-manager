package com.tunnelmanager.server.security;


import com.tunnelmanager.security.SecurityConfiguration;

import java.security.KeyStore;
import java.security.Security;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
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
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(SecurityKeyStore.asInputStream(), SecurityKeyStore.getKeyStorePassword());

            // Set up key manager factory to use our key store
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm);
            kmf.init(ks, SecurityKeyStore.getCertificatePassword());

            // Initialize the SSLContext to work with our key managers.
            context = SSLContext.getInstance(SecurityConfiguration.protocol);
            context.init(kmf.getKeyManagers(), null, null);
        } catch (Exception e) {
            throw new Error("Failed to initialize the server-side SSLContext", e);
        }
    }

    public static SSLContext getContext() {
        return context;
    }
}
