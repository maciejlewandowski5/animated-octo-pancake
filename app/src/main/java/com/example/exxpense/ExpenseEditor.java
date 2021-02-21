package com.example.exxpense;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import model.GroupManager;

public class ExpenseEditor extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_editor);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =fragmentManager.beginTransaction();
        Fragment topBar = TopBar.newInstance(new GroupManager(),true);
        fragmentTransaction.replace(R.id.fragment,topBar);
        fragmentTransaction.commit();
    }
}