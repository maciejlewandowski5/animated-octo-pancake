package com.maaps.expense;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import modelv2.User;


public class BorrowerFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";

    private modelv2.User user;


    public BorrowerFragment() {
        // Required empty public constructor
    }

    public static BorrowerFragment newInstance(modelv2.User user) {
        BorrowerFragment fragment = new BorrowerFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = (User) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_borrower, container, false);
        TextView userName = root.findViewById(R.id.user_name);

        userName.setText(user.getName());

        return root;
    }
}