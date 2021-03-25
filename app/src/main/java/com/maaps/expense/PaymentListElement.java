package com.maaps.expense;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.maaps.expense.helpers.Utils;

import java.io.Serializable;

import modelv2.Expense;


public class PaymentListElement extends Fragment {

    private static final String ARG_PARAM1 = "expense";
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

    private void expenseFormatter(
            TextView amount,
            TextView userName,ImageView circle,
            TextView borrowerName) {

        amount.setText(Utils.formatPriceLocale(expense.getAmount().floatValue()));
        userName.setText(expense.getPayer().getName());

        //....getBorrowers().get(0) <- 0 because in payments list expenses always have one borrower.
        borrowerName.setText(expense.getBorrowers().get(0).getName());

        initializeCircleColor(circle);
    }

    private void initializeCircleColor(ImageView circle) {
        if(isCurrentUserAPayer()) {
            circle.setImageResource(R.drawable.circle_red);
        }else if(isCurrentUserABorrower()){
            circle.setImageResource(R.drawable.cricle_teal);
        }else{
            circle.setImageResource(R.drawable.cricle_green);
        }
    }

    private boolean isCurrentUserABorrower() {
        //....getBorrowers().get(0) <- 0 because in payments list expenses always have one borrower.
        return expense.getBorrowers().get(0).getId().equals(
                FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

    private boolean isCurrentUserAPayer() {
        return expense.getPayer().getId().equals(
                FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_payment_list_element, container, false);
        if (expense != null) {

            TextView amount = root.findViewById(R.id.amount);
            TextView userName = root.findViewById(R.id.user_name);
            ImageView circle = root.findViewById(R.id.circle);
            TextView borrower = root.findViewById(R.id.to);

            expenseFormatter(amount, userName,circle,borrower);

        }
        return root;

    }
}