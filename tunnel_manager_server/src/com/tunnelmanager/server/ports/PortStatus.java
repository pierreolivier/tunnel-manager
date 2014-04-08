package com.tunnelmanager.server.ports;

/**
 * Class PortStatus
 *
 * @author Pierre-Olivier on 07/04/2014.
 */
public class PortStatus {
    public enum PortState {FREE, WAITING, BOUND };

    private PortState state;
    private String pid;

    public PortStatus(PortState state, String pid) {
        this.state = state;
        this.pid = pid;
    }

    public PortState getState() {
        return state;
    }

    public void setState(PortState state) {
        this.state = state;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }
}
