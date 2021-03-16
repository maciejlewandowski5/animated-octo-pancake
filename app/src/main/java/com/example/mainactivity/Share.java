package com.example.mainactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import net.glxn.qrgen.android.QRCode;
import net.glxn.qrgen.core.image.ImageType;

import modelv2.UserSession;

public class Share extends AppCompatActivity {
    private String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_share);
// override the image type to be JPG
        ImageView imageView = findViewById(R.id.imageView4);
        id = UserSession.getInstance().getCurrentGroup().getId();
        imageView.setImageBitmap(QRCode.from(id).withSize(500,500).withColor(0xFF222831, 0xEEEEEEEE).to(ImageType.BMP).bitmap());

    }

    public void share(View view) {
         Intent shareIntent = new Intent(Intent.ACTION_SEND);
         shareIntent.setType("text/plain");
         shareIntent.putExtra(Intent.EXTRA_TEXT,id);
          startActivity(Intent.createChooser(shareIntent, "Share..."));
    }
}