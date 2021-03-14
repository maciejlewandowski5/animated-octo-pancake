package com.example.mainactivity;


import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Point;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Pair;
import android.view.View;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mainactivity.helpers.AccountHelper;
import com.example.mainactivity.helpers.ImageViewResizeAnimation;
import com.example.mainactivity.helpers.InfiniteScroller;
import com.example.mainactivity.helpers.Utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import modelv2.Expense;

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

    private static AccountHelper accountHelper;
    private InfiniteScroller<modelv2.Expense> infiniteScroller;
    private UserSession userSession;

    public static void signOut() {
        accountHelper.signOut(TAG);
    }


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

        accountHelper = new AccountHelper(this);
        accountHelper.configureGoogleClient();
        accountHelper.setSignInSuccessful(new AccountHelper.SignInSuccessful() {
            @Override
            public void signInSuccessful(FirebaseUser user) {
                userSession = UserSession.getInstance();
                userSession.setOnCurrentGroupNull(new UserSession.OnCurrentGroupNull() {
                    @Override
                    public void onCurrentGroupNull() {
                        Intent intent = new Intent(that, CreateGroup.class);
                        startActivity(intent);
                    }
                });
                userSession.setOnGroupUpdated(new UserSession.OnGroupUpdated() {
                    @Override
                    public void onGroupUpdated(modelv2.Group group) {
                        Point size = new Point();
                        getWindowManager().getDefaultDisplay().getRealSize(size);
                        float total = 0;
                        try {
                            total = group.getTotal(UserSession.getInstance().getCurrentUser());
                        } catch (IllegalArgumentException e) {
                            total = 0f;
                        }
                        float absoluteTotal = group.getAbsoluteTotal();

                        int finalWidth = Float.valueOf(size.x * (total / absoluteTotal)).intValue();
                        ImageViewResizeAnimation anim = new ImageViewResizeAnimation(ratioBar,
                                ratioBar.getLayoutParams().width, ratioBar.getLayoutParams().height,
                                finalWidth, ratioBar.getLayoutParams().height);
                        ratioBar.setAnimation(anim);
                        ratioBar.getLayoutParams().width = finalWidth;
                        if (total/absoluteTotal*100>66) {
                            ratioBar.setImageResource(R.drawable.background_accent_variant);
                        } else if (total/absoluteTotal*100<33) {
                            ratioBar.setImageResource(R.drawable.background_accent);
                        } else {
                            ratioBar.setImageResource(R.drawable.background_teal);
                        }
                        totalAmount.setText(Utils.formatPriceLocale(total));

                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        TopBar topBar = TopBar.newInstance(true);
                        // fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
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

    private void initializeViews() {
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
    }

    @Override
    protected void onStop() {
        super.onStop();
        //userSession.removeOnGroupUpdated();
        //userSession.removeOnExpensesUpdated();
    }

    public void logout(View view) {
        accountHelper.signOut(TAG);
    }
}