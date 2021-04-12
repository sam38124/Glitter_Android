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
 * Class Name: CobsFraming
 ******************************************************************************/

package com.onsemi.protocol.cobs;

import com.onsemi.protocol.utility.DataExchange;
import com.onsemi.protocol.utility.DataExchangeListener;
import com.onsemi.protocol.utility.Log;
import com.onsemi.protocol.utility.ProtocolException;

import java.sql.Time;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public class CobsFraming implements DataExchange {

    /**
     * Length of the CRC used.
     */
    private final int CrcLength = 2;
    private final static String TAG = "Cobs";
    private int maxDataLength = getMaxDataLength() + 2 + 2;

    private final LinkedList<DataExchangeListener> dataExchangeListeners;
    private final LinkedBlockingDeque<Byte> sendQueue;
    private DataExchange lowerDataExchange;
    private Thread sendThread;
    private boolean exit;
    private DataExchangeListener listener = new DataExchangeListener() {
        @Override
        public void onDataReceived(byte[] data) {
            processData(data, 0, data.length);
        }
    };

    /**
     * Buffer for received RX data.
     */
    private byte[] frameRxBuffer;

    /**
     * Number of RX data in buffer.
     */
    private int frameRxCount;

    /**
     * Construtor
     * @param dataExchange The lower data exchange
     */
    public CobsFraming(DataExchange dataExchange) {
        lowerDataExchange = dataExchange;
        dataExchangeListeners = new LinkedList<>();
        sendQueue = new LinkedBlockingDeque<>();
        lowerDataExchange.addListener(listener);
        sendThread = new Thread(new Runnable() {
            @Override
            public void run() {
                executeSend();
            }
        });
        sendThread.start();
    }

    /**
     * Handle incoming data.
     * @param buffer    Buffer with incoming data.
     * @param offset    Offset of the incoming data.
     * @param count     Number of incoming bytes.
     */
    private void processData(byte[] buffer, int offset, int count)
    {
        // handle all incoming octets
        for (int j = 0; j < count; j++)
        {
            // get octet
            byte b = buffer[offset + j];

            // received protocol start/end flag ?
            if (b == 0)
            {
                // data received ?
                if ((frameRxBuffer != null) && (frameRxCount > 0))
                {
                    // Log.DebugFormat("RX - 0x{0}", BinaryString.ToString(frameRxBuffer, 0, frameRxCount));

                    // decode frame
                    byte[] raw = new byte[frameRxCount];
                    System.arraycopy(frameRxBuffer, 0, raw, 0, frameRxCount);
                    try
                    {
                        byte[] frameData = Cobs.decode(raw);

                        // check frame
                        if (frameData.length <= CrcLength)
                        {
                            Log.w(TAG, "Received frame is too short");
                        }
                        else
                        {
                            // calculate CRC
                            short crc = (short)~Crc16.calculate(frameData, 0, frameData.length - CrcLength, (short)0xffff);

                            // check CRC
                            if (((short)crc) != BufferAccess.readUInt16LittleEndian(frameData, frameData.length - CrcLength))
                            {
                                Log.w(TAG,"Received frame with invalid CRC");
                            }
                            else
                            {
                                try {
                                    // extract frame
                                    byte[] framePayload = new byte[frameData.length - CrcLength];
                                    System.arraycopy(frameData, 0, framePayload, 0, framePayload.length);
                                    invokeDataReceived(framePayload);
                                }
                                catch (Exception ex) {
                                    Log.w(TAG, String.format("Invalid frame payload (0x%s)!", BinaryString.toString(frameData, 0, frameData.length - 2)));
                                }
                            }
                        }
                    }
                    catch (Exception ex) {
                        Log.w(TAG, "Invalid COBS frame received");
                    }
                }

                // reset frame
                frameRxBuffer = new byte[maxDataLength];
                frameRxCount = 0;
            }
            else {
                // already a frame started ?
                if (frameRxBuffer != null) {
                    // add to buffer
                    if (frameRxCount < frameRxBuffer.length) {
                        frameRxBuffer[frameRxCount++] = b;
                    }
                    else {
                        // buffer overrun !
                        Log.e(TAG, "RX buffer overrun");

                        // reset RX
                        frameRxBuffer = null;
                        frameRxCount = 0;
                    }
                }
                else {
                    Log.w(TAG, "Received data while not in a frame");
                }
            }
        }
    }

    /**
     * The send method to be executed in a separate thread.
     * Runs until the exit flag is set
     */
    private void executeSend() {
        List<Byte> sendList = new LinkedList<>();
        Byte out;
        while (!exit) {
            try {
                sendList.clear();
                // wait for an entry in queue
                out = sendQueue.poll(1000, TimeUnit.MILLISECONDS);
                if (out == null) {
                    // queue is empty, continue
                    continue;
                }
                sendList.add(out);
                // take entries from queue until MaxDataLength is reached
                do {
                    // wait 20ms at max for an entry
                    out = sendQueue.poll(20, TimeUnit.MILLISECONDS);
                    if (out == null) {
                        break;
                    }
                    sendList.add(out);
                } while (sendList.size() < lowerDataExchange.getMaxDataLength());
                byte[] buffer = new byte[sendList.size()];
                int i = 0;
                for(Byte b : sendList) {
                    buffer[i] = b;
                    i++;
                }
                lowerDataExchange.transmit(buffer);
            }
            catch (Exception e) {
                Log.e(TAG, "Error while sending cobs frame: " + e.getMessage());
            }
        }
    }

    // ----------------------------------------- DataExchange --------------------------------------
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
        catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
    }


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

    @Override
    public int getMaxDataLength() {
        return 258;
    }

    @Override
    public void init() throws ProtocolException {

    }

    @Override
    public synchronized void transmit(byte[] data) throws ProtocolException {

        // encode frame
        byte[] buffer = new byte[data.length + CrcLength];
        System.arraycopy(data, 0, buffer, 0, data.length);

        short crc = (short) ~Crc16.calculate(buffer, 0, buffer.length - CrcLength, (short)0xffff);
        BufferAccess.writeUInt16LittleEndian(crc, buffer, buffer.length - CrcLength);

        // COBS encoding
        try{
            byte[] encoded = Cobs.encode(buffer);
            sendQueue.add((byte)0x00);
            for (byte b : encoded) {
                sendQueue.add(b);
            }
            sendQueue.add((byte)0x00);
        }
        catch(Exception e) {
            throw new ProtocolException("Failed to encode cobs: " + e.getMessage());
        }
    }

    @Override
    public void dispose() {
        exit = true;
        lowerDataExchange.removeListener(listener);
        sendQueue.clear();
    }
}
