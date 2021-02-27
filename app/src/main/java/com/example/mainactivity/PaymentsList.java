package com.example.mainactivity;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.example.mainactivity.helpers.InfiniteScroller;

import java.io.Serializable;
import java.util.ArrayList;

import model.Expense;
import model.Group;
import model.GroupManager;
import model.User;

public class PaymentsList extends AppCompatActivity {

    Group group;
    ArrayList<Expense> debts;
    ArrayList<Expense> toPay;

    InfiniteScroller<Expense> infiniteScroller;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments_list);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment topBar = TopBar.newInstance(false);
        fragmentTransaction.replace(R.id.fragment, topBar);
        fragmentTransaction.commit();


        group = new Group("XXAS","Lot",new User("Ola",""));
        debts = (ArrayList<Expense>) group.getCurrentUserSuggestedPayDebtExpenses();
        toPay = new ArrayList<>();

        LinearLayout container = findViewById(R.id.container);


        infiniteScroller = new InfiniteScroller<Expense>(container, 29 + 9 + 29, new InfiniteScroller.SpecificOnClickListener() {
            @Override
            public void onClick(View view, Serializable object, int index) {

                Expense expense = (Expense) object;
                CheckBox checkBox = ((CheckBox) ((ConstraintLayout) ((FrameLayout) ((ConstraintLayout) view.getParent())
                        .getChildAt(1))
                        .getChildAt(0))
                        .getChildAt(2));

                ConstraintLayout constraintLayout = ((ConstraintLayout) view.getParent());

                checkBox.setChecked(!checkBox.isChecked());
                if (checkBox.isChecked()) {
                    if (expense.getAmount() < 0) {
                        constraintLayout.setBackgroundColor(getColor(R.color.accent_secondary_transparent));
                    } else if (expense.getAmount() > 0) {
                        constraintLayout.setBackgroundColor(getColor(R.color.accent_variant_transparent));
                    }
                    toPay.add(expense);
                }else{
                    constraintLayout.setBackgroundColor(getColor(R.color.transparent));
                    toPay.remove(expense);
                }


            }
        }, PaymentListElement::newInstance, this);


    }

    public void evenChecked(View view) {
        //TODO:: update in firestore toPay
    }

    @Override
    protected void onResume() {
        super.onResume();
        infiniteScroller.populate(debts);
    }
}