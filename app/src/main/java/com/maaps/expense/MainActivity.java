package com.maaps.expense;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
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
import com.maaps.expense.helpers.ImageViewResizeAnimation;
import com.maaps.expense.helpers.InfiniteScroller;
import com.maaps.expense.helpers.MainActivity.HorizontalTabsScroller;
import com.maaps.expense.helpers.Utils;
import modelv2.Expense;
import modelv2.Group;
import modelv2.UserSession;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Exxpense.MainActivity";
    private static final String EXPENSE = "EXPENSE";

    private int topBarId;
    private int heightOfListElement;

    private ConstraintLayout history;
    private TextView totalAmount;
    private ImageView ratioBar;
    private LinearLayout container;
    private SplashScreen splashScreen;

    private AccountHelper accountHelper;
    private InfiniteScroller<Expense> infiniteScroller;
    private HorizontalTabsScroller horizontalTabsScroller;
    private UserSession userSession;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        initializeViews();

        //heightOfListElement should be size of fragment_list_element.xml
        //margin:11+text:11+margin:2+:smallText:9+image:18+:text12:margin:18
        heightOfListElement = 11 + 11 + 2 + 9 + 18 + 12 + 18;

        initializeInfiniteScroller();
        accountHelper = new AccountHelper(this);
        accountHelper.configureGoogleClient();
    }

    private void initializeInfiniteScroller() {
        MainActivity that = this;
        infiniteScroller = new InfiniteScroller<>(container,
                heightOfListElement,
                (view, object, index) -> {
                    Intent intent = new Intent(that, ExpenseEditor.class);
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(that
                            , new Pair<>(history, getString(R.string.animation_tag__history_title)));
                    intent.putExtra(EXPENSE, object);
                    startActivity(intent, options.toBundle());
                }, (scrolledPages, totalNumberOfPages, scrolledElements)
                -> userSession.extendExpenseListeners(),
                ListElement::newInstance,
                this);
    }


    private void initializeViews() {
        container = findViewById(R.id.container);
        history = findViewById(R.id.history_container);
        totalAmount = findViewById(R.id.textView4);
        ratioBar = findViewById(R.id.imageView);
        topBarId = R.id.fragment;
        splashScreen = SplashScreen.newInstance();

        FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
        transaction1.add(R.id.main, splashScreen, "SplashScreen");
        transaction1.hide(splashScreen);
        transaction1.commitNow();


        ConstraintLayout tab1 = findViewById(R.id.constraintLayout);
        ConstraintLayout tab2 = findViewById(R.id.tab2);
        HorizontalScrollView horizontalScrollView = findViewById(R.id.scrollViewHorizontal);

        ImageView[] pageIndicators = new ImageView[2];
        pageIndicators[0] = findViewById(R.id.imageView5);
        pageIndicators[1] = findViewById(R.id.imageView6);


        horizontalTabsScroller = new HorizontalTabsScroller(tab1, tab2, horizontalScrollView);
        horizontalTabsScroller.initializeScrollTabs(this, pageIndicators);

        splashScreen.show();

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == accountHelper.getRCSGININCode()) {
            accountHelper.verifySignInResults(TAG, data);
        }
    }


    private void initializeUserSession() {
        MainActivity that = this;
        int numberOfExpensesToListen = (((ScrollView) container.getParent()).getHeight()
                / Utils.dpToPx(heightOfListElement, this) + 1) * 2;

        userSession = UserSession.getInstance();
        userSession.setExpensesToRead(numberOfExpensesToListen);
        userSession.setOnCurrentGroupNull(() -> {
            Intent createGroupActivity = new Intent(that, CreateGroup.class);
            startActivity(createGroupActivity);
        });
        userSession.setOnGroupUpdated(group -> {
            float payedByUser = calculateTotal(group);
            setRatioBar(group, payedByUser);
            totalAmount.setText(Utils.formatPriceLocale(payedByUser));
            replaceTopBar();
        });
        userSession.setOnExpensesUpdated(expenses -> infiniteScroller.populate(expenses));
        userSession.setOnExtraExpensesUpdated(expenses -> infiniteScroller.add(expenses));
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


    private void setRatioBar(Group group, float payedByCurrentUser) {
        Point screenSizes = new Point();
        getWindowManager().getDefaultDisplay().getRealSize(screenSizes);
        float payedByGroup = group.getAbsoluteTotal();
        float ratioUserPToGroupP = payedByCurrentUser / payedByGroup;
        int ratioBarWidth = Float.valueOf(screenSizes.x * (ratioUserPToGroupP)).intValue();

        animateRatioBarTo(ratioBarWidth);
        setRatioBarColor(ratioUserPToGroupP);

        splashScreen.hide();
    }

    private void setRatioBarColor(float ratio) {
        final int GREEN_BAR_LIMIT_PERCENT = 66;
        final int RED_BAR_LIMIT_PERCENT = 33;

        if (ratio * 100 > GREEN_BAR_LIMIT_PERCENT) {
            ratioBar.setImageResource(R.drawable.background_accent_variant);
        } else if (ratio * 100 < RED_BAR_LIMIT_PERCENT) {
            ratioBar.setImageResource(R.drawable.background_accent);
        } else {
            ratioBar.setImageResource(R.drawable.background_teal);
        }
    }

    private void animateRatioBarTo(int targetProgressBarWidth) {
        ImageViewResizeAnimation anim = new ImageViewResizeAnimation(ratioBar,
                ratioBar.getLayoutParams().width, ratioBar.getLayoutParams().height,
                targetProgressBarWidth, ratioBar.getLayoutParams().height);

        ratioBar.setAnimation(anim);
        ratioBar.getLayoutParams().width = targetProgressBarWidth;
    }


    private void replaceTopBar() {
        TopBar topBar = TopBar.newInstance(true);
        topBar.setLogOutInterface(() -> accountHelper.signOut(TAG));

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.replace(topBarId, topBar);
        fragmentTransaction.commit();

        topBarId = topBar.getId();
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


    public void leaveGroup(View view) {
        if (userSession.checkIfUserIsPayerOrBorrower(userSession.getCurrentUser().getId())) {

            Utils.toastMessage(getString(R.string.please_pay_before_leaving), this);

            horizontalTabsScroller.scrollToTabOne();
            startPaymentsListActivity(view);

        } else {
            showLeaveGroupWarning();
            horizontalTabsScroller.scrollToTabOne();
        }
    }

    private void showLeaveGroupWarning() {
        String buttonText = getString(R.string.leave_group);
        if (userSession.amILastUser()) { // TODO:: Method to implement
            buttonText = getString(R.string.leaveAndDeleteGroup);
        }

        MainActivity that = this;


        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();

        alertDialog.setTitle(getString(R.string.read_this));
        alertDialog.setMessage(Utils.getLeaveGroupWarning(this));

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, buttonText,
                (dialog, which) -> {
                    if (!userSession.amILastUser()) {
                        try {
                            userSession.leaveCurrentGroup(this);
                        } catch (IllegalStateException tooFewGroupsToLeave) {
                            Utils.toastMessage(tooFewGroupsToLeave.getMessage(), that);
                        }
                    } else {
                        userSession.leaveAndDeleteCurrentGroup(this);
                    }
                });

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.cancel),
                (dialog, which) -> {
                    //closes dialog
                });
        alertDialog.show();
    }
}