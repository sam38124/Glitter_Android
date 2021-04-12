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
 * Class Name: FotaFrameHandler
 ******************************************************************************/

package com.onsemi.protocol.update;

import android.os.OperationCanceledException;

import com.onsemi.protocol.cobs.BufferAccess;
import com.onsemi.protocol.utility.DataExchange;
import com.onsemi.protocol.utility.DataExchangeListener;
import com.onsemi.protocol.utility.Log;
import com.onsemi.protocol.utility.ProtocolException;

import java.util.LinkedList;
import java.util.List;

/**
 * Frame handler for fota frames
 */
public class FotaFrameHandler implements DataExchange {

    private final static String TAG = "FotaFrameHandler";
    private final LinkedList<DataExchangeListener> dataExchangeListeners;
    private final LinkedList<FotaFrameHandlerListener> fotaListener;
    private final DataExchange lowerDataExchange;
    private final static byte DownloadImageCmd = 0x01;

    /**
     * Constructor
     * @param dataExchange The lower data exchange
     */
    public FotaFrameHandler(DataExchange dataExchange) {
        dataExchangeListeners = new LinkedList<>();
        fotaListener = new LinkedList<>();
        lowerDataExchange = dataExchange;
        lowerDataExchange.addListener(new DataExchangeListener() {
            @Override
            public void onDataReceived(byte[] data) {
                invokeDataReceived(data);
            }
        });
    }

    private boolean cancleRequested;

    /**
     * Performes an update with the given data
     * @param imageData The image data
     * @throws Exception
     */
    public void imageDownload(byte[] imageData) throws Exception{
        byte[] header = new byte[] {DownloadImageCmd, 0, 0, 0, 0, 0, 0, 0};
        BufferAccess.writeUInt32LittleEndian((long)imageData.length, header, 4);
        transmit(header, 0, header.length);
        transmit(imageData, 0, imageData.length);
    }

    private void transmit(byte[] data, int offset, int length) throws ProtocolException
    {
        cancleRequested = false;
        do
        {
            if (cancleRequested)
            {
                throw new OperationCanceledException();
            }
            int size = lowerDataExchange.getMaxDataLength() < (length - offset) ? lowerDataExchange.getMaxDataLength() : length - offset;
            byte[] toSend = new byte[size];
            System.arraycopy(data, offset, toSend, 0, size);
            lowerDataExchange.transmit(toSend);
            offset += size;
            invokeProgressChanged(offset, length);
        } while (offset < length);
    }


    /**
     * Cancels the update
     */
    public void cancle() {
        cancleRequested = true;
    }

    public void addListener(FotaFrameHandlerListener listener) {
        synchronized (fotaListener) {
            fotaListener.add(listener);
        }
    }

    public void removeListener(FotaFrameHandlerListener listener) {
        synchronized (fotaListener) {
            fotaListener.remove(listener);
        }
    }

    /**
     * Invokes the onDataReceived callback
     */
    private void invokeProgressChanged(int progress, int total) {
        List<FotaFrameHandlerListener> listCopy;
        synchronized (fotaListener) {
            listCopy = new LinkedList<>(fotaListener);
        }
        try {
            for(FotaFrameHandlerListener listener : listCopy) {
                listener.onProgressChanged(progress, total);
            }
        }
        catch(Exception ex) {
            Log.e(TAG, "invokeCharacteristicChanged failed: " + ex.getMessage());
        }
    }


    // ----------------------------------------- DataExchange --------------------------------------
    @Override
    public void addListener(DataExchangeListener listener) {
        synchronized (dataExchangeListeners) {
            dataExchangeListeners.add(listener);
        }
    }

    @Override
    public void removeListener(DataExchangeListener listener) {
        synchronized (dataExchangeListeners) {
            dataExchangeListeners.remove(listener);
        }
    }

    /**
     * Invokes the onDataReceived callback
     */
    private void invokeDataReceived(byte[] data) {
        List<DataExchangeListener> listCopy;
        synchronized (dataExchangeListeners) {
            listCopy = new LinkedList<>(dataExchangeListeners);
        }
        try {
            for(DataExchangeListener listener : listCopy) {
                listener.onDataReceived(data);
            }
        }
        catch(Exception ex) {
            Log.e(TAG, "invokeCharacteristicChanged failed: " + ex.getMessage());
        }
    }

    @Override
    public int getMaxDataLength() {
        return 1024 * 1024 * 100;
    }

    @Override
    public void init() throws ProtocolException {

    }

    @Override
    public void transmit(byte[] data) throws ProtocolException {
        transmit(data, 0, data.length);
    }

    @Override
    public void dispose() {
        cancleRequested = true;
    }
}
