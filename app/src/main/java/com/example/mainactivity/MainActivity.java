package com.example.mainactivity;


import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.mainactivity.helpers.AccountHelper;
import com.example.mainactivity.helpers.InfiniteScroller;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import model.Expense;
import model.Group;
import model.GroupManager;
import model.User;

public class MainActivity extends AppCompatActivity {
    private static final String EXPENSE = "EXPENSE";
    private static final int RC_SIGN_IN = 10;
    private static final String TAG = "AS";

    View floatButton;
    ConstraintLayout history;
    TextView historyTex;

    private FirebaseAuth mAuth;

    private AccountHelper accountHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        accountHelper = new AccountHelper(this);
        accountHelper.configureGoogleClient();
        accountHelper.setSignInSuccessful(new AccountHelper.SignInSuccessful() {
            @Override
            public void signInSuccessful(FirebaseUser user) {
                System.out.println("ZALOGOWANO");
            }
        });

        accountHelper.signInUsingGoogle();

        Group group = new Group("XASd", "name", new User("ala"));
        group.addUser(new User("Koń"));
        group.getExpenseManager().addExpense(50f, "Wyjazd", group.getCurrentUser(), group.getUsers());

        User user = new User("TOmasz");

        System.out.println("ROZPOCZĘTO ZAPISYWANIE");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Main").add(group.toMap()).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                System.out.println("ZAPISANO");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("NIEZAPISANO");
            }
        });

        db.collection("Main").document("Xov9e4ff9JWRDcTHuEsy").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Group.fromDocumentSnapshot(documentSnapshot);
            }
        });


        LinearLayout container = findViewById(R.id.container);
        floatButton = findViewById(R.id.floatingActionButton);
        history = findViewById(R.id.history_container);
        historyTex = findViewById(R.id.history);

        MainActivity that = this;
        InfiniteScroller<Expense> infiniteScroller = new InfiniteScroller<>(container, 11 + 11 + 2 + 9 + 18 + 12 + 18, new InfiniteScroller.SpecificOnClickListener() {
            @Override
            public void onClick(View view, Serializable object, int index) {
                Intent intent = new Intent(that, ExpenseEditor.class);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(that, new Pair<>(history, "cont"));
                intent.putExtra(EXPENSE, object);
                startActivity(intent, options.toBundle());
            }
        }, ListElement::newInstance, this);

        ArrayList<Expense> expenses = new ArrayList<Expense>();
        for (int i = 0; i < 10; i++) {
            ArrayList<User> borrowers = new ArrayList<>();
            for (int j = 0; j < 4; j++) {
                borrowers.add(new User(String.valueOf(i + j)));
            }
            ;
            expenses.add(new Expense((float) (i * 0.33 + 10), "Port", new User("Tomek"), new ArrayList<User>()));
        }

        infiniteScroller.populate(expenses);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment topBar = TopBar.newInstance(GroupManager.getInstance(), true);
        fragmentTransaction.replace(R.id.fragment, topBar);
        fragmentTransaction.commit();
    }


    public void startPaymentsList(View view) {
        Intent intent = new Intent(this, PaymentsList.class);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, new Pair<>(history, "cont"));
        startActivity(intent, options.toBundle());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        MainActivity that = this;
        if (requestCode == accountHelper.getRCSGININCode()) {
            accountHelper.verifySignInResults(TAG, data);
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        System.out.println("Auth in");
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            // Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            System.out.println("USERNAME:" + mAuth.getCurrentUser().getDisplayName());
                            // updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "signInWithCredential:failure", task.getException());
                            System.out.println("asd");
                            //updateUI(null);
                        }
                        System.out.println("Sign in comnplete");
                        // ...
                    }
                }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Sign in falure");
            }
        });
    }
}