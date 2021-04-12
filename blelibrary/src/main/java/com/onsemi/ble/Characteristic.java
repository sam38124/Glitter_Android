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
 * Class Name: Characteristic
 ******************************************************************************/

package com.onsemi.ble;

import android.text.method.BaseKeyListener;

import java.util.UUID;

/**
 * The characteristic interface
 */

public interface Characteristic extends Disposable {

    /**
     * Get the UUID identifying the characteristic.
     */
    UUID getUuid();

    /**
     * Get the local value of the characteristic. The local value is not valid on all implementations.
     * @return The current value
     */
    byte[] getValue();

    /**
     * Get the permissions associated with the characteristic. [CharacteristicPermissions]
     */
    int getPermission();

    /**
     * Get the properties associated with the characteristic. [CharacteristicProperties]
     */
    int getProperty();

    /**
     * Get the peripheral the characteristic is associated to.
     * @return The peripheral
     */
    Peripheral getPeripheral();

    /**
     * Retrieves the value of a specified characteristic from peripheral. This call blocks until
     * the data is read or en exception occurs.
     * @return Data read from the characteristic.
     */
    byte[] readData() throws BleException;

    /**
     * Writes the data of the characteristic to the peripheral. This call blocks until
     * the data is written or en exception occurs.
     * @param data Data to write to the characteristic.
     */
    void writeData(byte[] data) throws BleException;
    /**
     * Changes the notifications for the value of a specified characteristic. This call blocks until
     * the notification is changed or en exception occurs.
     * @param enable Enable or disable the notification.
     */
    void changeNotification(boolean enable) throws BleException;

    /**
     * Changes the indications for the value of a specified characteristic. This call blocks until
     * the indication is changed or en exception occurs.
     * @param enable Enable or disable the indication.
     */
    void changeIndication(boolean enable) throws BleException;

    /**
     * Adds an CharacteristicListener
     * @param listener The listener
     */
    void addListener(CharacteristicListener listener);

    /**
     * Removes an CharacteristicListener
     * @param listener The listener
     */
    void removeListener(CharacteristicListener listener);

}
