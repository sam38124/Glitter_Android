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
 * Class Name: PeripheralChangedListener
 ******************************************************************************/

package com.onsemi.ble;

/**
 * Callback for peripheral changes
 */

public interface PeripheralChangedListener {

    /**
     * Invoked when the peripherals name changed
     * @param name The current name
     */
    void onNameChanged(String name);

    /**
     * Invoked when the peripherals RSSI value changed
     * @param rssi The current RSSI value
     */
    void onRssiChanged(int rssi);

    /**
     * Invoked when the manufacturer specific data changed
     * @param data The data
     */
    void onManufacturerDataChanged(byte[] data);

    /**
     * Invoked when the peripherals {@link PeripheralState state} changed
     * @param oldState The old state.
     * @param newState The new state.
     */
    void onStateChanged(PeripheralState oldState, PeripheralState newState);

    /**
     * Invoked when the connection was terminated
     * @param fromHost True when the host terminated the connection, false when the user terminated the connection
     */
    void onDisconnected(boolean fromHost);
}
