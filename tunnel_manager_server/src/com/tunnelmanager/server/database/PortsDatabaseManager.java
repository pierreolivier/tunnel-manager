package com.tunnelmanager.server.database;

import com.tunnelmanager.server.ports.PortStatus;
import com.tunnelmanager.utils.Log;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Class PortsDatabaseManager
 * Connect ports table to java class
 *
 * @author Pierre-Olivier on 02/04/2014.
 */
public class PortsDatabaseManager {
    /**
     * Clear all ports in database table
     */
    public static void clearPorts() {
        Database database = new Database();

        try {
            database.exec("TRUNCATE TABLE prefix_ports");
        } catch (Exception e) {
            e.printStackTrace();
        }

        database.clean();
    }

    /**
     * Link a new port with database
     * @param port port
     */
    public static void insertPort(Port port) {
        Database database = new Database();

        try {
            database.exec("INSERT INTO prefix_ports VALUES ('0', " + port.getIdUser() + ", " + port.getLocalPort() + ", " + PortStatus.getDatabaseState(PortStatus.PortState.WAITING) + ", " + port.getStart() + ", " + port.getTimeout() + ", '" + port.getData() + "')");

            ResultSet resultQuery = database.query("SELECT * FROM prefix_ports WHERE local_port = " + port.getLocalPort());
            if(resultQuery.last() && resultQuery.getRow() == 1) {
                port.setId(resultQuery.getInt("id"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        database.clean();
    }

    /**
     * Update an existing port in database
     * @param port port
     */
    public static void updatePort(Port port) {
        if(port.getId() != -1) {
            Database database = new Database();

            try {
                database.exec("UPDATE prefix_ports SET local_port = " + port.getLocalPort() + ", state = " + port.getState() + ", start = " + port.getStart() + ", timeout = " + port.getTimeout() + ", data = '" + port.getData() + "' WHERE id = " + port.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }

            database.clean();
        }
    }

    /**
     * Delete an existing port in database
     * @param port port
     */
    public static void deletePort(Port port) {
        if(port.getId() != -1) {
            Database database = new Database();

            try {
                database.exec("DELETE FROM prefix_ports WHERE id = " + port.getId());

                port.setId(-1);
            } catch (Exception e) {
                e.printStackTrace();
            }

            database.clean();
        }
    }
}
