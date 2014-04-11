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

    private BufferedReader readerInput;
    private BufferedReader readerError;

    private ErrorThread errorThread;

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
