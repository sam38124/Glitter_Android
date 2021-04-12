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
 * Class Name: TaskCharacteristicChangeNotification
 ******************************************************************************/

package com.onsemi.ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattDescriptor;

import java.util.UUID;

import static android.bluetooth.BluetoothGatt.GATT_SUCCESS;
import static android.bluetooth.BluetoothProfile.STATE_CONNECTED;

/**
 * Task to enable/disable notification
 */

class TaskCharacteristicChangeNotification extends TaskCharacteristic {
    private static String TAG = "TaskChangeNotification";

    private boolean enable;
    private PeripheralCallback callback;
    private BluetoothGattDescriptor descriptor;

    TaskCharacteristicChangeNotification(Characteristic characteristic, int timeout, boolean enable) {
        super(characteristic, timeout);
        this.enable = enable;
        final TaskCharacteristicChangeNotification task = this;
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
            public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                // event characteristic matches our characteristic ?
                if (descriptor == task.descriptor)
                {
                    if (status != GATT_SUCCESS)
                    {
                        Log.e(TAG, String.format("Writing client characteristic control failed ({%d})", status));
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

        if(!Peripheral.getBluetoothGatt().setCharacteristicNotification(Characteristic.getGattCharacteristic(), enable)) {
            completed(BleResult.Failure);
            return;
        }

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // get descriptor
        descriptor =
                Characteristic.getGattCharacteristic().getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));

        // set value locally
        descriptor.setValue(enable
                ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);

        // initiate write operation
        if (!Peripheral.getBluetoothGatt().writeDescriptor(descriptor))
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
        return String.format("Change Notification (Address:%s)", Peripheral.getAddress());
    }
}
