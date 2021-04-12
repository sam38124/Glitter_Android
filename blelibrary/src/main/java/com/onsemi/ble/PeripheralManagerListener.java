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
 * Class Name: PeripheralManagerListener
 ******************************************************************************/

package com.onsemi.ble;

/**
 * Callbacks for the peripheral manger events
 */

public interface PeripheralManagerListener {

    /**
     * Invoked when the {@link PeripheralManager#peripherals() peripherals} list changed
     */
    void onPeripheralsListUpdated();

    /**
     * Invoked when peripheral is discovered
     */
    void onPeripheralDiscovered(Peripheral p);

    /**
     * Invoked when the Bluetooth adapter is enabled
     */
    void onBluetoothEnabled();

    /**
     * Invoked when the Bluetooth adapter is disabled
     */
    void onBluetoothDisabled();
}
