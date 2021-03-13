package com.example.mainactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import org.apache.commons.lang3.RandomStringUtils;
import modelv2.UserSession;

public class CreateGroup extends AppCompatActivity {
    private TextView textView;
    private String code;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        code = RandomStringUtils.randomAlphanumeric(5,7);
        ((TextView)findViewById(R.id.textView11)).setText(code);
        textView = findViewById(R.id.edit_code);
    }

    public void createGroup(View view) {
        if (textView.getText() != "" && textView.getText() != null) {
            UserSession.getInstance().createNewGroup(textView.getText().toString(),code);
        }
    }

    public void share(View view) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,"Join our group in exxpense with code: " + code);
        startActivity(Intent.createChooser(shareIntent, "Share..."));

    }

    @Override
    protected void onStart() {
        super.onStart();
        UserSession.getInstance().setOnGroupPushed(new UserSession.OnGroupPushed() {
            @Override
            public void onGroupPushed() {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        UserSession.getInstance().removeOnGroupPushed();
    }
}