package com.tunnelmanager.server.security;

import com.tunnelmanager.server.ServerManager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Class
 *
 * @author Pierre-Olivier on 07/03/14.
 */
public class SecurityKeyStore {
    /**
     * A bogus key store which provides all the required information to
     * create an example SSL connection.
     *
     * To generate a bogus key store:
     * <pre>
     * keytool  -genkey -alias securechat -keysize 2048 -validity 36500
     *          -keyalg RSA -dname "CN=securechat"
     *          -keypass secret -storepass secret
     *          -keystore cert.jks
     * </pre>
     *
     * keytool -genkey -alias tunnel_manager_certificate -keysize 2048 -validity 400000 -keyalg RSA -dname "CN=tunnel_manager_server" -keypass tunnel_manager -storepass tunnel_manager_keystore -keystore certificate.jks
     *
     */

    public static InputStream asInputStream() {
        try {
            return new FileInputStream(ServerManager.getCertificatePath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static char[] getCertificatePassword() {
        return ServerManager.getCertificatePassword().toCharArray();
    }

    public static char[] getKeyStorePassword() {
        return ServerManager.getCertificateKeyStorePassword().toCharArray();
    }
}
