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
 * Class Name: Service
 ******************************************************************************/

package com.onsemi.ble;

import java.util.List;
import java.util.UUID;

/**
 * An abstraction of the bluetooth gatt service
 */

public interface Service extends Disposable {

    /**
     * Get the UUID of the service.
     * @return the service uuid
     */
    UUID getUuid();

    /**
     * Get a list with all characteristics of this service.
     * @return the list of characteristics
     */
    List<Characteristic> getCharacteristics();

    /**
     * Get the characteristic with a given UUID.
     * @param uuid    UUID of the characteristic.
     * @return The characteristic found characteristic or null if no characteristic with this UUID is found.
     */
    Characteristic getCharacteristic(String uuid);

    /**
     * Get the characteristic with a given UUID.
     * @param uuid    UUID of the characteristic.
     * @return The characteristic found characteristic or null if no characteristic with this UUID is found.
     */
    Characteristic getCharacteristic(UUID uuid);
}
