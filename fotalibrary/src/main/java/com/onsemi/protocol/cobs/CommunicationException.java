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
 * Class Name: CommunicationException
 ******************************************************************************/

package com.onsemi.protocol.cobs;

/**
 * Exception thrown by the cobs library
 */

public class CommunicationException extends Exception {
    public CommunicationException(String Message) {
        super(Message);
    }
}
