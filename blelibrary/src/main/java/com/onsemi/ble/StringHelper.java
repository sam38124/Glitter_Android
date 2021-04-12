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
 * Class Name: StringHelper
 ******************************************************************************/

package com.onsemi.ble;

/**
 * Helper class to convert object to string
 */

public class StringHelper {
    /**
     * Converts a byte array to hex string
     * @param bytes The byte array
     * @return  The hex string
     */
    public static String toHex(byte[] bytes) {
        if(bytes == null) {
            return "[ null ]";
        }
        StringBuilder sb = new StringBuilder(bytes.length * 3 + 2);
        sb.append("[ ");
        for(byte b : bytes) {
            sb.append(String.format("%02x ", b));
        }
        sb.append(']');
        return sb.toString();
    }

    /**
     * Converts a byte array to hex string
     * @param bytes The byte array
     * @return  The hex string
     */
    public static String toHex(byte[] bytes, String delimiter, String start, String end) {
        if(bytes == null) {
            return "[ null ]";
        }
        StringBuilder sb = new StringBuilder(bytes.length * 3 + 2);
        sb.append(start);
        for(byte b : bytes) {
            sb.append(String.format("%02x", b) + delimiter);
        }
        sb.append(end);
        return sb.toString();
    }
}
