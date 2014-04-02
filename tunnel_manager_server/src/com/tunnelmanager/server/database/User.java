package com.tunnelmanager.server.database;

/**
 * Class User
 * Java table
 *
 * @author Pierre-Olivier on 02/04/2014.
 */
public class User {
    private final int id;
    private final String sshPublicKey;
    private final String apiKey;
    private final int allowedTunnels;

    public User(int id, String sshPublicKey, String apiKey, int allowedTunnels) {
        this.id = id;
        this.sshPublicKey = sshPublicKey;
        this.apiKey = apiKey;
        this.allowedTunnels = allowedTunnels;
    }

    public int getId() {
        return id;
    }

    public String getSshPublicKey() {
        return sshPublicKey;
    }

    public String getApiKey() {
        return apiKey;
    }

    public int getAllowedTunnels() {
        return allowedTunnels;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", sshPublicKey='" + sshPublicKey + '\'' +
                ", apiKey='" + apiKey + '\'' +
                ", allowedTunnels=" + allowedTunnels +
                '}';
    }
}
