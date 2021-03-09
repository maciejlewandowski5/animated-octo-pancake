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

import model.Group;
import model.GroupManager;
import model.User;
import modelv2.Expense;
import modelv2.ShallowGroup;
import modelv2.UserSession;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Exxpense.MainActivity";
    private static final String EXPENSE = "EXPENSE";
    private static final String EMPTY = "EMPTY";

    private ConstraintLayout history;
    private TextView totalAmount;
    private ImageView ratioBar;
    private LinearLayout container;
    private int id = 0;

    private AccountHelper accountHelper;
    private InfiniteScroller<modelv2.Expense> infiniteScroller;
    private UserSession userSession;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
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
    }

    private void initializeViews(){
        container = findViewById(R.id.container);
        history = findViewById(R.id.history_container);
        totalAmount = findViewById(R.id.textView4);
        ratioBar = findViewById(R.id.imageView);
        id = R.id.fragment;
    }

    public void startPaymentsList(View view) {
        Intent intent = new Intent(this, PaymentsList.class);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, new Pair<>(history, "cont"));
        startActivity(intent, options.toBundle());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
    protected void onStart() {
        super.onStart();
        accountHelper = new AccountHelper(this);
        accountHelper.configureGoogleClient();
        accountHelper.setSignInSuccessful(new AccountHelper.SignInSuccessful() {
            @Override
            public void signInSuccessful(FirebaseUser user) {
                userSession = UserSession.getInstance();

                userSession.setOnGroupUpdated(new UserSession.OnGroupUpdated() {
                    @Override
                    public void onGroupUpdated(modelv2.Group group) {
                        Point size = new Point();
                        getWindowManager().getDefaultDisplay().getRealSize(size);
                        float total = 0;
                        try {
                           total = group.getTotal(user.getUid());
                        }catch (IllegalArgumentException e) {
                            total = 0f;
                        }
                        float absoluteTotal = group.getAbsoluteTotal();

                        int finalWidth = Float.valueOf(size.x * (total / absoluteTotal)).intValue();
                        ImageViewResizeAnimation anim = new ImageViewResizeAnimation(ratioBar,
                                ratioBar.getLayoutParams().width, ratioBar.getLayoutParams().height,
                                finalWidth, ratioBar.getLayoutParams().height);
                        ratioBar.setAnimation(anim);
                        ratioBar.getLayoutParams().width = finalWidth;
                        totalAmount.setText(Utils.formatPriceLocale(total));


                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        TopBar topBar = TopBar.newInstance(true);
                        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                        fragmentTransaction.replace(id, topBar);
                        fragmentTransaction.commit();
                        id = topBar.getId();
                    }
                });
                userSession.setOnExpensesUpdated(new UserSession.OnExpensesUpdated() {
                    @Override
                    public void onExpensesUpdated(ArrayList<Expense> expenses) {
                        infiniteScroller.populate(expenses);
                    }
                });

            }
        });
        accountHelper.signInUsingGoogle();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //userSession.removeOnGroupUpdated();
        //userSession.removeOnExpensesUpdated();
    }
}