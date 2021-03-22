package com.maaps.expense;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.maaps.expense.helpers.Utils;

import modelv2.UserSession;

public class JoinGroup extends AppCompatActivity {

    TextView textView;
    boolean qrCodeScannerRunning;
    private static final int REQUEST_CODE = 512;
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_join_group);
        textView = findViewById(R.id.edit_code);
        qrCodeScannerRunning = false;
    }

    public void joinGroup(View view) {
        if (!textView.getText().toString().isEmpty()) {
            UserSession.getInstance().joinGroup(textView.getText().toString());
        } else {
            Utils.toastMessage(getString(R.string.provide_gorup_code), this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        JoinGroup that = this;
        UserSession.getInstance().setOnJoinGroupError(new UserSession.OnJoinGroupError() {
            @Override
            public void onJoinGroupError() {
                Utils.toastMessage("No group with provided code", that);
            }
        });
        UserSession.getInstance().setOnJoinGroupSuccess(new UserSession.OnJoinGroupSuccess() {
            @Override
            public void onJoinGroupSuccess() {
                onBackPressed();
            }
        });
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
                    textView.setText(data.getData().toString());
                }
            }
        }
    }


}