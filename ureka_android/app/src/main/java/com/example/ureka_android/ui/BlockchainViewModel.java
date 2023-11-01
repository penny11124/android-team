package com.example.ureka_android.ui;


import static org.web3j.protocol.core.DefaultBlockParameterName.EARLIEST;
import static org.web3j.protocol.core.DefaultBlockParameterName.LATEST;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ureka_android.R;
import com.example.ureka_android.contract.UREKA_sale;
import com.logos.ticket_module.UTicket;

import org.fidoalliance.fdo.protocol.Composite;
import org.web3j.abi.EventEncoder;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.websocket.WebSocketService;
import org.web3j.tuples.generated.Tuple10;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.StaticGasProvider;

import java.math.BigInteger;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;


public class BlockchainViewModel extends AndroidViewModel {
    private final String TAG = "blockchain";
    private final MutableLiveData<String> mText;
    private final MutableLiveData<List<UREKA_sale.Item_soldEventResponse>> itemSoldEventsLiveData;
    private final MutableLiveData<List<UREKA_sale.UTicket_uploadedEventResponse>> uticketEventsLiveData;
    private final MutableLiveData<List<UTicket>> uTicketsLiveData;
    private final List<UREKA_sale.UTicket_uploadedEventResponse> uticketEvents = new ArrayList<>();
    private final List<UREKA_sale.Item_soldEventResponse> itemSoldEvents = new ArrayList<>();
    private final List<UTicket> uTickets = new ArrayList<>();
//    private final Credentials credentials;
    private final Application application;
//    private final ContractGasProvider provider;
//    private final Web3j web3;
//    private final Web3j web3_socket;
    private UREKA_sale contract;

    @SuppressLint("CheckResult")
    public BlockchainViewModel(Application application) throws Exception {
        super(application);
        this.application = application;
//
//        credentials = Credentials.create(application.getResources().getString(R.string.blockchain_sk));
//        String clientVersion = "";
//        web3 = Web3j.build(new HttpService(application.getResources().getString(R.string.blockchain_url)));
//
//
//        Web3ClientVersion web3ClientVersion = web3.web3ClientVersion().sendAsync().get();
//        clientVersion = web3ClientVersion.getWeb3ClientVersion();
//
//        WebSocketService ws = new WebSocketService(application.getResources().getString(R.string.blockchain_url_socket), false);
//        ws.connect();
//        web3_socket = Web3j.build(ws);
//
//        provider = new StaticGasProvider(BigInteger.valueOf(20000000000L), BigInteger.valueOf(6721975L));
//
        mText = new MutableLiveData<>();
        itemSoldEventsLiveData = new MutableLiveData<>();
        uticketEventsLiveData = new MutableLiveData<>();
        uTicketsLiveData = new MutableLiveData<>();
//        mText.setValue("This is blockchain view model: " + clientVersion);
//
//        application.registerReceiver(gattRTicketReceiver, new IntentFilter("READ"));
//        application.registerReceiver(gattAddressReceiver, new IntentFilter("READ"));
    }
//
//    // when ble receive rticket, upload rticket to audit platform.
//    private final BroadcastReceiver gattRTicketReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            final String action = intent.getAction();
////            if (new String("READ").equals(action)) {
//            final String rticket = intent.getStringExtra("RTICKET");
//            if (rticket == null) {
//                return;
//            }
//            Log.d(TAG, "onReceive: read rticket: " + rticket);
//            mText.setValue(rticket);
//            try {
//                uploadRTicket(rticket);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
////            }
//        }
//    };
//
//    // when ble receive address, connect to contract.
//    private final BroadcastReceiver gattAddressReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            final String action = intent.getAction();
////            if (new String("READ").equals(action)) {
//            final String address = intent.getStringExtra("ADDRESS");
//            if (address == null || address.equals("")) {
//                return;
//            }
//            Log.d(TAG, "onReceive: read address: " + address);
//            mText.setValue(address);
//            try {
//                connectContract(address);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
////            }
//        }
//    };
//
//    public String createContract(PublicKey devicePK, BigInteger value, String name) throws Exception {
//        Log.d(TAG, "createContract");
//        contract = UREKA_sale.deploy(web3, credentials, provider, value, name, devicePK.getEncoded()).sendAsync().get();
//
//        String contractAddress = contract.getContractAddress();
//        System.out.println(contractAddress);
//        mText.postValue("This is blockchain view model: " + contractAddress);
//        return contractAddress;
//    }
//
//    public void connectContract(String address) {
//        AsyncTask.execute(new Runnable() {
//            @Override
//            public void run() {
//                contract = UREKA_sale.load(address, web3, credentials, provider);
//                Log.d(TAG, "blockchain contract connected!");
//            }
//        });
//    }
//
//    public void uploadUTicket(String uTicket) throws Exception {
//        TransactionReceipt tr = contract.upload_uticket(uTicket).sendAsync().get();
//
//        Log.d(TAG, "uploadUTicket: " + tr.getBlockNumber());
//    }
//
//    public void retrieveUTicket() throws Exception {
//        TransactionReceipt tr = contract.retrive_uticket().sendAsync().get();
//
//        Log.d(TAG, "retrieveUTicket: " + tr.getBlockNumber());
//    }
//
//    public void uploadRTicket(String rTicket) throws Exception {
//        TransactionReceipt tr = contract.upload_rticket(rTicket).sendAsync().get();
//
//        Log.d(TAG, "uploadRTicket: " + tr.getBlockNumber());
//    }
//
//    @SuppressLint("CheckResult")
//    public void updateUticketList() {
//        if (contract == null) return;
//        EthFilter filter = new EthFilter(EARLIEST, LATEST, contract.getContractAddress())
//                .addSingleTopic(EventEncoder.encode(UREKA_sale.UTICKET_UPLOADED_EVENT));
//
//        contract.uTicket_uploadedEventFlowable(filter).subscribe(event -> {
//            if (uticketEvents.contains(event)) {
//                return;
//            }
//            uticketEvents.add(event);
//            uticketEventsLiveData.postValue(uticketEvents);
//            uTickets.add(new UTicket(Composite.fromObject(event.uTicket)));
//            uTicketsLiveData.postValue(uTickets);
//        }).dispose();
//    }
//
//    @SuppressLint("CheckResult")
//    public void updateRequestList() {
//        if (contract == null) return;
//        EthFilter filter = new EthFilter(EARLIEST, LATEST, contract.getContractAddress())
//                .addSingleTopic(EventEncoder.encode(UREKA_sale.ITEM_SOLD_EVENT));
//
//        contract.item_soldEventFlowable(filter).subscribe(event -> {
//            if (itemSoldEvents.contains(event)) {
//                return;
//            }
//            itemSoldEvents.add(event);
//            itemSoldEventsLiveData.postValue(itemSoldEvents);
//        }).dispose();
//    }
//
//    @SuppressLint("CheckResult")
//    public TransactionReceipt purchaseItem(PublicKey userPk) throws Exception {
//
//        Tuple10<String, BigInteger, BigInteger, String, String, Boolean, Boolean, byte[], byte[], BigInteger> contractInfo = contract.read_listing().sendAsync().get();
//        BigInteger price = contractInfo.component2();
//        TransactionReceipt tr = contract.purchase(userPk.getEncoded(), price).sendAsync().get();
//
//        return tr;
//    }


    public LiveData<List<UREKA_sale.Item_soldEventResponse>> getItemSoldEvents() {
        return itemSoldEventsLiveData;
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<List<UREKA_sale.UTicket_uploadedEventResponse>> getUTicketEvents() {
        return uticketEventsLiveData;
    }

    public LiveData<List<UTicket>> getUTickets() {
        return uTicketsLiveData;
    }

}
