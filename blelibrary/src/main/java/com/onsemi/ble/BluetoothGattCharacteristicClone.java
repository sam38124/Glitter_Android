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
 * Class Name: BluetoothGattCharacteristicClone
 ******************************************************************************/

package com.onsemi.ble;

import android.Manifest;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;


/**
 * A Class to be able to create a copy of a  BluetoothGattCharacteristic instance.
 * This is needed, because write and notify on the same characteristic can end in a raise condition.
 * To prevent this, create a copy of the BluetoothGattCharacteristic instance and use the copy
 * to write data.
 */
class BluetoothGattCharacteristicClone extends BluetoothGattCharacteristic {

    BluetoothGattCharacteristic characteristic;
    byte[] value;
    /**
     * Create a copy of a BluetoothGattCharacteristic.
     * <p>Requires {@link Manifest.permission#BLUETOOTH} permission.
     *
     * @param characteristic        The characteristic to copy
     * @param value                 The the value to set
     */
     public BluetoothGattCharacteristicClone(BluetoothGattCharacteristic characteristic, byte[] value) {
        super(characteristic.getUuid(), characteristic.getProperties(), characteristic.getProperties());
        this.characteristic = characteristic;
        this.value = value;
    }

    @Override
    public BluetoothGattService getService() {
        return characteristic.getService();
    }

    @Override
    public int getInstanceId() {
        return characteristic.getInstanceId();
    }

    @Override
    public byte[] getValue() {
         return value;
    }
}
