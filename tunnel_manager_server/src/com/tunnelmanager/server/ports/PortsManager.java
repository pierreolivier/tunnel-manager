package com.tunnelmanager.server.ports;

import com.tunnelmanager.server.ServerManager;
import com.tunnelmanager.server.database.User;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Class PortsManager
 *
 * @author Pierre-Olivier on 07/04/2014.
 */
public class PortsManager {
    private final static HashMap<Integer, PortStatus> portsStatus = new HashMap<>();
    private final static HashMap<User, List<Integer>> portsUser = new HashMap<>();

    private static void updatePortsStatus() {
        String OS = System.getProperty("os.name").toLowerCase();

        try {
            if(OS.contains("win")) {
                Process process = Runtime.getRuntime().exec("netstat -an");

                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                String line;
                do {
                    line = reader.readLine();
                    if (line != null) {
                        String[] tokens = line.split(":");

                        if (tokens.length == 3) {
                            String[] tokensPort = tokens[1].split(" ");

                            if (tokensPort.length >= 1 && tokensPort[0].matches("^[0-9]*$")) {
                                PortsManager.portsStatus.put(Integer.parseInt(tokensPort[0]), PortStatus.BOUND);
                            }
                        }
                    }
                } while (line != null);
            } else if (OS.contains("nix") || OS.contains("nux") || OS.contains("aix")) {
                // TODO
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Integer acquirePort(User user) {
        Integer port;

        synchronized (PortsManager.portsStatus) {
            updatePortsStatus();

            Random random = new Random();

            do {
                port = new Integer(random.nextInt(ServerManager.maxTunnelPort - ServerManager.minTunnelPort) + ServerManager.minTunnelPort);
            } while(PortsManager.portsStatus.containsKey(port));

            PortsManager.portsStatus.put(new Integer(port), PortStatus.WAITING);

            addPortToUser(user, new Integer(port));
        }

        return port;
    }

    private static void addPortToUser(User user, Integer port) {
        synchronized (PortsManager.portsUser) {
            List<Integer> ports = PortsManager.portsUser.get(user);
            if(ports == null) {
                ports = new ArrayList<>();
                PortsManager.portsUser.put(user, ports);
            }

            if(!ports.contains(port)) {
                ports.add(port);
            }
        }
    }

    public static void releasePorts(User user) {
        synchronized (PortsManager.portsUser) {
            PortsManager.portsUser.remove(user);

            // TODO
        }
    }
}
