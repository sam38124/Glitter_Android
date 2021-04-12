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
 * Class Name: BleResult
 ******************************************************************************/

package com.onsemi.ble;

/**
 * Result values for library operations.
 */
public enum BleResult {
    /**
     * Operation succeeded.
     */
    Success,

    /**
     * Operation terminated by a timeout.
     */
    Timeout,

    /**
     * Operation failed because the peripheral is not connected.
     */
    NotConnected,


    /**
     *  Operation failed because the peripheral is not connectible.
     */
    NotConnectable,

    /**
     * Operation failed because the peripheral could not be connected.
     */
    UnableToConnect,


    /**
     * Operation failed because the connection to the peripheral has been lost.
     */
    ConnectionLost,

    /**
     * Operation failed because the connection is already established.
     */
    ConnectionAlreadyEstablished,

    /**
     * Operation failed because it's not supported.
     */
    NotSupported,

    /**
     * Operation failed because the authentication is insufficient.
     */
    InsufficientAuthentication,

    /**
     *  Operation failed because the authorization is insufficient.
     */
    InsufficientAuthorization,

    /**
     * Operation failed because the encryption is insufficient.
     */
    InsufficientEncryption,

    /**
     *  Operation failed because it's not permitted.
     */
    NotPermitted,

    /**
     * Operation failed because the authentication failed.
     */
    AuthenticationFailure,

    /**
     * Operation failed because the authentication key is missing.
     */
    AuthenticationKeyMissing,

    /**
     * Operation failed because no response has been received.
     */
    NoResponse,

    /**
     * Searched attribute was not found
     */
    AttributeNotFound,

    /**
     * Link supervision timeout expired.
     */
    ConnectionTimeout,

    /**
     * Controller is at limit of connections it can support.
     */
    ConnectionLimitExceeded,

    /**
     * User input of passkey failed.
     */
    PasskeyEntryFailed,

    /**
     * Pairing is not supported by the device.
     */
    PairingNotSupported,

    /**
     * Operation failed because of an unknown failure.
     */
    Failure,

    /**
     * Operation failed because of a thrown exception.
     */
    Exception,

    /**
     * Operation canceled.
     */
    Canceled,

    /**
     * Bluetooth is disabled.
     */
    BluetoothDisabled,

    /**
     * Operation is still pending.
     */
    Pending,
}
