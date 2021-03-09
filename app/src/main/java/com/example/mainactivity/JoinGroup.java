package com.example.mainactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.mainactivity.helpers.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import model.Group;
import model.GroupManager;
import model.User;
import modelv2.UserSession;

public class JoinGroup extends AppCompatActivity {

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);
        textView = findViewById(R.id.edit_code);
    }

    public void joinGroup(View view) {
        if (!textView.getText().toString().equals("") && textView.getText() != null) {
            UserSession.getInstance().joinGroup(textView.getText().toString());
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
        UserSession.getInstance().removeOnJoinGroupError();
        UserSession.getInstance().removeOnJoinGroupSuccess();
    }
}