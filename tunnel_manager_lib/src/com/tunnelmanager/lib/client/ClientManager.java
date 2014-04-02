package com.tunnelmanager.lib.client;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

/**
 * Class
 *
 * @author Pierre-Olivier on 02/04/2014.
 */
public class ClientManager {
    /**
     * id_rsa.pub path
     */
    private static String publicKeyPath;

    /**
     * Load properties file
     */
    public static void loadPropertiesFile() {
        try {
            Properties prop = new Properties();
            InputStream input = new FileInputStream("config.properties");
            prop.load(input);

            ClientManager.publicKeyPath = prop.getProperty("public_key_path");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getPublicKeyPath() {
        return publicKeyPath;
    }

    public static String getPublicKey() {
        return ClientManager.getPublicKey(ClientManager.publicKeyPath);
    }

    public static String getPublicKey(final String path) {
        String result = null;

        try {
            List<String> lines = Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8);
            if(lines.size() > 0) {
                String[] tokens = lines.get(0).split(" ");
                if(tokens.length >= 2) {
                    result = tokens[0] + " " + tokens[1];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
