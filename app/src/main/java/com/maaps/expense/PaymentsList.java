package com.maaps.expense;


import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.maaps.expense.helpers.InfiniteScroller;

import com.google.firebase.auth.FirebaseAuth;

import java.io.Serializable;
import java.util.ArrayList;

import modelv2.Expense;

import modelv2.UserSession;

public class PaymentsList extends AppCompatActivity {

    private static final String TAG = "s";
    ArrayList<modelv2.Expense> debts;
    ArrayList<modelv2.Expense> toPay;

    InfiniteScroller<modelv2.Expense> infiniteScroller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_payments_list);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment topBar = TopBar.newInstance(false);
        fragmentTransaction.replace(R.id.fragment, topBar);
        fragmentTransaction.commit();

        debts = new ArrayList<>();
        toPay = new ArrayList<>();

        LinearLayout container = findViewById(R.id.container);

        infiniteScroller = new InfiniteScroller<modelv2.Expense>(container, 29 + 9 + 29, new InfiniteScroller.SpecificOnClickListener() {
            @Override
            public void onClick(View view, Serializable object, int index) {

                Expense expense = (modelv2.Expense) object;
                CheckBox checkBox = ((CheckBox) ((ConstraintLayout) ((FrameLayout) ((ConstraintLayout) view.getParent())
                        .getChildAt(1))
                        .getChildAt(0))
                        .getChildAt(2));

                ConstraintLayout constraintLayout = ((ConstraintLayout) view.getParent());

                checkBox.setChecked(!checkBox.isChecked());
                if (checkBox.isChecked()) {
                    if (expense.getPayer().getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        constraintLayout.setBackgroundColor(getColor(R.color.accent_secondary_transparent));
                    } else if (expense.getBorrowers().get(0).getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        constraintLayout.setBackgroundColor(getColor(R.color.accent_transparent));
                    } else {
                        constraintLayout.setBackgroundColor(getColor(R.color.accent_variant_transparent));
                    }
                    toPay.add(expense);
                } else {
                    constraintLayout.setBackgroundColor(getColor(R.color.transparent));
                    toPay.remove(expense);
                }


            }
        }, new InfiniteScroller.OnPenultimatePageWasScrolled() {
            @Override
            public void onScrolled(int scrolledPages, int totalNumberOfPages, int scrolledElements) {

            }
        }, PaymentListElement::newInstance, this);


    }

    public void evenChecked(View view) {
        UserSession userSession = UserSession.getInstance();
        //toPay.forEach(userSession::addExpense);
        userSession.addExpenses(toPay,0);
        onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        UserSession.getInstance().setOnDebtUpdated(new UserSession.OnDeptUpdated() {
            @Override
            public void onDebtUpdated(ArrayList<modelv2.Expense> expenses) {
                infiniteScroller.populate(expenses);
            }
        });
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