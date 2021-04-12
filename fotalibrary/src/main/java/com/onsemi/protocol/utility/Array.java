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
 * Class Name: Array
 ******************************************************************************/

package com.onsemi.protocol.utility;

/**
 * Helper class for arrays
 */
public class Array {

    /**
     * Reverses the given array
     * @param data The data to be reversed
     * @param <T> The type
     */
    public static <T> void reverse(T[] data) {
        for(int i = 0; i < data.length / 2; i++)
        {
            T temp = data[i];
            data[i] = data[data.length - i - 1];
            data[data.length - i - 1] = temp;
        }
    }

    /**
     * Reverses the given array
     * @param data The data to be reversed
     */
    public static  void reverse(byte[] data) {
        for(int i = 0; i < data.length / 2; i++)
        {
            byte temp = data[i];
            data[i] = data[data.length - i - 1];
            data[data.length - i - 1] = temp;
        }
    }

    /**
     * Checks if each element of an array 1 is equal the element in array 2
     * @param a1 The first array
     * @param a2 The second array
     * @param <T> The type
     * @return True if equal, false otherwise
     */
    public static <T> boolean sequenceEqual(T[] a1, T[] a2) {
        if(a1 == null || a2 == null) {
            return false;
        }

        if(a1.length != a2.length) {
            return false;
        }

        if(a1.equals(a2)) {
            return true;
        }

        for(int i = 0; i < a1.length; i++) {
            if(a1[i] != a2[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if each element of an array 1 is equal the element in array 2
     * @param a1 The first array
     * @param a2 The second array
     * @return True if equal, false otherwise
     */
    public static boolean sequenceEqual(byte[] a1, byte[] a2) {
        if(a1 == null || a2 == null) {
            return false;
        }

        if(a1.length != a2.length) {
            return false;
        }

        if(a1.equals(a2)) {
            return true;
        }

        for(int i = 0; i < a1.length; i++) {
            if(a1[i] != a2[i]) {
                return false;
            }
        }
        return true;
    }
}
