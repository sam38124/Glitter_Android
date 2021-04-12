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
 * Class Name: TaskPeripheral
 ******************************************************************************/

package com.onsemi.ble;

import static android.bluetooth.BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION;
import static android.bluetooth.BluetoothGatt.GATT_INSUFFICIENT_ENCRYPTION;
import static android.bluetooth.BluetoothGatt.GATT_READ_NOT_PERMITTED;
import static android.bluetooth.BluetoothGatt.GATT_REQUEST_NOT_SUPPORTED;
import static android.bluetooth.BluetoothGatt.GATT_SUCCESS;
import static android.bluetooth.BluetoothGatt.GATT_WRITE_NOT_PERMITTED;

/**
 * A base class for all tasks which access a peripheral
 */

abstract class TaskPeripheral extends TaskBase {

    protected PeripheralImpl Peripheral;

    TaskPeripheral(PeripheralImpl peripheral, int timeout) {
        super(timeout);
        Peripheral = peripheral;
    }

    protected BleResult gattStatusToBleResult(int status)
    {
        switch (status)
        {
            case GATT_INSUFFICIENT_AUTHENTICATION:
                return (BleResult.InsufficientAuthentication);
            case GATT_INSUFFICIENT_ENCRYPTION:
                return (BleResult.InsufficientEncryption);
            case GATT_READ_NOT_PERMITTED:
                return (BleResult.NotPermitted);
            case GATT_REQUEST_NOT_SUPPORTED:
                return (BleResult.NotSupported);
            case GATT_SUCCESS:
                return (BleResult.Success);
            case GATT_WRITE_NOT_PERMITTED:
                return (BleResult.NotPermitted);
        }

        return (BleResult.Failure);
    }
}
