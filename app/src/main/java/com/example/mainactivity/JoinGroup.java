package com.example.mainactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

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

public class JoinGroup extends AppCompatActivity {

    TextView textView;
    Group currentGroup;
    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);
        textView = findViewById(R.id.edit_code);
        currentGroup = GroupManager.getInstance().getCurrentGroup();
        currentUser = currentGroup.getCurrentUser();
        if(currentGroup==null){
            System.out.println("Group is null");
        }

        if(currentUser==null){
            System.out.println("User is null");
        }
    }

    public void joinGroup(View view) {

        if (!textView.getText().toString().equals("") && textView.getText() != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Groups").whereEqualTo("code", textView.getText().toString()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()) {

                        if(ds.exists()) {
                            Map<String, Object> a = ((Map<String, Object>) ds.get("users"));
                            if (!a.containsKey(currentUser.getId())) {
                                a.put(currentUser.getId(),currentUser.getName());
                                ds.getReference().update("users", a).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        System.out.println("SUCCES");
                                    }
                                });
                                Map<String, Object> c = new HashMap<>();
                                //TODO:: update other users lists with debts
                                for (Map.Entry<String, Object> b : a.entrySet()) {
                                    c = ((Map<String, Object>) ds.getData().get(b.getKey()));
                                    if(c!= null) {
                                        if (c.isEmpty()) {

                                            //c.put()

                                        }
                                        c.put(currentUser.getId(), 0.0);
                                        System.out.println("PRINTING C");
                                        System.out.println(b.getKey() + "  " + b.getValue());
                                        System.out.println(c);

                                        System.out.println("PRINTING C");
                                        System.out.println(c);
                                        ds.getReference().update(b.getKey(), c);
                                    }
                                    }

                                c = new HashMap<>();
                                for (Map.Entry<String, Object> b : a.entrySet()) {
                                    //if (!b.getKey().equals(GroupManager.getInstance().getCurrentGroup().getCurrentUser().getId())) {
                                        c.put(b.getKey(), 0.0);
                                   // }
                                }


                                Map.Entry<String,Object> cd = null;
                                for(Map.Entry<String,Object> b : a.entrySet()){
                                    currentUser.setCurrentGroupData(cd);
                                }
                                ds.getReference().update(currentUser.getId(), c);
                                db.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update(currentUser.toMap());
                                System.out.println(currentUser.toMap());

                            }
                        }
                    }
                }
            });
        }
        onBackPressed();

    }
}