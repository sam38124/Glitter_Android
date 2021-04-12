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
 * Class Name: TaskCharacteristicRead
 ******************************************************************************/

package com.onsemi.ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

import static android.bluetooth.BluetoothGatt.GATT_SUCCESS;
import static android.bluetooth.BluetoothProfile.STATE_CONNECTED;

/**
 * Task to read characteristic data
 */

class TaskCharacteristicRead extends TaskCharacteristic {

    private static String TAG = "TaskCharacteristicWrite";

    public byte[] getValue() {
        return value;
    }

    private byte[] value;
    private PeripheralCallback callback;

    TaskCharacteristicRead(Characteristic characteristic, int timeout) {
        super(characteristic, timeout);
        this.value = new byte[0];
        callback = new PeripheralCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);
                // take bluetooth gatt from callback
                Peripheral.setBluetoothGatt(gatt);

                // get result
                if (newState != STATE_CONNECTED)
                {
                    completed(BleResult.ConnectionLost);
                }
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, byte[] data, int status) {
                // event characteristic matches our characteristic ?
                if (characteristic == Characteristic.getGattCharacteristic())
                {
                    if (status != GATT_SUCCESS)
                    {
                        Log.e(TAG, String.format("Write characteristic failed ({%d})", status));
                    }
                    else
                    {
                        value = data;
                    }
                    completed(gattStatusToBleResult(status));
                }
            }
        };
    }

    @Override
    protected void start() {
        // peripheral not connected ?
        if (!Peripheral.isConnected())
        {
            completed(BleResult.NotConnected);
            return;
        }

        // attach result event
        Peripheral.addListener(callback);

        // initiate write operation
        if (!Peripheral.getBluetoothGatt().readCharacteristic(Characteristic.getGattCharacteristic()))
        {
            completed(BleResult.Failure);
        }
    }

    @Override
    protected void cleanup()
    {
        // detach result events
        Peripheral.removeListener(callback);

        // cleanup base
        super.cleanup();
    }

    @Override
    public String toString()
    {
        return String.format("Read characteristic (Address:%s)", Peripheral.getAddress());
    }
}
