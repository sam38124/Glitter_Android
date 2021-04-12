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
 * Class Name: BleException
 ******************************************************************************/

package com.onsemi.ble;

/**
 * The exception thrown by the ble library
 */

public class BleException extends Exception {

    /**
     * The result of the operation which caused the exception
     * @return The result
     */
    public BleResult getResult() {
        return result;
    }

    private BleResult result;

    public BleException(String text, BleResult result) {
        super(text);
        this.result = result;
    }
}
