package com.maaps.expense;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.maaps.expense.helpers.Utils;

public class SplashScreen extends Fragment {

    public SplashScreen() {
        // Required empty public constructor
    }


    public static SplashScreen newInstance() {
        SplashScreen fragment = new SplashScreen();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_splash_screen, container, false);

        ConstraintLayout background = root.findViewById(R.id.blacker);

        background.setOnClickListener(
                v -> Utils.toastMessage(
                        getString(R.string.please_wait_for_connection),
                        requireActivity()));

        return root;
    }

    public void show() {
        FragmentActivity activity = showFragment();
        hideStructureUnderFragment(activity);
    }
    public void hide() {
        FragmentActivity activity = hideFragment();
        showStructureUnderFragment(activity);
    }

    private FragmentActivity showFragment() {
        FragmentActivity activity = requireActivity();
        FragmentTransaction transaction1 = activity.getSupportFragmentManager().beginTransaction();
        transaction1.show(this);
        transaction1.commitNow();
        return activity;
    }
    private FragmentActivity hideFragment() {
        FragmentActivity activity = requireActivity();
        FragmentTransaction transaction1 = activity.getSupportFragmentManager().beginTransaction();
        transaction1.hide(this);
        transaction1.commitNow();
        return activity;
    }

    private void showStructureUnderFragment(FragmentActivity activity) {
        ((TextView) activity.findViewById(R.id.textView3)).setText(activity.getString(R.string.your_balance));
        ((TextView) activity.findViewById(R.id.history)).setText(activity.getString(R.string.history));
        ((Button) activity.findViewById(R.id.imageButton2)).setText(activity.getString(R.string.payments));
        ((Button) activity.findViewById(R.id.imageButton2)).setVisibility(View.VISIBLE);
        ((View) activity.findViewById(R.id.floatingActionButton)).setVisibility(View.VISIBLE);
    }

    private void hideStructureUnderFragment(FragmentActivity activity) {
        ((TextView) activity.findViewById(R.id.textView3)).setText("");
        ((TextView) activity.findViewById(R.id.history)).setText("");
        ((Button) activity.findViewById(R.id.imageButton2)).setText("");
        ((Button) activity.findViewById(R.id.imageButton2)).setVisibility(View.INVISIBLE);
        ((View) activity.findViewById(R.id.floatingActionButton)).setVisibility(View.INVISIBLE);
    }





}