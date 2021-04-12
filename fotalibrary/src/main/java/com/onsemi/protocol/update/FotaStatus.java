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
 * Class Name: FotaStatus
 ******************************************************************************/

package com.onsemi.protocol.update;

/**
 * The status of the fota operation
 */
public class FotaStatus {

    /**
     * update finished with success
     */
    public static int Sucess = 0x00;

    /**
     * The device id is not compatible
     */
    public static int IncompatibleDeviceId = 0x01;

    /**
     * The build id is not compatible
     */
    public static int IncompatibleBuildId = 0x02;

    /**
     * The image size is not valid
     */
    public static int WrongImageSize = 0x03;

    /**
     * Failed to store the image into the flash
     */
    public static int FlashStorageError = 0x04;

    /**
     * The signature is not valid
     */
    public static int InvalidSignature = 0x05;

    /**
     * The image start address is not valid
     */
    public static int InvalidStartAddress = 0x06;

    /**
     * Unspecified error
     */
    public static int GeneralError = -1;

    /**
     * Converts the status to string
     * @param status The status
     * @return a string
     */
    public static String toString(int status) {
        if(status == Sucess) {
            return "Sucess";
        }
        else if(status == IncompatibleDeviceId) {
            return "Incompatible Device Id";
        }
        else if(status == IncompatibleBuildId) {
            return "Incompatible Build Id";
        }
        else if(status == WrongImageSize) {
            return "Wrong Image Size";
        }
        else if(status == FlashStorageError) {
            return "Flash Storage Error";
        }
        else if(status == InvalidSignature) {
            return "Invalid Signature";
        }
        else if(status == InvalidStartAddress) {
            return "Invalid Start Address";
        }
        else if(status == GeneralError) {
            return "General Error";
        }
        else {
            return "Unknown";
        }
    }
}
