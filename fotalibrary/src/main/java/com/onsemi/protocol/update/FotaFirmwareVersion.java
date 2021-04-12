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
 * Class Name: FotaFirmwareVersion
 ******************************************************************************/

package com.onsemi.protocol.update;

import com.onsemi.protocol.cobs.BufferAccess;
import com.onsemi.protocol.utility.Log;

/**
 * The version of the fota firmware
 */
public class FotaFirmwareVersion {

    /**
     * The image id, a 6 character string.
     */
    private String imageId;
    public void setImageId(String id) { imageId = id; }
    public String getImageId() { return imageId; }

    /**
     * The image version x.y.z
     */
    private String imageVersion;
    public void setImageVersion(String version) { imageVersion = version; }
    public String getImageVersion() { return imageVersion; }

    /**
     * Sets the version from byte array.
     * @param data The version data from firmware image file.
     * @throws Exception
     */
    public void setVersion(byte[] data) throws Exception
    {
        setVersion(data, 0, data.length);
    }

    /**
     * Sets the version from byte array
     * @param data The version data from firmware image file
     * @param offset The offset in the data arra
     * @param length The length of the data
     * @throws Exception When an error occurred.
     */
    public void setVersion(byte[] data, int offset, long length) throws Exception
    {
        if (length != 8)
        {
            throw new IllegalArgumentException("Expected 8 byte version data but received" + data.length);
        }

        if (data[offset] == 0x00)
        {
            setImageVersion("N/A");
            setImageId("");
            return;
        }

        setImageId(new String(data, offset, 6, "UTF-8"));
        offset += 6;
        int code = BufferAccess.readUInt16LittleEndian(data, (int)offset);
        setImageVersion(decodeVersion16Bit(code));
    }

    private String decodeVersion16Bit(int code)
    {
        return ((code >> 12) & 0xF) + "." + ((code >> 8) & 0xF) + "." + ((code >> 0) & 0xFF);
    }

    /**
     * Default constructor
     */
    public FotaFirmwareVersion()
    {
        setImageVersion("N/A");
        setImageId("N/A");
    }

    /**
     * Initializes the image id and version with the given data.
     * @param data The version data as 8 byte array.
     */
    public FotaFirmwareVersion(byte[] data)
    {
        try
        {
            setVersion(data);
        }
        catch (Exception e)
        {
            Log.e("FirmwareVersion", e.getMessage());
        }
    }

    @Override
    public String toString()
    {
        return getImageId() + " " + getImageVersion();
    }
}
