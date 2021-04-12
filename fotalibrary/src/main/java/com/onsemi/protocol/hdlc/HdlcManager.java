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
 * Class Name: HdlcManager
 ******************************************************************************/

package com.onsemi.protocol.hdlc;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import com.onsemi.protocol.utility.*;

/**
 * The manager for HDLC frames
 */
public class HdlcManager implements Disposable, DataExchange {


    private final static String TAG = "HdlcManager";

    // HDLC configuration
    public final static int MaxPayloadLength = 251;
    private static final byte SequenceNumberCount = 8;
    private static final byte TransmitWindowSize = 4;
    private static final int TransmitAcknowledgeTimeout = 1000; // [ms]
    private static final int TransmitMaxRetryCount = 2;

    private byte expectedSequenceNumber;
    private byte sendSequenceNumber;
    private BlockingQueue<HdlcDataPacket> sendQueue;
    private BlockingQueue<HdlcDataPacket> acknowledgeQueue;;
    private DataExchange lowerDataExchange;
    private final LinkedList<DataExchangeListener> dataExchangeListeners;
    private final LinkedList<HdlcManagerListener> hdlcManagerListeners;
    final Semaphore checkTimeoutSemaphore = new Semaphore(0);
    final Semaphore transmitSemaphore = new Semaphore(TransmitWindowSize);
    private boolean exitThread = false;
    Executor executor = Executors.newSingleThreadExecutor();

    private DataExchangeListener listener = new DataExchangeListener() {
        @Override
        public void onDataReceived(final byte[] data) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        dataReceived(data);
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, e.toString());
                    }
                }
            });
        }
    };
    private static final byte HdlcUFrameTypeSABM = 0x07;
    private static final byte HdlcUFrameTypeUA = (byte)0xC0;

    /**
     * Constructor
     * @param dataExchange interface used to transmit HCLD frames
     */
    public HdlcManager(DataExchange dataExchange) {
        lowerDataExchange = dataExchange;
        dataExchangeListeners = new LinkedList<>();
        hdlcManagerListeners = new LinkedList<>();
        expectedSequenceNumber = 0;
        sendSequenceNumber = 0;
        sendQueue = new ArrayBlockingQueue<HdlcDataPacket>(20);
        acknowledgeQueue = new ArrayBlockingQueue<HdlcDataPacket>(10);

        lowerDataExchange.addListener(listener);

        executeFrameObservation();
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

    /**
     * Add a listener for HdlcManagerListener callbacks
     * @param listener
     */
    public void addListener(HdlcManagerListener listener) {
        synchronized (hdlcManagerListeners) {
            hdlcManagerListeners.add(listener);
        }
    }

    /**
     * Remove listener for HdlcManagerListener callbacks
     * @param listener
     */
    public void removeListener(HdlcManagerListener listener) {
        synchronized (hdlcManagerListeners) {
            hdlcManagerListeners.remove(listener);
        }
    }
    /**
     * Invokes the onConnectionError callback
     */
    private void invokeConnectionError() {
        List<HdlcManagerListener> listCopy;
        synchronized (hdlcManagerListeners) {
            listCopy = new LinkedList<>(hdlcManagerListeners);
        }
        try {
            for(HdlcManagerListener listener : listCopy) {
                listener.onConnectionError();
            }
        }
        catch(Exception ex) {
            Log.e(TAG, "invokeCharacteristicChanged failed: " + ex.getMessage());
        }
    }
    /**
     * sets the HdlcManager back to its initial state
     * and sends the SABM u frame to the peer
     */
    public void reset() {
        sendQueue.clear();
        transmitSemaphore.tryAcquire(TransmitWindowSize);
        // set TransmitWindowSize permits
        transmitSemaphore.release(TransmitWindowSize);


        synchronized (acknowledgeQueue) {
            acknowledgeQueue.clear();
        }

        expectedSequenceNumber = 0;
        sendSequenceNumber = 0;
        // send reset command
        sendUFrame(HdlcUFrameTypeSABM);
    }

    /**
     * This method has to be called when data is received from interface.
     * @param data The data received
     */
    public void dataReceived(byte[] data) {
        boolean isIFrame = isIFrame(data);
        byte receiveSequenceNumber = getReceiveSequenceNumber(data);
        if(isIFrame) {
            handleIFrame(data);
        }
        else {
            handleSFrame(data);
        }

        synchronized (acknowledgeQueue) {
            // check receive sequence number and remove sent frames from queue
            while ((acknowledgeQueue.size() > 0)
                    && (acknowledgeQueue.peek().getSequenceNumber() != receiveSequenceNumber)) {
                // remove element from queue
                acknowledgeQueue.poll();
                transmitSemaphore.release();
            }

        }

        // check transmit window
        if ((!processSendQueue()) && (isIFrame)) {
            sendSFrame();
        }

        if(checkTimeoutSemaphore.availablePermits() == 0) {
            checkTimeoutSemaphore.release();
        }
    }

    /**
     * Handles an i frame
     * @param data
     */
    private void handleIFrame(byte[] data) {
        byte sentSequenceNumber = getSentSequenceNumber(data);
        byte receiveSequenceNumber = getReceiveSequenceNumber(data);
        Log.i(TAG, "I-frame received - N(R):" + receiveSequenceNumber +" N(S):" + sentSequenceNumber);
        if (sentSequenceNumber == expectedSequenceNumber) {
            // update expected sequence number
            expectedSequenceNumber = (byte)((expectedSequenceNumber + 1) % SequenceNumberCount);
            byte type = data[1];
            byte[] payload = new byte[data.length - 1];
            System.arraycopy(data, 1, payload, 0, payload.length);
            invokeDataReceived(payload);
        }
        else {
            Log.w(TAG, "I-frame with invalid sent sequence number ignored (Received:" + sentSequenceNumber + " Expected:" + expectedSequenceNumber);
        }

    }

    /**
     * Handles a s frame
     * @param data
     */
    private void handleSFrame(byte[] data) {
        byte receiveSequenceNumber = getReceiveSequenceNumber(data);
        boolean isRej = (data[0] & 0x08) > 0;
        boolean isRNR = (data[0] & 0x04) > 0;
        if(isRej) {
            Log.w(TAG, "S-frame received  - N(R):" + receiveSequenceNumber + " Rejected!");
        } else if(isRNR) {
            Log.w(TAG, "S-frame received  - N(R):" + receiveSequenceNumber + " Receive not ready!");
        }
        else {
            Log.i(TAG, "S-frame received  - N(R):" + receiveSequenceNumber);
        }

    }

    /**
     * Returns true if the corrent data belongs to an i frame
     * @param data The frame data
     * @return  True if i frame, false otherwise
     */
    private boolean isIFrame(byte[] data) {
        return (data[0] & 0x01) == 0;
    }

    /**
     * Returns the sent sequence number of a frame
     * @param data The frame data
     * @return The number
     */
    private byte getSentSequenceNumber(byte[] data) {
        return (byte)((data[0] >> 1) & 0x07);
    }

    /**
     * Returns the receive sequence number of a frame
     * @param data The frame data
     * @return The number
     */
    private byte getReceiveSequenceNumber(byte[] data) {
        return (byte)((data[0] >> 5) & 0x07);
    }

    /**
     * Checks if thea cknowledgeQueue amd sends a frame from sendQueue if it is allowed
     * @return true if a frame was sent
     */
    private boolean processSendQueue() {
        boolean frameSent = false;
        synchronized (acknowledgeQueue) {
            // maximum transmit window not yet reached and some frames pending ?
            while ((acknowledgeQueue.size() < TransmitWindowSize)
                    && (sendQueue.size() > 0)) {
                // get entry from queue
                HdlcDataPacket transmitEntry = sendQueue.poll();

                // send frame
                sendIFrame(transmitEntry);
                transmitEntry.setSendCount(1);
                transmitEntry.setSendTime(new Date());
                frameSent = true;

                // add to acknowledge queue
                acknowledgeQueue.add(transmitEntry);
            }
        }
        return frameSent;
    }

    /**
     * Sends a i frame
     * @param data The data
     */
    private void sendIFrame(HdlcDataPacket data) {
        Log.i(TAG, "Send I-frame - N(R):" + expectedSequenceNumber +" N(S):" + data.getSequenceNumber());
        byte[] frame = new byte[1 /*HDLC header*/ + data.getData().length];
        frame[0] = (byte)(((expectedSequenceNumber << 5) & 0xE0) | ((data.getSequenceNumber() << 1) & 0x0E));
        System.arraycopy(data.getData(), 0, frame, 1, data.getData().length);
        try {
            lowerDataExchange.transmit(frame);
        }
        catch(Exception e) {
            Log.e(TAG, "Failed to transmit I-frame");
        }
    }


    /**
     * Sends a u frame
     * @param uFrameType The u frame type
     */
    private void sendUFrame(byte uFrameType) {

        Log.i(TAG, "Send U-frame - Type: " + uFrameType);

        // encode frame
        byte[] frame = new byte[1 /*HDLC header*/];
        frame[0] = (byte)((((byte)uFrameType << 3) & 0xE0) | (((byte)uFrameType << 2) & 0x0C) | 0x03);
        try {
            lowerDataExchange.transmit(frame);
        }
        catch(Exception e) {
            Log.e(TAG, "Failed to transmit U-frame");
        }
    }

    /**
     * Sends a s frame
     */
    private void sendSFrame() {
        Log.i(TAG, "Send S-frame - N(R):" + expectedSequenceNumber);
        byte[] frame = new byte[1];
        frame[0] = (byte)(((expectedSequenceNumber << 5) & 0xE0) | 0x01);
        try {
            lowerDataExchange.transmit(frame);
        }
        catch(Exception e) {
            Log.e(TAG, "Failed to transmit S-frame");
        }
    }

    /**
     * Starts the frame observation task
     */
    private void executeFrameObservation() {
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(!exitThread) {
                    try {
                        // check the acknowledge queue for the next timeout
                        HdlcDataPacket packet = null;
                        synchronized (acknowledgeQueue) {
                            packet = acknowledgeQueue.peek();
                        }
                        int timeout = 1000;
                        if(packet != null) {
                            timeout = TransmitAcknowledgeTimeout - ((int)(new Date().getTime() - packet.getSendTime().getTime()));
                            timeout = timeout < 0 ? 1 : timeout;
                        }

                        // wait for the timeout or the semaphore is set
                        boolean success = checkTimeoutSemaphore.tryAcquire(timeout, TimeUnit.MILLISECONDS);

                        // check for a frame timeout
                        checkFrameTimeout();
                    } catch (InterruptedException e) {
                        Log.e(TAG, e.getMessage());
                        e.printStackTrace();
                    }
                    catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

    /**
     * Checks if a timeout for a frame in the queue is exceeded
     */
    private void checkFrameTimeout() {
        synchronized (acknowledgeQueue) {
            // check if timeout expired
            if(acknowledgeQueue.size() <= 0) {
                return;
            }
            boolean timeout = acknowledgeQueue.peek().getSendTime().before(new Date(System.currentTimeMillis() - TransmitAcknowledgeTimeout));
            if (timeout) {
                // check if number of retries is reached
                if (acknowledgeQueue.peek().getSendCount() > TransmitMaxRetryCount) {
                    Log.e(TAG, "Acknowledge timeout -> terminate session");

                    // terminate session
                    reset();
                    invokeConnectionError();
                }
                else {
                    Log.w(TAG, "Acknowledge timeout -> resend not acknowledged frames");

                    // resend all frames
                    for (HdlcDataPacket transmitEntry : acknowledgeQueue) {
                        sendIFrame(transmitEntry);
                        transmitEntry.setSendTime(new Date());
                        transmitEntry.setSendCount(transmitEntry.getSendCount() + 1);
                    }
                }
            }
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
    @Override
    public int getMaxDataLength() {
        return 160;
    }

    @Override
    public void init() throws ProtocolException {
        reset();
    }

    @Override
    public void transmit(byte[] data) throws ProtocolException {
        if(data.length > MaxPayloadLength) {
            throw new ProtocolException("A payload length of " + data.length + " is not permitted for hdlc");
        }
        try {
            transmitSemaphore.acquire();
        }
        catch(Exception e) {
            throw new ProtocolException("Failed to aquire semaphore: " + e.getMessage());
        }


        sendQueue.add(new HdlcDataPacket(data, sendSequenceNumber));
        sendSequenceNumber = (byte)((sendSequenceNumber + 1) % SequenceNumberCount);
        processSendQueue();
        if(checkTimeoutSemaphore.availablePermits() == 0) {
            checkTimeoutSemaphore.release();
        }
    }

    @Override
    public void dispose() {
        exitThread = true;
        lowerDataExchange.removeListener(listener);
        reset();
    }


}
