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
 * Class Name: PeripheralState
 ******************************************************************************/

package com.onsemi.ble;

/**
 * All possible states of a bluetooth le peripheral
 */

public enum PeripheralState {
    /**
     * Peripheral is not used.
     */
    Idle,

    /**
     * Attempt to establish a link to the peripheral.
     */
    EstablishLink,

    /**
     * Peripheral is connected and the services are going to be discovered.
     */
    DiscoveringServices,

    /**
     * The initial checks and readings from the peripherals are done now.
     */
    Initialize,

    /**
     * The peripheral is ready to be used.
     */
    Ready,

    /*
    * Firmware update in progress
     */
    Update,

    /**
     * Tear down the peripheral link.
     */
    TearDownLink,

}
