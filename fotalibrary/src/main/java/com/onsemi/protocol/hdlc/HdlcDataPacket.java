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
 * Class Name: HdlcDataPacket
 ******************************************************************************/

package com.onsemi.protocol.hdlc;

import java.util.Date;

/**
 * Class to hold the hdlc specific data
 */

class HdlcDataPacket {
    /**
     * The number of send attemps
     * @return
     */
    public int getSendCount() {
        return sendCount;
    }
    public void setSendCount(int sendCount) {
        this.sendCount = sendCount;
    }

    /**
     * The moment of sending
     * @return
     */
    public Date getSendTime() {
        return sendTime;
    }
    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    /**
     * The data
     * @return
     */
    public byte[] getData() {
        return data;
    }

    /**
     * The sequence number
     * @return
     */
    public byte getSequenceNumber() {
        return sequenceNumber;
    }

    private int sendCount;
    private Date sendTime;
    private byte[] data;
    private byte sequenceNumber;

    /**
     * Constructor
     * @param data  The data to send
     * @param sequenceNumber The sequence number of this packet
     */
    public HdlcDataPacket(byte[] data, byte sequenceNumber)
    {
        this.sendCount = 0;
        this.data = data;
        this.sequenceNumber = sequenceNumber;
    }}
