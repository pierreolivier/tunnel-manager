package com.tunnelmanager.server.ports;

import com.tunnelmanager.server.ServerManager;
import com.tunnelmanager.server.database.Port;
import com.tunnelmanager.server.database.PortsDatabaseManager;
import com.tunnelmanager.server.database.User;
import com.tunnelmanager.utils.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Class PortsManager
 *
 * @author Pierre-Olivier on 07/04/2014.
 */
public class PortsManager {
    private final static HashMap<Integer, PortStatus> portsStatus = new HashMap<>();
    private final static HashMap<User, List<Port>> portsUser = new HashMap<>();

    private static void updatePortsStatus() {
        try {
            if(ServerManager.onWindows()) {
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
            } else if (ServerManager.onLinux()) {
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

                            if (tokensPort.length > 0 && tokensPort[0].matches("^[0-9]+$")) {
                                PortsManager.portsStatus.put(Integer.parseInt(tokensPort[0]), new PortStatus(PortStatus.PortState.BOUND, pid));
                            }
                        }

                        if(tokens.length > 3) { // ::1: or :::
                            String[] tokensPort = tokens[3].split(" ");
                            if (tokensPort.length > 0 && tokensPort[0].matches("^[0-9]+$")) {
                                PortsManager.portsStatus.put(Integer.parseInt(tokensPort[0]), new PortStatus(PortStatus.PortState.BOUND, pid));
                            }
                        }
                    }
                } while (line != null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getPid(int portNumber) {
        try {
            if(ServerManager.onWindows()) {
                Process process = Runtime.getRuntime().exec("netstat -ano");

                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                String line;
                do {
                    line = reader.readLine();
                    if (line != null && line.matches(".*" + portNumber + ".*")) {
                        String[] tokensPid = line.split(" ");

                        return tokensPid[tokensPid.length - 1];
                    }
                } while (line != null);
            } else if (ServerManager.onLinux()) {
                Process process = Runtime.getRuntime().exec("netstat -lntp");

                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                String line;
                do {
                    line = reader.readLine();
                    if (line != null && line.matches(".*" + portNumber + ".*")) {
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

    public static Integer acquirePort(User user, String data) {
        Integer portNumber;

        synchronized (PortsManager.portsStatus) {
            updatePortsStatus();

            Random random = new Random();

            do {
                portNumber = new Integer(random.nextInt(ServerManager.getMaxTunnelPort() - ServerManager.getMinTunnelPort()) + ServerManager.getMinTunnelPort());
            } while(PortsManager.portsStatus.containsKey(portNumber));

            PortsManager.portsStatus.put(new Integer(portNumber), new PortStatus(PortStatus.PortState.WAITING, null));

            addPortToUser(user, new Integer(portNumber), data);
        }

        return portNumber;
    }

    public static void validatePort(User user, Integer portNumber) {
        Port port = getPort(user, portNumber);
        if(port != null) {
            synchronized (PortsManager.portsStatus) {
                PortStatus portStatus = PortsManager.portsStatus.get(portNumber);
                portStatus.setState(PortStatus.PortState.BOUND);
                portStatus.setPid(getPid(portNumber));

                port.setState(PortStatus.getDatabaseState(PortStatus.PortState.BOUND));
            }
        }
    }

    public static void releasePort(User user, Integer portNumber) {
        Port port;

        synchronized (PortsManager.portsStatus) {
            PortStatus portStatus = PortsManager.portsStatus.get(portNumber);
            if(portStatus != null && portStatus.getPid() != null) {
                try {
                    if (ServerManager.onWindows()) {
                        Runtime.getRuntime().exec("taskkill " + portStatus.getPid()).waitFor();
                    } else if (ServerManager.onLinux()) {
                        Runtime.getRuntime().exec("kill -9 " + portStatus.getPid()).waitFor();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            PortsManager.portsStatus.remove(portNumber);

            synchronized (PortsManager.portsUser) {
                port = getPort(user, portNumber);
                List<Port> ports = PortsManager.portsUser.get(user);

                if (ports != null && port != null) {
                    ports.remove(port);

                    if(ports.size() == 0) {
                        PortsManager.portsUser.remove(user);
                    }
                }
            }
        }

        if(port != null) {
            PortsDatabaseManager.deletePort(port);
        }
    }

    public static void releaseAllPorts(User user) {
        synchronized (PortsManager.portsUser) {
            PortsManager.portsUser.remove(user);

            // TODO kill pids + database clear
        }
    }

    private static void addPortToUser(User user, Integer portNumber, String data) {
        synchronized (PortsManager.portsUser) {
            List<Port> ports = PortsManager.portsUser.get(user);
            if(ports == null) {
                ports = new ArrayList<>();
                PortsManager.portsUser.put(user, ports);
            }

            Port port = new Port(user.getId(), portNumber, System.currentTimeMillis(), ServerManager.getTunnelTimeout(), data);
            PortsDatabaseManager.insertPort(port);

            if(!ports.contains(port)) {
                ports.add(port);
            }
        }
    }

    private static boolean isPortToUser(User user, Integer portNumber) {
        synchronized (PortsManager.portsUser) {
            List<Port> ports = PortsManager.portsUser.get(user);

            for(Port port : ports) {
                if(port.getLocalPort() == portNumber) {
                    return true;
                }
            }

            return false;
        }
    }

    private static Port getPort(User user, Integer portNumber) {
        synchronized (PortsManager.portsUser) {
            List<Port> ports = PortsManager.portsUser.get(user);

            for(Port port : ports) {
                if(port.getLocalPort() == portNumber) {
                    return port;
                }
            }

            return null;
        }
    }

    public static PortStatus getPortStatus(Integer portNumber) {
        synchronized (PortsManager.portsStatus) {
            return PortsManager.portsStatus.get(portNumber);
        }
    }
}
