package com.maaps.expense;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import com.google.firebase.auth.FirebaseAuth;
import com.maaps.expense.helpers.InfiniteScroller;
import java.util.ArrayList;
import modelv2.Expense;
import modelv2.UserSession;

public class PaymentsList extends AppCompatActivity {

    ArrayList<modelv2.Expense> allDebts;
    ArrayList<modelv2.Expense> debtsToPay;
    InfiniteScroller<modelv2.Expense> infiniteScroller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_payments_list);

        initializeViewsAndNonViewsParameters();
        initializeInfiniteScroller();
    }

    private void initializeViewsAndNonViewsParameters() {
        TopBar topBar = TopBar.newInstance(true);
        TopBar.refreshTopBar(R.id.fragment, this, topBar);
        allDebts = new ArrayList<>();
        debtsToPay = new ArrayList<>();
    }

    private void initializeInfiniteScroller() {

        //see fragment_payment_list_element.xml
        //margin:29+image:9+margin:29
        int heightOfPaymentListElementInPx = 29+9+29;
        LinearLayout container = findViewById(R.id.container);
        infiniteScroller = new InfiniteScroller<>(
                container,
                29 + 9 + 29,
                (paymentListElementView, object, index) -> {

                    initializeInfiniteScrollerOnClickLogic(paymentListElementView, (Expense) object);

                }, (scrolledPages, totalNumberOfPages, scrolledElements) -> {
        }, PaymentListElement::newInstance, this);
    }

    private void initializeInfiniteScrollerOnClickLogic(View paymentListElementView, Expense expense) {
        CheckBox checkBox = extractCheckBox(paymentListElementView);
        ConstraintLayout elementContainer = ((ConstraintLayout) paymentListElementView.getParent());

        checkBox.setChecked(!checkBox.isChecked());
        if (checkBox.isChecked()) {
            setColorBasedOnExpense(expense, elementContainer);
            debtsToPay.add(expense);
        } else {
            setBackgroundColor(
                    elementContainer,
                    R.color.transparent);
            debtsToPay.remove(expense);
        }
    }

    private void setColorBasedOnExpense(Expense expense, ConstraintLayout elementContainer) {
        if (isCurrentUserAPayer(expense)) {
            setBackgroundColor(
                    elementContainer,
                    R.color.accent_secondary_transparent);
        } else if (isCurrentUserABorrower(expense)) {
            setBackgroundColor(
                    elementContainer,
                    R.color.accent_transparent);
        } else {
            setBackgroundColor(
                    elementContainer,
                    R.color.accent_variant_transparent);
        }
    }

    private void setBackgroundColor(ConstraintLayout constraintLayout, int colorId) {
        constraintLayout.setBackgroundColor(ContextCompat.getColor(
                this,
                colorId));
    }

    private boolean isCurrentUserABorrower(Expense expense) {
        return expense.getBorrowers().get(0).getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

    private boolean isCurrentUserAPayer(Expense expense) {
        return expense.getPayer().getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

    private CheckBox extractCheckBox(View view) {
        int frameLIndex = 1;
        int constraintLIndex = 0;
        int checkBoxLIndex = 2;

        ConstraintLayout constraintLayout = (ConstraintLayout) view.getParent();
        FrameLayout frameLayout = (FrameLayout) constraintLayout.getChildAt(frameLIndex);
        ConstraintLayout checkboxParent = (ConstraintLayout) frameLayout.getChildAt(constraintLIndex);

        return (CheckBox) checkboxParent.getChildAt(checkBoxLIndex);
    }

    public void evenChecked(View view) {
        UserSession userSession = UserSession.getInstance();
        userSession.addExpenses(debtsToPay, 0);
        onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        UserSession.getInstance().setOnDebtUpdated(expenses -> infiniteScroller.populate(expenses));
        infiniteScroller.populate(UserSession.getInstance().getDebtExpenses());

    }

    @Override
    protected void onStop() {
        super.onStop();
        UserSession.getInstance().removeOnDebtUpdated();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}