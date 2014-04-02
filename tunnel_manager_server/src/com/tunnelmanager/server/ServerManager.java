package com.tunnelmanager.server;

import com.tunnelmanager.server.database.Database;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Class
 *
 * @author Pierre-Olivier on 02/04/2014.
 */
public class ServerManager {
    private static String authorizedKeysPath;

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
        Database database = new Database();
        PrintWriter writer = new PrintWriter(ServerManager.authorizedKeysPath, "UTF-8");

        ResultSet result = database.query("SELECT ssh_public_key FROM prefix_users");

        while (result.next()) {
            String sshPublicKey = result.getString("ssh_public_key");

            writer.println(sshPublicKey);
        }

        writer.close();
        database.clean();
    }
}
