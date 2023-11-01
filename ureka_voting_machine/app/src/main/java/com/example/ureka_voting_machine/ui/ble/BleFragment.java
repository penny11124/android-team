package com.example.ureka_voting_machine.ui.ble;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.ureka_voting_machine.R;
import com.example.ureka_voting_machine.databinding.FragmentBleBinding;
import com.example.ureka_voting_machine.model.AppDatabase;
import com.example.ureka_voting_machine.model.device.DeviceDB;
import com.example.ureka_voting_machine.model.device.DeviceDao;
import com.example.ureka_voting_machine.model.device.DeviceStorage;
import com.example.ureka_voting_machine.ui.MainViewModel;
import com.example.ureka_voting_machine.ureka.CommandType;

public class BleFragment extends Fragment {
    private String TAG = "BleFragment";

    TextView textView;
    TextView advStatus;
    TextView connectionStatus;
    TextView characteristicValue;
    ImageView qrcode;

    DeviceDB deviceDB;
    AppDatabase db;
    DeviceDao deviceDao;
    DeviceStorage deviceStorage;

    private FragmentBleBinding binding;
    private Context thisContext;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        BleViewModel bleViewModel = new ViewModelProvider(getActivity()).get(BleViewModel.class);
        MainViewModel mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);

        binding = FragmentBleBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        NavController navController = NavHostFragment.findNavController(this);

        textView = binding.textBle;
        advStatus = binding.textViewAdvertisingStatus;
        connectionStatus = binding.textViewConnectionStatus;
        characteristicValue = binding.characteristicValue;
        qrcode = binding.qrcode;

        bleViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        bleViewModel.getAdvertisingStatus().observe(getViewLifecycleOwner(), advStatus::setText);
        bleViewModel.getConnectionStatus().observe(getViewLifecycleOwner(), connectionStatus::setText);
        bleViewModel.getCharacteristicValue().observe(getViewLifecycleOwner(), characteristicValue::setText);
        bleViewModel.getQrcode().observe(getViewLifecycleOwner(), new Observer<Bitmap>() {
            @Override
            public void onChanged(Bitmap bitmap) {
                qrcode.setImageBitmap(bitmap);
            }
        });

        bleViewModel.getDeviceDB().observe(getViewLifecycleOwner(), new Observer<DeviceDB>() {
            @Override
            public void onChanged(DeviceDB deviceDB) {
                if (deviceDB != null) {
                    deviceStorage = new DeviceStorage(deviceDB);
                    if (deviceStorage.getState() == 0) {
                        textView.setText("wait for new owner");
                    } else {
                        textView.setText("wait for using");
                    }
                }
            }
        });

        // 驗票在ble view model做，所以會在那執行command，把執行的結果傳到main view model中
//        bleViewModel.getVoting().observe(getViewLifecycleOwner(), new Observer<Voting>() {
//            @Override
//            public void onChanged(Voting voting) {
//                if (voting != null)
//                    mainViewModel.setVoting(voting);
//            }
//        });

        mainViewModel.getStatus().observe(getViewLifecycleOwner(), new Observer<CommandType>() {
            @Override
            public void onChanged(CommandType commandType) {
                Log.d(TAG, "getStatus onChanged: " + commandType.name());
                if (commandType == CommandType.VOTING) {
                    navController.navigate(R.id.navigation_voting);
                } else if (commandType == CommandType.SETTING) {
                    navController.navigate(R.id.navigation_setting);
                } else if (commandType == CommandType.BILLING) {
                    navController.navigate(R.id.navigation_billing);
                }
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private class InitDevice extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            DeviceDB deviceInit = new DeviceDB();

            deviceDao.insert(deviceInit);

            return null;
        }
    }
}