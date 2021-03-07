package com.example.mainactivity;


import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Point;
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
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.mainactivity.helpers.AccountHelper;
import com.example.mainactivity.helpers.ImageViewResizeAnimation;
import com.example.mainactivity.helpers.InfiniteScroller;
import com.example.mainactivity.helpers.Utils;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

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
    TextView totalAmount;
    ImageView ratioBar;

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
    private static TopBar.RefreshCurrentGroup interf;


    int id = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        LinearLayout container = findViewById(R.id.container);
        floatButton = findViewById(R.id.floatingActionButton);
        history = findViewById(R.id.history_container);
        historyTex = findViewById(R.id.history);
        totalAmount = findViewById(R.id.textView4);
        ratioBar = findViewById(R.id.imageView);
        listenerIsSet = false;
        groupListnerSet = false;
        expenseListnerSet = false;

        groupLis = null;
        expenseLis = null;
        id = R.id.fragment;

        currentUser = new User("", "");
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


        interf = new TopBar.RefreshCurrentGroup() {
            @Override
            public void refreshCurrentGroup(Map.Entry<String, String> group) {
                groupLis.remove();
                expenseLis.remove();
                groupListnerSet = false;
                expenseListnerSet = false;
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                // GroupManager.getInstance().getCurrentGroup().getCurrentUser().setCurrentGroupData1(group);
                System.out.println("GROUP  :  " + group.getKey());
                currentUser.setCurrentGroupData1(group);
                db.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update(currentUser.toMap());
                setListeners();
            }
        };

        accountHelper = new AccountHelper(this);
        accountHelper.configureGoogleClient();
        accountHelper.setSignInSuccessful(new AccountHelper.SignInSuccessful() {
            @Override
            public void signInSuccessful(FirebaseUser user) {
                setListeners();
            }
        });
        accountHelper.signInUsingGoogle();


    }

    public static void refreshCurrentGroup(Map.Entry<String, Object> group) {
        Map.Entry<String, String> n = new Map.Entry<String, String>() {
            @Override
            public String getKey() {
                return group.getKey();
            }

            @Override
            public String getValue() {
                return (String) group.getValue();
            }

            @Override
            public String setValue(String value) {
                return null;
            }
        };
        interf.refreshCurrentGroup(n);
    }


    private void setGroupListener() {

        if (groupListnerSet == false) {


            GroupManager groupManager = GroupManager.getInstance();
            groupManager.clearGroups();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            MainActivity that = this;

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

                        float total = 0;
                        for (Map.Entry<String, Object> borrower : ((Map<String, Object>) value.getData().get(currentUser.getId())).entrySet()) {
                            try {
                                total += ((Double) borrower.getValue()).floatValue();
                            } catch (ClassCastException e) {
                                total += ((Long) borrower.getValue()).floatValue();
                            }
                        }


                        float absoluteTotal = 0;
                        for (Map.Entry<String, Object> user : ((Map<String, Object>) value.getData().get("users")).entrySet()) {
                            for (Map.Entry<String, Object> borrower : ((Map<String, Object>) value.getData().get(user.getKey())).entrySet()) {
                                try {
                                    absoluteTotal += ((Double) borrower.getValue()).floatValue();
                                } catch (ClassCastException e) {
                                    absoluteTotal += ((Long) borrower.getValue()).floatValue();
                                }
                            }

                        }
                        Point size = new Point();
                        getWindowManager().getDefaultDisplay().getRealSize(size);
                        int finalWidth = Float.valueOf(size.x * (total / absoluteTotal)).intValue();
                        ImageViewResizeAnimation anim = new ImageViewResizeAnimation(ratioBar,
                                ratioBar.getLayoutParams().width, ratioBar.getLayoutParams().height,
                                finalWidth, ratioBar.getLayoutParams().height);
                        ratioBar.setAnimation(anim);
                        ratioBar.getLayoutParams().width =finalWidth;
                        totalAmount.setText(Utils.formatPriceLocale(total));


                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        Fragment topBar = TopBar.newInstance(true);
                        ((TopBar) topBar).setRefreshCurrentGroup(that.interf);
                        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
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

    private void setExpenseListner() {
        if (expenseListnerSet == false) {
            GroupManager groupManager = GroupManager.getInstance();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            expenseLis = db.collection("Groups").document(currentUser.getCurrentGroupId()).collection("Expenses").orderBy("dateTime", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
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

    private void setListeners() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get(Source.SERVER).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot != null && documentSnapshot.exists()) {

                    currentUser = new User(documentSnapshot);
                    System.out.println("Current user group:  " + currentUser.getCurrentGroupId());
                    setGroupListener();
                }
            }
        });
    }

    private void setUserListners() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w(TAG, "Listen failed.", error);
                            return;
                        }

                        if (value != null && value.exists()) {
                            currentUser = new User(value);
                            System.out.println("Current user group:  " + currentUser.getCurrentGroupId());
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
            setListeners();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (groupLis != null && expenseLis != null) {
            groupLis.remove();
            expenseLis.remove();
            groupListnerSet = false;
            expenseListnerSet = false;
        }
    }
}