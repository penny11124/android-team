package com.example.ureka_android.ui.audit_platform;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.ureka_android.contract.UREKA_sale;
import com.example.ureka_android.databinding.FragmentAuditPlatformBinding;
import com.example.ureka_android.ui.BleViewModel;
import com.example.ureka_android.ui.BlockchainViewModel;
import com.logos.ticket_module.Ticket;
import com.logos.ticket_module.UTicket;

import org.bouncycastle.util.encoders.Hex;

import java.util.List;
import java.util.concurrent.Executor;

public class AuditPlatformFragment extends Fragment {
    private final String TAG = "AuditPlatformFragment";
    private FragmentAuditPlatformBinding binding;
    private BleViewModel bleViewModel;
    private BlockchainViewModel blockchainViewModel;
    private byte[] requestingParty;
    private byte[] devicePublicKey;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private Context context;
    private String uTicketStr;
    private UTicket uTicket;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        blockchainViewModel = new ViewModelProvider(getActivity()).get(BlockchainViewModel.class);
        bleViewModel = new ViewModelProvider(getActivity()).get(BleViewModel.class);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
//                blockchainViewModel.updateUticketList();
//                blockchainViewModel.updateRequestList();
            }
        });

        binding = FragmentAuditPlatformBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView requestText = binding.request;
        final TextView uTicketInfo = binding.uTicketInfo;
        requestText.setText("this is request");
        uTicketInfo.setText("this is uticket");

        final Button grant = binding.grant;
        final Button downloadBtn = binding.download;
        final Button downloadAndApplyBtn = binding.downloadAndApply;

        // request text
        blockchainViewModel.getItemSoldEvents().observe(getViewLifecycleOwner(),
                new Observer<List<UREKA_sale.Item_soldEventResponse>>() {
                    @Override
                    public void onChanged(List<UREKA_sale.Item_soldEventResponse> item_soldEventResponses) {
                        int last = item_soldEventResponses.size() - 1;
                        requestingParty = item_soldEventResponses.get(last).sold_to_id;
                        devicePublicKey = item_soldEventResponses.get(last).device_id;
                        String requestStr = item_soldEventResponses.get(last).sold_to + " want to use this "
                                + Hex.toHexString(devicePublicKey) + " device.";
                        requestText.setText(requestStr);
                        grant.setEnabled(true);
                    }
                });

        // grant btn
        grant.setEnabled(false);
        grant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Ticket ticket = bleViewModel.issueVotingTicket(requestingParty, devicePublicKey);
                            // owner upload u ticket
//                            blockchainViewModel.uploadUTicket(Hex.toHexString(ticket.toCoseComposite().toBytes()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                BiometricPrompt.AuthenticationCallback callback = createAuthenticationCallback(runnable);

                biometricPrompt = createBiometricPrompt(callback);
                promptInfo = createPromptInfo("Voting Ticket", "subject: ", Hex.toHexString(requestingParty));
                biometricPrompt.authenticate(promptInfo);
            }
        });

        // uticket info
        blockchainViewModel.getUTickets().observe(getViewLifecycleOwner(), new Observer<List<UTicket>>() {
            @Override
            public void onChanged(List<UTicket> ticketList) {
                int last = ticketList.size() - 1;
                uTicket = ticketList.get(last);
                uTicketStr = Hex.toHexString(uTicket.toBytes());
                String uTicketInfoText = "this is uticket: " + uTicketStr;
                uTicketInfo.setText(uTicketInfoText);
                downloadBtn.setEnabled(true);
                downloadAndApplyBtn.setEnabled(true);
                Log.d(TAG, "onChanged: " + last + "\n" + uTicketStr);
                for (int i = 0; i < ticketList.size(); i++) {
                    UTicket t = ticketList.get(i);
                    Log.d(TAG, "onChanged: " + i + "\n" + Hex.toHexString(t.toBytes()));
                }
            }
        });

        // download
        downloadBtn.setEnabled(false);
        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
//                    blockchainViewModel.retrieveUTicket();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                bleViewModel.downloadTicket(uTicket);
            }
        });
        // download
        downloadAndApplyBtn.setEnabled(false);
        downloadAndApplyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
//                            blockchainViewModel.retrieveUTicket();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        bleViewModel.downloadAndApply(uTicket);
                    }
                };

                BiometricPrompt.AuthenticationCallback callback = createAuthenticationCallback(runnable);

                biometricPrompt = createBiometricPrompt(callback);
                promptInfo = createPromptInfo("Voting Ticket", "subject: ", Hex.toHexString(requestingParty));
                biometricPrompt.authenticate(promptInfo);
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

    private BiometricPrompt.AuthenticationCallback createAuthenticationCallback(Runnable runnable) {
        return new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                AsyncTask.execute(runnable);
            }
        };
    }
}