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
 * Class Name: FotaUpdateStep
 ******************************************************************************/

package com.onsemi.protocol.update;

/**
 * The steps of the fota
 */
public enum FotaUpdateStep {

    /**
     * Initial state
     */
    Idle,

    /**
     * Connect the peripheral
     */
    Connect,

    /**
     * Discover services
     */
    DiscoverServices,

    /**
     * Initialize the peripheral
     */
    Initialize,

    /**
     * A delay before starting the next step
     */
    Delay,


    /**
     * update the fota image
     */
    UpdateFotaImage,


    /**
     * update the app image
     */
    UpdateAppImage,

    /**
     * The device is in application mode, reboot into bootloader
     */
   RebootToBootloader,

    /**
     * Scan for a device in bootloader mode
     */
    ScanForDevice,

    /**
     * update finished with success
     */
    Finished,
}
