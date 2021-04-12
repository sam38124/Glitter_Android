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
 * Class Name: NotConnectedException
 ******************************************************************************/

package com.onsemi.ble;

/**
 * Throw when a peripheral is accessed but not connected
 */

public class NotConnectedException extends BleException {
    public NotConnectedException(String text)    {
        super(text, BleResult.NotConnected);
    }
}
