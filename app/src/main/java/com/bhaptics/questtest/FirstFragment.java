package com.bhaptics.questtest;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.companion.AssociationRequest;
import android.companion.BluetoothDeviceFilter;
import android.companion.CompanionDeviceManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import java.util.regex.Pattern;

public class FirstFragment extends Fragment {
    public static final String TAG = FirstFragment.class.getSimpleName();

    int SELECT_DEVICE_REQUEST_CODE = 1;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.button_first).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        setupExample();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "onClick: " + e.getMessage(), e);
                }

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == SELECT_DEVICE_REQUEST_CODE && data != null) {
            BluetoothDevice deviceToPair =
                    data.getParcelableExtra(CompanionDeviceManager.EXTRA_DEVICE);
            if (deviceToPair != null) {

//                deviceToPair.createBond();
                // Continue to interact with the paired device.
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupExample() {
        BluetoothDeviceFilter deviceFilter = new BluetoothDeviceFilter.Builder()
                // Match only Bluetooth devices whose name matches the pattern.
//                .setNamePattern(Pattern.compile("Tact"))
//                // Match only Bluetooth devices whose service UUID matches this pattern.
//                .addServiceUuid(new ParcelUuid(new UUID(0x123abcL, -1L)), null)
                .build();


        AssociationRequest pairingRequest = new AssociationRequest.Builder()
                // Find only devices that match this request filter.
                .addDeviceFilter(deviceFilter)
                // Stop scanning as soon as one device matching the filter is found.
//                .setSingleDevice(true)
                .build();

        CompanionDeviceManager deviceManager =
                (CompanionDeviceManager) getActivity().getSystemService(Context.COMPANION_DEVICE_SERVICE);

        deviceManager.associate(pairingRequest, new CompanionDeviceManager.Callback() {
            @Override
            public void onDeviceFound(IntentSender chooserLauncher) {
                try {
                    Log.e(TAG, "onDeviceFound: " + chooserLauncher.getCreatorPackage());
                    getActivity().startIntentSenderForResult(
                            chooserLauncher, SELECT_DEVICE_REQUEST_CODE, null, 0, 0, 0
                    );
                } catch (Exception e) {
                    Log.e("MainActivity", "Failed to send intent");
                }
            }

            @Override
            public void onFailure(CharSequence error) {
                // Handle the failure.
            }
        }, null);
    }
}