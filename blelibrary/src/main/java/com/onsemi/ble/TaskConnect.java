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
 * Class Name: TaskConnect
 ******************************************************************************/

package com.onsemi.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;

import static android.bluetooth.BluetoothProfile.STATE_CONNECTED;
import static android.bluetooth.BluetoothProfile.STATE_DISCONNECTED;
import static android.bluetooth.BluetoothProfile.STATE_DISCONNECTING;

/**
 * A task to establish a connection to a peripheral
 */
class TaskConnect extends TaskPeripheral {

    private static String TAG = "TaskConnect";
    private PeripheralCallback callback;
    private PeripheralManagerListener managerListener;

    TaskConnect(PeripheralImpl peripheral, int timeout) {
        super(peripheral, timeout);
        callback = new PeripheralCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);
                // take bluetooth gatt from callback
                Peripheral.setBluetoothGatt(gatt);

                // get result
                if (newState == STATE_CONNECTED)
                {
                    completed(BleResult.Success);
                }
                if ((newState == STATE_DISCONNECTED) || (newState == STATE_DISCONNECTING))
                {
                    completed(BleResult.UnableToConnect);
                }
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

        if(!Peripheral.isDisconnected()) {
            completed(BleResult.ConnectionAlreadyEstablished);
            return;
        }

        // get device and context
        BluetoothDevice device = Peripheral.getPeripheralManager().getBluetoothAdapter().getRemoteDevice(Peripheral.getAddress());

        // create new BluetoothGatt or reconnect the existing one
        if (Peripheral.getBluetoothGatt() == null)
        {
            // do not take bluetooth gatt only from from device.ConnectGatt().
            // Sometimes callback is called before device.ConnectGatt returns
            Peripheral.setBluetoothGatt(device.connectGatt(Peripheral.getPeripheralManager().getContext(), false, Peripheral));
        }
        else
        {
            if (!Peripheral.getBluetoothGatt().connect())
            {
                completed(BleResult.UnableToConnect);
            }
        }
    }

    @Override
    protected void onCompleted(BleResult result)
    {
        // not successfully connected -> cancel connection
        if (result != BleResult.Success && result != BleResult.ConnectionAlreadyEstablished)
        {
            if (Peripheral.getBluetoothGatt() != null)
            {
                Peripheral.getBluetoothGatt().disconnect();

                try
                {
                    Peripheral.getBluetoothGatt().close();
                }
                catch (Exception ex)
                {
                    Log.w(TAG, "Failed to close BleutoothGatt:" + ex.getMessage());
                }
                finally
                {
                    Peripheral.setBluetoothGatt(null);
                }
            }
        }
    }

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
        return String.format("Connect (Address: "+ Peripheral.getAddress() +")");
    }
}
