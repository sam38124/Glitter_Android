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
 * Class Name: FotaController
 ******************************************************************************/

package com.onsemi.protocol.update;

import com.onsemi.ble.BleException;
import com.onsemi.ble.BleResult;
import com.onsemi.ble.Characteristic;
import com.onsemi.ble.CharacteristicListener;
import com.onsemi.ble.CharacteristicProperties;
import com.onsemi.ble.Peripheral;
import com.onsemi.ble.PeripheralChangedListener;
import com.onsemi.ble.PeripheralImpl;
import com.onsemi.ble.PeripheralManagerImpl;
import com.onsemi.ble.PeripheralManagerListener;
import com.onsemi.ble.PeripheralState;
import com.onsemi.ble.Service;
import com.onsemi.ble.UpdateController;
import com.onsemi.ble.UpdateControllerListener;
import com.onsemi.ble.UpdateOptions;
import com.onsemi.protocol.cobs.CobsFraming;
import com.onsemi.protocol.hdlc.HdlcManager;
import com.onsemi.protocol.hdlc.HdlcManagerListener;
import com.onsemi.protocol.utility.Array;
import com.onsemi.protocol.utility.DataExchange;
import com.onsemi.protocol.utility.DataExchangeListener;
import com.onsemi.protocol.utility.Log;
import com.onsemi.protocol.utility.ProtocolException;
import com.onsemi.protocol.utility.StringHelper;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Controls the fota upadate procedure
 */
public class FotaController implements UpdateController, DataExchange {

    private final static String TAG = "FotaController";

    private final static String ServiceUuidDefault = "b2152466-d600-11e8-9f8b-f2801f1b9fd1";
    private final static String DataCharacteristicUuid = "b2152466-d601-11e8-9f8b-f2801f1b9fd1";
    private final static String DeviceIdCharacteristicUuid = "b2152466-d602-11e8-9f8b-f2801f1b9fd1";
    private final static String BootloaderVersionCharacteristicUuid = "b2152466-d603-11e8-9f8b-f2801f1b9fd1";
    private final static String BleStackVersionCharacteristicUuid = "b2152466-d604-11e8-9f8b-f2801f1b9fd1";
    private final static String ApplicationVersionCharacteristicUuid = "b2152466-d605-11e8-9f8b-f2801f1b9fd1";
    private final static String BleStackBuildIdCharacteristicUuid = "b2152466-d606-11e8-9f8b-f2801f1b9fd1";
    private final static String EnterFotaCharacteristicUuid = "b2152466-d607-11e8-9f8b-f2801f1b9fd1";
    private final static int FotaTimeout = 120 * 1000; // [s]

    private final LinkedList<UpdateControllerListener> updateControllerListeners;
    private final LinkedList<DataExchangeListener> dataExchangeListeners;
    private HdlcManager hdlcFlowControl;
    private CobsFraming lowerCobs;
    private Characteristic dataCharacteristic;
    private Characteristic deviceIdCharacteristic;
    private Characteristic bootloaderVersionCharacteristic;
    private Characteristic bleStackVersionCharacteristic;
    private Characteristic applicationVersionCharacteristic;
    private Characteristic bleStackBuildIdCharacteristic;
    private Characteristic enterFotaCharacteristic;
    private FotaFrameHandler fotaFrameHandler;
    private PeripheralImpl selected;
    private FotaOptions fotaOptions;
    private FotaUpdateStep updateStep;
    private int total;
    private UUID ServiceUuid;

    byte[] DeviceId;
    FotaFirmwareVersion BootloaderVersion;
    FotaFirmwareVersion BleStackVersion;
    FotaFirmwareVersion ApplicationVersion;
    byte[] FotaBuildId;

    public FotaUpdateStep getUpdateStatus() {
        return updateStep;
    }

    private CharacteristicListener characteristicListener = new CharacteristicListener() {
        @Override
        public void onNotificationReceived(Characteristic characteristic, byte[] data) {
            super.onNotificationReceived(characteristic, data);
            invokeDataReceived(data);
        }
    };

    private HdlcManagerListener hdlcManagerListener = new HdlcManagerListener() {
        @Override public void onFrameReceived(byte type, byte[] data) { }

        @Override
        public void onConnectionError() {
            if(selected != null) {
                try {
                    selected.disconnect();
                } catch (BleException e) {
                    Log.e(TAG, "Failed to disconnect peripherals: " + e.getMessage());
                }
            }
        }
    };

    /**
     * Constructor
     */
    public FotaController() {
        updateControllerListeners = new LinkedList<>();
        dataExchangeListeners = new LinkedList<>();
        updateStep = FotaUpdateStep.Idle;
        initProtocol();
    }

    @Override
    public void addListener(UpdateControllerListener listener) {
        synchronized (updateControllerListeners) {
            updateControllerListeners.add(listener);
        }
    }

    @Override
    public void removeListener(UpdateControllerListener listener) {
        synchronized (updateControllerListeners) {
            updateControllerListeners.remove(listener);
        }
    }

    @Override
    public void update(PeripheralImpl peripheral, UpdateOptions options) throws Exception {
        try {
            FotaOptions fotaOptoins = (FotaOptions)options;
            if (fotaOptoins == null) {
                throw new FotaException("No options provided", FotaStatus.GeneralError);
            }

            if (fotaOptoins.getFile().getFotaImage().getFotaServiceUuid() != null) {
                ServiceUuid = fotaOptoins.getFile().getFotaImage().getFotaServiceUuid();
            }

            update(peripheral, fotaOptoins);
            invokeOnCompleted(FotaStatus.Sucess);
        }
        catch (FotaException e) {
            setUpdateStep(FotaUpdateStep.Finished);
            invokeOnCompleted(e.getStatus());
            throw e;
        }
        catch (Exception e) {
            setUpdateStep(FotaUpdateStep.Finished);
            invokeOnCompleted(FotaStatus.GeneralError);
            throw e;
        }
    }

    private void update(PeripheralImpl peripheral, FotaOptions options) throws Exception
    {
        Log.i(TAG, "Start udpate");
        setUpdateStep(FotaUpdateStep.Idle);
        selected = peripheral;
        this.fotaOptions = options;
        do {
            if (!selected.isConnected()) {
                Log.i(TAG, "Peripheral is not connected, connect");
                connect(selected);
            }

            initPeripheral(selected, options);

            if (!isInBootloader()) {
                Log.i(TAG, "Peripheral is not in bootloader, reboot");
                rebootToBootloader();
                continue;
            }

            selected.setConnectionPriorityHigh();
            try {
                selected.set2MbPhy();
            }
            catch(Exception e) {
                Log.e(TAG, e.getMessage());
            }
            try {
                selected.requestMaxWriteLength(244);
            }
            catch(Exception e) {
                Log.e(TAG, e.getMessage());
            }

            checkDeviceId(options.getFile());

            if (FotaBuildId == null) {
                Log.e(TAG, "FotaBuildId is null, update not permitted");
                throw new FotaException("Failed to read FotaBuildId", FotaStatus.GeneralError);
            }


            // Fota image update only required if build ids don't match
            if (!Array.sequenceEqual(FotaBuildId, options.getFile().getFotaImage().getBuildId())) {
                Log.i(TAG, "FotaBuildIds not equal, update fota image first");
                setUpdateStep(FotaUpdateStep.UpdateFotaImage);
            }
            else {
                Log.i(TAG, "FotaBuildIds are equal, update app image: " + options.getFile().getAppImage().getVersion());
                setUpdateStep(FotaUpdateStep.UpdateAppImage);
            }

            try {
                initializeDataCharacteristic(selected);
                if (updateStep == FotaUpdateStep.UpdateFotaImage) {
                    Log.i(TAG, "update fota image: " + options.getFile().getFotaImage().getVersion());
                    byte[] data = options.getFile().getFotaImage().getImageData();
                    total = data.length;
                    sendFirmware(data);
                    setUpdateStep(FotaUpdateStep.UpdateAppImage);
                    invokeOnProgressChanged(0, total, updateStep);
                }
                else {
                    byte[] data = options.getFile().getAppImage().getImageData();
                    total = data.length;
                    sendFirmware(data);
                    setUpdateStep(FotaUpdateStep.Finished);
                }
            }
            catch (Exception e) {
                Log.e(TAG, e.getMessage());
                throw e;
            }
            finally {
                if (selected.isConnected()) {
                    try {
                        // give time to complete hdlc
                        sleep(200);
                        selected.disconnect();
                    }
                    catch (Exception e) {
                        Log.w(TAG, "Teardonw failed");
                    }
                }

                // give ble stack time to completely disconnect
                sleep(2500);

                total = 0;
                deinitializeDataCharacteristic(selected);
            }
        } while (updateStep != FotaUpdateStep.Finished);

        Log.i(TAG, "update finished with success");
    }

    private void initProtocol()
    {
        if (lowerCobs == null) {
            lowerCobs = new CobsFraming(this);
        }

        if (hdlcFlowControl == null) {
            hdlcFlowControl = new HdlcManager(lowerCobs);
        }

        if (fotaFrameHandler == null) {
            fotaFrameHandler = new FotaFrameHandler(hdlcFlowControl);
        }
    }

    private void connect(PeripheralImpl peripheral) throws Exception
    {
        Exception exception = null;
        for (int i = 0; i < 3; i++) {
            try {
                setUpdateStep(FotaUpdateStep.Connect);
                peripheral.connect();
                setUpdateStep(FotaUpdateStep.DiscoverServices);
                peripheral.discoverServices();
                return;
            }
            catch (Exception e) {
                exception = e;
            }

            // a small delay to give the Android BLE stack some time
            sleep(500);
        }

        throw exception;
    }

    private boolean isInBootloader() {
        if (!selected.isConnected()) {
            return false;
        }

        if (selected.findCharacteristic(DataCharacteristicUuid) != null) {
            return true;
        }

        return false;
    }

    private void initPeripheral(PeripheralImpl peripheral, FotaOptions options) throws Exception {
        setUpdateStep(FotaUpdateStep.Initialize);
        Service productService = peripheral.findService(ServiceUuid);
        if (productService == null) {
            Log.e(TAG, "update service not found!");
            throw new Exception("Service not found");
        }

        deviceIdCharacteristic = productService.getCharacteristic(DeviceIdCharacteristicUuid);
        bootloaderVersionCharacteristic = productService.getCharacteristic(BootloaderVersionCharacteristicUuid);
        bleStackVersionCharacteristic = productService.getCharacteristic(BleStackVersionCharacteristicUuid);
        applicationVersionCharacteristic = productService.getCharacteristic(ApplicationVersionCharacteristicUuid);
        bleStackBuildIdCharacteristic = productService.getCharacteristic(BleStackBuildIdCharacteristicUuid);
        enterFotaCharacteristic = productService.getCharacteristic(EnterFotaCharacteristicUuid);

        try {
            if (deviceIdCharacteristic != null) {
                DeviceId = deviceIdCharacteristic.readData();
            }
        }
        catch (Exception e) {
            Log.e(TAG,e.getMessage());
        }

        try {
            if (bootloaderVersionCharacteristic != null) {
                BootloaderVersion = new FotaFirmwareVersion(bootloaderVersionCharacteristic.readData());
                Log.i(TAG, "BootloaderVersion: " + BootloaderVersion);
            }
        }
        catch (Exception e) {
            Log.e(TAG,e.getMessage());
        }

        try {
            if (bleStackVersionCharacteristic != null) {
                BleStackVersion = new FotaFirmwareVersion(bleStackVersionCharacteristic.readData());
                Log.i(TAG, "BleStackVersion: " + BleStackVersion);
        }
        }
        catch (Exception e) {
            Log.e(TAG,e.getMessage());
        }

        try {
            if (applicationVersionCharacteristic != null) {
                ApplicationVersion = new FotaFirmwareVersion(applicationVersionCharacteristic.readData());
                Log.i(TAG, "ApplicationVersion: " + ApplicationVersion);
            }
        }
        catch (Exception e) {
            Log.e(TAG,e.getMessage());
        }

        try {
            if (bleStackBuildIdCharacteristic != null) {
                FotaBuildId = bleStackBuildIdCharacteristic.readData();
                Log.i(TAG, "FotaBuildId: " + StringHelper.toHex(FotaBuildId));

            }
        }
        catch (Exception e) {
            Log.e(TAG,e.getMessage());
        }

    }

    private void initializeDataCharacteristic(PeripheralImpl peripheral) throws Exception {
        Service productService = peripheral.findService(ServiceUuid);
        if (productService == null) {
            Log.e(TAG, "update service not found!");
            throw new Exception("Service not found");
        }

        dataCharacteristic = productService.getCharacteristic(DataCharacteristicUuid);

        if (
                (dataCharacteristic.getProperty() & CharacteristicProperties.WriteWithoutResponse) !=
                        CharacteristicProperties.WriteWithoutResponse) {
            Log.e(TAG, "Data characteristic is not writable");
            throw new Exception("Data characteristic is not writable");
        }

        dataCharacteristic.addListener(characteristicListener);
        dataCharacteristic.changeNotification(true);
        lowerCobs.init();
        hdlcFlowControl.init();
    }

    private void sendFirmware(final byte[] data) throws Exception {
        final Semaphore semaphore = new Semaphore(0);

        final byte[][] outframe = new byte[1][];
        final Exception[] innerException = new Exception[1];

        PeripheralChangedListener peripheralListener = new PeripheralChangedListener() {
            @Override public void onNameChanged(String name) {}
            @Override public void onRssiChanged(int rssi) {}
            @Override public void onManufacturerDataChanged(byte[] data) {}
            @Override public void onStateChanged(PeripheralState oldState, PeripheralState newState) { }

            @Override
            public void onDisconnected(boolean fromHost) {
                innerException[0] = new FotaException("Disconnected", FotaStatus.GeneralError);
                semaphore.release();
            }
        };

        DataExchangeListener dataExchangeListener = new DataExchangeListener() {
            @Override
            public void onDataReceived(byte[] data) {
                outframe[0] = data;
                semaphore.release();
            }
        };

        FotaFrameHandlerListener fotaFrameHandlerListener = new FotaFrameHandlerListener() {
            @Override
            public void onProgressChanged(int progress, int total) {
                invokeOnProgressChanged(progress, total, updateStep);
            }
        };

        try {
            selected.addListener(peripheralListener);
            fotaFrameHandler.addListener(dataExchangeListener);
            fotaFrameHandler.addListener(fotaFrameHandlerListener);
            hdlcFlowControl.addListener(hdlcManagerListener);
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        fotaFrameHandler.imageDownload(data);
                    }
                    catch (Exception e) {
                        innerException[0] = e;
                        semaphore.release();
                    }
                }
            });
            thread.start();
            try {
                if(!semaphore.tryAcquire(FotaTimeout, TimeUnit.MILLISECONDS)) {
                    throw new FotaException("Timeout while updating", FotaStatus.GeneralError);
                }
            }
            catch(Exception ex) {
                Log.e(TAG, "Failed to wait for task completion: " + ex.getMessage());
            }
            finally {
                fotaFrameHandler.cancle();
            }

            if(innerException[0] != null) {
                throw innerException[0];
            }
        }
        catch (Exception e) {
            Log.e(TAG, e.getMessage());
            if(selected != null && selected.getState() != PeripheralState.Idle) {
                selected.disconnect();
            }
            throw e;
        }
        finally {
            hdlcFlowControl.removeListener(hdlcManagerListener);
            fotaFrameHandler.removeListener(fotaFrameHandlerListener);
            fotaFrameHandler.removeListener(dataExchangeListener);
            selected.removeListener(peripheralListener);
        }

        if (outframe[0] == null || outframe[0].length < 6) {
            throw new FotaException("No response received", FotaStatus.GeneralError);
        }

        int status =  outframe[0][1];
        if (status != FotaStatus.Sucess) {
            throw new FotaException("update failed: " + status, status);
        }
    }
    private void deinitializeDataCharacteristic(PeripheralImpl peripheral) {
        dataCharacteristic.removeListener(characteristicListener);;
    }

    private void setUpdateStep(FotaUpdateStep step) {
        updateStep = step;
        invokeOnProgressChanged(0, total, step);
    }
    /**
     * Invokes the onCompleted callback
     */
    private void invokeOnCompleted(int status) {
        List<UpdateControllerListener> listCopy;
        synchronized (updateControllerListeners) {
            listCopy = new LinkedList<>(updateControllerListeners);
        }
        try {
            for(UpdateControllerListener listener : listCopy) {
                listener.onCompleted(status);
            }
        }
        catch(Exception ex) {
            Log.e(TAG, "invokeOnCompleted failed: " + ex.getMessage());
        }
    }

    private void checkDeviceId(FotaFirmwareFile file) throws FotaException {
        if (file.getFotaImage() == null) {
            throw new FotaException("No fota image provided, update not permitted", FotaStatus.GeneralError);
        }

        if (DeviceId == null || file.getFotaImage().getDeviceId() == null) {
            throw new FotaException("Device id not set, update not permitted", FotaStatus.GeneralError);
        }

        if (Array.sequenceEqual(DeviceId, new byte[] {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,})) {
            // all images are valid when device id is 0.
            Log.i(TAG, "Device id is 0, update permitted");
            return;
        }

        if (!Array.sequenceEqual(DeviceId, file.getFotaImage().getDeviceId())) {
            throw new FotaException("Device ids are not equal, update not permitted", FotaStatus.GeneralError);
        }

        if (file.getAppImage() != null) {
            if (file.getAppImage().getDeviceId() == null || !Array.sequenceEqual(DeviceId, file.getAppImage().getDeviceId())) {
                throw new FotaException("Device ids are not equal, update not permitted", FotaStatus.GeneralError);
            }
        }

        Log.i(TAG, "Device ids match, update permitted");
    }

    private void rebootToBootloader() throws FotaException {
        setUpdateStep(FotaUpdateStep.RebootToBootloader);

        // event handle for disconnect
        final Semaphore dfuPeripheralFound = new Semaphore(0);
        final PeripheralManagerImpl manager = selected.getPeripheralManager();
        final PeripheralImpl[] dfuPeripheral = new PeripheralImpl[1];

        PeripheralManagerListener managerListener = new PeripheralManagerListener() {
            @Override  public void onPeripheralsListUpdated() { }

            @Override
            public void onPeripheralDiscovered(Peripheral p) {
                if (dfuPeripheral[0] == null) {
                    dfuPeripheral[0] = (PeripheralImpl) p;
                    dfuPeripheralFound.release();
                }
            }

            @Override public void onBluetoothEnabled() {}

            @Override public void onBluetoothDisabled() {}
        };

        // enter bootloader
        if (fotaOptions.rebootToBootloader != null) {
            // enter bootloader and get the dfu peripheral
            dfuPeripheral[0] = fotaOptions.rebootToBootloader.run(selected);
        }
        else {
            rebootToBootloaderCharacteristic();
        }
        // wait for disconnected
        // search for device in DFU mode
        if (dfuPeripheral[0] == null) {

            manager.stopScan();

            setUpdateStep(FotaUpdateStep.ScanForDevice);

            String[] uuidFilter = null;
            try {
                manager.addListener(managerListener);
                uuidFilter = manager.getUuidFilterArray();
                manager.setUuidFilterArray(new String[] {ServiceUuid.toString()});
                manager.startScan();

                // wait for response
                boolean found = dfuPeripheralFound.tryAcquire(5000, TimeUnit.MILLISECONDS);
                if (!found) {
                    Log.w(TAG, "Device in DFU mode not found!");
                    throw new FotaException("No device in FOTA mode found", FotaStatus.GeneralError);
                }
            }
            catch (FotaException e) {
                throw e;
            }
            catch (Exception e) {
                Log.e(TAG, "Exception while scanning for fota peripheral: " + e.getMessage());
                throw new FotaException("Exception while scanning for fota peripheral: " +
                        e.getMessage(), FotaStatus.GeneralError);
            }
            finally {
                manager.removeListener(managerListener);
                manager.stopScan();
                manager.setUuidFilterArray(uuidFilter);
            }
        }

        // set new peripheral
        selected = dfuPeripheral[0];
    }

    private void rebootToBootloaderCharacteristic() throws FotaException {
        final Semaphore semaphore = new Semaphore(0);
        PeripheralChangedListener peripheralListener = new PeripheralChangedListener() {
            @Override public void onNameChanged(String name) {}
            @Override public void onRssiChanged(int rssi) {}
            @Override public void onManufacturerDataChanged(byte[] data) {}
            @Override public void onStateChanged(PeripheralState oldState, PeripheralState newState) {}

            @Override
            public void onDisconnected(boolean fromHost) {
                semaphore.release();
            }
        };

        try {
            // register for disconnected to detect entering bootloader
            selected.addListener(peripheralListener);
            if (enterFotaCharacteristic == null) {
                throw new FotaException("Failed to reboot into bootloader, enter fota characteristic not available",
                        FotaStatus.GeneralError);
            }

            try {
                enterFotaCharacteristic.writeData(new byte[] {0x01});
            }
            catch(BleException ex) {
                // characteristic write may fail with disconnected reason, ignore
                Log.w(TAG, "EnterFotaCharacteristic write failed with reason: " + ex.getMessage());
            }

            if(!semaphore.tryAcquire(10000, TimeUnit.MILLISECONDS)) {
                // disconnect
                try {
                    selected.disconnect();
                }
                catch (Exception e) {
                    Log.i(TAG, "Disconnect failed, " + e.getMessage());
                }
            }
        }
        catch (Exception e) {
            Log.w(TAG, "Failed to reboot to bootloader, " + e.getMessage());
            throw new FotaException("Failed to reboot to bootloader", FotaStatus.GeneralError);
        }
        finally {
            selected.removeListener(peripheralListener);
        }
    }

    /**
     * Invokes the onProgressChanged callback
     */
    private void invokeOnProgressChanged(int progress, int total, FotaUpdateStep step) {
        List<UpdateControllerListener> listCopy;
        synchronized (updateControllerListeners) {
            listCopy = new LinkedList<>(updateControllerListeners);
        }
        try {
            for(UpdateControllerListener listener : listCopy) {
                listener.onProgressChanged(progress, total, step.name());
            }
        }
        catch(Exception ex) {
            Log.e(TAG, "invokeOnProgressChanged failed: " + ex.getMessage());
        }
    }

    // ----------------------------------------- DataExchange --------------------------------------
    @Override
    public void addListener(DataExchangeListener listener) {
        synchronized (dataExchangeListeners) {
            dataExchangeListeners.add(listener);
        }
    }

    @Override
    public void removeListener(DataExchangeListener listener) {
        synchronized (dataExchangeListeners) {
            dataExchangeListeners.remove(listener);
        }
    }

    /**
     * Invokes the onDataReceived callback
     */
    private void invokeDataReceived(byte[] data) {
        List<DataExchangeListener> listCopy;
        synchronized (dataExchangeListeners) {
            listCopy = new LinkedList<>(dataExchangeListeners);
        }
        try {
            for(DataExchangeListener listener : listCopy) {
                listener.onDataReceived(data);
            }
        }
        catch(Exception ex) {
            Log.e(TAG, "invokeCharacteristicChanged failed: " + ex.getMessage());
        }
    }

    @Override
    public int getMaxDataLength() {
        if(selected == null) {
            return 20;
        }
        return selected.getMaxWriteLength();
    }

    @Override
    public void init() throws ProtocolException {

    }

    @Override
    public void transmit(byte[] data) throws ProtocolException {
        try {
            dataCharacteristic.writeData(data);
        }
        catch(Exception e) {
            throw new ProtocolException(e.getMessage());
        }

    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        }
        catch (Exception e) {
            Log.w(TAG, e.getMessage());
        }
    }
    @Override
    public void dispose() {
        if (fotaFrameHandler != null) {
            fotaFrameHandler.dispose();;
        }
        if (hdlcFlowControl != null) {
            hdlcFlowControl.dispose();
        }
        if (lowerCobs != null) {
            lowerCobs.dispose();
        }
    }
}
