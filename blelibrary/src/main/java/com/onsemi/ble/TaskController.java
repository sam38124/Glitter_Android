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
 * Class Name: TaskController
 ******************************************************************************/

package com.onsemi.ble;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Semaphore;

import static java.lang.Thread.MAX_PRIORITY;

/**
 * A task controller to run tasks sequentially
 */

class TaskController implements TaskCompletedCallback, Disposable {

    private static final String TAG = "TaskController";
    private Thread controllerTask;
    private TaskBase taskPending;
    private Queue<TaskBase> taskQueue;
    private boolean taskThreadAbort;
    private Semaphore taskThreadWakeupEvent;
    private final LinkedList<TaskControllerListener> listenerList;

    TaskController()
    {
        listenerList = new LinkedList<>();
        taskThreadAbort = false;
    }

    /**
     * Enqueue a task into the task queue
     * @param task The task to be executed
     */
    void enqueue(TaskBase task)
    {

        // enqueue task
        synchronized (this)
        {
            InstantiateMembers();
            taskQueue.add(task);
        }

        // wakeup task thread
        taskThreadWakeupEvent.release();
    }


    /**
     * Instantiate the members only if the taskcontroller is really used
     */
    private void InstantiateMembers()
    {
        if (controllerTask == null)
        {
            taskThreadWakeupEvent = new Semaphore(1);
            taskQueue = new ConcurrentLinkedDeque<>();
            controllerTask =  new Thread(new Runnable() {
                @Override
                public void run() {
                    TaskThread();
                }
            });
            controllerTask.setPriority(MAX_PRIORITY);
            controllerTask.start();
        }
    }

    /**
     * The thread function
     */
    private void TaskThread()
    {
        do
        {
            // wait for something to do
            try {
                taskThreadWakeupEvent.acquire();
            } catch (InterruptedException e) {
                Log.w(TAG, "taskThreadWakeupEvent interrupted");
                continue;
            }

            // no task pending ?
            if (taskPending == null)
            {
                // dequeue next task
                synchronized (this)
                {
                    if (taskQueue.size() > 0)
                    {
                        taskPending = taskQueue.poll();
                    }
                }

                // new task to start ?
                if (taskPending != null)
                {
                    // trigger task started event
                    invokeStarted(taskPending);

                    // initiate start
                    try
                    {
                        // use the UI thread to execute tasks
                        // it seams to be more stable on android
                            taskPending.start(this);
                    }
                    catch (Exception ex)
                    {
                        Log.e(TAG, String.format("Starting task '%s' failed with exception: %s", taskPending.toString(), ex));
                        taskPending.completed(BleResult.Failure);
                    }
                }
            }
        } while (!taskThreadAbort);
    }

    @Override
    public void completed(TaskBase task, BleResult result) {
        // check if completed matches pending task
        if (task != taskPending)
        {
            Log.w(TAG, "Unexpected task completed event");
            return;
        }

        // task completed
        taskPending = null;

        // trigger task completed event
        invokeCompleted(task, result);

        // set event to handle new task
        taskThreadWakeupEvent.release();

    }

    @Override
    public void dispose() {
        stopTaskThread();
    }

    /**
     * Wake up the task and set abort flag
     */
    private void stopTaskThread()
    {
        taskThreadAbort = true;
        if (taskThreadWakeupEvent != null)
        {
            taskThreadWakeupEvent.release();
        }
    }

    /**
     * Add a listener
     * @param listener The listener
     */
    void addListener(TaskControllerListener listener) {
        synchronized (listenerList) {
            listenerList.add(listener);
        }
    }

    /**
     * Remove a listener
     * @param listener The listener
     */
    void removeListener(TaskControllerListener listener) {
        synchronized (listenerList) {
            listenerList.remove(listener);
        }
    }

    /**
     * invoke the started callback
     * @param task The started task
     */
    private void invokeStarted(TaskBase task) {
        List<TaskControllerListener> listCopy;
        synchronized (listenerList) {
            listCopy = new LinkedList<>(listenerList);
        }
        try {
            for(TaskControllerListener listener : listCopy) {
                listener.TaskStarted(task);
            }
        }
        catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
    }

    /**
     * invoke the completed callback
     * @param task The completed task
     */
    private void invokeCompleted(TaskBase task, BleResult result) {
        List<TaskControllerListener> listCopy;
        synchronized (listenerList) {
            listCopy = new LinkedList<>(listenerList);
        }
        try {
            for(TaskControllerListener listener : listCopy) {
                listener.TaskCompleted(task, result);
            }
        }
        catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
    }
}
