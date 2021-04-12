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
 * Class Name: CharacteristicListener
 ******************************************************************************/

package com.onsemi.ble;

/**
 * Callbacks for characteristic events
 */

public abstract class CharacteristicListener {

    /**
     * Invoked when a notification received
     * @param characteristic The characteristic
     * @param data  The data
     */
    public void onNotificationReceived(Characteristic characteristic, byte[] data) {}

    /**
     * Invoked when a indication received
     * @param characteristic The characteristic
     * @param data  The data
     */
    public void onIndicationReceived(Characteristic characteristic, byte[] data) {}
}
