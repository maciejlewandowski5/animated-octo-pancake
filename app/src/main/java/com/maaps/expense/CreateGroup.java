package com.maaps.expense;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.maaps.expense.helpers.Utils;

import org.apache.commons.lang3.RandomStringUtils;

import modelv2.User;
import modelv2.UserSession;

public class CreateGroup extends AppCompatActivity {
    private TextView textView;
    private String code;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_create_group);
        code = RandomStringUtils.randomAlphanumeric(5,7);
        ((TextView)findViewById(R.id.textView11)).setText(code);
        textView = findViewById(R.id.edit_code);
    }

    public void createGroup(View view) {
        if (!textView.getText().toString().isEmpty()) {
            UserSession.getInstance().createNewGroup(textView.getText().toString(),code);
        }else{
            Utils.toastMessage(getString(R.string.privide_goroup_name), this);
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
                UserSession.getInstance().endSession();
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