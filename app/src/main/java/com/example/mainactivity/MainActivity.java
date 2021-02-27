package com.example.mainactivity;


import android.app.ActivityOptions;
import android.content.Intent;
import android.media.MediaDrm;
import android.os.Bundle;

import androidx.annotation.Nullable;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
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
    private static final String EMPTY = "EMPTY";
    private static final int RC_SIGN_IN = 10;
    private static final String TAG = "AS";

    View floatButton;
    ConstraintLayout history;
    TextView historyTex;

    private FirebaseAuth mAuth;
    private AccountHelper accountHelper;

    User currentUser;
    Group currentGroup;

    InfiniteScroller<Expense> infiniteScroller;

    boolean listenerIsSet;
    boolean groupListnerSet;
    boolean expenseListnerSet;

    ListenerRegistration groupLis;
    ListenerRegistration expenseLis;

    int id=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        LinearLayout container = findViewById(R.id.container);
        floatButton = findViewById(R.id.floatingActionButton);
        history = findViewById(R.id.history_container);
        historyTex = findViewById(R.id.history);
        listenerIsSet = false;
        groupListnerSet = false;
        expenseListnerSet = false;

        groupLis = null;
        expenseLis = null;
        id = R.id.fragment;

        currentUser = new User("","");
        MainActivity that = this;
        infiniteScroller = new InfiniteScroller<>(container, 11 + 11 + 2 + 9 + 18 + 12 + 18, new InfiniteScroller.SpecificOnClickListener() {
            @Override
            public void onClick(View view, Serializable object, int index) {
                Intent intent = new Intent(that, ExpenseEditor.class);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(that, new Pair<>(history, "cont"));
                intent.putExtra(EXPENSE, object);
                startActivity(intent, options.toBundle());
            }
        }, ListElement::newInstance, this);


        accountHelper = new AccountHelper(this);
        accountHelper.configureGoogleClient();
        accountHelper.setSignInSuccessful(new AccountHelper.SignInSuccessful() {
            @Override
            public void signInSuccessful(FirebaseUser user) {
                setUserListners();
            }
        });
        accountHelper.signInUsingGoogle();
    }


    private void setGroupListener(){

        if(groupListnerSet== false) {

            TopBar.RefreshCurrentGroup interf = new TopBar.RefreshCurrentGroup() {
                @Override
                public void refreshCurrentGroup(Map.Entry<String, String> group) {
                    groupLis.remove();
                    expenseLis.remove();
                    groupListnerSet = false;
                    expenseListnerSet = false;
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    GroupManager.getInstance().getCurrentGroup().getCurrentUser().setCurrentGroupData1(group);
                    db.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update(GroupManager.getInstance().getCurrentGroup().getCurrentUser().toMap());

                }

            };
            System.out.println("IAM HEEERE");
            GroupManager groupManager = GroupManager.getInstance();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            
             groupLis = db.collection("Groups").document(currentUser.getCurrentGroupId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Log.w(TAG, "Listen failed.", error);
                        return;
                    }

                    if (value != null && value.exists()) {
                        groupManager.addCurrentGroup(value);
                        groupManager.getCurrentGroup().setCurrentUser(currentUser);



                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        Fragment topBar = TopBar.newInstance(true);
                        ((TopBar)topBar).setRefreshCurrentGroup(interf);
                        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
                        fragmentTransaction.replace(id, topBar);
                        fragmentTransaction.commit();
                        setExpenseListner();
                        id = topBar.getId();
                    } else {
                        Log.d(TAG, "Current data: null");
                    }
                }
            });
        groupListnerSet = true;
        }
    }

    private  void setExpenseListner(){
           if(expenseListnerSet == false) {
               GroupManager groupManager = GroupManager.getInstance();
               FirebaseFirestore db = FirebaseFirestore.getInstance();
               expenseLis = db.collection("Groups").document(currentUser.getCurrentGroupId()).collection("Expenses").addSnapshotListener(new EventListener<QuerySnapshot>() {
                   @Override
                   public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                       if (error != null) {
                           Log.w(TAG, "Listen failed.", error);
                           return;
                       }

                       ArrayList<Expense> expenses = new ArrayList<>();
                       for (DocumentSnapshot ds : value.getDocuments()) {
                           expenses.add(new Expense(ds));
                       }
                       groupManager.getCurrentGroup().getExpenseManager().setExpenses(expenses);
                       infiniteScroller.populate((ArrayList<Expense>) groupManager.getCurrentGroup().getExpenseManager().getExpenses());
                   }
               });
            expenseListnerSet = true;
           }
    }

    private void setUserListners() {

        GroupManager groupManager = GroupManager.getInstance();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w(TAG, "Listen failed.", error);
                            return;
                        }

                        if (value != null && value.exists()) {
                            currentUser = new User(value);
                            System.out.println("FIREDDDDDDDDDDDDDDDDDDDDDDD");
                            setGroupListener();
                        } else {
                            Log.d(TAG, "Current data: null");
                        }
                    }
                });
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



    public void addExpense(View view) {
        Intent intent = new Intent(this, ExpenseEditor.class);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, new Pair<>(history, "cont"));
        intent.putExtra(EMPTY, "");
        startActivity(intent, options.toBundle());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

        }
    }
}