package com.example.mainactivity;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import model.Expense;


public class ListElement extends Fragment {


    private static final String ARG_PARAM1 = "param1";


    private modelv2.Expense expense;

    public ListElement() {
        // Required empty public constructor
    }


    public static ListElement newInstance(Serializable... expense) {
        ListElement fragment = new ListElement();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_list_element, container, false);
        if(expense!=null) {
            TextView title = root.findViewById(R.id.titleList);
            TextView user = root.findViewById(R.id.user);
            TextView date = root.findViewById(R.id.date);
            TextView amount = root.findViewById(R.id.amount);

            title.setText(expense.getName());
            user.setText(expense.getPayer().getName());

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm", Locale.getDefault());

            date.setText(simpleDateFormat.format(expense.getDateTime()));

            DecimalFormat format = new DecimalFormat("#.##");
            amount.setText(format.format(expense.getAmount()));
        }
        return root;
    }
}