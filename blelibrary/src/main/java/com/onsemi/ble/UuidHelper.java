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
 * Class Name: UuidHelper
 ******************************************************************************/

package com.onsemi.ble;

import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * Helper functions for simpler uuid usage
 */

public class UuidHelper {
    /**
     * Convert a UUID to a 128 bit Bluetooth UUID.
     *
     * If the given UUID is already 128 bits a copy of this object is returned. If the given UUID has a
     * length of 16 or 32 bits it will be converted to a Bluetooth SIG UUID with length of 128 bits. This
     * is done by inserting the short UUID in the Bluetooth base UUID YYYYXXXX-0000-1000-8000-00805f9b34fb.
     * @param uuid UUID to be converted to a 128-Bit UUID.
     * @return Created UUID with 128Bit width.UUID to be converted to a 128-Bit UUID.
     * @throws Exception When the uuid sting is not in a valid format
     */
    public static UUID ConvertTo128BitBluetoothUuid(String uuid) throws Exception {
        // already a 128Bit UUID ?
        if (uuid.length() == 36)
        {
            return (UUID.fromString(uuid));
        }

        if (uuid.length() == 4)
        {
            String sigUuid = "0000" + uuid + "-0000-1000-8000-00805f9b34fb";
            return UUID.fromString(sigUuid);
        }
        if (uuid.length() == 8)
        {
            String sigUuid = uuid + "-0000-1000-8000-00805f9b34fb";
        }

        throw new Exception("Given UUID can't be converted to a Bluetooth 128-Bit UUID");
    }

    /**
     * Convert a UUID to a 128 bit Bluetooth UUID.
     *
     * If the given UUID is already 128 bits a copy of this object is returned. If the given UUID has a
     * length of 16 or 32 bits it will be converted to a Bluetooth SIG UUID with length of 128 bits. This
     * is done by inserting the short UUID in the Bluetooth base UUID YYYYXXXX-0000-1000-8000-00805f9b34fb.
     * @param uuid UUID to be converted to a 128-Bit UUID.
     * @return Created UUID with 128Bit width.UUID to be converted to a 128-Bit UUID.
     * @throws Exception When the uuid sting is not in a valid format
     */
    public static UUID fromByteArray(byte[] uuid) throws Exception {
        // already a 128Bit UUID ?
        if (uuid.length == 16) {
            ByteBuffer bb = ByteBuffer.wrap(uuid);
            long high = bb.getLong();
            long low = bb.getLong();
            return new UUID(high, low);
        }
        throw new Exception("Given UUID can't be converted to a Bluetooth 128-Bit UUID");
    }

}
