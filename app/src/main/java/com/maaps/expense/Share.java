package com.maaps.expense;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

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

        id = UserSession.getInstance().getCurrentGroup().getId();

        initializeTitleAsGroupName();
        initializeQRCode();
    }

    private void initializeQRCode() {
        ImageView imageView = findViewById(R.id.imageView4);
        setImageContentAsIdQRCode(imageView);
    }

    private void initializeTitleAsGroupName() {
        TextView title = findViewById(R.id.textView14);
        String newTitle = UserSession.getInstance().getCurrentShallowGroup().getGroupName();
        title.setText(newTitle);
    }

    private void setImageContentAsIdQRCode(ImageView imageView) {
        imageView.setImageBitmap(
                QRCode.
                from(id).
                        withSize(500,500).
                        withColor(0xFF222831, 0xEEEEEEEE).
                        to(ImageType.BMP).
                        bitmap());
    }

    public void share(View view) {
         Intent shareIntent = new Intent(Intent.ACTION_SEND);
         shareIntent.setType("text/plain");
         shareIntent.putExtra(Intent.EXTRA_TEXT,id);
          startActivity(Intent.createChooser(shareIntent, getString(R.string.share)));
    }
}