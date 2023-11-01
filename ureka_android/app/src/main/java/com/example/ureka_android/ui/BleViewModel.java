package com.example.ureka_android.ui;

import android.annotation.SuppressLint;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import com.example.ureka_android.EvaluateService;
import com.example.ureka_android.model.AppDatabase;
import com.example.ureka_android.model.ticket.TicketDB;
import com.example.ureka_android.model.ticket.TicketDao;
import com.example.ureka_android.model.user.UserDB;
import com.example.ureka_android.model.user.UserDao;
import com.example.ureka_android.model.user.UserStorage;
import com.example.ureka_android.ureka.CommandType;
import com.example.ureka_android.ureka.DeviceCommand;
import com.logos.ticket_module.Communication;
import com.logos.ticket_module.Ticket;
import com.logos.ticket_module.TicketModule;
import com.logos.ticket_module.UTicket;
import com.logos.ticket_module.module_interface.UserModuleInterface;
import com.logos.ticket_module.service.UUserService;
import com.logos.ticket_module.storage.UserStorageInterface;

import org.bouncycastle.util.encoders.Hex;
import org.fidoalliance.fdo.protocol.Composite;
import org.fidoalliance.fdo.protocol.DispatchResult;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

// this implements ureka communication interface
public class BleViewModel extends AndroidViewModel implements Communication {
    private final String TAG = "BleViewModel";
    private final Application application;
    private final String EXTRA_DATA = "EXTRA_DATA";
    private Composite message;
    private boolean result;

    // room
    private final MutableLiveData<UserDB> userDBLiveData;
    private UserDB userDB;
    private final AppDatabase db;
    private final UserDao userDao;

    // ble
    private int connectionState;
    private final MutableLiveData<Boolean> isConnectedLiveData;
    private static final UUID UREKA_SERVICE_UUID = UUID.fromString("bb668484-3f87-425d-bde5-6ab977d4f8aa");
    private static final UUID UREKA_PACKAGE_UUID = UUID.fromString("37cbbf31-c7c2-46ae-89d1-705c15d792b4");
    private static final UUID UREKA_RTICKET_UUID = UUID.fromString("cc2d954d-703e-4a69-ae8c-f985c7cfd8d5");
    private static final UUID UREKA_ADDRESS_UUID = UUID.fromString("b9f919bc-d536-4b49-b9ad-e60ebd2510de");

    private final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private final BluetoothLeScanner bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
    private boolean scanning;
    private final Handler handler = new Handler();
    private final MutableLiveData<ArrayList<BluetoothDevice>> bluetoothDevices = new MutableLiveData<>();
    private final ArrayList<BluetoothDevice> bluetoothDeviceList = new ArrayList<>();
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;
    // GATT
    private BluetoothGatt bluetoothGatt;
    private List<BluetoothGattService> gattServices;
    private BluetoothGattService urekaService;
    private BluetoothGattCharacteristic packageCharacteristic;
    private BluetoothGattCharacteristic rticketCharacteristic;
    private BluetoothGattCharacteristic addressCharacteristic;

    ByteArrayOutputStream tempData = new ByteArrayOutputStream();


    // Device scan callback.
    private final ScanCallback leScanCallback =
            new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                    if (!bluetoothDeviceList.contains(result.getDevice())) {
                        bluetoothDeviceList.add(result.getDevice());
                    }
                }
            };

    // ureka
    private String devicePublicKeyString;
    private UserStorageInterface userStorage;
    private UUserService userService;
    private UserModuleInterface UModule;
    private PublicKey devicePublicKey;

    public BleViewModel(Application application) {
        super(application);
        this.application = application;

        // room
        db = Room.databaseBuilder(application.getApplicationContext(),
                AppDatabase.class, "AppDatabase").build();
        userDao = db.userDao();
        userDBLiveData = new MutableLiveData<>();
        isConnectedLiveData = new MutableLiveData<Boolean>();
        isConnectedLiveData.setValue(false);


        // init user db &
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                userDB = userDao.getFirstNow();
                if (userDB == null) {
                    userDB = new UserDB();
                    userDao.insert(userDB);
                }
                userDBLiveData.postValue(userDB);

                userStorage = new UserStorage(userDB);
                userService = new UUserService(userStorage);

            }
        });

        // scan device
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                scanLeDevice();
            }
        });
    }

    public LiveData<UserDB> getUserDB() {
        return userDBLiveData;
    }

    public LiveData<Boolean> getIsConnected() {
        return isConnectedLiveData;
    }

    public Ticket issueVotingTicket(byte[] subjectPublicKey, byte[] devicePublicKey) {
        if (UModule == null) {
            UModule = new TicketModule(this, userService, userStorage);
        }
        try {
            PublicKey requestPk = KeyFactory.getInstance("EC").generatePublic(new X509EncodedKeySpec(subjectPublicKey));
            PublicKey devicePk = KeyFactory.getInstance("EC").generatePublic(new X509EncodedKeySpec(devicePublicKey));
            return UModule.issueTicket(requestPk, devicePk, new DeviceCommand(CommandType.VOTING));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void downloadTicket(String uTicket) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                TicketDB ticketDB = new TicketDB(uTicket, devicePublicKey.getEncoded());
                TicketDao ticketDao = db.ticketDao();
                ticketDao.insert(ticketDB);
                userStorage.storeTicket(new UTicket(Composite.fromObject(uTicket)));
            }
        });
    }

    public void downloadAndApply(String uTicket) {
        if (UModule == null) {
            UModule = new TicketModule(this, userService, userStorage);
        }

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                TicketDB ticketDB = new TicketDB(uTicket, devicePublicKey.getEncoded());
                TicketDao ticketDao = db.ticketDao();
                ticketDao.insert(ticketDB);
                userStorage.storeTicket(new UTicket(Composite.fromObject(uTicket)));
                UModule.applyTicket(new UTicket(Composite.fromObject(uTicket)));
            }
        });
    }

    public void downloadTicket(UTicket uTicket) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                TicketDB ticketDB = new TicketDB(Hex.toHexString(uTicket.toBytes()), devicePublicKey.getEncoded());
                TicketDao ticketDao = db.ticketDao();
                ticketDao.insert(ticketDB);
                userStorage.storeTicket(uTicket);
            }
        });
    }

    public void downloadAndApply(UTicket uTicket) {
        if (UModule == null) {
            UModule = new TicketModule(this, userService, userStorage);
        }

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                TicketDB ticketDB = new TicketDB(Hex.toHexString(uTicket.toBytes()), devicePublicKey.getEncoded());
                TicketDao ticketDao = db.ticketDao();
                ticketDao.insert(ticketDB);
                userStorage.storeTicket(uTicket);
                UModule.applyTicket(uTicket);
            }
        });
    }

    public boolean scan(String name) {
        devicePublicKeyString = name;
        try {
            devicePublicKey = KeyFactory.getInstance("EC").generatePublic(new X509EncodedKeySpec(Base64.decode(this.devicePublicKeyString, Base64.DEFAULT)));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }

        name = name.substring(0, 5);
        return connect(name);
    }

    @SuppressLint("MissingPermission")
    public void scanLeDevice() {
//        Log.d(TAG, "scanLeDevice: start");
        if (!scanning) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scanning = false;
//                    Log.d(TAG, "scanLeDevice: stop scanning on run");
                    bluetoothLeScanner.stopScan(leScanCallback);
                }
            }, SCAN_PERIOD);

            scanning = true;

//            Log.d(TAG, "scanLeDevice: start scanning");
            bluetoothLeScanner.startScan(leScanCallback);
        } else {
            scanning = false;
//            Log.d(TAG, "scanLeDevice: stop scanning");
            bluetoothLeScanner.stopScan(leScanCallback);
        }
    }

    @SuppressLint("MissingPermission")
    public boolean connect(String name) {
        for (BluetoothDevice device :
                bluetoothDeviceList) {
            if (Objects.equals(name, device.getName())) {
                bluetoothGatt = device.connectGatt(
                        application.getApplicationContext(),
                        false,
                        bluetoothGattCallback);

                return true;
            }
        }
        return false;
    }

    @SuppressLint("MissingPermission")
    public void write(byte[] data) {
//        Log.d(TAG, "write: " + Hex.toHexString(data));
        packageCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
//        packageCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        packageCharacteristic.setValue(data);
        bluetoothGatt.writeCharacteristic(packageCharacteristic);
    }

    @SuppressLint("MissingPermission")
    public void writeAddress(String address) {
//        Log.d(TAG, "write address: " + address);
        addressCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
        addressCharacteristic.setValue(address);
        bluetoothGatt.writeCharacteristic(addressCharacteristic);
    }

    @SuppressLint("MissingPermission")
    public void read() {
        bluetoothGatt.readCharacteristic(packageCharacteristic);
    }

    @SuppressLint("MissingPermission")
    public void readAddress() {
        bluetoothGatt.readCharacteristic(addressCharacteristic);
    }

    @SuppressLint("MissingPermission")
    private final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                // successfully connected to the GATT Server
                connectionState = BluetoothProfile.STATE_CONNECTED;
                bluetoothGatt.discoverServices();
                isConnectedLiveData.postValue(true);
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                // disconnected from the GATT Server
                connectionState = BluetoothProfile.STATE_DISCONNECTED;
                isConnectedLiveData.postValue(false);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {

                gattServices = bluetoothGatt.getServices();
                for (BluetoothGattService gattService :
                        gattServices) {
                    if (gattService.getUuid().equals(UREKA_SERVICE_UUID)) {
                        urekaService = gattService;
                        packageCharacteristic = gattService.getCharacteristic(UREKA_PACKAGE_UUID);
                        bluetoothGatt.setCharacteristicNotification(packageCharacteristic, true);

                        rticketCharacteristic = gattService.getCharacteristic(UREKA_RTICKET_UUID);
                        bluetoothGatt.setCharacteristicNotification(rticketCharacteristic, true);

                        addressCharacteristic = gattService.getCharacteristic(UREKA_ADDRESS_UUID);
                    }
                }

                if (bluetoothGatt.requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_HIGH)) {
                    Log.d(TAG, "connect: priority success");
                } else {
                    Log.d(TAG, "connect: priority false");

                }
            }
            readAddress();
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            final byte[] data = characteristic.getValue();
            Log.d(TAG, "onCharacteristicRead: ");
            if (data == null) {
                return;
            }
//            Intent intent = new Intent();

            if (characteristic.getUuid().equals(UREKA_PACKAGE_UUID)) {
                message = Composite.fromObject(data);
//                intent.setAction("READ");
//                intent.putExtra(EXTRA_DATA, Hex.toHexString(data));
//                Log.d(TAG, "onCharacteristicRead: " + Hex.toHexString(data));
//                application.sendBroadcast(intent);
                synchronized (TAG) {
                    TAG.notifyAll();
                }
//                Log.d(TAG, "unlock 2");
            }

            if (characteristic.getUuid().equals(UREKA_RTICKET_UUID)) {
                // upload rticket
//                intent.setAction("READ");
//                intent.putExtra("RTIonCharacteristicWriteCKET", Hex.toHexString(data));
//                application.sendBroadcast(intent);
            }

            if (characteristic.getUuid().equals(UREKA_ADDRESS_UUID)) {
//                intent.setAction("READ");
//                Log.d(TAG, "onCharacteristicRead: " + new String(data));
//                intent.putExtra("ADDRESS", new String(data));
//                application.sendBroadcast(intent);
            }
        }


        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            if (characteristic.getUuid().equals(UREKA_PACKAGE_UUID)) {
//                Log.d(TAG, "onCharacteristicWrite: " + Hex.toHexString(characteristic.getValue())); // this will get the value that the central device send, rather than the value peripheral device respond
//                read();
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
//            Log.d(TAG, "onCharacteristicChanged: " + characteristic.getUuid()+" " + Hex.toHexString(characteristic.getValue()));
//            Log.d(TAG, "onCharacteristicChanged: is end "+ Arrays.equals(characteristic.getValue(),"end".getBytes()));
//            Log.d(TAG, "onCharacteristicChanged: end "+Hex.toHexString("end".getBytes()));
            if (characteristic.getUuid().equals(UREKA_RTICKET_UUID)) {
                // read r ticket
                bluetoothGatt.readCharacteristic(rticketCharacteristic);
            }

            if (characteristic.getUuid().equals(UREKA_PACKAGE_UUID)) {
                try {
                    if (Arrays.equals(characteristic.getValue(), "end".getBytes())) {
                        byte[] tempDataByteArray = tempData.toByteArray();
//                        Log.d(TAG, "onCharacteristicChanged:end "+Hex.toHexString(tempDataByteArray));
                        message = Composite.fromObject(tempDataByteArray);
                        synchronized (TAG) {
                            TAG.notifyAll();
                        }

                        tempData.reset();
                    } else {
                        tempData.write(characteristic.getValue());
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "onMtuChanged:success mtu " + mtu);
//                synchronized (TAG) {
//                    TAG.notifyAll();
//                }
            } else {
                Log.d(TAG, "onMtuChanged:failed mtu " + mtu);
            }
        }
    };

    // ureka communication
    @Override
    public DispatchResult sendMessage(Composite message) {
        EvaluateService.stopEvaluate();

//        Log.d(TAG, "lock 1");
        // write ble message there
        // this lock wait for ble onCharacteristicWrite callback to unlock. At there will set device response message

        try {
            this.write(message.toBytes());
            synchronized (TAG) {
                // wait until read and change the this.message from request to response of the peripheral device
                TAG.wait();
            }
//            Log.d(TAG, "lock 3");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // this unlock will let user to send another message to device
//        Log.d(TAG, "unlock 4");
//        Log.d(TAG, "sendMessage: " + Hex.toHexString(this.message.toBytes()));
        EvaluateService.restartEvaluate();
        return new DispatchResult(this.message, this.result);
    }

    @Override
    public DispatchResult receiveMessage() {
        return null;
    }

    @Override
    public void responseMessage(DispatchResult response) {
        return;
    }

    public void setMessage(byte[] message) {
        this.message = Composite.fromObject(message);
    }

    public byte[] getMessage() {
        return message.toBytes();
    }
}