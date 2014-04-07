package com.tunnelmanager.lib.client.security;

import com.tunnelmanager.lib.client.ClientManager;
import com.tunnelmanager.security.SecurityConfiguration;

import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.Certificate;

/**
 * Class SecurityContextFactory
 * Create security context
 *
 * @author Pierre-Olivier on 06/04/2014.
 */
public class SecurityContextFactory {
    /**
     * SSL Context
     */
    private static SSLContext context;

    /**
     * Trusted public key
     */
    private static PublicKey publicKey;

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

        try {
            FileInputStream in = new FileInputStream(ClientManager.getTrustStorePath());

            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(in, ClientManager.getTrustStorePassword().toCharArray());

            Certificate certificate = keyStore.getCertificate(keyStore.aliases().nextElement());
            SecurityContextFactory.publicKey = certificate.getPublicKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static SSLContext getContext() {
        return context;
    }

    public static PublicKey getPublicKey() {
        return publicKey;
    }
}
