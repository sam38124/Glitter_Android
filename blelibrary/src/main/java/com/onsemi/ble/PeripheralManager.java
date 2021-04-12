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
 * Class Name: PeripheralManager
 ******************************************************************************/

package com.onsemi.ble;

import java.util.List;

/**
 * A manger for the bluetooth peripherals.
 * All peripherals are created by the implementation of this class
 */

public interface PeripheralManager<TPeripheral> {

    /**
     * A list of visible Bluetooth LE peripherals. The list is updated when a new peripheral is found
     * or when a peripheral is not seen until the InvisibleTimeout.
     * Register for an update event with {@link #addListener(PeripheralManagerListener listener) addListener}.
     * @return a list of visible peripherals.
     */
    List<TPeripheral> peripherals();

    /**
     * Returns if the scan for Bluetooth LE peripherals is started or not.
     * @return True when the scan is started, false otherwise.
     */
    boolean isScanStarted();

    /**
     * Starts the scan for nearby peripherals.
     */
    void startScan();

    /**
     * Stops a running scan for peripherals
     */
    void stopScan();

    /**
     * Clears the list of peripherals if {@link #canRemove(Object)} returns true
     */
    void clearPeripherals();

    /**
     * Checks if the peripheral can be safely removed from the {@link #peripherals() peripherals} list.
     * @param p The peripheral to check
     * @return True when a peripheral can be removed from the {@link #peripherals() peripherals} list,
     * false otherwise
     */
    boolean canRemove(TPeripheral p);

    /**
     * Adds an PeripheralManagerListener
     * @param listener The listener
     */
    void addListener(PeripheralManagerListener listener);

    /**
     * Removes an PeripheralManagerListener
     * @param listener The listener
     */
    void removeListener(PeripheralManagerListener listener);

    /**
     * Returns the state of the Bluetooth adapter.
     * In a transition from disabled to enabled, it can happen that
     * {@link #isBluetoothEnabled() isBluetoothEnabled} != {@link #isBluetoothDisabled() isBluetoothDisabled}
     * @return True if enabled, false otherwise
     */

    boolean isBluetoothEnabled();
    /**
     * Returns the state of the Bluetooth adapter.
     * In a transition from disabled to enabled, it can happen that
     * {@link #isBluetoothEnabled() isBluetoothEnabled} != {@link #isBluetoothDisabled() isBluetoothDisabled}
     * @return True if disabled, false otherwise
     */
    boolean isBluetoothDisabled();
}
