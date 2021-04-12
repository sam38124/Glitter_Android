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
 * Class Name: TaskCharacteristic
 ******************************************************************************/

package com.onsemi.ble;

/**
 * Base class for a task interacting with a characteristic
 */

abstract class TaskCharacteristic extends TaskPeripheral {

    protected CharacteristicImpl Characteristic;

    TaskCharacteristic(Characteristic characteristic, int timeout) {
        super((PeripheralImpl) characteristic.getPeripheral(), timeout);
        Characteristic = (CharacteristicImpl)characteristic;
    }
}
