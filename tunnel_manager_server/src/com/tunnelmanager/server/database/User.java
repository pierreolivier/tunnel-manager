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
    private final int allwedTunnels;

    public User(int id, String sshPublicKey, String apiKey, int allwedTunnels) {
        this.id = id;
        this.sshPublicKey = sshPublicKey;
        this.apiKey = apiKey;
        this.allwedTunnels = allwedTunnels;
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

    public int getAllwedTunnels() {
        return allwedTunnels;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", sshPublicKey='" + sshPublicKey + '\'' +
                ", apiKey='" + apiKey + '\'' +
                ", allwedTunnels=" + allwedTunnels +
                '}';
    }
}
