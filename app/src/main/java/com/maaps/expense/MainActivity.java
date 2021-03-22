package com.maaps.expense;


import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.util.Pair;
import android.view.View;

import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.messaging.FirebaseMessaging;
import com.maaps.expense.helpers.AccountHelper;
import com.maaps.expense.helpers.ImageViewResizeAnimation;
import com.maaps.expense.helpers.InfiniteScroller;
import com.maaps.expense.helpers.Utils;

import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import modelv2.Expense;

import modelv2.Group;
import modelv2.User;
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
    private LottieAnimationView loadingIcon;
    private ConstraintLayout blacker;
    private int numberOfExpensesToListen;
    private int heightOfPaymentListElementIdDp;
    private ConstraintLayout tab1;
    private ConstraintLayout tab2;
    private HorizontalScrollView horizontalScrollView;
    private ImageView logo;
    private ImageView[] pageIndicators;

    private static AccountHelper accountHelper;
    private InfiniteScroller infiniteScroller;
    private UserSession userSession;

    public static void signOut() {
        accountHelper.signOut(TAG);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        initializeViews();

        heightOfPaymentListElementIdDp = 11 + 11 + 2 + 9 + 18 + 12 + 18;
        MainActivity that = this;
        infiniteScroller = new InfiniteScroller(container, heightOfPaymentListElementIdDp, new InfiniteScroller.SpecificOnClickListener() {
            @Override
            public void onClick(View view, Serializable object, int index) {
                Intent intent = new Intent(that, ExpenseEditor.class);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(that, new Pair<>(history, "cont"));
                intent.putExtra(EXPENSE, object);
                startActivity(intent, options.toBundle());
            }
        }, new InfiniteScroller.OnPenultimatePageWasScrolled() {
            @Override
            public void onScrolled(int scrolledPages, int totalNumberOfPages, int scrolledElements) {
                userSession.extendExpenseListeners();
            }
        }, ListElement::newInstance, this);

        accountHelper = new AccountHelper(this);
        accountHelper.configureGoogleClient();



    }


    private void initializeViews() {
        container = findViewById(R.id.container);
        history = findViewById(R.id.history_container);
        totalAmount = findViewById(R.id.textView4);
        ratioBar = findViewById(R.id.imageView);
        id = R.id.fragment;
        loadingIcon = findViewById(R.id.number_loading);
        blacker = findViewById(R.id.blacker);
        tab1 = findViewById(R.id.constraintLayout);
        tab2 = findViewById(R.id.tab2);
        horizontalScrollView = findViewById(R.id.scrollViewHorizontal);
        logo = findViewById(R.id.logo);
        pageIndicators = new ImageView[2];
        pageIndicators[0] = findViewById(R.id.imageView5);
        pageIndicators[1] = findViewById(R.id.imageView6);

        initializeScrollTabs();
        showSplashScreen();

    }

    public void initializeScrollTabs() {

        Point point = new Point();
        this.getWindowManager().getDefaultDisplay().getSize(point);
        int width = point.x;
        ViewGroup.LayoutParams layoutParams = tab1.getLayoutParams();
        layoutParams.width = width;
        tab1.setLayoutParams(layoutParams);
        layoutParams = tab2.getLayoutParams();
        layoutParams.width = width / 3;
        tab2.setLayoutParams(layoutParams);

        final boolean[] scrolling = {false};
        final boolean[] tab2IsActive = {false};// => final boolean[] tab1IsActive = {true};
        horizontalScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                System.out.println(scrolling[0]);
                if (!tab2IsActive[0]) {
                    if (horizontalScrollView.getScrollX() / (float) (width + width / 3) * 100 >= 22) {
                        tab2IsActive[0] = true;
                        pageIndicators[0].setImageResource(R.drawable.circle_dark_grey);
                        pageIndicators[1].setImageResource(R.drawable.circle_grey);
                    }
                    System.out.println(horizontalScrollView.getScrollX() / (float) (width + width / 3) * 100);
                    if (!scrolling[0]) {
                        if (horizontalScrollView.getScrollX() / (float) (width + width / 3) * 100 >= 7) {
                            horizontalScrollView.smoothScrollTo(width, 0);
                        }
                    }
                } else {
                    if (horizontalScrollView.getScrollX() / (float) (width + width / 3) * 100 <= 2) {
                        tab2IsActive[0] = false;
                        pageIndicators[0].setImageResource(R.drawable.circle_grey);
                        pageIndicators[1].setImageResource(R.drawable.circle_dark_grey);
                    }
                    if (!scrolling[0]) {
                        if (horizontalScrollView.getScrollX() / (float) (width + width / 3) * 100 <= 18) {
                            horizontalScrollView.smoothScrollTo(0, 0);
                        }
                    }
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

    private void initializeUserSession() {
        MainActivity that = this;
        numberOfExpensesToListen = (((ScrollView) container.getParent()).getHeight()
                / Utils.dpToPx(heightOfPaymentListElementIdDp, this) + 1) * 2;

        userSession = UserSession.getInstance();
        userSession.setExpensesToRead(numberOfExpensesToListen);
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
                float total = calculateTotal(group);
                setRatioBar(group, total);
                totalAmount.setText(Utils.formatPriceLocale(total));
                replaceTopBar();
            }
        });
        userSession.setOnExpensesUpdated(new UserSession.OnExpensesUpdated() {
            @Override
            public void onExpensesUpdated(ArrayList<Expense> expenses) {
                infiniteScroller.populate(expenses);
            }
        });
        userSession.setOnExtraExpensesUpdated(new UserSession.OnExtraExpensesUpdated() {
            @Override
            public void onExtraExpensesUpdated(ArrayList<Expense> expenses) {
                infiniteScroller.add(expenses);
            }
        });
    }

    private float calculateTotal(Group group) {
        float total = 0;
        try {
            total = group.getTotal(UserSession.getInstance().getCurrentUser());
        } catch (IllegalArgumentException e) {
            total = 0f;
        }
        return total;
    }


    private void setRatioBar(Group group, float total) {
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getRealSize(size);
        float absoluteTotal = group.getAbsoluteTotal();
        int finalWidth = Float.valueOf(size.x * (total / absoluteTotal)).intValue();
        ImageViewResizeAnimation anim = new ImageViewResizeAnimation(ratioBar,
                ratioBar.getLayoutParams().width, ratioBar.getLayoutParams().height,
                finalWidth, ratioBar.getLayoutParams().height);
        ratioBar.setAnimation(anim);
        ratioBar.getLayoutParams().width = finalWidth;
        if (total / absoluteTotal * 100 > 66) {
            ratioBar.setImageResource(R.drawable.background_accent_variant);
        } else if (total / absoluteTotal * 100 < 33) {
            ratioBar.setImageResource(R.drawable.background_accent);
        } else {
            ratioBar.setImageResource(R.drawable.background_teal);
        }
        hideSplashScreen();
    }

    private  void showSplashScreen(){
        loadingIcon.setVisibility(View.VISIBLE);
        blacker.setVisibility(View.VISIBLE);
        logo.setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.textView3)).setText("");
        ((TextView)findViewById(R.id.history)).setText("");
        ((Button)findViewById(R.id.imageButton2)).setText("");
        ((Button)findViewById(R.id.imageButton2)).setVisibility(View.INVISIBLE);
        ((View)findViewById(R.id.floatingActionButton)).setVisibility(View.INVISIBLE);
    }

    private  void hideSplashScreen(){
        loadingIcon.setVisibility(View.INVISIBLE);
        blacker.setVisibility(View.INVISIBLE);
        logo.setVisibility(View.INVISIBLE);
        ((TextView)findViewById(R.id.textView3)).setText(getString(R.string.your_balance));
        ((TextView)findViewById(R.id.history)).setText(getString(R.string.history));
        ((Button)findViewById(R.id.imageButton2)).setText(getString(R.string.payments));
        ((Button)findViewById(R.id.imageButton2)).setVisibility(View.VISIBLE);
        ((View)findViewById(R.id.floatingActionButton)).setVisibility(View.VISIBLE);
    }
    private void replaceTopBar() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        TopBar topBar = TopBar.newInstance(true);
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.replace(id, topBar);
        fragmentTransaction.commit();
        id = topBar.getId();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (accountHelper.isLoggedIn()) {
            initializeUserSession();
            if (UserSession.getInstance().getCurrentGroup() != null) {
                infiniteScroller.populate(UserSession.getInstance().getCurrentGroup().getExpenses());
            }
        } else {
            accountHelper.setSignInSuccessful(new AccountHelper.SignInSuccessful() {
                @Override
                public void signInSuccessful(FirebaseUser user) {
                    initializeUserSession();



                }
            });
            accountHelper.signInUsingGoogle();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (userSession != null) {
            userSession.removeOnGroupUpdated();
            userSession.removeOnExpensesUpdated();
            userSession.removeOnCurrentGroupNull();
            userSession.removeOnExtraExpensesUpdated();
        }
    }

    public void logout(View view) {
        accountHelper.signOut(TAG);
    }

    public void waitForConnection(View view) {
        Utils.toastMessage("Pleas, wait for connection", this);
    }

    public void startHistory(View view) {

    }

    private boolean checkIfUserIsPayerOrBorrower(String id) {
        for (Expense expense : userSession.getDebtExpenses()) {
            if (expense.getPayer().getId().equals(id)) {
                return true;
            } else {
                for (User borrower : expense.getBorrowers()) {
                    if (borrower.getId().equals(id)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void scrollToTab1() {
        horizontalScrollView.post(new Runnable() {
            @Override
            public void run() {
                horizontalScrollView.smoothScrollTo(0, 0);
            }
        });
    }

    public void leaveGroup(View view) {
        if (checkIfUserIsPayerOrBorrower(userSession.getCurrentUser().getId())) {
            Utils.toastMessage("Please get or pay your expenses before leaving group.", this);
            scrollToTab1();
            startPaymentsList(view);
        } else {
            String buttonText = "Leave group";
            if (userSession.amILastUser()) { // TODO:: Method to implement
                buttonText = "Leave and delete group";
            }

            MainActivity that = this;
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Read this!");
            alertDialog.setMessage("You are about to leave " + userSession.getCurrentShallowGroup().getGroupName() + "."
                    + "You wont be able to see content of this group. If you are the last user in this group, it will be" +
                    " deleted with all expenses. There is no going back to retrieve data.");
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, buttonText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (!userSession.amILastUser()) {
                        try {
                            userSession.leaveCurrentGroup();
                        } catch (IllegalStateException e) {
                            Utils.toastMessage(e.getMessage(), that);
                        }

                    } else {
                        userSession.leaveAndDeleteCurrentGroup();
                    }
                }
            });
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alertDialog.show();
            scrollToTab1();
        }
    }
}