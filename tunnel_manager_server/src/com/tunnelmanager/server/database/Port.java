package com.tunnelmanager.server.database;

/**
 * Class
 *
 * @author Pierre-Olivier on 09/04/2014.
 */
public class Port {
    private int id;
    private int idUser;
    private int localPort;
    private int state;
    private long start;
    private long timeout;
    private String data;

    public Port(int idUser, int localPort, long start, long timeout, String data) {
        this.id = -1;
        this.idUser = idUser;
        this.localPort = localPort;
        this.start = start;
        this.timeout = timeout;
        this.data = data;
    }

    public int getId() {
        return id;
    }

    public int getIdUser() {
        return idUser;
    }

    public int getLocalPort() {
        return localPort;
    }

    public int getState() {
        return state;
    }

    public long getStart() {
        return start;
    }

    public long getTimeout() {
        return timeout;
    }

    public String getData() {
        return data;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLocalPort(int localPort) {
        this.localPort = localPort;
        PortsDatabaseManager.updatePort(this);
    }

    public void setState(int state) {
        this.state = state;
        PortsDatabaseManager.updatePort(this);
    }

    public void setStart(long start) {
        this.start = start;
        PortsDatabaseManager.updatePort(this);
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
        PortsDatabaseManager.updatePort(this);
    }

    public void setData(String data) {
        this.data = data;
        PortsDatabaseManager.updatePort(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Port port = (Port) o;

        if (localPort != port.localPort) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return localPort;
    }
}
