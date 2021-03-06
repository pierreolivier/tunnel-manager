package com.tunnelmanager.server.database;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Class UsersDatabaseManager
 * Connect users table to java class
 *
 * @author Pierre-Olivier on 02/04/2014.
 */
public class UsersDatabaseManager {
    /**
     * Get all ssh public keys from database
     * @return list of string
     */
    public static List<String> getAllSshPublicKeys() {
        Database database = new Database();
        List<String> resultList = new ArrayList<>();

        try {
            ResultSet resultQuery = database.query("SELECT ssh_public_key FROM prefix_users");

            while (resultQuery.next()) {
                resultList.add(resultQuery.getString("ssh_public_key"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        database.clean();

        return resultList;
    }

    /**
     * Get user with public key and api key
     * @param sshPublicKey public key
     * @param apiKey api key
     * @return User object
     */
    public static User getUser(String sshPublicKey, String apiKey) {
        Database database = new Database();
        User resultUser = null;

        try {
            ResultSet resultQuery = database.query("SELECT * FROM prefix_users WHERE ssh_public_key = '" + sshPublicKey + "' AND api_key = '" + apiKey + "'");

            if(resultQuery.last() && resultQuery.getRow() == 1) {
                resultUser = new User(resultQuery.getInt("id"), resultQuery.getString("ssh_public_key"), resultQuery.getString("api_key"), resultQuery.getInt("allowed_tunnels"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        database.clean();

        return resultUser;
    }
}
