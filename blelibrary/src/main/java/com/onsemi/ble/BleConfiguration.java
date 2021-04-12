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
 * Class Name: BleConfiguration
 ******************************************************************************/

package com.onsemi.ble;

import static android.bluetooth.le.ScanSettings.SCAN_MODE_LOW_LATENCY;

/**
 * The global configuration for the ble library
 */

public abstract class BleConfiguration {


     // Create the default configuration
    static {
        peripheralInvisibleTimeout = 0;
        connectTimeout = 7500;
        requestMtuTimeout = 500;
        discoverServicesTimeout = 10000;
        disconnectTimeout = 5000;
        readCharacteristicTimeout = 4000;
        writeCharacteristicTimeout = 4000;
        changeNotificationTimeout = 5000;
        // suspend scan only on Android <= 5 by default
        supendScanWhileEstablish = android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.LOLLIPOP;
        scanMode = SCAN_MODE_LOW_LATENCY;
        rssiUpdateInterval = 0;
    }

    private static int peripheralInvisibleTimeout;
    private static int connectTimeout;
    private static int requestMtuTimeout;
    private static int discoverServicesTimeout;
    private static int disconnectTimeout;
    private static int readCharacteristicTimeout;
    private static int writeCharacteristicTimeout;
    private static int changeNotificationTimeout;
    private static boolean supendScanWhileEstablish;
    private static int scanMode;
    private static int rssiUpdateInterval;


    /**
     * Returns the peripheral timeout [ms]
     * @return The timeout [ms]
     */
    public static int getPeripheralInvisibleTimeout() {
        return peripheralInvisibleTimeout;
    }

    /**
     * Sets the peripheral timeout [ms]. A peripheral is removed from the {@link PeripheralManager#peripherals()} list
     * when it is not seen for this time. Set to 0 if peripheral should always be visible
     * @param peripheralInvisibleTimeout The timeout [ms]
     */
    public static void setPeripheralInvisibleTimeout(int peripheralInvisibleTimeout) {
        BleConfiguration.peripheralInvisibleTimeout = peripheralInvisibleTimeout;
    }

    /**
     * Returns the connect timeout [ms]
     * @return The timeout [ms]
     */
    public static int getConnectTimeout() {
        return connectTimeout;
    }

    /**
     * Sets the connect timeout [ms]. A peripheral can take up to this timeout until its state changes to
     * connected. The connection is terminated otherwise.
     * @param connectTimeout The timeout [ms].
     */
    public static void setConnectTimeout(int connectTimeout) {
        BleConfiguration.connectTimeout = connectTimeout;
    }

    /**
     * Returns the request mtu timeout [ms]
     * @return The timeout [ms]
     */
    public static int getRequestMtuTimeout() {
        return requestMtuTimeout;
    }

    /**
     * Sets the request mtu timeout [ms].
     * @param requestMtuTimeout The timeout [ms].
     */
    public static void setRequestMtuTimeout(int requestMtuTimeout) {
        BleConfiguration.requestMtuTimeout = requestMtuTimeout;
    }

    /**
     * Returns the discover services timeout.
     * @return The timeout [ms].
     */
    public static int getDiscoverServicesTimeout() {
        return discoverServicesTimeout;
    }

    /**
     * Sets the discover services timeout.
     * @param discoverServicesTimeout The timeout [ms].
     */
    public static void setDiscoverServicesTimeout(int discoverServicesTimeout) {
        BleConfiguration.discoverServicesTimeout = discoverServicesTimeout;
    }

    /**
     * Returns the disconnect timeout.
     * @return The timeout [ms].
     */
    public static int getDisconnectTimeout() {
        return disconnectTimeout;
    }

    /**
     * Sets the disconnect timeout.
     * @param disconnectTimeout The timeout [ms].
     */
    public static void setDisconnectTimeout(int disconnectTimeout) {
        BleConfiguration.disconnectTimeout = disconnectTimeout;
    }

    /**
     * Returns the read characteristic timeout.
     * @return The timeout [ms].
     */
    public static int getReadCharacteristicTimeout() {
        return readCharacteristicTimeout;
    }

    /**
     * Sets the read characteristic timeout.
     * @param readCharacteristicTimeout The timeout [ms].
     */
    public static void setReadCharacteristicTimeout(int readCharacteristicTimeout) {
        BleConfiguration.readCharacteristicTimeout = readCharacteristicTimeout;
    }

    /**
     * Returns the write characteristic timeout.
     * @return The timeout [ms].
     */
    public static int getWriteCharacteristicTimeout() {
        return writeCharacteristicTimeout;
    }

    /**
     * Sets the write characteristic timeout.
     *  @param writeCharacteristicTimeout The timeout [ms].
     */
    public static void setWriteCharacteristicTimeout(int writeCharacteristicTimeout) {
        BleConfiguration.writeCharacteristicTimeout = writeCharacteristicTimeout;
    }

    /**
     * Returns the change notification/indication timeout.
     * @return The timeout [ms].
     */
    public static int getChangeNotificationTimeout() {
        return changeNotificationTimeout;
    }

    /**
     * Sets the change notification/indication timeout.
     * @param changeNotificationTimeout The timeout [ms].
     */
    public static void setChangeNotificationTimeout(int changeNotificationTimeout) {
        BleConfiguration.changeNotificationTimeout = changeNotificationTimeout;
    }

    /**
     * Returns is the scan for peripherals is stopped while a connection is established.
     * @return True of scan is suspended while establish, false otherwise.
     */
    public static boolean isSupendScanWhileEstablish() {
        return supendScanWhileEstablish;
    }

    /**
     * On some devices, it is not possible to establish a connection while the  scan for peripheral
     * is running. Set to true in this case.
     * @param supendScanWhileEstablish True of scan is suspended while establish, false otherwise.
     */
    public static void setSupendScanWhileEstablish(boolean supendScanWhileEstablish) {
        BleConfiguration.supendScanWhileEstablish = supendScanWhileEstablish;
    }

    /**
     * Retuns the scan mode.
     * @return The selected scan mode
     */
    public static int getScanMode() {
        return scanMode;
    }

    /**
     * The scan mode for {@link PeripheralManager#startScan()}.
     * The value can be one of:
     * android.bluetooth.le.ScanSettings.SCAN_MODE_BALANCED;
     * android.bluetooth.le.ScanSettings.SCAN_MODE_LOW_LATENCY;
     * android.bluetooth.le.ScanSettings.SCAN_MODE_LOW_POWER;
     * The scan for peripherals has to be disabled and reenabled that this setting takes effect.
     * @param scanMode
     */
    public static void setScanMode(int scanMode) {
        if(
            scanMode != android.bluetooth.le.ScanSettings.SCAN_MODE_BALANCED &&
            scanMode != android.bluetooth.le.ScanSettings.SCAN_MODE_LOW_LATENCY &&
            scanMode != android.bluetooth.le.ScanSettings.SCAN_MODE_LOW_POWER) {
            throw new IllegalArgumentException(String.format("%d is not a valid scan mode", scanMode));
        }
        BleConfiguration.scanMode = scanMode;
    }

    /**
     * Returns the rssi update interval
     * @return The rssi update interval
     */
    public static int getRssiUpdateInterval() {
        return rssiUpdateInterval;
    }

    /**
     * Sets the interval for rssi updates in the connected state. [ms]
     * A value of 0 disables the rssi update.
     * A value below 1.2s is not permitted due to bluetooth stack limitations.
     * @param rssiUpdateInterval The rssi update interval
     */
    public static void setRssiUpdateInterval(int rssiUpdateInterval) {
        if(rssiUpdateInterval != 0 && rssiUpdateInterval < 1200) {
            rssiUpdateInterval = 1200;
        }
        BleConfiguration.rssiUpdateInterval = rssiUpdateInterval;
    }


}
