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
 * Class Name: TaskDiscoverServices
 ******************************************************************************/

package com.onsemi.ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattService;

import java.util.LinkedList;
import java.util.List;

import static android.bluetooth.BluetoothGatt.GATT_SUCCESS;
import static android.bluetooth.BluetoothProfile.STATE_CONNECTED;

/**
 * A task to discover services
 */

class TaskDiscoverServices extends TaskPeripheral {

    private PeripheralCallback callback;
    private LinkedList<Service> services;

    List<Service> getServices() {return services;}

    TaskDiscoverServices(PeripheralImpl peripheral, int timeout) {
        super(peripheral, timeout);
        services = new LinkedList<>();
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
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                // success
                if (status == GATT_SUCCESS)
                {
                    services = new LinkedList<>();
                    for(BluetoothGattService s : gatt.getServices())
                    {
                        services.add(new ServiceImpl(Peripheral, s));
                    }
                }

                // return result
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

        Peripheral.addListener(callback);

        // start service discovery
        if (!Peripheral.getBluetoothGatt().discoverServices())
        {
            completed(BleResult.Failure);
        }
    }

    @Override
    protected void cleanup()
    {
        // detach result event
        Peripheral.removeListener(callback);

        // cleanup base
        super.cleanup();
    }

    @Override
    public String toString()
    {
        return String.format("Discover Services (Address:%s)", Peripheral.getAddress());
    }
}
