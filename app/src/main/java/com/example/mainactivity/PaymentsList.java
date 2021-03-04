package com.example.mainactivity;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.example.mainactivity.helpers.InfiniteScroller;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import model.DebtManager;
import model.Expense;
import model.Group;
import model.GroupManager;
import model.User;

public class PaymentsList extends AppCompatActivity {

    private static final String TAG = "s";
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


        group = GroupManager.getInstance().getCurrentGroup();
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
        }, PaymentListElement::newInstance, this);


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        PaymentsList that = this;
        db.collection("Groups").document(group.getId()).addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w(TAG, "Listen failed.", error);
                    return;
                }

                if (value != null && value.exists()) {
                    DebtManager debtManager = new DebtManager(value);
                    debtManager.simplifyDebts();
                    // debtManager.reduceDebts();
                    debts = debtManager.getExpenses(that);
                    infiniteScroller.populate(debts);

                } else {
                    Log.d(TAG, "Current data: null");
                }


            }
        });
    }

    public void evenChecked(View view) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        for(Expense expense:toPay) {
            db.collection("Groups").document(group.getId()).collection("Expenses").add(expense.toMap())
            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    db.collection("Groups").document(group.getId())
                            .update(expense.getPayer().getId() + "." + expense.getBorrowers().get(0).getId(), FieldValue.increment(expense.getAmount()));
                }
            });
        }
        onBackPressed();
        //toPay.forEach(item ->
          //      db.collection("Groups")
            //            .document(group.getId()).collection("Expenses").add(item));

    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}