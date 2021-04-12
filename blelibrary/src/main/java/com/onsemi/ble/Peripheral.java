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
 * Class Name: Peripheral
 ******************************************************************************/

package com.onsemi.ble;

import java.util.Date;
import java.util.UUID;

/**
 * A representation of a Bluetooth LE peripheral
 */

public interface Peripheral {

    /**
     * The name of the peripheral
     * @return The name
     */
    String getName();

    /**
     * The manufacturer specific data out of the advertising data
     * @return The manufacturer specific data
     */
    byte[] getManufacturerData();

    /**
     * The last received RSSI value.
     * @return The RSSI value.
     */
    int getRssi();

    /**
     * The Bluetooth address of the peripheral
     * @return The Bluetooth address
     */
    String getAddress();

    /**
     * The state of the peripheral. Add a {@link PeripheralChangedListener PeripheralChangedListener} for state changed updates.
     * @return The state.
     */
    PeripheralState getState();

    /**
     * The timestamp of the last advertising data update.
     * @return The timestamp.
     */
    Date getLastUpdate();

    /**
     * Returns the maximum write length for this peripheral.
     * This is the BLE att mtu size - 3
     * @return The max write length
     */
    int getMaxWriteLength();

    /**
     * Adds a {@link PeripheralChangedListener PeripheralChangedListener}
     * @param listener The listener
     */
    void addListener(PeripheralChangedListener listener);

    /**
     * Removes a {@link PeripheralChangedListener PeripheralChangedListener}
     * @param listener The listener
     */
    void removeListener(PeripheralChangedListener listener);

    /**
     * Find a service in the service list.
     * The service list is available after completing the service discovery.
     *
     * @param uuid The UUID of the service to search for.
     * @return The service object found, or null if no service has been found.
     */
    Service findService(UUID uuid);

    /**
     * Find a service in the service list.
     * The service list is available after completing the service discovery.
     *
     * @param uuid The UUID of the service to search for.
     * @return The service object found, or null if no service has been found.
     */
    Service findService(String uuid);

    /**
     * Establishes a connection to the peripheral. This is a blocking operation and it returns
     * when ever the connection is established successful or not.
     */
    void establish() throws Exception;

    /**
     * Executes a firmware update of the peripheral
     * @throws Exception
     */
    void update(UpdateController controller, UpdateOptions options) throws  Exception;

    /**
     * Sets the BLE connection priority to high.
     * This is recommended for high throughput
     */
    void setConnectionPriorityHigh();

    /**
     * Sets the BLE connection priority to balanced.
     * This is a balanced mode between power and throughput
     */
    void setConnectionPriorityBalanced();

    /**
     * Sets the BLE connection priority to low power.
     * This is recommended for low power applications
     */
    void setConnectionPriorityLowPower();

    /**
     * Requests the maximum write length
     * @param length The maximum length
     * @throws BleException
     */
    void requestMaxWriteLength(int length) throws BleException;


    /**
     * Sets the 2 MBit Phy for Rx and Tx
     * @throws BleException
     */
    void set2MbPhy() throws BleException;

    /**
     * Terminates a connection to the peripheral. This is a blocking operation and it returns
     * when the connection is terminated.
     */
    void teardown() throws Exception;
}
