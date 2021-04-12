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
 * Class Name: Version
 ******************************************************************************/

package com.onsemi.protocol.cobs;

/**
 * Holds the version information of this library
 */

public class Version {
    /**
     * The version name string
     * @return The version name.
     */
    public static String getVersionName() {
        return versionName;
    }

    /**
     * The ascending version number
     * @return the version code
     */
    public static int getVersionCode() {
        return versionNumber;
    }

    /**
     * The git hash of this sourcecode version
     * @return the hash
     */
    public static String getHash() {
        return hash;
    }

    private static String versionName = "1.1.0";
    private static int versionNumber = 9999;
    private static String hash = "FFFFFFFFFFFFFFFF";
}
