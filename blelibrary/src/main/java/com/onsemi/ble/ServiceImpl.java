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
 * Class Name: ServiceImpl
 ******************************************************************************/

package com.onsemi.ble;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * The implementation of the Service interface
 */
class ServiceImpl implements Service {

    private static String TAG = "Service";
    private BluetoothGattService service;
    private LinkedList<Characteristic> characteristics;

    ServiceImpl(PeripheralImpl peripheral, BluetoothGattService service) {
        this.service = service;
        characteristics = new LinkedList<>();
        for(BluetoothGattCharacteristic c : service.getCharacteristics()) {
            characteristics.add(new CharacteristicImpl(peripheral, c));
        }
    }

    @Override
    public UUID getUuid() {
        return service.getUuid();
    }

    @Override
    public List<Characteristic> getCharacteristics() {
        return new LinkedList<>(characteristics);
    }

    @Override
    public Characteristic getCharacteristic(String uuid) {
        UUID id;
        try {
           id = UuidHelper.ConvertTo128BitBluetoothUuid(uuid);
        }
        catch  (Exception ex) {
            Log.e(TAG, ex.getMessage());
            return null;
        }
        return getCharacteristic(id);
    }

    @Override
    public Characteristic getCharacteristic(UUID uuid) {
        for(Characteristic c : characteristics) {
            if(c.getUuid().compareTo(uuid) == 0) {
                return c;
            }
        }
        return null;
    }

    @Override
    public void dispose() {
        for(Characteristic c : characteristics) {
            c.dispose();
        }
        characteristics.clear();
    }
}
