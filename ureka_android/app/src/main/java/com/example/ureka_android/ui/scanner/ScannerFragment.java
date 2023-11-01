package com.example.ureka_android.ui.scanner;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.ureka_android.EvaluateService;
import com.example.ureka_android.databinding.FragmentScannerBinding;
import com.example.ureka_android.model.user.UserDB;
import com.example.ureka_android.model.user.UserStorage;
import com.example.ureka_android.ui.BleViewModel;
import com.example.ureka_android.ui.BlockchainViewModel;
import com.example.ureka_android.ureka.CommandType;
import com.example.ureka_android.ureka.DeviceCommand;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.logos.ticket_module.Ticket;
import com.logos.ticket_module.TicketModule;
import com.logos.ticket_module.module_interface.UserModuleInterface;
import com.logos.ticket_module.service.UUserService;
import com.logos.ticket_module.storage.UserStorageInterface;

import org.bouncycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.concurrent.Executor;

public class ScannerFragment extends Fragment {
    private final String TAG = "ScannerFragment";
    private BleViewModel bleViewModel;
    private BlockchainViewModel blockchainViewModel;
    private ImageButton qrBtn;
    private FragmentScannerBinding binding;
    private Button authBtn;
    private Button initBtn;
    private Button billingBtn;
    private Button settingBtn;
    private Button votingBtn;
    private Button requestBtn;
    private Context context;

    // ureka
    private String devicePublicKeyString;
    private UserStorageInterface userStorage;
    private UUserService userService;
    private UserModuleInterface UModule;
    private PublicKey devicePublicKey;

    // bio
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    // qr code scan result
    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
        result -> {
            this.devicePublicKeyString = result.getContents();
            if (this.devicePublicKeyString != null) {
                KeyFactory kf = null;
                try {
                    kf = KeyFactory.getInstance("EC");
                    this.devicePublicKey = kf.generatePublic(
                        new X509EncodedKeySpec(Base64.decode(this.devicePublicKeyString, Base64.DEFAULT)));
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (InvalidKeySpecException e) {
                    e.printStackTrace();
                }
                if (bleViewModel.scan(this.devicePublicKeyString)) {
                    // this.setEnabled(true);
                }
            }
        });

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        blockchainViewModel = new ViewModelProvider(getActivity()).get(BlockchainViewModel.class);
        bleViewModel = new ViewModelProvider(getActivity()).get(BleViewModel.class);

        binding = FragmentScannerBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textScanner;

        qrBtn = binding.qrButton;
        qrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                barcodeLauncher.launch(new ScanOptions());
            }
        });

        initBtn = binding.initBtn;
        billingBtn = binding.billingBtn;
        settingBtn = binding.settingBtn;
        votingBtn = binding.votingBtn;
        requestBtn = binding.requestBtn;

        this.setEnabled(false);

        initBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Log.d(TAG, "user pk: " + Hex.toHexString(userStorage.getPublicKey().getEncoded()));
//                Log.d(TAG, "device pk: " + Hex.toHexString(devicePublicKey.getEncoded()));
                Ticket ticket = UModule.issueInitTicket(userStorage.getPublicKey(), devicePublicKey);
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // user and owner are same
                            // create contract
                            // String address = blockchainViewModel.createContract(
                            // devicePublicKey,
                            // BigInteger.valueOf(10L),
                            // "voting machine");
                            // bleViewModel.writeAddress(address);
                            // // user buy this to create event and notify owner
                            // blockchainViewModel.purchaseItem(userStorage.getPublicKey());
                            // // owner upload u ticket
                            // blockchainViewModel.uploadUTicket(Hex.toHexString(ticket.toCoseComposite().toBytes()));
                            // // user confirm
                            // blockchainViewModel.retrieveUTicket();

                            UModule.applyTicket(ticket);

                            // tell device its contract address
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                BiometricPrompt.AuthenticationCallback callback = createAuthenticationCallback(runnable);

                biometricPrompt = createBiometricPrompt(callback);
                promptInfo = createPromptInfo("Init Ticket", "subject: ",
                    Hex.toHexString(userStorage.getPublicKey().getEncoded()));
                biometricPrompt.authenticate(promptInfo);
            }
        });

        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Ticket ticket = UModule.issueTicket(userStorage.getPublicKey(), devicePublicKey,
                    new DeviceCommand(CommandType.SETTING));
                BiometricPrompt.AuthenticationCallback callback = createAuthenticationCallback(ticket);

                biometricPrompt = createBiometricPrompt(callback);
                promptInfo = createPromptInfo("Setting", "subject: ",
                    Hex.toHexString(userStorage.getPublicKey().getEncoded()));
                biometricPrompt.authenticate(promptInfo);

            }
        });

        billingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Ticket ticket = UModule.issueTicket(userStorage.getPublicKey(), devicePublicKey,
                    new DeviceCommand(CommandType.BILLING));
                BiometricPrompt.AuthenticationCallback callback = createAuthenticationCallback(ticket);

                biometricPrompt = createBiometricPrompt(callback);
                promptInfo = createPromptInfo("Billing", "subject: ",
                    Hex.toHexString(userStorage.getPublicKey().getEncoded()));
                biometricPrompt.authenticate(promptInfo);
            }
        });

        votingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Ticket ticket = UModule.issueTicket(userStorage.getPublicKey(), devicePublicKey,
                    new DeviceCommand(CommandType.VOTING));
                BiometricPrompt.AuthenticationCallback callback = createAuthenticationCallback(ticket);

                biometricPrompt = createBiometricPrompt(callback);
                promptInfo = createPromptInfo("Voting", "subject: ",
                    Hex.toHexString(userStorage.getPublicKey().getEncoded()));
                biometricPrompt.authenticate(promptInfo);
            }
        });

        requestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // blockchainViewModel.purchaseItem(userStorage.getPublicKey());
                            // Log.d(TAG, "request voting: success");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        // ureka
        bleViewModel.getUserDB().observe(getViewLifecycleOwner(), new Observer<UserDB>() {
            @Override
            public void onChanged(UserDB userDB) {
                if (userDB != null) {
                    Log.d(TAG, "onChanged: user db");
                    userStorage = new UserStorage(userDB);
                    userService = new UUserService(userStorage);
                    UModule = new TicketModule(bleViewModel, userService, userStorage);
                }
            }
        });

        bleViewModel.getIsConnected().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isConnected) {
                setEnabled(isConnected);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // biometric
    private BiometricPrompt createBiometricPrompt(BiometricPrompt.AuthenticationCallback callback) {
        Executor executor = ContextCompat.getMainExecutor(context);
        return new BiometricPrompt(this, executor, callback);
    }

    private BiometricPrompt.PromptInfo createPromptInfo(String title, String subtitle, String description) {
        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
            .setTitle(title) // e.g. "Sign in"
            .setSubtitle(subtitle) // e.g. "Biometric for My App"
            .setDescription(description) // e.g. "Confirm biometric to continue"
            .setConfirmationRequired(false)
            .setNegativeButtonText("cancel") // e.g. "Use Account Password"
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK)

            // .setDeviceCredentialAllowed(true)
            .build();
        return promptInfo;
    }

    private BiometricPrompt.AuthenticationCallback createAuthenticationCallback(Ticket ticket) {
        return new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // blockchainViewModel.uploadUTicket(Hex.toHexString(ticket.toCoseComposite().toBytes()));
                            // blockchainViewModel.retrieveUTicket();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        // for (int i = 0; i < 200; i++) {
                        for (int i = 0; i < 1; i++) {

                            EvaluateService evaluateService = new EvaluateService("total");
                            UModule.applyTicket(ticket);
                            evaluateService.evaluateResult();
                        }
                    }
                });
            }
        };
    }

    private BiometricPrompt.AuthenticationCallback createAuthenticationCallback(Runnable runnable) {
        return new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                AsyncTask.execute(runnable);
            }
        };
    }

    // set btn enabled
    private void setEnabled(boolean isEnable) {
        initBtn.setEnabled(isEnable);
        billingBtn.setEnabled(isEnable);
        settingBtn.setEnabled(isEnable);
        votingBtn.setEnabled(isEnable);
        requestBtn.setEnabled(isEnable);
    }
}