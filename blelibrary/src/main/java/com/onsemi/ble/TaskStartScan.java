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
 * Class Name: TaskStartScan
 ******************************************************************************/

package com.onsemi.ble;

import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;

import java.util.List;

/**
 * A task to start the scan for Bluetooth LE peripherals
 */

class TaskStartScan extends TaskBase {

    private BluetoothLeScanner leScanner;
    private List<ScanFilter> scanFilter;
    private ScanSettings scanSettings;
    private ScanCallback scanCallback;

    TaskStartScan(BluetoothLeScanner leScanner, List<ScanFilter> scanFilter, ScanSettings scanSettings, ScanCallback scanCallback, int timeout) {
        super(timeout);
        this.leScanner = leScanner;
        this.scanFilter = scanFilter;
        this.scanSettings = scanSettings;
        this.scanCallback = scanCallback;
    }

    @Override
    protected void start() {
        leScanner.startScan(scanFilter, scanSettings, scanCallback);
        completed(BleResult.Success);
    }

}
