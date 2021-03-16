package com.example.mainactivity.helpers;

import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.FragmentTransaction;

import java.io.Serializable;
import java.util.ArrayList;

public class ExpandableInfiniteScroller<T extends Serializable> extends InfiniteScroller<T>{


    public ExpandableInfiniteScroller(LinearLayout container, int height, SpecificOnClickListener onClickListener, OnPenultimatePageWasScrolled onPenultimatePageWasScrolled, AbstractFactory factory, AppCompatActivity app) {
        super(container, height, onClickListener, onPenultimatePageWasScrolled, factory, app);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void populateWithItems() {
        super.populateWithItems();
        /*TextView textView = new TextView(app);
        textView.setText("see all");
        textView.setId(View.generateViewId());
        LinearLayout linearLayout = new LinearLayout(app);
        linearLayout.
        constraintLayout.setId(View.generateViewId());
        ConstraintSet constraintSet = new ConstraintSet();
        ConstraintSet constraintSet2 = new ConstraintSet();

        container.addView(constraintLayout);
        Button button = new Button(app);
        button.setId(View.generateViewId());

        button.setId(View.generateViewId());
        button.setBackgroundColor(Color.valueOf(0.5f,0,0,0.5f).toArgb());//);
        button.setLayoutParams(constraintLayout.getLayoutParams());
        button.setHeight(Utils.dpToPx(height, app));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("I WAC CLICKEd");
            }
        });
        constraintLayout.addView(textView);
        constraintLayout.addView(button);


         */
    }
}
