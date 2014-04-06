package com.tunnelmanager.server;

import com.tunnelmanager.server.database.UsersManager;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Class ServerManager
 * Manage the server
 *
 * @author Pierre-Olivier on 02/04/2014.
 */
public class ServerManager {
    /**
     * authorized_keys path
     */
    private static String authorizedKeysPath;

    private static String certificatePath;

    private static String certificatePassword;

    private static String certificateKeyStorePassword;

    /**
     * Load properties file
     */
    public static void loadPropertiesFile() {
        try {
            Properties prop = new Properties();
            InputStream input = new FileInputStream("config.properties");
            prop.load(input);

            ServerManager.authorizedKeysPath = prop.getProperty("authorized_keys_path");
            ServerManager.certificatePath = prop.getProperty("certificate_path");
            ServerManager.certificatePassword = prop.getProperty("certificate_password");
            ServerManager.certificateKeyStorePassword = prop.getProperty("certificate_key_store_password");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getAuthorizedKeysPath() {
        return authorizedKeysPath;
    }

    public static String getCertificatePath() {
        return certificatePath;
    }

    public static String getCertificatePassword() {
        return certificatePassword;
    }

    public static String getCertificateKeyStorePassword() {
        return certificateKeyStorePassword;
    }

    public static void updateAuthorizedKeysFile() throws SQLException, IOException, ClassNotFoundException {
        PrintWriter writer = new PrintWriter(ServerManager.authorizedKeysPath, "UTF-8");

        for (String sshPublicKey : UsersManager.getAllSshPublicKeys()) {
            writer.println(sshPublicKey);
        }

        writer.close();
    }
}
