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
    private boolean refresh;

    public PortStatus(PortState state, String pid) {
        this.state = state;
        this.pid = pid;
        this.refresh = false;
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

    public boolean isRefresh() {
        return refresh;
    }

    public void setRefresh(boolean refresh) {
        this.refresh = refresh;
    }

    public static int getDatabaseState(PortState state) {
        switch (state) {
            case FREE:
                return -1;
            case WAITING:
                return 0;
            case BOUND:
                return 1;
        }

        return 0;
    }
}
