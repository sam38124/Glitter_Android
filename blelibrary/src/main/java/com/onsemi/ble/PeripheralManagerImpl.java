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
 * Class Name: PeripheralManagerImpl
 ******************************************************************************/

package com.onsemi.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.ParcelUuid;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static android.bluetooth.le.ScanSettings.CALLBACK_TYPE_ALL_MATCHES;
import static android.bluetooth.le.ScanSettings.MATCH_MODE_AGGRESSIVE;
import static android.bluetooth.le.ScanSettings.MATCH_NUM_MAX_ADVERTISEMENT;

/**
 * The implementation of the PeripheralManager interface
 */

public class PeripheralManagerImpl<TPeripheral> extends ScanCallback implements PeripheralManager<TPeripheral> {


    private Hashtable<String, TPeripheral> periperhals;
    private static final String TAG = "PeripheralManager";
    private Context context;
    private boolean scanEnabled;
    private boolean scanStarted;
    private TaskController taskController;
    private BluetoothAdapter bluetoothAdapter;
    private List<ScanFilter> scanFilterArray;
    private List<Object> scanSuspendPeripheralList;
    private CreatePeripheralFunction<TPeripheral> createFunction;
    private BlockingQueue<ScanResult> scanDataQueue;
    private Thread peripheralUpdateThread;
    private boolean exit;
    private Date lastCheckForRemove;
    private final LinkedList<PeripheralManagerListener> listenerList;

    private String[] uuidFilterArray;

    /**
     * The constructor
     * @param context The current application context
     * @param createPeripheralFunc A function to create a concrete peripheral
     */
    protected PeripheralManagerImpl(Context context, CreatePeripheralFunction<TPeripheral> createPeripheralFunc) {
        this.context = context;
        scanEnabled = false;
        scanStarted = false;
        createFunction = createPeripheralFunc;
        periperhals = new Hashtable<>();
        scanSuspendPeripheralList = new ArrayList<>();
        listenerList = new LinkedList<>();
        scanDataQueue = new ArrayBlockingQueue<>(1000);
        taskController = new TaskController();
        exit = false;
        lastCheckForRemove = new Date();
        try {
            // try to get the adapter by the proposed method for Android >= jelly bean MR2
            // see https://jira.arendi.ch/browse/ABL-172
            BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            bluetoothAdapter = bluetoothManager.getAdapter();
        } catch (Exception ex) {
            Log.w(TAG, String.format("Failed to get the bluetooth adapter by context access %s", ex));
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }

        // update bluetooth enabled/disabled properties.
        onBluetoothStateChanged(bluetoothAdapter.getState());
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED))
                {
                    int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                    onBluetoothStateChanged(state);
                }
            }
        }, filter);

        peripheralUpdateThread = new Thread(new Runnable() {
            @Override
            public void run() {
                updatePeripheralTask();
            }
        });
        peripheralUpdateThread.start();
    }

    /**
     * Returns the current application context
     * @return The context
     */
    Context getContext() {
        return context;
    }

    /**
     * Returns the bluetooth adapter
     * @return The bluetooth adapter
     */
    BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }

    /**
     * Returns the uuid filter array
     * @return The uuid filter array
     */
    public String[] getUuidFilterArray() {
        return uuidFilterArray;
    }

    /**
     * Only peripherals advertising service uuids included in this array are found and included into
     * list of peripherals.
     * @param uuidFilterArray The uuid filter array
     */
    public void setUuidFilterArray(String[] uuidFilterArray) {
        this.uuidFilterArray = uuidFilterArray;
    }

    @Override
    public List<TPeripheral> peripherals() {
        return new ArrayList<>(periperhals.values());
    }

    @Override
    public boolean isScanStarted() {
        return scanEnabled;
    }

    @Override
    public void startScan() {
        if (!bluetoothAdapter.isEnabled()) {
            Log.i(TAG, "StartScan: Bluetooth is not enabled");
            return;
        }

        synchronized (this) {
            if (scanEnabled) {
                Log.i(TAG, "Scan is already enabled");
                return;
            }

            scanEnabled = true;
            if (uuidFilterArray != null && uuidFilterArray.length > 0) {
                scanFilterArray = new ArrayList<>();
                for (String u : uuidFilterArray) {

                    ScanFilter.Builder filterBuilder = new ScanFilter.Builder();
                    UUID id;
                    try {
                        id = UuidHelper.ConvertTo128BitBluetoothUuid(u);
                        filterBuilder.setServiceUuid(new ParcelUuid(id));
                        scanFilterArray.add(filterBuilder.build());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                scanFilterArray = null;
            }
            updateScanning();
        }
    }

    @Override
    public void stopScan() {
        synchronized (this) {
            if (!scanEnabled) {
                Log.i(TAG, "Scan is already disabled");
                return;
            }

            scanEnabled = false;
            updateScanning();
        }
        Log.i(TAG, "Scan for peripheral stopped");
    }

    @Override
    public void clearPeripherals() {
        List<TPeripheral> toRemove = new LinkedList<>();
        synchronized (periperhals) {
            for (TPeripheral p : periperhals.values() ) {
                if(!canRemove(p))
                {
                    continue;
                }
                toRemove.add(p);
            }

            for (TPeripheral p : toRemove) {
                periperhals.remove(((Peripheral)p).getAddress());
                PeripheralImpl pi = (PeripheralImpl) p;
                if(pi != null) {
                    pi.dispose();
                }
                Log.i(TAG, "Peripheral  " + p.toString() + " removed");
            }
        }
        if(toRemove.size() > 0) {
            invokePeripheralsChanged();
        }
    }

    /**
     * Evaluates if scanning is needed or not
     */
    private void updateScanning() {
        // evaluate if scan should be started
        boolean expectedScanStarted = (scanEnabled && (scanSuspendPeripheralList.size() == 0 && bluetoothAdapter.isEnabled()));

        // already in right state
        if (scanStarted == expectedScanStarted) {
            // nothing to do
            return;
        }

        // change state
        if (expectedScanStarted) {
            // throw an exception when the use of this library is not authorized
            // start scanning
            scanStarted = true;
            // update scan settings every time a scan is started. Otherwise it is not possible to switch between foreground and background scanning with different settings.
            ScanSettings scanSettings = buildScanSettings();
            taskController.enqueue(new TaskStartScan(bluetoothAdapter.getBluetoothLeScanner(), scanFilterArray, scanSettings, this, 500));
        } else {
            // stop scanning
            Log.i(TAG, "StopScanTarget");
            scanStarted = false;
            taskController.enqueue(new TaskStopScan(bluetoothAdapter.getBluetoothLeScanner(), this, 500));
        }
    }

    /**
     * Builds the settings use for ble scan
     * @return The scan settings
     */
    private ScanSettings buildScanSettings() {
        ScanSettings.Builder settingsBuilder = new ScanSettings.Builder();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            settingsBuilder.setCallbackType(CALLBACK_TYPE_ALL_MATCHES);
            settingsBuilder.setMatchMode(MATCH_MODE_AGGRESSIVE);
            settingsBuilder.setNumOfMatches(MATCH_NUM_MAX_ADVERTISEMENT);
        }

        settingsBuilder.setScanMode(BleConfiguration.getScanMode());
        settingsBuilder.setReportDelay(0);
        return (settingsBuilder.build());
    }

    /**
     * Suspends the scanning for peripherals
     * @param peripheral the peripheral
     */
    void suspendScanning(Object peripheral) {
        synchronized (this) {
            scanSuspendPeripheralList.add(peripheral);
            updateScanning();
        }
    }


    /**
     * Resumes the scanning for peripherals
     * @param peripheral the peripheral
     */
    void resumeScanning(Object peripheral) {
        synchronized (this) {
            scanSuspendPeripheralList.remove(peripheral);
            updateScanning();
        }

    }

    @Override
    public void onScanResult(int callbackType, ScanResult result) {
        if(scanDataQueue.remainingCapacity() > 0){
            scanDataQueue.add(result);
        }
    }

    @Override
    public void onBatchScanResults(List<ScanResult> results) {
        for (ScanResult result :  results) {
            scanDataQueue.add(result);
        }
    }

    @Override
    public void onScanFailed(int errorCode) {
        Log.e(TAG, "Scan failed with code " + errorCode);
    }

    private void updatePeripheralsList(ScanResult result)
    {
        boolean peripheralsChanged = false;
        PeripheralImpl pImpl = null;
        synchronized (periperhals) {
            TPeripheral peripheral = periperhals.get(result.getDevice().getAddress());
            if(peripheral == null)
            {
                if(createFunction == null)
                {
                    Log.e(TAG, "create peripheral function is null!");
                    return;
                }
                TPeripheral p = createFunction.create(result.getDevice(), result.getRssi(), result.getScanRecord());
                pImpl = (PeripheralImpl)p;
                pImpl.setPeripheralManager(this);
                periperhals.put(pImpl.getAddress(), p);
                Log.i(TAG, "New peripheral found " + p.toString());
                peripheralsChanged = true;
            }
            else
            {
                pImpl = (PeripheralImpl) peripheral;
                if(pImpl == null) {
                    return;
                }
                pImpl.update(result.getRssi(), result.getScanRecord());
            }
        }

        invokePeripheralDiscovered(pImpl);

        if(peripheralsChanged) {
            invokePeripheralsChanged();
        }
    }

    /**
     * Removed invisible peripherals from the peripherals list
     */
    private void removeInvisiblePeripherals() {

        if(BleConfiguration.getPeripheralInvisibleTimeout() == 0) {
            return;
        }

        // check only all 500ms
        if(lastCheckForRemove.after(new Date(System.currentTimeMillis() - 500)))
        {
            return;
        }
        lastCheckForRemove = new Date();

        List<TPeripheral> toRemove = new LinkedList<>();
        synchronized (periperhals) {
            for (TPeripheral p : periperhals.values() ) {
                if(!canRemove(p))
                {
                    continue;
                }
                if(((PeripheralImpl)p).getLastUpdate().before(new Date(System.currentTimeMillis() -
                        BleConfiguration.getPeripheralInvisibleTimeout())))
                {
                    toRemove.add(p);
                }
            }

            for (TPeripheral p : toRemove) {
                periperhals.remove(((Peripheral)p).getAddress());
                PeripheralImpl pi = (PeripheralImpl) p;
                if(pi != null) {
                    pi.dispose();
                }
                Log.i(TAG, "Peripheral  " + p.toString() + " removed");
            }
        }
        if(toRemove.size() > 0) {
            invokePeripheralsChanged();
        }
    }


    /**
     * The execution method for updating the peripherals list
     */
    private void updatePeripheralTask()
    {
        ScanResult scanResult;
        while(!exit)
        {
            try {
                removeInvisiblePeripherals();
                scanResult = scanDataQueue.poll(1000, TimeUnit.MILLISECONDS);
                if(scanResult == null) {
                    continue;
                }
                //Log.i(TAG, "Scan result  " + scanResult.toString());
                updatePeripheralsList(scanResult);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public boolean canRemove(TPeripheral p)
    {
        return true;
    }

    @Override
    public void addListener(PeripheralManagerListener listener) {
        synchronized (listenerList) {
            listenerList.add(listener);
        }
    }

    @Override
    public void removeListener(PeripheralManagerListener listener) {
        synchronized (listenerList) {
            listenerList.remove(listener);
        }
    }

    private boolean isBluetoothEnabled;
    @Override
    public boolean isBluetoothEnabled() {
        return isBluetoothEnabled;
    }

    private void setIsBluetoothEnabled(boolean enabled) {
        if(isBluetoothEnabled == enabled)
        {
            return;
        }
        isBluetoothEnabled = enabled;
        if(isBluetoothEnabled) {
            invokeBluetoothEnabled();
        }
    }

    private boolean isBluetoothDisabled;
    @Override
    public boolean isBluetoothDisabled() {
        return isBluetoothDisabled;
    }

    private void setIsBluetoothDisabled(boolean disabled) {
        if(isBluetoothDisabled == disabled)
        {
            return;
        }
        isBluetoothDisabled = disabled;
        if(isBluetoothDisabled) {
            invokeBluetoothDisabled();
        }
    }

    /**
     * Invokes the onPeripheralChanged callback
     */
    private void invokePeripheralsChanged()
    {
        List<PeripheralManagerListener> listCopy;
        synchronized (listenerList) {
            listCopy = new LinkedList<>(listenerList);
        }
        try {
            for(PeripheralManagerListener listener : listCopy) {
                listener.onPeripheralsListUpdated();
            }
        }
        catch(Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
    }

    /**
     * Invokes the onPeripheralDiscovered callback
     */
    private void invokePeripheralDiscovered(PeripheralImpl p)
    {
        List<PeripheralManagerListener> listCopy;
        synchronized (listenerList) {
            listCopy = new LinkedList<>(listenerList);
        }
        try {
            for(PeripheralManagerListener listener : listCopy) {
                listener.onPeripheralDiscovered(p);
            }
        }
        catch(Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
    }

    /**
     * Invokes the onBluetoothEnabled callback
     */
    private void invokeBluetoothEnabled()
    {
        List<PeripheralManagerListener> listCopy;
        synchronized (listenerList) {
            listCopy = new LinkedList<>(listenerList);
        }
        try {
            for(PeripheralManagerListener listener : listCopy) {
                listener.onBluetoothEnabled();
            }
        }
        catch(Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
    }

    /**
     * Invokes the onBluetoothDisabled callback
     */
    private void invokeBluetoothDisabled()
    {
        List<PeripheralManagerListener> listCopy;
        synchronized (listenerList) {
            listCopy = new LinkedList<>(listenerList);
        }
        try {
            for(PeripheralManagerListener listener : listCopy) {
                listener.onBluetoothDisabled();
            }
        }
        catch(Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
    }

    /**
     * Sets the bluetooth state and updates scanning
     * @param state the current state
     */
    private void onBluetoothStateChanged(int state)
    {
        if(state == BluetoothAdapter.STATE_ON) {
            setIsBluetoothEnabled(true);
            setIsBluetoothDisabled(false);
        }
        else if(state == BluetoothAdapter.STATE_OFF) {
            setIsBluetoothEnabled(false);
            setIsBluetoothDisabled(true);
        }
        else {
            setIsBluetoothEnabled(false);
            setIsBluetoothDisabled(false);
        }
        updateScanning();
    }
}
