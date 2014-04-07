package com.tunnelmanager.server;

import com.tunnelmanager.server.client.ClientHandler;
import com.tunnelmanager.server.database.UsersManager;
import com.tunnelmanager.utils.Log;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;

/**
 * Class ServerManager
 * Manage the server
 *
 * @author Pierre-Olivier on 02/04/2014.
 */
public class ServerManager {
    /**
     * Client port
     */
    public static int clientPort;

    /**
     * Web server port
     */
    public static int webApiPort;

    /**
     * authorized_keys path
     */
    private static String authorizedKeysPath;

    /**
     * Certificate path
     */
    private static String certificatePath;

    /**
     * Certificate password
     */
    private static String certificatePassword;

    /**
     * Key store password
     */
    private static String certificateKeyStorePassword;

    /**
     * Connected (only logged) clients
     */
    private final static HashMap<String, ClientHandler> clients = new HashMap<>();

    /**
     * Load properties file
     */
    public static void loadPropertiesFile() {
        try {
            Properties prop = new Properties();
            InputStream input = new FileInputStream("config.properties");
            prop.load(input);

            try {
                ServerManager.clientPort = new Integer(prop.getProperty("client_port", "12000"));
            } catch (Exception e) {
                Log.e("client_port is not an integer");

                System.exit(1);
            }
            try {
                ServerManager.webApiPort = new Integer(prop.getProperty("web_api_port", "12001"));
            } catch (Exception e) {
                Log.e("web_api_port is not an integer");

                System.exit(1);
            }
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

    /**
     * Add a new logged client
     * @param apiKey api key
     * @param handler client handler
     */
    public static void addClient(String apiKey, ClientHandler handler) {
        synchronized (ServerManager.clients) {
            ServerManager.clients.put(apiKey, handler);
        }
    }

    /**
     * Get a logged client
     * @param apiKey api key
     * @return client handler
     */
    public static ClientHandler getClient(String apiKey) {
        synchronized (ServerManager.clients) {
            return ServerManager.clients.get(apiKey);
        }
    }

    /**
     * Remove a logged client
     * @param apiKey api key
     */
    public static void removeClient(String apiKey) {
        synchronized (ServerManager.clients) {
            ServerManager.clients.remove(apiKey);
        }
    }
}
