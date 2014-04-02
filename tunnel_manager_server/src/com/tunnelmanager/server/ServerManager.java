package com.tunnelmanager.server;

import com.tunnelmanager.server.database.Database;
import com.tunnelmanager.server.database.UsersManager;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.ResultSet;
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

    /**
     * Load properties file
     */
    public static void loadPropertiesFile() {
        try {
            Properties prop = new Properties();
            InputStream input = new FileInputStream("config.properties");
            prop.load(input);

            ServerManager.authorizedKeysPath = prop.getProperty("authorized_keys_path");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getAuthorizedKeysPath() {
        return authorizedKeysPath;
    }

    public static void updateAuthorizedKeysFile() throws SQLException, IOException, ClassNotFoundException {
        PrintWriter writer = new PrintWriter(ServerManager.authorizedKeysPath, "UTF-8");

        for (String sshPublicKey : UsersManager.getAllSshPublicKeys()) {
            writer.println(sshPublicKey);
        }

        writer.close();
    }
}
