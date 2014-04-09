package com.tunnelmanager.server.ports;

import com.tunnelmanager.server.ServerManager;
import com.tunnelmanager.server.database.User;
import com.tunnelmanager.utils.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

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
                Process process = Runtime.getRuntime().exec("netstat -ano");

                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                String line;
                do {
                    line = reader.readLine();
                    if (line != null) {
                        String[] tokens = line.split(":");
                        String[] tokensPid = line.split(" ");

                        String pid = tokensPid[tokensPid.length - 1];

                        if (tokens.length == 3) { // x.x.x.x:
                            String[] tokensPort = tokens[1].split(" ");

                            if (tokensPort.length >= 1 && tokensPort[0].matches("^[0-9]*$")) {
                                PortsManager.portsStatus.put(Integer.parseInt(tokensPort[0]), new PortStatus(PortStatus.PortState.BOUND, pid));
                            }
                        } else if (tokens.length == 5) { // [::]:
                            String[] tokensPort = tokens[3].split(" ");

                            if (tokensPort.length >= 1 && tokensPort[0].matches("^[0-9]*$")) {
                                PortsManager.portsStatus.put(Integer.parseInt(tokensPort[0]), new PortStatus(PortStatus.PortState.BOUND, pid));
                            }
                        }
                    }
                } while (line != null);
            } else if (OS.contains("nix") || OS.contains("nux") || OS.contains("aix")) {
                Process process = Runtime.getRuntime().exec("netstat -lntp");

                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                String line;
                do {
                    line = reader.readLine();
                    if (line != null) {
                        String[] tokens = line.split(":");
                        String pid = null;

                        String[] tokensPid = line.split("/");
                        if(tokensPid.length > 0) {
                            String[] subTokensPid = tokensPid[0].split(" ");

                            pid = subTokensPid[subTokensPid.length - 1];
                        }

                        if (tokens.length > 1) { // x.x.x.x:
                            String[] tokensPort = tokens[1].split(" ");

                            if (tokensPort.length >= 1 && tokensPort[0].matches("^[0-9]*$")) {
                                PortsManager.portsStatus.put(Integer.parseInt(tokensPort[0]), new PortStatus(PortStatus.PortState.BOUND, pid));
                            }

                            Log.v(tokensPort[0] + pid);
                        }
                    }
                } while (line != null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getPid(int port) {
        String OS = System.getProperty("os.name").toLowerCase();

        try {
            if(OS.contains("win")) {
                Process process = Runtime.getRuntime().exec("netstat -ano");

                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                String line;
                do {
                    line = reader.readLine();
                    if (line != null && line.matches(".*" + port + ".*")) {
                        String[] tokensPid = line.split(" ");

                        return tokensPid[tokensPid.length - 1];
                    }
                } while (line != null);
            } else if (OS.contains("nix") || OS.contains("nux") || OS.contains("aix")) {
                Process process = Runtime.getRuntime().exec("netstat -lntp");

                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                String line;
                do {
                    line = reader.readLine();
                    if (line != null && line.matches(".*" + port + ".*")) {
                        String[] tokensPid = line.split("/");
                        if(tokensPid.length > 0) {
                            String[] subTokensPid = tokensPid[0].split(" ");

                            return subTokensPid[subTokensPid.length - 1];
                        }
                    }
                } while (line != null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Integer acquirePort(User user) {
        Integer port;

        synchronized (PortsManager.portsStatus) {
            updatePortsStatus();

            Random random = new Random();

            do {
                port = new Integer(random.nextInt(ServerManager.maxTunnelPort - ServerManager.minTunnelPort) + ServerManager.minTunnelPort);
            } while(PortsManager.portsStatus.containsKey(port));

            PortsManager.portsStatus.put(new Integer(port), new PortStatus(PortStatus.PortState.WAITING, null));

            addPortToUser(user, new Integer(port));
        }

        return port;
    }

    public static void validatePort(User user, Integer port) {
        if(isPortToUser(user, port)) {
            synchronized (PortsManager.portsStatus) {
                PortStatus portStatus = PortsManager.portsStatus.get(port);
                portStatus.setState(PortStatus.PortState.BOUND);
                portStatus.setPid(getPid(port));
            }
        }
    }

    public static void releasePorts(User user) {
        synchronized (PortsManager.portsUser) {
            PortsManager.portsUser.remove(user);

            // TODO kill pids
        }
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

    private static boolean isPortToUser(User user, Integer port) {
        synchronized (PortsManager.portsUser) {
            List<Integer> ports = PortsManager.portsUser.get(user);
            if(ports != null && ports.contains(port)) {
                return true;
            } else {
                return false;
            }
        }
    }
}
