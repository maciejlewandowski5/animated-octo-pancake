package com.maaps.expense;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.maaps.expense.helpers.AccountHelper;
import com.maaps.expense.helpers.InfiniteScroller;
import com.maaps.expense.helpers.MainActivity.HorizontalTabsScroller;
import com.maaps.expense.helpers.MainActivity.RatioBar;
import com.maaps.expense.helpers.Utils;

import java.io.Serializable;

import modelv2.Expense;
import modelv2.Group;
import modelv2.UserSession;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Expense.MainActivity";
    private static final String EXPENSE_ARG_FOR_EXPENSE_EDITOR = "EXPENSE";

    private int topBarId;
    private int heightOfListElement;

    private ConstraintLayout history;
    private TextView totalAmount;
    private LinearLayout container;
    private SplashScreen splashScreen;

    private RatioBar ratioBar;
    private AccountHelper accountHelper;
    private InfiniteScroller<Expense> infiniteScroller;
    private HorizontalTabsScroller horizontalTabsScroller;
    private UserSession userSession;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        prepareViews();

        //heightOfListElement should be size of fragment_list_element.xml
        //margin:11+text:11+margin:2+:smallText:9+image:18+:text12:margin:18
        heightOfListElement = 11 + 11 + 2 + 9 + 18 + 12 + 18;

        initializeInfiniteScroller();
        accountHelper = new AccountHelper(this);
        accountHelper.configureGoogleClient();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == accountHelper.getRCSGININCode()) {
            accountHelper.verifySignInResults(TAG, data);
        }
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
            accountHelper.setSignInSuccessful(user -> initializeUserSession());
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

    private void initializeInfiniteScroller() {
        infiniteScroller = new InfiniteScroller<>(
                container,
                heightOfListElement,
                this::infiniteScrollerOnClickListener,
                (scrolledPages, totalNumberOfPages, scrolledElements)
                        -> userSession.extendExpenseListeners(),
                ListElement::newInstance,
                this);
    }

    private void infiniteScrollerOnClickListener(View view, Object object, Integer index) {
        Intent intent = new Intent(this, ExpenseEditor.class);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this
                , new Pair<>(history, getString(R.string.animation_tag__history_title)));
        intent.putExtra(EXPENSE_ARG_FOR_EXPENSE_EDITOR, (Serializable) object);
        startActivity(intent, options.toBundle());
    }

    private void prepareViews() {
        initializeSimpleViews();
        prepareSplashScreenTransaction();
        prepareHorizontalTabsScroller();
        splashScreen.show();
    }

    private void initializeSimpleViews() {
        container = findViewById(R.id.container);
        history = findViewById(R.id.history_container);
        totalAmount = findViewById(R.id.textView4);
        ratioBar = new RatioBar(findViewById(R.id.imageView));
        topBarId = R.id.fragment;
    }

    private void prepareHorizontalTabsScroller() {
        ConstraintLayout tab1 = findViewById(R.id.constraintLayout);
        ConstraintLayout tab2 = findViewById(R.id.tab2);
        HorizontalScrollView horizontalScrollView = findViewById(R.id.scrollViewHorizontal);

        ImageView[] pageIndicators = new ImageView[2];
        pageIndicators[0] = findViewById(R.id.imageView5);
        pageIndicators[1] = findViewById(R.id.imageView6);

        horizontalTabsScroller = new HorizontalTabsScroller(tab1, tab2, horizontalScrollView);
        horizontalTabsScroller.initialize(this, pageIndicators);
    }

    private void prepareSplashScreenTransaction() {
        splashScreen = SplashScreen.newInstance();
        FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
        transaction1.add(R.id.main, splashScreen, "SplashScreen");
        transaction1.hide(splashScreen);
        transaction1.commitNow();
    }

    public void startPaymentsListActivity(View view) {
        Intent intent = new Intent(this, PaymentsList.class);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this,
                new Pair<>(history, getString(R.string.animation_tag__history_title)));
        startActivity(intent, options.toBundle());
    }

    public void startAddExpenseActivity(View view) {
        Intent intent = new Intent(this, ExpenseEditor.class);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this,
                new Pair<>(history, getString(R.string.animation_tag__history_title)));
        startActivity(intent, options.toBundle());
    }

    private void initializeUserSession() {
        MainActivity that = this;
        int numberOfExpensesToListen = calculateNumberOfExpensesToListen();

        userSession = UserSession.getInstance();
        userSession.setExpensesToRead(numberOfExpensesToListen);
        userSession.setOnCurrentGroupNull(this::startCreateGroupActivity);
        userSession.setOnGroupUpdated(this::initializeOnGroupUpdated);
        userSession.setOnExpensesUpdated(expenses -> infiniteScroller.populate(expenses));
        userSession.setOnExtraExpensesUpdated(expenses -> infiniteScroller.add(expenses));
    }

    private int calculateNumberOfExpensesToListen() {
        return (((ScrollView) container.getParent()).getHeight()
                / Utils.dpToPx(heightOfListElement, this) + 1) * 2; // for two pages, avoid zero
    }

    private void startCreateGroupActivity() {
        Intent createGroupActivity = new Intent(this, CreateGroup.class);
        startActivity(createGroupActivity);
    }

    private void initializeOnGroupUpdated(Group group) {
        float payedByUser = calculateTotal(group);
        ratioBar.initialize(group, payedByUser, this);
        splashScreen.hide();
        totalAmount.setText(Utils.formatPriceLocale(payedByUser));

        TopBar topBar = TopBar.newInstance(true);
        topBar.setLogOutInterface(() -> accountHelper.signOut(TAG));
        topBarId = TopBar.refreshTopBar(topBarId, this, topBar);
    }

    private float calculateTotal(Group group) {
        float total;
        try {
            total = group.getTotal(UserSession.getInstance().getCurrentUser());
        } catch (IllegalArgumentException userNotInGroup) {
            total = 0f;
        }
        return total;
    }

    public void attemptToLeaveGroup(View view) {
        horizontalTabsScroller.scrollToTabOne();
        if (userSession.checkIfUserIsPayerOrBorrower(userSession.getCurrentUser().getId())) {
            Utils.toastMessage(getString(R.string.please_pay_before_leaving), this);
            startPaymentsListActivity(view);
        } else {
            horizontalTabsScroller.showLeaveGroupWarning(this);
        }
    }
}