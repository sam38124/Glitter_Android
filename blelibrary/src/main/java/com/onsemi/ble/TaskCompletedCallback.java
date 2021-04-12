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
 * Class Name: TaskCompletedCallback
 ******************************************************************************/

package com.onsemi.ble;

/**
 * Callback for the task completed event
 */

interface TaskCompletedCallback {

     /**
      * Invoked when the task execution completed
      * @param task      The task.
      * @param result    The result.
      */
     void completed(TaskBase task, BleResult result);
}
