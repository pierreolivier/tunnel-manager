package com.tunnelmanager.handlers;

/**
 * Interface Handler
 *
 * @author Pierre-Olivier on 02/04/2014.
 */
public interface Handler {
    /**
     * Get next free ack id
     * @return ack id
     */
    public int nextAckId();

    /**
     * Remove ack id
     */
    public void removeAckId(int ackId);
}
