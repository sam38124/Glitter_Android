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
 * Class Name: FirmwareImage
 ******************************************************************************/

package com.onsemi.protocol.update;

import com.onsemi.ble.UuidHelper;
import com.onsemi.protocol.cobs.BufferAccess;
import com.onsemi.protocol.utility.Array;

import java.util.UUID;

/**
 * The representation of a fota or an application firmware image.
 */
public class FirmwareImage {

    private FotaFirmwareVersion version;

    /**
     * The version of the firmware
     * @return The version
     */
    public FotaFirmwareVersion getVersion() { return version; }

    private byte[] deviceId;

    /**
     * The device id
     * @return The device id
     */
    public byte[] getDeviceId() { return deviceId; }

    private byte[] imageData;

    /**
     * The image data
     * @return The image data
     */
    public byte[] getImageData() { return imageData; }

    private byte[] buildId;

    /**
     * The build id
     * @return The build id
     */
    public byte[] getBuildId() { return buildId; }

    private UUID fotaServiceUuid;

    /**
     * The uuid of the fota service. Can be null for an application image
     * @return The fota service uuid
     */
    public UUID getFotaServiceUuid() { return fotaServiceUuid; }


    private static int SignatureSize = 64;


    /**
     * Parses the data of a fota firmware file
     * @param fileData The raw data
     * @param offset The offset in the fileData
     * @return The last position
     * @throws Exception When an error occurred
     */
    public long Parse(byte[] fileData, long offset) throws Exception
    {
        long fileOffset = offset;
        boolean isFotaImage = fileOffset == 0;

        long initialStackPointer = BufferAccess.readUInt32LittleEndian(fileData, (int)offset);
        offset += 4;
        long resetHandler = BufferAccess.readUInt32LittleEndian(fileData, (int)offset);
        offset += 4;
        // Exception Handler 2 - 6
        offset += 4 * 5;
        long versionInfoPointer = BufferAccess.readUInt32LittleEndian(fileData, (int)offset);
        offset += 4;
        long imageDescriptorPointer = BufferAccess.readUInt32LittleEndian(fileData, (int)offset);
        offset += 4;

        long imageStartAddress = (long)(resetHandler & ~0x7ff);

        long offsetVersionInfo = versionInfoPointer - imageStartAddress + fileOffset;
        long offsetImageDescriptor = imageDescriptorPointer - imageStartAddress + fileOffset;

        offset = offsetVersionInfo;
        CheckBounds(fileData, offset, 8 + 16, "Version info offset");

        version = new FotaFirmwareVersion();
        getVersion().setVersion(fileData, (int)offset, 8);
        offset += 8;
        deviceId = new byte[16];
        System.arraycopy(fileData, (int)offsetVersionInfo + 8, deviceId, 0, 16);
        offset += 16;

        if (isFotaImage)
        {
            // read configuration structure
            long configLength = BufferAccess.readUInt32LittleEndian(fileData, (int)offset);
            offset += 4;

            // public key
            offset += 64;

            // service uuid
            byte[] serviceUuidData = new byte[16];
            System.arraycopy(fileData, (int)offset, serviceUuidData, 0, 16);
            Array.reverse(serviceUuidData);
            fotaServiceUuid = UuidHelper.fromByteArray(serviceUuidData);
            offset += 16;
            long nameLength = BufferAccess.readUInt16LittleEndian(fileData, (int)offset);
            offset += 2;
            String name = new String(fileData, (int)offset, (int)nameLength, "UTF-8");
        }

        offset = offsetImageDescriptor;
        CheckBounds(fileData, offset, SignatureSize + 4 + 32, "Image descriptor offset");

        long imageSize = BufferAccess.readUInt32LittleEndian(fileData, (int)offset);
        imageSize += SignatureSize;
        offset += 4;
        buildId = new byte[32];
        System.arraycopy(fileData, (int)offset, getBuildId(), 0, 32);

        CheckBounds(fileData,fileOffset + imageSize, 0, "Image end");
        imageData = new byte[(int)imageSize];
        System.arraycopy(fileData, (int)fileOffset, imageData, 0, (int)imageSize);
        offset = imageSize;

        // pad offset to 2048
        offset = offset + 2048 - (offset) % 2048;

        return offset;
    }

    private void CheckBounds(byte[] data, long address, long size, String info) throws FotaException
    {
        if ((address + size) > data.length)
        {
            throw new FotaException(info + " out of range: 0x{address:X4}", FotaStatus.GeneralError);
        }
    }

}
