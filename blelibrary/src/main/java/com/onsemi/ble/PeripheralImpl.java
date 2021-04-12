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
 * Class Name: PeripheralImpl
 ******************************************************************************/

package com.onsemi.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.le.ScanRecord;
import android.os.Build;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import static android.bluetooth.BluetoothGatt.CONNECTION_PRIORITY_BALANCED;
import static android.bluetooth.BluetoothGatt.CONNECTION_PRIORITY_HIGH;
import static android.bluetooth.BluetoothGatt.CONNECTION_PRIORITY_LOW_POWER;
import static android.bluetooth.BluetoothGatt.GATT_SUCCESS;
import static android.bluetooth.BluetoothProfile.STATE_CONNECTED;
import static android.bluetooth.BluetoothProfile.STATE_CONNECTING;
import static android.bluetooth.BluetoothProfile.STATE_DISCONNECTED;
import static android.bluetooth.BluetoothProfile.STATE_DISCONNECTING;

/**
 * The implementation of the Peripheral interface
 */

public abstract class PeripheralImpl extends BluetoothGattCallback implements Peripheral,  Disposable {

    private static String TAG = "Peripheral";
    private static short companyId = 0x064C;

    public Date getLastUpdate() {
        return lastUpdate;
    }

    private int maxWriteLength = 20;
    public int getMaxWriteLength() {return maxWriteLength; }

    private Date lastUpdate;
    private int rssi;
    private BluetoothDevice bluetoothDevice;
    private final List<PeripheralChangedListener> listenerList;
    private final LinkedList<PeripheralCallback> gattCallbackListener;
    private final LinkedList<CharacteristicChangedListener> characteristicChangedListener;
    private final List<Service> services;
    private TaskController taskController;
    private PeripheralManagerImpl manager;
    private BluetoothGatt bluetoothGatt;
    private ExecutorService executor;
    private int peripheralConnectionStatus;
    private Timer readRemoteRssiTimer;

    /**
     * The constructor
     * @param device The native Android BluetoothDevice
     * @param rssi The current rssi level
     * @param scanRecord The ScanRecord with the advertising data
     */
    public PeripheralImpl(BluetoothDevice device, int rssi, ScanRecord scanRecord) {
        peripheralConnectionStatus = STATE_DISCONNECTED;
        lastUpdate = new Date();
        bluetoothDevice = device;
        this.rssi = rssi;
        name = device.getName();
        if(scanRecord.getDeviceName() != null) {
            name = scanRecord.getDeviceName();
        }
        listenerList = new LinkedList<>();
        taskController = new TaskController();
        gattCallbackListener = new LinkedList<>();
        characteristicChangedListener = new LinkedList<>();
        executor = Executors.newSingleThreadExecutor();
        services = new LinkedList<>();
        manufacturerData = scanRecord.getManufacturerSpecificData(companyId);
    }

    /**
     * Returns the instance of the peripheral manger
     * @return The peripheral manager
     */
    public PeripheralManagerImpl getPeripheralManager() {
        return manager;
    }

    /**
     * Sets the peripheral manager
     * @param m The peripheral manager
     */
    protected void setPeripheralManager(PeripheralManagerImpl m) {
        manager = m;
    }

    /**
     * Returns the instance of the bluetooth gatt
     * @return The bluetooth gatt
     */
    BluetoothGatt getBluetoothGatt() {
        return bluetoothGatt;
    }

    /**
     * Sets the bluetooth gatt
     * @param gatt The bluetooth gatt
     */
    void setBluetoothGatt(BluetoothGatt gatt) {
        bluetoothGatt = gatt;
    }

    /**
     * Returns the instance of the task controller
     * @return The task controller
     */
    TaskController getTaskController() {
        return taskController;
    }

    private String name;
    @Override
    public String getName() {
        if(name == null || name.isEmpty()) {
            return "(null)";
        }
        return name;
    }

    /**
     * Sets the name of the peripheral
     * @param name The new name
     */
    private void setName(String name) {
        if(this.name.equals(name)) {
            return;
        }
        this.name = name;
        invokeNameChanged();
    }

    @Override
    public byte[] getManufacturerData() {
        return manufacturerData;
    }

    public void setManufacturerData(byte[] manufacturerData) {
        if(Arrays.equals(this.manufacturerData, manufacturerData)) {
            return;
        }
        this.manufacturerData = manufacturerData;
        invokeManufacturerDataChanged();
    }

    byte[] manufacturerData;

    @Override
    public int getRssi() {
        return rssi;
    }
    private void setRssi(int rssi) {
        if(this.rssi == rssi) {
            return;
        }
        this.rssi = rssi;
        invokeRssiChanged();
    }

    @Override
    public String getAddress() {
        return bluetoothDevice.getAddress();
    }

    /**
     * Returns the connection statie
     * @return true if connected, false otherwise
     */
    public boolean isConnected() {
        return peripheralConnectionStatus == STATE_CONNECTED;}

    /**
     * Returns the connection statie
     * @return true if disconnected, false otherwise
     */
    boolean isDisconnected() {
        return peripheralConnectionStatus == STATE_DISCONNECTED;}

    private PeripheralState state = PeripheralState.Idle;
    private Object stateLock = new Object();
    @Override
    public PeripheralState getState() {
        synchronized (stateLock) {
            return state;
        }
    }

    /**
     * Sets the peripheral state
     * @param state The new state
     */
    private void setState(PeripheralState state) {
        PeripheralState oldState;
        synchronized (stateLock) {
            if (this.state == state) {
                return;
            }
            oldState = this.state;
            this.state = state;
        }
        onStateChanged(oldState, this.state);
        invokeStateChanged(oldState, this.state);
    }

    /**
     * Updates the peripheral from scan record data
     * @param rssi          The current rssi level
     * @param scanRecord    The current scan record
     */
     void update(int rssi, ScanRecord scanRecord) {
        lastUpdate = new Date();
        setRssi(rssi);
         String tmpName = scanRecord.getDeviceName();
         if(tmpName != null) {
             setName(tmpName);
         }

         byte[] data = scanRecord.getManufacturerSpecificData(companyId);
         if(data == null) {
             return;
         }
         setManufacturerData(data);

    }

    /**
     * Write characteristic data
     * @param characteristic The characteristic to write
     * @return  True if success, false otherwise
     */
    boolean writeCharacteristic(BluetoothGattCharacteristic characteristic) {
        return bluetoothGatt.writeCharacteristic(characteristic);
    }

    @Override
    public void addListener(PeripheralChangedListener listener) {
        synchronized (listenerList) {
            listenerList.add(listener);
        }
    }

    @Override
    public void removeListener(PeripheralChangedListener listener) {
        synchronized (listenerList) {
            listenerList.remove(listener);
        }
    }

    @Override
    public Service findService(UUID uuid) {
        for(Service s : services) {
            if(s.getUuid().compareTo(uuid) == 0) {
                return s;
            }
        }
        return null;
    }

    @Override
    public Service findService(String uuid) {
        try {
            return findService(UuidHelper.ConvertTo128BitBluetoothUuid(uuid));
        }
        catch (Exception ex) {
            Log.e(TAG, "findService failed: " + ex.toString());
            return null;
        }
    }

    /**
     * Search the characteristic with the given uuid
     * @param uuid The characteristics uuid
     * @return The found characteristic or null
     */
    public Characteristic findCharacteristic(UUID uuid) {
        for(Service s : services) {
            Characteristic c = s.getCharacteristic(uuid);
            if(c != null) {
                return c;
            }
        }
        return null;
    }

    /**
     * Search the characteristic with the given uuid
     * @param uuid The characteristics uuid
     * @return The found characteristic or null
     */
    public Characteristic findCharacteristic(String uuid) {
        try {
            return findCharacteristic(UuidHelper.ConvertTo128BitBluetoothUuid(uuid));
        }
        catch (Exception ex) {
            Log.e(TAG, "findService failed: " + ex.toString());
            return null;
        }
    }

    @Override
    public void establish() throws BleException {
        BleException establishException = null;

        if(manager != null && BleConfiguration.isSupendScanWhileEstablish()) {
            manager.suspendScanning(this);
        }

        try {
            setState(PeripheralState.EstablishLink);
            connect();
            setState(PeripheralState.DiscoveringServices);
            discoverServices();
            setState(PeripheralState.Initialize);
            initialize(10000);
            setState(PeripheralState.Ready);
            startReadRemoteRssiTimer();
        }
        catch (BleException ex) {
            establishException = ex;
        }
        catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
            establishException = new BleException(ex.getMessage(), BleResult.Failure);
        }
        finally {
            if(manager != null && BleConfiguration.isSupendScanWhileEstablish()) {
                manager.resumeScanning(this);
            }
        }

        if(getState() != PeripheralState.Ready || establishException != null || !isConnected()) {
            synchronized (stateLock) {
                if (getState() != PeripheralState.Idle) {
                    setState(PeripheralState.TearDownLink);
                }
            }

            try {
                if(!isDisconnected()) {
                    disconnect();
                }
                else {
                    setState(PeripheralState.Idle);
                }
            }
            catch (Exception ex) {
                setState(PeripheralState.Idle);
            }
        }

        if(establishException != null) {
            throw  establishException;
        }
    }

    @Override
    public void update(UpdateController controller, UpdateOptions options) throws BleException {
        try {
            controller.update(this, options);
        }
        catch (Exception e) {
            throw new BleException(e.getMessage(), BleResult.Failure);
        }
    }

    @Override
    public void setConnectionPriorityHigh() {
        boolean success = getBluetoothGatt().requestConnectionPriority(CONNECTION_PRIORITY_HIGH);
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setConnectionPriorityBalanced() {
        boolean success = getBluetoothGatt().requestConnectionPriority(CONNECTION_PRIORITY_BALANCED);
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setConnectionPriorityLowPower() {
        boolean success = getBluetoothGatt().requestConnectionPriority(CONNECTION_PRIORITY_LOW_POWER);
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void requestMaxWriteLength(int length) throws BleException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final TaskBase taskRequestMtu = new TaskRequestMtu(this, length + 3, BleConfiguration.getRequestMtuTimeout());
            final Semaphore semaphore = new Semaphore(0);
            final BleResult finalResult[] = new BleResult[1];
            TaskControllerListener listener = new TaskControllerListener() {
                @Override
                public void TaskStarted(TaskBase task) {
                    if(task == taskRequestMtu) {
                        Log.i(TAG, "Request MTU task started");
                    }
                }

                @Override
                public void TaskCompleted(TaskBase task, BleResult result) {
                    if(task == taskRequestMtu) {
                        finalResult[0] = result;
                        semaphore.release();
                    }
                }
            };
            taskController.addListener(listener);
            taskController.enqueue(taskRequestMtu);
            try {
                semaphore.acquire();
            }
            catch(Exception ex) {
                Log.e(TAG, "Failed to wait for task completion: " + ex.getMessage());
            }
            finally {
                taskController.removeListener(listener);
            }

            if(finalResult[0] != BleResult.Success) {
                throw ExceptionHelper.ResultToException(finalResult[0], "Unable to request MTU");
            }
        }
    }

    @Override
    public void set2MbPhy() throws BleException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final TaskBase taskSet2MbPhy = new TaskSet2MbPhy(this, BleConfiguration.getRequestMtuTimeout());
            final Semaphore semaphore = new Semaphore(0);
            final BleResult finalResult[] = new BleResult[1];
            TaskControllerListener listener = new TaskControllerListener() {
                @Override
                public void TaskStarted(TaskBase task) {
                    if(task == taskSet2MbPhy) {
                        Log.i(TAG, "Set 2 MBit Phy task started");
                    }
                }

                @Override
                public void TaskCompleted(TaskBase task, BleResult result) {
                    if(task == taskSet2MbPhy) {
                        finalResult[0] = result;
                        semaphore.release();
                    }
                }
            };
            taskController.addListener(listener);
            taskController.enqueue(taskSet2MbPhy);
            try {
                semaphore.acquire();
            }
            catch(Exception ex) {
                Log.e(TAG, "Failed to wait for task completion: " + ex.getMessage());
            }
            finally {
                taskController.removeListener(listener);
            }

            if(finalResult[0] != BleResult.Success) {
                throw ExceptionHelper.ResultToException(finalResult[0], "Unable to set 2 Mbit Phy");
            }
        }
    }

    @Override
    public void teardown() throws Exception {
        synchronized (stateLock) {
            if (getState() == PeripheralState.Idle) {
                return;
            }
            setState(PeripheralState.TearDownLink);
        }
        disconnect();
    }

    @Override
    public String toString() {
        return getName() + " [" + getAddress() + "] " ;
    }

    @Override
    public void dispose() {
        synchronized (listenerList) {
            listenerList.clear();
        }
    }

    /**
     * Invokes the onRssiChanged callback
     */
    private void invokeRssiChanged() {
        List<PeripheralChangedListener> listCopy;
        synchronized (listenerList) {
            listCopy = new LinkedList<>(listenerList);
        }
        try {
            for(PeripheralChangedListener listener : listCopy) {
                listener.onRssiChanged(rssi);
            }
        }
        catch (Exception ex) {
            Log.e(TAG, "invokeRssiChanged failed: " + ex.getMessage());
        }
    }

    /**
     * Invokes the onNameChanged callback
     */
    private void invokeNameChanged() {
        List<PeripheralChangedListener> listCopy;
        synchronized (listenerList) {
            listCopy = new LinkedList<>(listenerList);
        }
        try {
            for(PeripheralChangedListener listener : listCopy) {
                listener.onNameChanged(getName());
            }
        }
        catch (Exception ex) {
            Log.e(TAG, "invokeNameChanged: " + ex.getMessage());
        }
    }

    /**
     * Invokes the onNameChanged callback
     */
    private void invokeManufacturerDataChanged() {
        List<PeripheralChangedListener> listCopy;
        synchronized (listenerList) {
            listCopy = new LinkedList<>(listenerList);
        }
        try {
            for(PeripheralChangedListener listener : listCopy) {
                listener.onManufacturerDataChanged(getManufacturerData());
            }
        }
        catch (Exception ex) {
            Log.e(TAG, "invokeManufacturerDataChanged: " + ex.getMessage());
        }
    }

    /**
     * Invoked when the peripheral state changed
     * @param oldState The old peripheral state
     * @param newState The new peripheral state
     */
    protected void onStateChanged(PeripheralState oldState, PeripheralState newState) {}

    /**
     * Invokes the onStateChanged callback
     */
    private void invokeStateChanged(PeripheralState oldState, PeripheralState newState) {
        List<PeripheralChangedListener> listCopy;
        synchronized (listenerList) {
            listCopy = new LinkedList<>(listenerList);
        }
        try {
            for(PeripheralChangedListener listener : listCopy) {
                listener.onStateChanged(oldState, newState);
            }
        }
        catch (Exception ex) {
            Log.e(TAG, "invokeStateChanged failed: " + ex.getMessage());
        }
    }

    /**
     * Invokes the onDisconnected callback
     */
    private void invokeDisconnected(boolean fromHost) {
        List<PeripheralChangedListener> listCopy;
        synchronized (listenerList) {
            listCopy = new LinkedList<>(listenerList);
        }
        try {
            for(PeripheralChangedListener listener : listCopy) {
                listener.onDisconnected(fromHost);
            }
        }
        catch (Exception ex) {
            Log.e(TAG, "invokeDisconnected failed: " + ex.getMessage());
        }
    }
    /**
     * Adds a CharacteristicChangedListener
     * @param listener The listener
     */
    void addListener(CharacteristicChangedListener listener) {
        synchronized (characteristicChangedListener) {
            characteristicChangedListener.add(listener);
        }
    }

    /**
     * Removes the CharacteristicChangedListener
     * @param listener The listener
     */
    void removeListener(CharacteristicChangedListener listener) {
        synchronized (characteristicChangedListener) {
            characteristicChangedListener.remove(listener);
        }
    }

    /**
     * Invokes the onCharacteristicChanged callback
     */
    private void invokeCharacteristicChanged(BluetoothGattCharacteristic characteristic, byte[] data) {
        List<CharacteristicChangedListener> listCopy;
        synchronized (characteristicChangedListener) {
            listCopy = new LinkedList<>(characteristicChangedListener);
        }
        try {
            for(CharacteristicChangedListener listener : listCopy) {
                listener.onCharacteristicChanged(characteristic, data);
            }
        }
        catch(Exception ex) {
            Log.e(TAG, "invokeCharacteristicChanged failed: " + ex.getMessage());
        }
    }

    /**
     * Adds the BluetoothGattCallback
     * @param listener The listener
     */
    void addListener(PeripheralCallback listener) {
        synchronized (gattCallbackListener) {
            gattCallbackListener.add(listener);
        }
    }

    /**
     * Removes the BluetoothGattCallback
     * @param listener The listener
     */
    void removeListener(PeripheralCallback listener) {
        synchronized (gattCallbackListener) {
            gattCallbackListener.remove(listener);
        }
    }

    /**
     * Invokes the onConnectionStateChange callback
     */
    private void invokeStateChanged(BluetoothGatt gatt, int status, int newSate) {
        List<PeripheralCallback> listCopy;
        synchronized (listenerList) {
            listCopy = new LinkedList<>(gattCallbackListener);
        }
        try {
            for(PeripheralCallback listener : listCopy) {
                listener.onConnectionStateChange(gatt, status, newSate);
            }
        }
        catch(Exception ex) {
            Log.e(TAG, "invokeStateChanged failed: " + ex.getMessage());
        }
    }

    /**
     * Invokes the onServicesDiscovered callback
     */
    private void invokeServicesDiscovered(BluetoothGatt gatt, int status) {
        List<PeripheralCallback> listCopy;
        synchronized (listenerList) {
            listCopy = new LinkedList<>(gattCallbackListener);
        }
        try {
            for(PeripheralCallback listener : listCopy) {
                listener.onServicesDiscovered(gatt, status);
            }
        }
        catch(Exception ex) {
            Log.e(TAG, "invokeServicesDiscovered failed: " + ex.getMessage());
        }
    }

    /**
     * Invokes the onCharacteristicWrite callback
     */
    private void invokeDataWritten(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        List<PeripheralCallback> listCopy;
        synchronized (gattCallbackListener) {
            listCopy = new LinkedList<>(gattCallbackListener);
        }
        try {
            for(PeripheralCallback listener : listCopy) {
                listener.onCharacteristicWrite(gatt, characteristic, status);
            }
        }
        catch(Exception ex) {
            Log.e(TAG, "invokeDataWritten failed: " + ex.getMessage());
        }
    }

    /**
     * Invokes the onCharacteristicRead callback
     */
    private void invokeDataRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, byte[] data, int status) {
        List<PeripheralCallback> listCopy;
        synchronized (gattCallbackListener) {
            listCopy = new LinkedList<>(gattCallbackListener);
        }
        try {
            for(PeripheralCallback listener : listCopy) {
                listener.onCharacteristicRead(gatt, characteristic, data, status);
            }
        }
        catch(Exception ex) {
            Log.e(TAG, "invokeDataRead failed: " + ex.getMessage());
        }
    }

    /**
     * Invokes the onDescriptorRead callback
     */
    private void invokeDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        List<PeripheralCallback> listCopy;
        synchronized (gattCallbackListener) {
            listCopy = new LinkedList<>(gattCallbackListener);
        }
        try {
            for(PeripheralCallback listener : listCopy) {
                listener.onDescriptorRead(gatt, descriptor, status);
            }
        }
        catch(Exception ex) {
            Log.e(TAG, "invokeDescriptorRead failed: " + ex.getMessage());
        }
    }

    /**
     * Invokes the onDescriptorWrite callback
     */
    private void invokeDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        List<PeripheralCallback> listCopy;
        synchronized (gattCallbackListener) {
            listCopy = new LinkedList<>(gattCallbackListener);
        }
        try {
            for(PeripheralCallback listener : listCopy) {
                listener.onDescriptorWrite(gatt, descriptor, status);
            }
        }
        catch(Exception ex) {
            Log.e(TAG, "invokeDescriptorWrite failed: " + ex.getMessage());
        }
    }

    /**
     * Invokes the onReadRemoteRssi callback
     */
    private void invokeReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
        List<PeripheralCallback> listCopy;
        synchronized (gattCallbackListener) {
            listCopy = new LinkedList<>(gattCallbackListener);
        }
        try {
            for(PeripheralCallback listener : listCopy) {
                listener.onReadRemoteRssi(gatt, rssi, status);
            }
        }
        catch(Exception ex) {
            Log.e(TAG, "invokeReadRemoteRssi failed: " + ex.getMessage());
        }
    }

    /**
     * Invokes the onMtuChanged callback
     */
    private void invokeMtuChanged(int mtu, int status) {
        List<PeripheralCallback> listCopy;
        synchronized (gattCallbackListener) {
            listCopy = new LinkedList<>(gattCallbackListener);
        }
        try {
            for(PeripheralCallback listener : listCopy) {
                listener.onMtuChanged(mtu, status);
            }
        }
        catch(Exception ex) {
            Log.e(TAG, "onMtuChanged failed: " + ex.getMessage());
        }
    }

    /**
     * Invokes the onMtuChanged callback
     */
    private void invokePhyChanged(int txPhy, int rxPhy, int status) {
        List<PeripheralCallback> listCopy;
        synchronized (gattCallbackListener) {
            listCopy = new LinkedList<>(gattCallbackListener);
        }
        try {
            for(PeripheralCallback listener : listCopy) {
                listener.onPhyUpdate(txPhy, rxPhy, status);
            }
        }
        catch(Exception ex) {
            Log.e(TAG, "onPhyUpdate failed: " + ex.getMessage());
        }
    }



    /**
     * Connects to a peripheral. This method call is blocking.
     * @throws Exception If an exception occurs
     */
    public void connect() throws BleException {
        final TaskBase taskConnect = new TaskConnect(this, BleConfiguration.getConnectTimeout());
        final Semaphore semaphore = new Semaphore(0);
        final BleResult finalResult[] = new BleResult[1];
        TaskControllerListener listener = new TaskControllerListener() {
            @Override
            public void TaskStarted(TaskBase task) {
                if(task == taskConnect) {
                    Log.i(TAG, "Connect task started");
                }
            }

            @Override
            public void TaskCompleted(TaskBase task, BleResult result) {
                if(task == taskConnect) {
                    finalResult[0] = result;
                    semaphore.release();
                }
            }
        };
        taskController.addListener(listener);
        taskController.enqueue(taskConnect);
        try {
            semaphore.acquire();
        }
        catch(Exception ex) {
            Log.e(TAG, "Failed to wait for task completion: " + ex.getMessage());
        }
        finally {
            taskController.removeListener(listener);
        }

        if(finalResult[0] != BleResult.Success) {
            throw ExceptionHelper.ResultToException(finalResult[0], "Unable to connect peripheral");
        }
    }

    /**
     * Discovers the services of a connected peripheral. This method call is blocking.
     * @throws Exception If an exception occurs
     */
    public void discoverServices() throws BleException {
        final TaskDiscoverServices taskDiscoverServices =
                new TaskDiscoverServices(this, BleConfiguration.getDiscoverServicesTimeout());
        final Semaphore semaphore = new Semaphore(0);
        final BleResult finalResult[] = new BleResult[1];
        TaskControllerListener listener = new TaskControllerListener() {
            @Override
            public void TaskStarted(TaskBase task) {
                if(task == taskDiscoverServices) {
                    Log.i(TAG, "Connect task started");
                }
            }

            @Override
            public void TaskCompleted(TaskBase task, BleResult result) {
                if(task == taskDiscoverServices) {
                    finalResult[0] = result;
                    semaphore.release();
                }
            }
        };
        taskController.addListener(listener);
        taskController.enqueue(taskDiscoverServices);
        try {
            semaphore.acquire();
            synchronized (services) {
                services.clear();
                services.addAll(taskDiscoverServices.getServices());
            }
        }
        catch(Exception ex) {
            Log.e(TAG, "Failed to wait for task completion: " + ex.getMessage());
        }
        finally {
            taskController.removeListener(listener);
        }

        if(finalResult[0] != BleResult.Success) {
            throw ExceptionHelper.ResultToException(finalResult[0], "Unable to discover services");
        }
    }

    /**
     * Initializes a connected peripheral.
     * This is a good place to look for characteristics and do initial reads.
     * This method call is blocking.
     * @throws Exception If an exception occurs
     */
    protected abstract void initialize(int timeout) throws BleException;

    /**
     * Disconnects a connected peripheral. This method call is blocking.
     * @throws Exception If an exception occurs
     */
    public void disconnect() throws BleException {
        final TaskBase taskDisconnect = new TaskDisconnect(this, BleConfiguration.getDisconnectTimeout());
        final Semaphore semaphore = new Semaphore(0);
        final BleResult finalResult[] = new BleResult[] {BleResult.Failure};
        TaskControllerListener listener = new TaskControllerListener() {
            @Override
            public void TaskStarted(TaskBase task) {
                if(task == taskDisconnect) {
                    Log.i(TAG, "Disconnect task started");
                }
            }

            @Override
            public void TaskCompleted(TaskBase task, BleResult result) {
                if(task == taskDisconnect) {
                    finalResult[0] = result;
                    semaphore.release();
                }
            }
        };
        taskController.addListener(listener);
        taskController.enqueue(taskDisconnect);
        try {
            semaphore.acquire();
        }
        catch(Exception ex) {
            Log.e(TAG, "Failed to wait for task completion: " + ex.getMessage());
        }
        finally {
            taskController.removeListener(listener);
        }

        if(finalResult[0] != BleResult.Success) {
            throw ExceptionHelper.ResultToException(finalResult[0], "Unable to disconnect peripheral");
        }
    }

    /* BluetoothGattCallback implementation */
    @Override
    public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
        peripheralConnectionStatus = newState;
        executor.execute(new Runnable() {
            @Override
            public void run() {
                // Log
                Log.d(TAG, "Connection changed to " + newState + " with status " + status);
                invokeStateChanged(gatt, status, newState);

                // handle callback event
                switch (newState)
                {
                    case STATE_CONNECTED:
                        break;
                    case STATE_CONNECTING:
                        break;

                    case STATE_DISCONNECTING:
                        setState(PeripheralState.TearDownLink);
                        break;
                    case STATE_DISCONNECTED:
                        // state
                        PeripheralState disconnectedState = getState();

                        ResetPeripheralToDisconnected(false);

                        // trigger disconnected event if not active disconnecting
                        invokeDisconnected(disconnectedState != PeripheralState.TearDownLink ? true : false);
                        break;
                }
            }
        });
    }

    @Override
    public void onServicesDiscovered(final BluetoothGatt gatt, final int status) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Services discovered with status " + status);
                invokeServicesDiscovered(gatt, status);
            }
        });
    }

    @Override
    public void onCharacteristicRead(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, final int status) {
        // make copy of characteristics data because it can change while processing
        final byte[] data;
        byte[] original = characteristic.getValue();
        int length = 0;
        if(original != null) {
            length = original.length;
            data = new byte[length];
            System.arraycopy(original, 0, data, 0, length);
        }
        else {
            data = new byte[0];
        }

        executor.execute(new Runnable() {
            @Override
            public void run() {
                //Log.d(TAG, "Characteristic read with status " + status);
                invokeDataRead(gatt, characteristic, data, status);
            }
        });
    }

    @Override
    public void onCharacteristicWrite(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, final int status) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                //Log.d(TAG, "Characteristic written with status " + status);
                invokeDataWritten(gatt, characteristic, status);
            }
        });
    }

    @Override
    public void onCharacteristicChanged(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
        // make copy of characteristics data because it can change while processing
        byte[] original = characteristic.getValue();
        //Log.d(TAG, "Characteristic changed " + StringHelper.toHex(original));
        final byte[] data = new byte[original.length];
        System.arraycopy(original, 0, data, 0, original.length);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                invokeCharacteristicChanged(characteristic, data);
            }
        });
    }

    @Override
    public void onDescriptorRead(final BluetoothGatt gatt, final BluetoothGattDescriptor descriptor, final int status) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Descriptor read with status " + status);
                invokeDescriptorRead(gatt, descriptor, status);
            }
        });
    }

    @Override
    public void onDescriptorWrite(final BluetoothGatt gatt, final BluetoothGattDescriptor descriptor, final int status) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Descriptor write with status " + status);
                invokeDescriptorWrite(gatt, descriptor, status);
            }
        });
    }

    @Override
    public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
    }

    @Override
    public void onReadRemoteRssi(final BluetoothGatt gatt, final int rssi, final int status) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                invokeReadRemoteRssi(gatt, rssi, status);
                if(status == GATT_SUCCESS) {
                    setRssi(rssi);
                }
            }
        });
    }

    @Override
    public void onMtuChanged(BluetoothGatt gatt, final int mtu, final int status) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                if(status == GATT_SUCCESS) {
                    maxWriteLength = mtu - 3;
                }
                invokeMtuChanged(mtu, status);
            }
        });
    }

    @Override
    public void onPhyUpdate(BluetoothGatt gatt, final int txPhy, final int rxPhy, final int status) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                invokePhyChanged(txPhy, rxPhy, status);
            }
        });
    }

    /**
     * Disconnects a peripheral
     * @param forceDisconnection Call disconnect even when the peripheral is not connected
     */
    private void ResetPeripheralToDisconnected(boolean forceDisconnection)
    {
        // disconnect only if not already disconnected. Otherwise it can lead to strange behavior on some devices
        // force disconnection to terminate a pending connection
        if (bluetoothGatt != null)
        {
            if (forceDisconnection)
            {
                bluetoothGatt.disconnect();
            }

            try
            {
                bluetoothGatt.close();
            }
            catch (Exception ex)
            {
                Log.w(TAG, "Failed to close BluetoothGatt: " + ex.getMessage());
            }
            finally
            {
                bluetoothGatt = null;
            }
        }

        // remove services
        //ServiceListClear();
        maxWriteLength = 20;

        // set disconnected State
        setState(PeripheralState.Idle);
    }

    private void runRemoteRssiUpdate() {
        taskController.enqueue(new TaskReadRemoteRssi(this, 500));
    }

    synchronized private void startReadRemoteRssiTimer() {
        if (BleConfiguration.getRssiUpdateInterval() != 0) {
            if(readRemoteRssiTimer != null) {
                readRemoteRssiTimer.cancel();
                readRemoteRssiTimer = null;
            }
            readRemoteRssiTimer = new Timer();
            readRemoteRssiTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    runRemoteRssiUpdate();
                }
            }, 100, BleConfiguration.getRssiUpdateInterval());

        }
    }

    synchronized private void stopReadRemoteRssiTimer() {
        if(readRemoteRssiTimer != null) {
            readRemoteRssiTimer.cancel();
        }
    }
}
