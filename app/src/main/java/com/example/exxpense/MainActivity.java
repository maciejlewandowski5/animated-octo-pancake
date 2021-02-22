package com.example.exxpense;


import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.exxpense.helpers.InfiniteScroller;

import java.io.Serializable;
import java.util.ArrayList;

import model.Expense;
import model.GroupManager;

public class MainActivity extends AppCompatActivity {
    private static final String EXPENSE = "EXPENSE";

    View floatButton;
    ConstraintLayout history;
    TextView historyTex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout container = findViewById(R.id.container);
        floatButton = findViewById(R.id.floatingActionButton);
        history = findViewById(R.id.history_container);
        historyTex = findViewById(R.id.history);

        MainActivity that =  this;
        InfiniteScroller<Expense> infiniteScroller = new InfiniteScroller<>(container, 11 +11+2+ 9 + 18 + 12 + 18, new InfiniteScroller.SpecificOnClickListener() {
            @Override
            public void onClick(View view, Serializable object, int index) {
                Intent intent = new Intent(that,ExpenseEditor.class);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(that, new Pair<>(history,"cont"));
                intent.putExtra(EXPENSE,object);
                startActivity(intent,options.toBundle());
            }
        },ListElement::newInstance,this);

        ArrayList<Expense> expenses = new ArrayList<Expense>();
        for(int i=0;i<10;i++){
            expenses.add(new Expense());
        }

        infiniteScroller.populate(expenses);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =fragmentManager.beginTransaction();
        Fragment topBar = TopBar.newInstance(new GroupManager(),true);
        fragmentTransaction.replace(R.id.fragment,topBar);
        fragmentTransaction.commit();
    }

    public void startPaymentsList(View view) {
        Intent intent = new Intent(this,PaymentsList.class);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, new Pair<>(history,"cont"));
        startActivity(intent,options.toBundle());
    }
}