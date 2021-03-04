package com.example.mainactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import org.apache.commons.lang3.RandomStringUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import model.Group;
import model.GroupManager;

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
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            GroupManager.getInstance().addGroup(code, textView.getText().toString(), GroupManager.getInstance().getCurrentGroup().getCurrentUser());
            Group group = GroupManager.getInstance().getGroups().get(GroupManager.getInstance().getGroups().size() - 1);
            group.addUser(GroupManager.getInstance().getCurrentGroup().getCurrentUser());
            db.collection("Groups").add(group.toMap()).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    group.setId(documentReference.getId());
                    Map<String, Object> a = new HashMap<>();
                    a.put(documentReference.getId(), group.getName());

                    Map.Entry<String,Object> c = null;
                    for(Map.Entry<String,Object> b : a.entrySet()){
                        GroupManager.getInstance().getCurrentGroup().getCurrentUser().setCurrentGroupData(b);

                    c = b;
                    }
                    GroupManager.getInstance().getCurrentGroup().getCurrentUser().addGroup(documentReference.getId(),group.getName());

                    db.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update(GroupManager.getInstance().getCurrentGroup().getCurrentUser().toMap());
                    MainActivity.refreshCurrentGroup(c);
                    onBackPressed();
                }
            });
        }
    }

    public void share(View view) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,"Join our group in exxpense with code: " + code);
        startActivity(Intent.createChooser(shareIntent, "Share..."));

    }
}