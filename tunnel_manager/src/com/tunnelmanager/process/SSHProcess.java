package com.tunnelmanager.process;

import com.tunnelmanager.utils.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Class SSHProcess
 *
 * @author Pierre-Olivier on 08/04/2014.
 */
public class SSHProcess extends Thread {
    private String arguments;

    private Process process;

    private BufferedReader reader;

    private final Object lock;

    private boolean connected;

    public SSHProcess(String arguments) {
        this.arguments = arguments;

        this.lock = new Object();
        this.connected = false;
    }

    public boolean waitTunnel() {
        try {
            this.process = Runtime.getRuntime().exec("ssh " + this.arguments);

            this.reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            start();

            synchronized (this.lock) {
                this.lock.wait(10000);
            }

            if(!this.connected) {
                this.process.destroy();
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public void run() {
        try {
            String line;
            do {
                line = reader.readLine();

                if (line != null) {
                    Log.v(line);

                    this.connected = true;

                    break;
                }
            } while (line != null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        synchronized (this.lock) {
            this.lock.notify();
        }
    }
}
