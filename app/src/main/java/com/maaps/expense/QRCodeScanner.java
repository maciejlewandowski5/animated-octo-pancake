package com.maaps.expense;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.maaps.expense.helpers.Utils;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QRCodeScanner extends Activity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private static final String OPERATED_BAR_CODE_TYPE = "QR_CODE";


    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);

        if (isCameraPermissionGranted()) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CAMERA},
                    MY_CAMERA_REQUEST_CODE);
        }


    }

    private boolean isCameraPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED;
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==MY_CAMERA_REQUEST_CODE){
            if (grantResults[0]==PackageManager.PERMISSION_DENIED){
                Utils.toastMessageLong(
                        getString(R.string.camera_permission_rationale),
                        this);
                onBackPressed();
            }
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

        if (rawResult.getBarcodeFormat().toString().equals(OPERATED_BAR_CODE_TYPE)) {
            Intent data = new Intent();
            data.setData(Uri.parse(rawResult.getText()));
            setResult(RESULT_OK, data);
            finish();
        }else{
            mScannerView.resumeCameraPreview(this);
            Utils.toastMessage(getString(R.string.plese_scan_from)
                    +getString(R.string.app_name)+
                    " "+
                    getString(R.string.application),this);
        }
    }

}
