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
 * Class Name: TaskDisconnect
 ******************************************************************************/

package com.onsemi.ble;

import android.bluetooth.BluetoothGatt;

import static android.bluetooth.BluetoothProfile.STATE_DISCONNECTED;

/**
 * A task to disconnect from a peripheral
 */

class TaskDisconnect extends TaskPeripheral {

    PeripheralCallback callback;

    TaskDisconnect(PeripheralImpl peripheral, int timeout) {
        super(peripheral, timeout);
        callback = new PeripheralCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);
                if ((newState == STATE_DISCONNECTED))
                {
                    completed(BleResult.Success);
                }
            }
        };
    }

    @Override
    protected void start() {
        // already disconnected ?
        if (   Peripheral.isDisconnected()
                || Peripheral.getBluetoothGatt() == null)
        {
            completed(BleResult.NotConnected);
            return;
        }

        // attach result event
        Peripheral.addListener(callback);

        // initiate disconnect
        Peripheral.getBluetoothGatt().disconnect();
    }

    @Override
    protected void cleanup() {
        Peripheral.removeListener(callback);
        if (Peripheral.getBluetoothGatt() != null)
        {
            Peripheral.getBluetoothGatt().close();
            Peripheral.setBluetoothGatt(null);
        }
        super.cleanup();
    }

    @Override
    public String toString()
    {
        return String.format("Disconnect (Address:%s)", Peripheral.getAddress());
    }

}
