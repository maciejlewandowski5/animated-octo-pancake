package com.maaps.expense;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.maaps.expense.helpers.Utils;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QRCodeScanner extends Activity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    private String TAG = "QRCodeScanner";
    private static final int MY_CAMERA_REQUEST_CODE = 100;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    protected void onStart() {
        super.onStart();
        QRCodeScanner that = this;

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        if (rawResult.getBarcodeFormat().toString().equals("QR_CODE")) {
            Intent data = new Intent();
            data.setData(Uri.parse(rawResult.getText()));
            setResult(RESULT_OK, data);
            finish();
        }else{
            mScannerView.resumeCameraPreview(this);
            Utils.toastMessage("Please, scan QRCode from another application",this);
        }
    }

}
