package com.example.exxpense;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.example.exxpense.helpers.InfiniteScroller;

import java.io.Serializable;
import java.util.ArrayList;

import model.Expense;
import model.GroupManager;

public class MainActivity extends AppCompatActivity {
    private static final String EXPENSE = "EXPENSE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout container = findViewById(R.id.container);


        MainActivity that =  this;
        InfiniteScroller<Expense> infiniteScroller = new InfiniteScroller<>(container, 11 +11+2+ 9 + 18 + 12 + 18, new InfiniteScroller.SpecificOnClickListener() {
            @Override
            public void onClick(View view, Serializable object, int index) {
                Intent intent = new Intent(that,ExpenseEditor.class);
                intent.putExtra(EXPENSE,object);
                startActivity(intent);
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
}