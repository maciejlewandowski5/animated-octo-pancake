package com.example.exxpense;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Locale;

import model.Expense;


public class ListElement extends Fragment {


    private static final String ARG_PARAM1 = "param1";


    private Expense expense;

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
            expense = (Expense) getArguments().getSerializable(ARG_PARAM1);
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

            title.setText(expense.getTitle());
            user.setText(expense.getUser());

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm", Locale.getDefault());

            date.setText(simpleDateFormat.format(expense.getDate()));

            amount.setText(String.format(Locale.getDefault(), "#.##", expense.getAmount()));
        }
        return root;
    }
}