package com.maaps.expense;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.maaps.expense.helpers.Utils;

import modelv2.UserSession;

public class JoinGroup extends AppCompatActivity {

    TextView groupIdInput;
    boolean qrCodeScannerRunning;
    private static final int REQUEST_CODE = 512;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_join_group);

        groupIdInput = findViewById(R.id.edit_code);
        qrCodeScannerRunning = false;
    }

    public void joinGroup(View view) {
        if (!isGroupIdInputEmpty()) {
            UserSession.getInstance().joinGroup(groupIdInput.getText().toString());
        } else {
            Utils.toastMessage(getString(R.string.provide_gorup_code), this);
        }
    }

    private boolean isGroupIdInputEmpty() {
        return groupIdInput.getText().toString().isEmpty();
    }

    @Override
    protected void onStart() {
        super.onStart();
        JoinGroup that = this;
        UserSession.getInstance().setOnJoinGroupError(()
                -> Utils.toastMessage(getString(R.string.no_group_with_code), that));
        UserSession.getInstance().setOnJoinGroupSuccess(this::onBackPressed);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!qrCodeScannerRunning) {
            UserSession.getInstance().removeOnJoinGroupError();
            UserSession.getInstance().removeOnJoinGroupSuccess();
        }
    }

    public void startQRCodeScanner(View view) {
        Intent intent = new Intent(this, QRCodeScanner.class);
        qrCodeScannerRunning = true;
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data.getData() != null) {
                    groupIdInput.setText(data.getData().toString());
                }
            }
        }
    }


}