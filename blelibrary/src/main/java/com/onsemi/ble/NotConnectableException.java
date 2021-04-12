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
 * Class Name: NotConnectableException
 ******************************************************************************/

package com.onsemi.ble;

/**
 * Throw when a peripheral is not connectable
 */

public class NotConnectableException extends BleException {
    public NotConnectableException(String text) {
        super(text, BleResult.NotConnectable);
    }
}
