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
 * Class Name: TaskBase
 ******************************************************************************/

package com.onsemi.ble;

import java.util.Timer;
import java.util.TimerTask;

/**
 * The base class for all tasks
 */

abstract class TaskBase {

    private final String TAG = "TaskBase";
    private int taskTimeout;
    private Timer taskTimeoutTimer;
    private TaskCompletedCallback taskCompleted;
    private boolean isCanceled;

    TaskBase(int timeout) {
        taskTimeout = timeout;
    }

    /**
     * Executed from task controller when the task is started
     * @param taskCompletedCallback Invoked when the task completed
     */
    public final void start(TaskCompletedCallback taskCompletedCallback) {
        synchronized (this) {
            isCanceled = false;

            // log started task
            Log.d(TAG, String.format("Task started (%s)", toString()));

            taskCompleted = taskCompletedCallback;
            if (taskTimeout > 0) {
                if (taskTimeoutTimer != null) {
                    taskTimeoutTimer.cancel();
                }

                taskTimeoutTimer = new Timer();
                taskTimeoutTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        completed(BleResult.Timeout);
                    }
                }, taskTimeout);
            }
        }

        try {
            if (!isCanceled) {
                start();
            }

            if (isCanceled) {
                completed(BleResult.Canceled);
            }
        } catch (Exception ex) {
            Log.e(TAG, String.format("Task start failed with exception (%s), %s", toString(), ex.getMessage()));
            completed(BleResult.Exception);
        }
    }

    /**
     * Executed from task controller when the task has completed
     * @param result The result of the task execution
     */
    protected final void completed(BleResult result) {
        synchronized (this) {
            if (taskCompleted == null) {
                Log.w(TAG, String.format("Unexpected task completed from task '{%s}'", toString()));
                return;
            }

            // stop task timeout
            if (taskTimeoutTimer != null) {
                taskTimeoutTimer.cancel();
                taskTimeoutTimer = null;
            }

            // call on completed method for task specific task complete action
            onCompleted(result);

            // log started task
            Log.d(TAG, String.format("Task '%s' completed (Result:%s)", toString(), result));

            // cleanup task
            cleanup();

            // call task completed method
            taskCompleted.completed(this, result);
            taskCompleted = null;
        }
    }

    /**
     * Cancels a task execution
     */
    void cancle() {
        isCanceled = true;
        completed(BleResult.Canceled);
    }

    /**
     * Executed from task controller when the task is started
     */
    protected abstract void start();

    /**
     * Is called when the task execution has completed
     * @param result The result of the execution
     */
    protected void onCompleted(BleResult result) {}

    /**
     * Cleanup you resources
     */
    protected void cleanup() {}
}
