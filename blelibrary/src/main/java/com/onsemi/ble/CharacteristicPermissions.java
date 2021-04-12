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
 * Class Name: CharacteristicPermissions
 ******************************************************************************/

package com.onsemi.ble;

/**
 * The Characteristic permissions
 */

public class CharacteristicPermissions {
    public static int Readable = 0x01;
    public static int Writeable = 0x02;
    public static int ReadEncryptionRequired = 0x04;
    public static int WriteEncryptionRequired = 0x08;
}
