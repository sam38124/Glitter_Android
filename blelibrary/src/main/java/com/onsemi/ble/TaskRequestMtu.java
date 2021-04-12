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
 * Class Name: TaskRequestMtu
 ******************************************************************************/

package com.onsemi.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;

import static android.bluetooth.BluetoothProfile.STATE_CONNECTED;
import static android.bluetooth.BluetoothProfile.STATE_DISCONNECTED;
import static android.bluetooth.BluetoothProfile.STATE_DISCONNECTING;

/**
 * A task to request a MTU
 */
class TaskRequestMtu extends TaskPeripheral {

    private static String TAG = "TaskRequestMtu";
    private PeripheralCallback callback;
    private PeripheralManagerListener managerListener;
    private int mtu;

    TaskRequestMtu(PeripheralImpl peripheral, int mtu, int timeout) {
        super(peripheral, timeout);
        this.mtu = mtu;
        callback = new PeripheralCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);

                // get result
                if ((newState == STATE_DISCONNECTED) || (newState == STATE_DISCONNECTING))
                {
                    completed(BleResult.Failure);
                }
            }

            @Override
            public void onMtuChanged(int mtu, int status) {
                completed(BleResult.Success);
            }
        };

        managerListener = new PeripheralManagerListener() {
            @Override
            public void onPeripheralsListUpdated() {}

            @Override
            public void onPeripheralDiscovered(Peripheral p) { }

            @Override
            public void onBluetoothEnabled() {}

            @Override
            public void onBluetoothDisabled() {
                // failed
                completed(BleResult.BluetoothDisabled);
            }
        };
        Peripheral.addListener(callback);
        Peripheral.getPeripheralManager().addListener(managerListener);
    }

    @Override
    protected void start() {
        if(!Peripheral.getPeripheralManager().isBluetoothEnabled()) {
            completed(BleResult.BluetoothDisabled);
            return;
        }

        if(Peripheral.isDisconnected()) {
            completed(BleResult.NotConnected);
            return;
        }
        if (Peripheral.getBluetoothGatt() == null)
        {
            completed(BleResult.Failure);
        }

        boolean success = Peripheral.getBluetoothGatt().requestMtu(mtu);
        if (!success) {
            completed(BleResult.Failure);
        }
    }

    @Override
    protected void onCompleted(BleResult result) { }

    @Override
    protected void cleanup()
    {
        // detach event
        Peripheral.removeListener(callback);
        Peripheral.getPeripheralManager().removeListener(managerListener);

        // cleanup base
        super.cleanup();
    }

    @Override
    public String toString()
    {
        return String.format("Request mtu " + mtu);
    }
}
