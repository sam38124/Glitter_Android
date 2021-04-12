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
 * Class Name: UpdateControllerListener
 ******************************************************************************/

package com.onsemi.ble;

/**
 * Callbacks for the Update controller
 */
public interface UpdateControllerListener {

    /**
     * Invoked when the progress changed
     * @param progress The current progress
     * @param total The total progress
     * @param step The current step
     */
    void onProgressChanged(int progress, int total, String step);

    /**
     * Invoked when the udpate completed
     * @param status The status of the update
     */
    void onCompleted(int status);
}
