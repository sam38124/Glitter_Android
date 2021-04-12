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
 * Class Name: BinaryString
 ******************************************************************************/

package com.onsemi.protocol.cobs;

/**
 * This class consists of a few static methods to handle a binary string like "FFAA3498".
 */

public class BinaryString {

    /**
     * Build string of a given binary data.
     * @param buffer    Binary data to convert into a string.
     * @param offset    Offset of the data to convert in the buffer.
     * @param count     Number of bytes to be represented by the string.
     * @return          String the represents the binary data.
     */
    public static String toString(byte[] buffer, int offset, int count) {
        return toString(buffer, offset, count, "");
    }

    /**
     * Build string of a given binary data.
     * @param buffer    Binary data to convert into a string.
     * @param offset    Offset of the data to convert in the buffer.
     * @param count     Number of bytes to be represented by the string.
     * @param separator Separator string in the output between two bytes.
     * @return          String the represents the binary data.
     */
    public static String toString(byte[] buffer, int offset, int count, String separator)
    {
        StringBuilder builder = new StringBuilder(buffer.length);

        for (int i = 0; i < count; i++)
        {
            if (builder.length() > 0)
            {
                builder.append(separator);
            }
            builder.append(String.format("%x02", buffer[offset + i]));
        }

        return (builder.toString());
    }
}
