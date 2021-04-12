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
 * Class Name: HdlcManagerListener
 ******************************************************************************/

package com.onsemi.protocol.hdlc;

/**
 * Listener for the HdlcManager
 */

public interface HdlcManagerListener {
    /**
     * Is invoked when a frame is received.
     * @param type The frame type
     * @param data The data
     */
    void onFrameReceived(byte type, byte[] data);

    /**
     * Invoked when a frame timed out
     */
    void onConnectionError();
}
