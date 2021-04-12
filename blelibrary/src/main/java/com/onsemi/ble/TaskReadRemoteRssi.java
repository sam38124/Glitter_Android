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
 * Class Name: TaskReadRemoteRssi
 ******************************************************************************/

package com.onsemi.ble;

import android.bluetooth.BluetoothGatt;

import static android.bluetooth.BluetoothAdapter.STATE_CONNECTED;

/**
 * Task to read the remote rssi level in connected state
 */

class TaskReadRemoteRssi extends TaskPeripheral {

    public int getRssi() {
        return remoteRssi;
    }

    private int remoteRssi;

    PeripheralCallback callback;

    TaskReadRemoteRssi(PeripheralImpl peripheral, int timeout) {
        super(peripheral, timeout);
        callback = new PeripheralCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);
                if ((newState != STATE_CONNECTED))
                {
                    completed(BleResult.ConnectionLost);
                }
            }

            @Override
            public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
                remoteRssi = rssi;
                completed(gattStatusToBleResult(status));
            }
        };
    }

    @Override
    protected void start() {
        // not connected ?
        if (!Peripheral.isConnected())
        {
            completed(BleResult.NotConnected);
            return;
        }

        // attach result event
        Peripheral.addListener(callback);

        // start reading of remote RSSI
        if (!Peripheral.getBluetoothGatt().readRemoteRssi())
        {
            completed(BleResult.Failure);
        }
    }

    @Override
    protected void cleanup() {
        Peripheral.removeListener(callback);
        super.cleanup();
    }

    @Override
    public String toString()
    {
        return String.format("Read remote RSSI (Address:%s)", Peripheral.getAddress());
    }
}
