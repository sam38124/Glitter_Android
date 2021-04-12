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
 * Class Name: CharacteristicImpl
 ******************************************************************************/

package com.onsemi.ble;

import android.bluetooth.BluetoothGattCharacteristic;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Semaphore;

import static android.bluetooth.BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT;
import static android.bluetooth.BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE;
import static com.onsemi.ble.StringHelper.toHex;

/**
 * The implementation of the Characteristic interface
 */

public class CharacteristicImpl implements Characteristic ,CharacteristicChangedListener {

    private static String TAG = "Characteristic";
    private BluetoothGattCharacteristic characteristic;
    private PeripheralImpl peripheral;
    private int property;
    private int permission;
    private final LinkedList<CharacteristicListener> listenerList;

    CharacteristicImpl(PeripheralImpl peripheral, BluetoothGattCharacteristic characteristic) {
        this.characteristic = characteristic;
        this.peripheral = peripheral;

        // set property
        property = characteristic.getProperties();
        permission = characteristic.getPermissions();

        // modify write type
        characteristic.setWriteType((property & CharacteristicProperties.WriteWithoutResponse) != 0 ? WRITE_TYPE_NO_RESPONSE : WRITE_TYPE_DEFAULT);
        listenerList = new LinkedList<>();
        peripheral.addListener(this);
    }

    BluetoothGattCharacteristic getGattCharacteristic() {
        return characteristic;
    }

    @Override
    public UUID getUuid() {
        return characteristic.getUuid();
    }

    @Override
    public byte[] getValue() {
        return characteristic.getValue();
    }

    @Override
    public int getPermission() {
        return permission;
    }

    @Override
    public int getProperty() {
        return property;
    }


    @Override
    public Peripheral getPeripheral() {
        return peripheral;
    }

    @Override
    public byte[] readData() throws BleException {
        // is the peripheral connected ?
        if (!peripheral.isConnected()) {
            Log.w(TAG, "Read from peripheral failed, not connected");
            throw ExceptionHelper.ResultToException(BleResult.NotConnected, "Read from peripheral failed, not connected");
        }

        // read supported by characteristic
        if ((property & CharacteristicProperties.Read) == 0) {
            Log.w(TAG, "Read from peripheral failed, not supported by characteristic");
            throw ExceptionHelper.ResultToException(BleResult.NotSupported, "Read from peripheral failed, not supported by characteristic");
        }

        final TaskCharacteristicRead taskRead =
                new TaskCharacteristicRead(this, BleConfiguration.getReadCharacteristicTimeout());
        final Semaphore semaphore = new Semaphore(0);
        final BleResult finalResult[] = new BleResult[1];
        TaskControllerListener listener = new TaskControllerListener() {
            @Override
            public void TaskStarted(TaskBase task) {
                if (task == taskRead) {
                    Log.i(TAG, "Read task started");
                }
            }

            @Override
            public void TaskCompleted(TaskBase task, BleResult result) {
                if (task == taskRead) {
                    finalResult[0] = result;
                    semaphore.release();
                }
            }
        };
        peripheral.getTaskController().addListener(listener);
        peripheral.getTaskController().enqueue(taskRead);
        try {
            semaphore.acquire();
        } catch (Exception ex) {
            Log.e(TAG, "Failed to wait for task completion: " + ex.getMessage());
        } finally {
            peripheral.getTaskController().removeListener(listener);
        }

        if (finalResult[0] != BleResult.Success) {
            throw ExceptionHelper.ResultToException(finalResult[0], "Unable to read characteristic data");
        }

        return characteristic.getValue();
    }

    @Override
    public void writeData(byte[] data) throws BleException {
                // is the peripheral connected ?
                if (!peripheral.isConnected()) {
                    Log.w(TAG, "Write to peripheral failed, not connected");
                    throw ExceptionHelper.ResultToException(BleResult.NotConnected, "Write from peripheral failed, not connected");
                }

                // read supported by characteristic
                if ((property & (CharacteristicProperties.WriteWithoutResponse | CharacteristicProperties.Write)) == 0) {
                    Log.w(TAG, "Write to peripheral failed, not supported by characteristic");
                    throw ExceptionHelper.ResultToException(BleResult.NotSupported, "Write from peripheral failed, not supported by characteristic");
                }

                final TaskCharacteristicWrite taskWrite =
                        new TaskCharacteristicWrite(this, BleConfiguration.getWriteCharacteristicTimeout(), data);
                final Semaphore semaphore = new Semaphore(0);
                final BleResult finalResult[] = new BleResult[1];
                TaskControllerListener listener = new TaskControllerListener() {
                    @Override
                    public void TaskStarted(TaskBase task) {
                        if (task == taskWrite) {
                            Log.i(TAG, "Write task started");
                        }
                    }

                    @Override
                    public void TaskCompleted(TaskBase task, BleResult result) {
                        if (task == taskWrite) {
                            finalResult[0] = result;
                            semaphore.release();
                        }
                    }
                };
                peripheral.getTaskController().addListener(listener);
                peripheral.getTaskController().enqueue(taskWrite);
                try {
                    semaphore.acquire();
                } catch (Exception ex) {
                    Log.e(TAG, "Failed to wait for task completion: " + ex.getMessage());
                } finally {
                    peripheral.getTaskController().removeListener(listener);
                }

                if (finalResult[0] != BleResult.Success) {
                    throw ExceptionHelper.ResultToException(finalResult[0], "Unable to write characteristic data");
                }
            }

    @Override
    public void changeNotification(final boolean enable) throws BleException {
        // is the peripheral connected ?
        if (!peripheral.isConnected()) {
            Log.w(TAG, "Change notification failed, not connected");
            throw ExceptionHelper.ResultToException(BleResult.NotConnected, "Change notification failed, not connected");
        }

        // read supported by characteristic
        if ((property & (CharacteristicProperties.Notify | CharacteristicProperties.NotifyEncryptionRequired)) == 0) {
            Log.w(TAG, "Change notification failed, not supported by characteristic");
            throw ExceptionHelper.ResultToException(BleResult.NotSupported, "Change notification failed, not supported by characteristic");
        }

        final TaskCharacteristicChangeNotification taskChangeNotification =
                new TaskCharacteristicChangeNotification(this, BleConfiguration.getChangeNotificationTimeout(), enable);
        final Semaphore semaphore = new Semaphore(0);
        final BleResult finalResult[] = new BleResult[1];
        TaskControllerListener listener = new TaskControllerListener() {
            @Override
            public void TaskStarted(TaskBase task) {
                if (task == taskChangeNotification) {
                    Log.i(TAG, "Change notification task started");
                }
            }

            @Override
            public void TaskCompleted(TaskBase task, BleResult result) {
                if (task == taskChangeNotification) {
                    finalResult[0] = result;
                    semaphore.release();
                }
            }
        };
        peripheral.getTaskController().addListener(listener);
        peripheral.getTaskController().enqueue(taskChangeNotification);
        try {
            semaphore.acquire();
        } catch (Exception ex) {
            Log.e(TAG, "Failed to wait for task completion: " + ex.getMessage());
        } finally {
            peripheral.getTaskController().removeListener(listener);
        }

        if (finalResult[0] != BleResult.Success) {
            throw ExceptionHelper.ResultToException(finalResult[0], "Unable to change notification");
        }
    }

    @Override
    public void changeIndication(final boolean enable) throws BleException {
        // is the peripheral connected ?
        if (!peripheral.isConnected()) {
            Log.w(TAG, "Change indication failed, not connected");
            throw ExceptionHelper.ResultToException(BleResult.NotConnected, "Change indication failed, not connected");
        }

        // read supported by characteristic
        if ((property & (CharacteristicProperties.Indicate | CharacteristicProperties.IndicateEncryptionRequired)) == 0) {
            Log.w(TAG, "Change indication failed, not supported by characteristic");
            throw ExceptionHelper.ResultToException(BleResult.NotSupported, "Change indication failed, not supported by characteristic");
        }

        final TaskCharacteristicChangeIndication taskChangeIndication =
                new TaskCharacteristicChangeIndication(this, BleConfiguration.getChangeNotificationTimeout(), enable);
        final Semaphore semaphore = new Semaphore(0);
        final BleResult finalResult[] = new BleResult[1];
        TaskControllerListener listener = new TaskControllerListener() {
            @Override
            public void TaskStarted(TaskBase task) {
                if (task == taskChangeIndication) {
                    Log.i(TAG, "Change notification task started");
                }
            }

            @Override
            public void TaskCompleted(TaskBase task, BleResult result) {
                if (task == taskChangeIndication) {
                    finalResult[0] = result;
                    semaphore.release();
                }
            }
        };
        peripheral.getTaskController().addListener(listener);
        peripheral.getTaskController().enqueue(taskChangeIndication);
        try {
            semaphore.acquire();
        } catch (Exception ex) {
            Log.e(TAG, "Failed to wait for task completion: " + ex.getMessage());
        } finally {
            peripheral.getTaskController().removeListener(listener);
        }

        if (finalResult[0] != BleResult.Success) {
            throw ExceptionHelper.ResultToException(finalResult[0], "Unable to change indication");
        }
    }

    @Override
    public void addListener(CharacteristicListener listener) {
        synchronized (listenerList) {
            listenerList.add(listener);
        }
    }

    @Override
    public void removeListener(CharacteristicListener listener) {
        synchronized (listenerList) {
            listenerList.remove(listener);
        }
    }

    @Override
    public void dispose() {
        peripheral.removeListener(this);
    }

    @Override
    public void onCharacteristicChanged(BluetoothGattCharacteristic characteristic, byte[] data) {
        // event characteristic matches our characteristic ?
        if(characteristic == this.characteristic)
        {
            if ((property & (CharacteristicProperties.Notify | CharacteristicProperties.NotifyEncryptionRequired)) != 0)
            {
                // received notification
                Log.i(TAG, String.format("Notification received (UUID:{%s} Value:{%s})", getUuid(), data != null ? toHex(data) : "-"));

                // trigger notify
                invokeNotificationReceived(this, data == null ? new byte[0] : data);
            }
            if ((property & (CharacteristicProperties.Indicate | CharacteristicProperties.IndicateEncryptionRequired)) != 0)
            {
                // received indication
                Log.i(TAG, String.format("Indication received (UUID:{0} Value:{1})", getUuid(), data != null ? toHex(data) : "-"));

                // trigger event
                invokeIndicationReceived(this, data == null ? new byte[0] : data);
            }
        }
    }

    /**
     * Invokes the onNotificationReceived callback
     * @param characteristic The characteristic
     * @param data  The data
     */
    void invokeNotificationReceived(Characteristic characteristic, byte[] data) {
        List<CharacteristicListener> listCopy;
        synchronized (listenerList) {
            listCopy = new LinkedList<>(listenerList);
        }
        try {
            for(CharacteristicListener listener : listCopy) {
                listener.onNotificationReceived(characteristic, data);
            }
        }
        catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
    }

    /**
     * Invokes the onIndicationReceived callback
     * @param characteristic The characteristic
     * @param data  The data
     */
    void invokeIndicationReceived(Characteristic characteristic, byte[] data) {
        List<CharacteristicListener> listCopy;
        synchronized (listenerList) {
            listCopy = new LinkedList<>(listenerList);
        }
        try {
            for(CharacteristicListener listener : listCopy) {
                listener.onIndicationReceived(characteristic, data);
            }
        }
        catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
    }


}
