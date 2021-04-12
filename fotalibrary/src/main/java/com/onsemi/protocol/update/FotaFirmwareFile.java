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
 * Class Name: FotaFirmwareFile
 ******************************************************************************/

package com.onsemi.protocol.update;

import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * The representation of a fota firmware file
 */
public class FotaFirmwareFile {

    public String getFilename() {
        return filename;
    }

    /**
     * The fota image
     * @return The fota image
     */
    public FirmwareImage getFotaImage() {
        return fotaImage;
    }

    /**
     * The application image
     * @return The app image
     */
    public FirmwareImage getAppImage() {
        return appImage;
    }

    private String filename;
    private FirmwareImage fotaImage;
    private FirmwareImage appImage;

    /**
     * Constructor parses the given firmware file
     * @param inputStream The input stream to the firmware file
     * @throws Exception When an error occurred.
     */
    public FotaFirmwareFile(InputStream inputStream) throws Exception
    {
        //filename =  inputStream. uri.getPath();

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        // Make a file object from the path name

        byte[] fileData;

        try {
            int length = 0;
            byte[] buffer = new byte[1024 * 2];
            do {
                length = inputStream.read(buffer, 0, buffer.length);
                if(length < 0)
                {
                    // end of file
                    length = 0;
                }
                os.write(buffer, 0, length);
            }
            while (length > 0);
            fileData = os.toByteArray();
        }
        catch(Exception e) {
            throw e;
        }
        finally {
            inputStream.close();
            os.close();
        }

        long offset = 0;

        fotaImage = new FirmwareImage();
        offset = fotaImage.Parse(fileData, offset);

        appImage = new FirmwareImage();
        offset = appImage.Parse(fileData, offset);
    }
}
