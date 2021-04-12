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
 * Class Name: CreatePeripheralFunction
 ******************************************************************************/

package com.onsemi.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanRecord;

/**
 * Function for creating a concrete peripheral object
 */
public interface CreatePeripheralFunction<TPeripheral> {

    /**
     * Function needed to create a new peripheral
     * @param device        The BluetoothDevice
     * @param rssi          The current rssi value
     * @param scanRecord    The advertising scan record
     * @return              A new perihperal object
     */
    TPeripheral create(BluetoothDevice device, int rssi, ScanRecord scanRecord);
}
