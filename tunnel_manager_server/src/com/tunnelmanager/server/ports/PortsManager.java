package com.tunnelmanager.server.ports;

import com.tunnelmanager.server.ServerManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Random;

/**
 * Class PortsManager
 *
 * @author Pierre-Olivier on 07/04/2014.
 */
public class PortsManager {
    private final static HashMap<Integer, PortStatus> portsStatus = new HashMap<>();

    public static void updatePortsStatus() {
        String OS = System.getProperty("os.name").toLowerCase();

        try {
            if(OS.contains("win")) {
                Process process = Runtime.getRuntime().exec("netstat -an");

                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                String line;
                synchronized (PortsManager.portsStatus) {
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
                }
            } else if (OS.contains("nix") || OS.contains("nux") || OS.contains("aix")) {
                // TODO
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Integer acquirePort() {
        Integer port;

        synchronized (PortsManager.portsStatus) {
            Random random = new Random();

            do {
                port = new Integer(random.nextInt(ServerManager.maxTunnelPort - ServerManager.minTunnelPort) + ServerManager.minTunnelPort);
            } while(PortsManager.portsStatus.containsKey(port));

            PortsManager.portsStatus.put(new Integer(port), PortStatus.BOUND);
        }

        return port;
    }
}
