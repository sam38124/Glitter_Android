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
 * Class Name: UpdateController
 ******************************************************************************/

package com.onsemi.ble;

public interface UpdateController {

    /**
     * Adds a {@link UpdateControllerListener}
     * @param listener The listener
     */
    void addListener(UpdateControllerListener listener);

    /**
     * Removes a {@link UpdateControllerListener}
     * @param listener The listener
     */
    void removeListener(UpdateControllerListener listener);


    /**
     * Updates the peripheral with the given update options
     * @param peripheral The peripheral to update
     * @param options The update options
     * @throws Exception
     */
    void update(PeripheralImpl peripheral, UpdateOptions options) throws Exception;
}
