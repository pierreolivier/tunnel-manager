package com.tunnelmanager.server.database;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class UsersManager
 * Connect users table to java class
 *
 * @author Pierre-Olivier on 02/04/2014.
 */
public class UsersManager {
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
}
