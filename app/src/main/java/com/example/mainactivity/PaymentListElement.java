package com.example.mainactivity;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.io.Serializable;
import java.text.DecimalFormat;

import modelv2.Expense;


public class PaymentListElement extends Fragment {

    private static final String ARG_PARAM1 = "param1";

    private Expense expense;


    public PaymentListElement() {
        // Required empty public constructor
    }

    public static PaymentListElement newInstance(Serializable ...expense) {
        PaymentListElement fragment = new PaymentListElement();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, expense[0]);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            expense = (modelv2.Expense) getArguments().getSerializable(ARG_PARAM1);

        }
    }

    private void expenseFormatter(TextView amount, TextView userName,ImageView circle, TextView borrowerName) {
        DecimalFormat format = new DecimalFormat("#.##");
        amount.setText(format.format(expense.getAmount()));
        String userNameString =expense.getPayer().getName();//.substring(0,8)+"...";
        userName.setText(userNameString);
        borrowerName.setText(expense.getBorrowers().get(0).getName());
        if(expense.getPayer().getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            circle.setImageResource(R.drawable.circle_red);
        }else if(expense.getBorrowers().get(0).getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            circle.setImageResource(R.drawable.cricle_teal);

        }else{
            circle.setImageResource(R.drawable.cricle_green);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_payment_list_element, container, false);
        if (expense != null) {
            CheckBox checkBox = root.findViewById(R.id.checkbox);
            TextView amount = root.findViewById(R.id.amount);
            TextView userName = root.findViewById(R.id.user_name);
            ImageView circle = root.findViewById(R.id.circle);
            TextView borrower = root.findViewById(R.id.to);

            expenseFormatter(amount, userName,circle,borrower);

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        //TODO:: add expense


                    }
                }
            });
        }
        return root;

    }
}