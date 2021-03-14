package com.example.mainactivity.helpers;

import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;


import java.io.Serializable;
import java.util.ArrayList;

public class InfiniteScroller<T extends Serializable> {

    LinearLayout container;
    int height;     //Height of one element in dp;
    SpecificOnClickListener onClickListener;
    AbstractFactory factory;
    AppCompatActivity app;
    ArrayList<T> items;

    public InfiniteScroller(LinearLayout container, int height, SpecificOnClickListener onClickListener, AbstractFactory factory, AppCompatActivity app) {
        this.container = container;
        this.height = height;
        this.onClickListener = onClickListener;
        this.factory = factory;
        this.app = app;
        items = new ArrayList<T>();

    }

    private void clean(){
        Animation anim = AnimationUtils.makeInChildBottomAnimation(app.getApplicationContext());//makeOutAnimation(app.getApplicationContext(),true);
        anim.setInterpolator(new AccelerateInterpolator());
        anim.setDuration(100);
        anim.setFillAfter(true);
     //   new Handler().postDelayed(new Runnable() { // Not with Animation listner becouse it is not realible for many objects
      //      public void run() {
    //            container.clearAnimation();
                container.removeAllViews();
                populateWithItems();
       //     }
     //   }, anim.getDuration()+10);
      //  container.startAnimation(anim);
    }


    private void populateWithItems(){
        int i = 0;
        for (T item : items) {

            FragmentTransaction transaction = app.getSupportFragmentManager().beginTransaction();
            //transaction.setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out);

            ConstraintLayout constraintLayout = new ConstraintLayout(app);
            Fragment fragment = factory.newInstance(item, i);

            constraintLayout.setId(View.generateViewId());
            container.addView(constraintLayout);
            Button button = new Button(app);
            button.setId(View.generateViewId());
            button.setBackgroundColor(Color.TRANSPARENT);//Color.valueOf(0.5f,0,0,0.5f).toArgb());//);
            button.setLayoutParams(constraintLayout.getLayoutParams());


            button.setHeight(Utils.dpToPx(height, app));

            int finalI = i;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onClick(v, item, finalI);
                }
            });

            constraintLayout.addView(button);
            transaction.add(constraintLayout.getId(), fragment, String.valueOf(item.hashCode()));
            i++;
            transaction.commit();
        }
    }
    public void populate(ArrayList<T> items) {
        this.items = new ArrayList<>();
        this.items = items;
            clean();
    }


    public interface SpecificOnClickListener {

        void onClick(View view, Serializable object, int index);

    }


}
