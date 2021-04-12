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
 * Class Name: FotaFrameHandlerListener
 ******************************************************************************/

package com.onsemi.protocol.update;

/**
 * Listener for the fota frame handler
 */
public interface FotaFrameHandlerListener {

    /**
     * Invoke when the progress changed
     * @param progress The current progress
     * @param total The total number of steps
     */
    void onProgressChanged(int progress, int total);
}
