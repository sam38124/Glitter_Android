/*******************************************************************************
 * Copyright (c) 2019, Semiconductor Components Industries, LLC
 * (d/b/a ON Semiconductor). All rights reserved.
 *
 * This code is the property of ON Semiconductor and may not be redistributed
 * in any form without prior written permission from ON Semiconductor.
 * The terms of use and warranty for this code are covered by contractual
 * agreements between ON Semiconductor and the licensee.
 *
 * This is Reusable Code.
 *
 * Class Name: DataExchange
 ******************************************************************************/

package com.onsemi.protocol.utility;

public interface DataExchange extends Disposable {

    /**
     * Adds a DataExchangeListener
     * @param listener The listener
     */
    void addListener(DataExchangeListener listener);

    /**
     * Removes a DataExchangeListener
     * @param listener The listener
     */
    void removeListener(DataExchangeListener listener);

    /**
     * The maximum length of the data to transmit
     * @return the maximum length
     */
    int getMaxDataLength();

    /**
     * Initializes the module.
     * Has to be called before <see cref="Transmit"/> the first time
     */
    void init() throws ProtocolException;

    /**
     * Transmits data
     * @param data The data to transmit
     */
    void transmit(byte[] data)  throws ProtocolException;
}
