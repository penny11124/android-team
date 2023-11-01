package com.example.ureka_voting_machine.ui.ble;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.ParcelUuid;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import com.example.ureka_voting_machine.EvaluateService;
import com.example.ureka_voting_machine.R;
import com.example.ureka_voting_machine.model.AppDatabase;
import com.example.ureka_voting_machine.model.device.DeviceDB;
import com.example.ureka_voting_machine.model.device.DeviceDao;
import com.example.ureka_voting_machine.model.device.DeviceStorage;
import com.example.ureka_voting_machine.model.voting.Voting;
import com.example.ureka_voting_machine.ureka.CommandExecution;
import com.example.ureka_voting_machine.ureka.UrekaCommunication;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.logos.ticket_module.TicketModule;
import com.logos.ticket_module.UTicket;
import com.logos.ticket_module.module_interface.DeviceModuleInterface;
import com.logos.ticket_module.service.UDeviceService;

import org.bouncycastle.util.encoders.Hex;
import org.fidoalliance.fdo.protocol.Composite;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;


public class BleViewModel extends AndroidViewModel {
    private static final int MAX_CHUNK_SIZE = 20; // Maximum size of each chunk

    private final MutableLiveData<String> mText;
    private final MutableLiveData<String> advertisingStatus;
    private final MutableLiveData<String> connectionStatus;
    private final MutableLiveData<String> characteristicValue;
    private final MutableLiveData<Bitmap> qrcode;

    // voting
    private MutableLiveData<Voting> voting;

    // room
    private final LiveData<DeviceDB> deviceDBLiveData;
    private DeviceDB deviceDB;
    private final DeviceDao deviceDao;
    private AppDatabase db;

    // ureka
    private final UrekaCommunication urekaCommunication;
    private UDeviceService deviceService;
    private CommandExecution commandExecution;
    private DeviceStorage deviceStorage;
    private DeviceModuleInterface UModule;
    private String deviceName;
    private String deviceNameHash;

    private static final String TAG = BleViewModel.class.getCanonicalName();

    private static final UUID UREKA_SERVICE_UUID = UUID.fromString("bb668484-3f87-425d-bde5-6ab977d4f8aa");
    private static final UUID UREKA_PACKAGE_UUID = UUID.fromString("37cbbf31-c7c2-46ae-89d1-705c15d792b4");
    private static final UUID UREKA_RTICKET_UUID = UUID.fromString("cc2d954d-703e-4a69-ae8c-f985c7cfd8d5");
    private static final UUID UREKA_ADDRESS_UUID = UUID.fromString("b9f919bc-d536-4b49-b9ad-e60ebd2510de");

    // GATT
    private BluetoothGattServer urekaServer;
    private BluetoothGattService urekaService;
    private BluetoothGattCharacteristic packageCharacteristic;
    private BluetoothGattCharacteristic addressCharacteristic;
    private BluetoothGattCharacteristic rticketCharacteristic;
    private HashSet<BluetoothDevice> bluetoothDevices;
    private BluetoothDevice bluetoothDevice;

    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeAdvertiser advertiser;
    private AdvertiseSettings advSettings;
    private AdvertiseData advData;
    private AdvertiseData advScanResponse;

    ByteArrayOutputStream tempData = new ByteArrayOutputStream();
    ByteArrayOutputStream addressStream = new ByteArrayOutputStream();

    private Context thisContext;
    private final Application application;

    public BleViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        urekaCommunication = new UrekaCommunication();

        // room
        db = Room.databaseBuilder(application.getApplicationContext(),
                AppDatabase.class, "AppDatabase").build();
        deviceDao = db.deviceDao();

        deviceDBLiveData = deviceDao.getFirst();
        new InitDevice().execute();

        // view
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
        advertisingStatus = new MutableLiveData<>();
        advertisingStatus.setValue("This is advertising status");
        connectionStatus = new MutableLiveData<>();
        connectionStatus.setValue("This is connection status");
        characteristicValue = new MutableLiveData<>();
        characteristicValue.setValue("This is characteristic value");
        qrcode = new MutableLiveData<>();
        voting = new MutableLiveData<>();

        EvaluateService.startEvaluate();
        EvaluateService.stopEvaluate();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                deviceDB = deviceDao.getFirstNow();
                deviceStorage = new DeviceStorage(deviceDB);
                startBLE();
            }
        });
    }

    // BLE
    @SuppressLint("MissingPermission")
    public void startBLE() {
        deviceName = new String(Base64.encode(deviceStorage.getPublicKey().getEncoded(), Base64.DEFAULT));
        if (deviceDB.uticket != null) {
            deviceStorage.storeTicket(new UTicket(Composite.fromObject(deviceDB.uticket)));
        }
        this.commandExecution = new CommandExecution(this.application);
        this.deviceService = new UDeviceService(deviceStorage, this.commandExecution);
        this.UModule = new TicketModule(this.urekaCommunication, this.deviceService, deviceStorage);
        try {
            this.deviceNameHash = new String(this.deviceName).substring(0, 5);
//            Log.d(TAG, "startBLE: device name adv: " + this.deviceNameHash);
//            Log.d(TAG, "startBLE: device name qrcode: " + this.deviceName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // BLE setting
        thisContext = application.getApplicationContext();
        packageCharacteristic = new BluetoothGattCharacteristic(
                UREKA_PACKAGE_UUID,
                BluetoothGattCharacteristic.PROPERTY_READ
                        | BluetoothGattCharacteristic.PROPERTY_NOTIFY
                        | BluetoothGattCharacteristic.PROPERTY_WRITE,
                BluetoothGattCharacteristic.PERMISSION_READ
                        | BluetoothGattCharacteristic.PERMISSION_WRITE);
//        packageCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
//        packageCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        addressCharacteristic = new BluetoothGattCharacteristic(
                UREKA_ADDRESS_UUID,
                BluetoothGattCharacteristic.PROPERTY_READ
                        | BluetoothGattCharacteristic.PROPERTY_NOTIFY
                        | BluetoothGattCharacteristic.PROPERTY_WRITE,
                BluetoothGattCharacteristic.PERMISSION_READ
                        | BluetoothGattCharacteristic.PERMISSION_WRITE);

        rticketCharacteristic = new BluetoothGattCharacteristic(
                UREKA_RTICKET_UUID,
                BluetoothGattCharacteristic.PROPERTY_READ
                        | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                BluetoothGattCharacteristic.PERMISSION_READ);

        urekaService = new BluetoothGattService(UREKA_SERVICE_UUID,
                BluetoothGattService.SERVICE_TYPE_PRIMARY);

        urekaService.addCharacteristic(packageCharacteristic);
        urekaService.addCharacteristic(addressCharacteristic);
        urekaService.addCharacteristic(rticketCharacteristic);

        bluetoothDevices = new HashSet<>();
        bluetoothManager = (BluetoothManager) thisContext.getSystemService(Context.BLUETOOTH_SERVICE);

        bluetoothAdapter = bluetoothManager.getAdapter();
        bluetoothAdapter.setName(deviceNameHash);

        advSettings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
                .setConnectable(true)
                .build();
        advData = new AdvertiseData.Builder()
                .setIncludeTxPowerLevel(true)
                .addServiceUuid(new ParcelUuid(urekaService.getUuid()))
                .build();
        advScanResponse = new AdvertiseData.Builder()
                .setIncludeDeviceName(true)
                .build();

        // start
        resetStatusViews();

        urekaServer = bluetoothManager.openGattServer(thisContext, urekaServerCallback);
        if (urekaServer == null) {
            ensureBleFeaturesAvailable();
            return;
        }
        urekaServer.addService(urekaService);

        if (bluetoothAdapter.isMultipleAdvertisementSupported()) {
//            Log.d(TAG, "BleViewModel: " + bluetoothAdapter.getName());
            advertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
            advertiser.startAdvertising(advSettings, advData, advScanResponse, advCallback);
        } else {
            advertisingStatus.postValue(application.getString(R.string.status_noLeAdv));
        }
        this.deviceName = deviceName;
    }

    private final AdvertiseCallback advCallback = new AdvertiseCallback() {
        @Override
        public void onStartFailure(int errorCode) {
            super.onStartFailure(errorCode);
            Log.e(TAG, "Not broadcasting: " + errorCode);
            int statusText;
            switch (errorCode) {
                case ADVERTISE_FAILED_ALREADY_STARTED:
                    statusText = R.string.status_advertising;
//                    Log.w(TAG, "App was already advertising");
                    break;
                case ADVERTISE_FAILED_DATA_TOO_LARGE:
                    statusText = R.string.status_advDataTooLarge;
                    break;
                case ADVERTISE_FAILED_FEATURE_UNSUPPORTED:
                    statusText = R.string.status_advFeatureUnsupported;
                    break;
                case ADVERTISE_FAILED_INTERNAL_ERROR:
                    statusText = R.string.status_advInternalError;
                    break;
                case ADVERTISE_FAILED_TOO_MANY_ADVERTISERS:
                    statusText = R.string.status_advTooManyAdvertisers;
                    break;
                default:
                    statusText = R.string.status_notAdvertising;
//                    Log.wtf(TAG, "Unhandled error: " + errorCode);
            }
            advertisingStatus.postValue(application.getString(statusText));
        }

        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);
//            Log.v(TAG, "Broadcasting");
            advertisingStatus.postValue(application.getString(R.string.status_advertising));
            if (ActivityCompat.checkSelfPermission(application.getApplicationContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//                Log.d(TAG, "onStartSuccess: permission");
            }

            // create qrcode
            QRCodeWriter writer = new QRCodeWriter();
            try {
                BitMatrix bitMatrix = writer.encode(BleViewModel.this.deviceName, BarcodeFormat.QR_CODE, 512, 512);
                int width = bitMatrix.getWidth();
                int height = bitMatrix.getHeight();
                Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                    }
                }
                qrcode.postValue(bmp);
            } catch (WriterException e) {
                e.printStackTrace();
            }

            // load contract address
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    DeviceDB tempDeviceDb = deviceDao.getFirstNow();
                    if (tempDeviceDb == null || tempDeviceDb.address == null) {
                        return;
                    }
//                    Log.d(TAG, "run: address: " + new String(tempDeviceDb.address));
                    addressCharacteristic.setValue(tempDeviceDb.address);
                }
            });
        }
    };

    private final BluetoothGattServerCallback urekaServerCallback = new BluetoothGattServerCallback() {
        @Override
        public void onMtuChanged(BluetoothDevice device, int mtu) {
            super.onMtuChanged(device, mtu);
            Log.d(TAG, "onMtuChanged: " + mtu);
        }

        @Override
        public void onConnectionStateChange(BluetoothDevice device, final int status, int newState) {
            super.onConnectionStateChange(device, status, newState);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothGatt.STATE_CONNECTED) {
                    bluetoothDevices.add(device);
                    updateConnectedDevicesStatus();

//                    Log.v(TAG, "Connected to device: " + device.getAddress());
                } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                    bluetoothDevices.remove(device);
                    updateConnectedDevicesStatus();
//                    Log.v(TAG, "Disconnected from device");
                }
            } else {
                bluetoothDevices.remove(device);
                updateConnectedDevicesStatus();
                Log.e(TAG, "Error when connecting: " + status);
            }
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset,
                                                BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicReadRequest(device, requestId, offset, characteristic);
            byte[] value;

            // if is R-ticket characteristic, create R-ticket first.
            if (characteristic.getUuid().equals(UREKA_RTICKET_UUID)) {
                UModule.getRTicket();
//                characteristic.setValue(urekaCommunication.getMessage());
            }
            value = characteristic.getValue();
//            Log.d(TAG, "onCharacteristicReadRequest: " + characteristic.getUuid());
            if (value == null) {
//                Log.d(TAG, "onCharacteristicReadRequest: value is null");
                urekaServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, null);
                return;
            }
            value = Arrays.copyOfRange(value, offset, value.length);
//            Log.d(TAG, "onCharacteristicReadRequest: " + Hex.toHexString(value));
//            Log.d(TAG, "onCharacteristicReadRequest: " + new String(value) + ", offset: " + offset + ", value length" + value.length);
//            characteristicValue.postValue(new String(value));
            urekaServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, value);
        }

        @Override
        public void onNotificationSent(BluetoothDevice device, int status) {
            super.onNotificationSent(device, status);
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId,
                                                 BluetoothGattCharacteristic characteristic,
                                                 boolean preparedWrite, boolean responseNeeded,
                                                 int offset, byte[] value) {
            super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite,
                    responseNeeded, offset, value);
//            Log.d(TAG, "onCharacteristicWriteRequest: value "+Hex.toHexString(value));
            // 不分段
            if (!preparedWrite) {
                characteristicValue.postValue(new String(Hex.toHexString(value)));
                try {
                    if (characteristic.getUuid().equals(UREKA_PACKAGE_UUID)) {
                        boolean isDone = doUreka(value);
                        if (isDone) {
                            EvaluateService.restartEvaluate();

                            EvaluateService.evaluateResult();
//                            Log.d(TAG, "store u ticket to db. UTICKET: " + new String(deviceStorage.getUTicket().toBytes()));
                            deviceDao.updateUTicket(deviceStorage.getUTicket().toBytes());
                            deviceDao.updateRTicket(urekaCommunication.getMessage());
                            rticketCharacteristic.setValue(urekaCommunication.getMessage());
                            urekaServer.notifyCharacteristicChanged(device, rticketCharacteristic, false);
                            notifyResponse(device, packageCharacteristic, packageCharacteristic.getValue());

                        }
                    }
                    if (characteristic.getUuid().equals(UREKA_ADDRESS_UUID)) {
//                        addressCharacteristic.setValue(value);
//                        deviceDao.updateAddress(new String(value));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                urekaServer.notifyCharacteristicChanged(device, characteristic, false);
            }
            // 分段
            else {
                try {
                    if (characteristic.getUuid().equals(UREKA_PACKAGE_UUID)) {
                        tempData.write(value);
                    }
                    if (characteristic.getUuid().equals(UREKA_ADDRESS_UUID)) {
                        addressStream.write(value);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

//            Log.d(TAG, "onCharacteristicWriteRequest: responseNeeded " + responseNeeded);
            if (responseNeeded) {
                boolean result = urekaServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, value);
            } else {
                boolean result = urekaServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, new byte[0]);
            }
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onExecuteWrite(BluetoothDevice device, int requestId, boolean execute) {
            super.onExecuteWrite(device, requestId, execute);
            byte[] tempDataByteArray = tempData.toByteArray();
            byte[] addressByteArray = addressStream.toByteArray();
            tempData.reset();
            addressStream.reset();
//            Log.d(TAG, "onExecuteWrite: temp data " + Hex.toHexString(tempDataByteArray));

            try {
                if (tempDataByteArray.length > 0) {
                    boolean isDone = doUreka(tempDataByteArray);
                    if (isDone) {
                        EvaluateService.restartEvaluate();
                        EvaluateService.evaluateResult();
//                        deviceDao.updateUTicket(deviceStorage.getUTicket().toBytes());
//                        deviceDao.updateRTicket(urekaCommunication.getMessage());
                        rticketCharacteristic.setValue(urekaCommunication.getMessage());
                        urekaServer.notifyCharacteristicChanged(device, rticketCharacteristic, false);
                    }
                }
                if (addressByteArray.length > 0) {
//                    addressCharacteristic.setValue(addressByteArray);
//                    deviceDao.updateAddress(new String(addressByteArray));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

//            characteristicValue.postValue(new String(Hex.toHexString(tempDataByteArray)));

            if (execute) {
//                urekaServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, packageCharacteristic.getValue());
//                Log.d(TAG, "onExecuteWrite: " + Hex.toHexString(packageCharacteristic.getValue()));
                notifyResponse(device, packageCharacteristic, packageCharacteristic.getValue());
                urekaServer.sendResponse(device, requestId, BluetoothGatt.GATT_FAILURE, 0, null);
            } else {
                urekaServer.sendResponse(device, requestId, BluetoothGatt.GATT_FAILURE, 0, null);
            }
        }
    };


    private void resetStatusViews() {
        advertisingStatus.postValue(application.getString(R.string.status_notAdvertising));
        updateConnectedDevicesStatus();
    }

    @SuppressLint("MissingPermission")
    private void updateConnectedDevicesStatus() {

        final String message = application.getString(R.string.status_devicesConnected) + " "
                + bluetoothManager.getConnectedDevices(BluetoothGattServer.GATT).size();

        connectionStatus.postValue(message);
    }

    private void ensureBleFeaturesAvailable() {
        if (bluetoothAdapter == null) {
            Toast.makeText(thisContext, R.string.bluetoothNotSupported, Toast.LENGTH_LONG).show();
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onCleared() {
        super.onCleared();
//        Log.d(TAG, "onCleared");
        if (urekaServer != null) {
            urekaServer.close();
        }
        if (bluetoothAdapter.isEnabled() && advertiser != null) {
            advertiser.stopAdvertising(advCallback);
        }
        resetStatusViews();
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<String> getAdvertisingStatus() {
        return advertisingStatus;
    }

    public LiveData<String> getConnectionStatus() {
        return connectionStatus;
    }

    public LiveData<String> getCharacteristicValue() {
        return characteristicValue;
    }

    public LiveData<Bitmap> getQrcode() {
        return qrcode;
    }

    public LiveData<DeviceDB> getDeviceDB() {
        return deviceDBLiveData;
    }

    public LiveData<Voting> getVoting() {
        return voting;
    }

    private class InitDevice extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            DeviceDB deviceInit = new DeviceDB();
            if (deviceDao.getFirstNow() == null) {
                deviceDao.insert(deviceInit);
            }
            return null;
        }
    }

    private boolean doUreka(byte[] data) throws Exception {
        EvaluateService.restartEvaluate();
        // set ureka communication message
        urekaCommunication.setMessage(data);
        // write ureka response into ureka communication message
        boolean isDone = UModule.receivePackage();
//         get response message and save to vale
        Intent intent = new Intent();
        intent.setAction("COMMAND");
        intent.putExtra("COMMAND", commandExecution.getVoting().commandType.name());
        application.sendBroadcast(intent);

//        voting.postValue(commandExecution.getVoting());
        if (!packageCharacteristic.setValue(urekaCommunication.getMessage())) {
            throw new Exception();
        }

        EvaluateService.stopEvaluate();
        return isDone;
    }


    @SuppressLint("MissingPermission")
    private void notifyResponse(BluetoothDevice device, BluetoothGattCharacteristic characteristic, byte[] response) {
        // Split the response into chunks of MAX_CHUNK_SIZE bytes
//        int length = Hex.toHexString(response).length();
//        Log.d(TAG, "notifyResponse: response: " + response.length);
        for (int i = 0; i < response.length; i += MAX_CHUNK_SIZE) {
            byte[] chunk = Arrays.copyOfRange(response, i, Math.min(i + MAX_CHUNK_SIZE, response.length));
//            Log.d(TAG, "notifyResponse: chunk " + Hex.toHexString(chunk));

            // Set the value of the characteristic
            characteristic.setValue(chunk);

            // Send a notification to the central device
            urekaServer.notifyCharacteristicChanged(device, characteristic, false);
            try {
                Thread.sleep(1);
//                Log.d(TAG, "notifyResponse: wait");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        characteristic.setValue("end".getBytes());
        Log.d(TAG, "notifyResponse: end " + Hex.toHexString("end".getBytes()));
        urekaServer.notifyCharacteristicChanged(device, characteristic, false);
    }
}
