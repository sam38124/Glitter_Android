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
 * Class Name: CharacteristicProperties
 ******************************************************************************/

package com.onsemi.ble;

/**
 * The characteristic properties
 */

public class CharacteristicProperties {
    public static int Broadcast = 0x01;
    public static int Read = 0x02;
    public static int WriteWithoutResponse = 0x04;
    public static int Write = 0x08;
    public static int Notify = 0x10;
    public static int Indicate = 0x20;
    public static int AuthenticatedSignedWrites = 0x40;
    public static int ExtendedProperties = 0x80;
    public static int NotifyEncryptionRequired = 0x100;
    public static int IndicateEncryptionRequired = 0x200;
}
