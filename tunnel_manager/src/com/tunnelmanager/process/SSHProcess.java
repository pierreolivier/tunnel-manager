package com.tunnelmanager.process;

import com.tunnelmanager.utils.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Class SSHProcess
 * Create a SSH processus
 *
 * @author Pierre-Olivier on 08/04/2014.
 */
public class SSHProcess extends Thread {
    /**
     * Arguments given to ssh
     */
    private String arguments;

    /**
     * Instance of the ssh process
     */
    private Process process;

    /**
     * Standard input reader
     */
    private BufferedReader readerInput;

    /**
     * Error input reader
     */
    private BufferedReader readerError;

    /**
     * Error output thread
     */
    private ErrorThread errorThread;

    /**
     * Object used for synchronization (wait)
     */
    private final Object lock;

    /**
     * Connection status
     */
    private boolean connected;

    /**
     * Default constructor
     * @param arguments ssh arguments
     */
    public SSHProcess(String arguments) {
        this.arguments = arguments;

        this.lock = new Object();
        this.connected = false;
    }

    /**
     * Wait for the tunnel
     * @return true if the tunnel is opened else false
     */
    public boolean waitTunnel() {
        try {
            this.process = Runtime.getRuntime().exec("ssh " + this.arguments);

            this.readerInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            this.readerError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            this.errorThread = new ErrorThread();

            this.errorThread.start();
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
                line = readerInput.readLine();

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

    /**
     * Class ErrorThread
     * Log error into error output
     */
    private class ErrorThread extends Thread {
        @Override
        public void run() {
            try {
                String line;
                do {
                    line = readerError.readLine();

                    if (line != null) {
                        Log.e(line);
                    }
                } while (line != null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
