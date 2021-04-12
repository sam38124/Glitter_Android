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
 * Class Name: TaskControllerListener
 ******************************************************************************/

package com.onsemi.ble;

/**
 *  Callbacks for the task controller
 */

interface TaskControllerListener {

    /**
     * Invoked when a task is started
     * @param task The task.
     */
    void TaskStarted(TaskBase task);

    /**
     * Invoked when a tasks execution completed
     * @param task      The task.
     * @param result    The result.
     */
    void TaskCompleted(TaskBase task, BleResult result);
}
