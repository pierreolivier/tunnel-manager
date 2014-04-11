package com.tunnelmanager.server.ports;

import com.tunnelmanager.server.ServerManager;
import com.tunnelmanager.server.database.Port;
import com.tunnelmanager.server.database.PortsDatabaseManager;
import com.tunnelmanager.server.database.User;
import com.tunnelmanager.utils.SystemConfiguration;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Class PortsManager
 * Manager ports on the server
 *
 * @author Pierre-Olivier on 07/04/2014.
 */
public class PortsManager {
    /**
     * Ports status
     */
    private final static HashMap<Integer, PortStatus> portsStatus = new HashMap<>();

    /**
     * Users ports
     */
    private final static HashMap<User, List<Port>> portsUser = new HashMap<>();

    /**
     * Update portsStatus
     */
    private static void updatePortsStatus() {
        try {
            if(SystemConfiguration.onWindows()) {
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
            } else if (SystemConfiguration.onLinux()) {
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

    /**
     * Get a pid associated to a port
     * @param portNumber port
     * @return pid string
     */
    public static String getPid(int portNumber) {
        try {
            if(SystemConfiguration.onWindows()) {
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
            } else if (SystemConfiguration.onLinux()) {
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

    /**
     * Acquire a port
     * Set port in WAITING status
     * @param user user
     * @param data data key
     * @return port
     */
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

    /**
     * Validate a port
     * Set port in BOUND status
     * @param user user
     * @param portNumber port
     */
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

    /**
     * Release a port
     * @param user user
     * @param portNumber port
     */
    public static void releasePort(User user, Integer portNumber) {
        Port port;

        synchronized (PortsManager.portsStatus) {
            PortStatus portStatus = PortsManager.portsStatus.get(portNumber);
            if(portStatus != null && portStatus.getPid() != null) {
                try {
                    if (SystemConfiguration.onWindows()) {
                        Runtime.getRuntime().exec("taskkill " + portStatus.getPid()).waitFor();
                    } else if (SystemConfiguration.onLinux()) {
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

    /**
     * Release all ports for a user
     * @param user user
     */
    public static void releaseAllPorts(User user) {
        synchronized (PortsManager.portsUser) {
            PortsManager.portsUser.remove(user);

            // TODO kill pids + database clear
        }
    }

    /**
     * Associate a port to a user
     * @param user user
     * @param portNumber port
     * @param data data key
     */
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

    /**
     * Check if user has this port
     * @param user user
     * @param portNumber port
     * @return true if the port is allocated for this user else false
     */
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

    /**
     * Get port database object
     * @param user user
     * @param portNumber port number
     * @return port instance
     */
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

    /**
     * Get port status with port number
     * @param portNumber port number
     * @return port status
     */
    public static PortStatus getPortStatus(Integer portNumber) {
        synchronized (PortsManager.portsStatus) {
            return PortsManager.portsStatus.get(portNumber);
        }
    }

    /**
     * Get port status with user and data key
     * @param user user
     * @param data data key
     * @return port status
     */
    public static PortStatus getPortStatus(User user, String data) {
        Port port = null;
        synchronized (PortsManager.portsUser) {
            List<Port> ports = PortsManager.portsUser.get(user);

            if(ports != null) {
                for (Port p : ports) {
                    if (p.getData().equals(data)) {
                        port = p;
                    }
                }
            } else {
                return null;
            }
        }

        if(port != null) {
            synchronized (PortsManager.portsStatus) {
                return PortsManager.portsStatus.get(port.getLocalPort());
            }
        } else {
            return null;
        }
    }
}
