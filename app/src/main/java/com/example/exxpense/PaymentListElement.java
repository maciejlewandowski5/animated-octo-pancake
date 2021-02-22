package com.example.exxpense;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;
import java.text.DecimalFormat;

import model.Expense;


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
            expense = (Expense) getArguments().getSerializable(ARG_PARAM1);

        }
    }

    private void expenseFormatter(TextView amount, TextView userName,ImageView circle) {
        DecimalFormat format = new DecimalFormat("#.##");
        amount.setText(format.format(expense.getAmount()));
        userName.setText(expense.getUser());
        if(expense.getAmount()<0) {
            circle.setImageResource(R.drawable.circle_red);
        }else if (expense.getAmount()>0){

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

            expenseFormatter(amount, userName,circle);

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