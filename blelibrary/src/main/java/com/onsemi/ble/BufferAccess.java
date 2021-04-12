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
 * Class Name: BufferAccess
 ******************************************************************************/

package com.onsemi.ble;

/**
 * BufferAccess is a collection of static methods that handles the reading
 * of data from or to a buffer.
 */

public class BufferAccess {

    /**
     * Write an UInt16 into a byte array (little endian).
     * @param data      Value to write into the buffer.
     * @param buffer    Buffer to write the value in.
     * @param offset    Offset of the first byte of the value.
     */
    public static void writeUInt16LittleEndian(short data, byte[] buffer, int offset)
    {
        buffer[offset + 0] = (byte)(data);
        buffer[offset + 1] = (byte)(data >> 8);
    }

    /**
     * Read an UInt16 from a byte array (little endian).
     * @param buffer    Buffer to read the value from.
     * @param offset    Offset of the first byte of the value.
     * @return  Value read from the buffer.
     */
    public static short readUInt16LittleEndian(byte[] buffer, int offset)
    {
        return (short)((((short)buffer[offset]) & 0x00ff) | ((((short)buffer[offset + 1]) << 8)));
    }

    /**
     * Write an UInt16 into a byte array (little endian).
     * @param data      Value to write into the buffer.
     * @param buffer    Buffer to write the value in.
     * @param offset    Offset of the first byte of the value.
     */
    public static void writeUInt16BigEndian(short data, byte[] buffer, int offset)
    {
        buffer[offset + 0] = (byte)(data >> 8);
        buffer[offset + 1] = (byte)(data);
    }

    /**
     * Read an UInt16 from a byte array (little endian).
     * @param buffer    Buffer to read the value from.
     * @param offset    Offset of the first byte of the value.
     * @return  Value read from the buffer.
     */
    public static short readUInt16BigEndian(byte[] buffer, int offset)
    {
        return (short)((((short)buffer[offset + 1]) & 0x00ff) | ((((short)buffer[offset]) << 8)));
    }
}
