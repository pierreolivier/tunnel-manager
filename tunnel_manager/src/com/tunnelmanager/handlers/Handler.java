package com.tunnelmanager.handlers;

import com.tunnelmanager.commands.Command;

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
    public int createAck();

    /**
     * Get next free ack id and exec runnable when response
     * @return ack id
     */
    public int createAck(Runnable runnable);

    /**
     * Remove ack id
     */
    public void removeAck(Command command);
}
