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
 * Class Name: ExceptionHelper
 ******************************************************************************/

package com.onsemi.ble;

/**
 * A helper class for exception handling
 */

public class ExceptionHelper {

    /**
     * Create BleLibrary exceptions based on a result value.
     * @param result Create BleLibrary exceptions based on a result value.
     * @param message Additional text message to describe the problem more specific.
     * @return BleLibrary exception that represents the result.
     */
    public static BleException ResultToException(BleResult result, String message)
    {
        String text = String.format("%s (%s)",message, result);

        switch (result)
        {
            case NotConnected:
                return new NotConnectedException(text);

            case NotConnectable:
                return new NotConnectableException(text);

            case Timeout:
                return new TimeoutException(text);

            case NotSupported:
                return new NotSupportedException(text);

            default:
                return new BleException(text, result);
        }
    }
}
