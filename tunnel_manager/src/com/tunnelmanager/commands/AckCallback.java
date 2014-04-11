package com.tunnelmanager.commands;

/**
 * Class AckCallback
 *
 * @author Pierre-Olivier on 11/04/2014.
 */
public abstract class AckCallback<T extends Command> {
    protected T command;

    protected AckCallback(T command) {
        this.command = command;
    }

    public abstract void run();
}
