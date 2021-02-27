package com.example.mainactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import model.Group;
import model.GroupManager;

public class CreateGroup extends AppCompatActivity {
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        textView = findViewById(R.id.edit_code);
    }

    public void createGroup(View view) {
        if (textView.getText() != "" && textView.getText() != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            GroupManager.getInstance().addGroup("code", textView.getText().toString(), GroupManager.getInstance().getCurrentGroup().getCurrentUser());
            Group group = GroupManager.getInstance().getGroups().get(GroupManager.getInstance().getGroups().size() - 1);

            db.collection("Groups").add(group.toMap()).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    group.setId(documentReference.getId());
                    Map<String, Object> a = new HashMap<>();
                    a.put(documentReference.getId(), group.getName());
                    for(Map.Entry<String,Object> b : a.entrySet()){
                        GroupManager.getInstance().getCurrentGroup().getCurrentUser().setCurrentGroupData(b);
                    }
                    GroupManager.getInstance().getCurrentGroup().getCurrentUser().addGroup(documentReference.getId(),group.getName());

                    db.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update(GroupManager.getInstance().getCurrentGroup().getCurrentUser().toMap());
                    onBackPressed();
                }
            });
        }
    }
}