package com.tunnelmanager.commands;

import java.io.Serializable;

/**
 * Class Command
 * Abstract command
 *
 * @author Pierre-Olivier on 01/04/2014.
 */
public abstract class Command implements Serializable {
    /**
     * Ack Id
     */
    protected int ackId;

    /**
     * Default Constructor
     */
    public Command(int ackId) {
        super();

        this.ackId = ackId;
    }

    public int getAckId() {
        return ackId;
    }
}
