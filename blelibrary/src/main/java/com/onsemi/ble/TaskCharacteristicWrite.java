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
 * Class Name: TaskCharacteristicWrite
 ******************************************************************************/

package com.onsemi.ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

import static android.bluetooth.BluetoothGatt.GATT_SUCCESS;
import static android.bluetooth.BluetoothProfile.STATE_CONNECTED;

/**
 * Task to write characteristic data
 */

class TaskCharacteristicWrite extends TaskCharacteristic {

    private static String TAG = "TaskCharacteristicWrite";
    //private byte[] data;
    private PeripheralCallback callback;
    private BluetoothGattCharacteristicClone toWrite;

    TaskCharacteristicWrite(Characteristic characteristic, int timeout, byte[] data) {
        super(characteristic, timeout);
        toWrite = new BluetoothGattCharacteristicClone(Characteristic.getGattCharacteristic(), data);

        //this.data = data;
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
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                // event characteristic matches our characteristic ?
                if (characteristic == Characteristic.getGattCharacteristic())
                {
                    if (status != GATT_SUCCESS)
                    {
                        Log.e(TAG, String.format("Write characteristic failed ({%d})", status));
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
        // set the value into the characteristic.
        // Bad because the value of the characteristic is change, but copy a characteristic is not possible...


        //Characteristic.getGattCharacteristic().setValue(data);

        // initiate write operation
        if (!Peripheral.writeCharacteristic(toWrite))
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
    public String toString() {
        return String.format("Write characteristic (Address:%s)", Peripheral.getAddress());
    }
}
