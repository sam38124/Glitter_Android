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
 * Class Name: FotaOptions
 ******************************************************************************/

package com.onsemi.protocol.update;

import com.onsemi.ble.UpdateOptions;

/**
 * Options for the fota controller
 */
public class FotaOptions implements UpdateOptions {

    /**
     * Returns the firmware file
     * @return The firmware file
     */
    public FotaFirmwareFile getFile(){
        return file;
    }
    public void setFile(FotaFirmwareFile file) {
        this.file = file;
    }
    private FotaFirmwareFile file;


    /**
     * A custom method to reboot the device into the bootloader
     */
    public RebootToBootloaderFunc rebootToBootloader;

}
