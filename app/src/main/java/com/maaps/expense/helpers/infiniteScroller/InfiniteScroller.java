package com.maaps.expense.helpers.infiniteScroller;

import android.graphics.Color;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;


import com.maaps.expense.helpers.Utils;

import java.io.Serializable;
import java.util.ArrayList;

public class InfiniteScroller<T extends Serializable> {

    final ElementsOutline elementsOutline;
    ScrollerBehaviour scrollerBehaviour;

    AppCompatActivity app;
    ArrayList<T> items;
    Integer loadedNumberOfItems;
    Integer maxScrolledItemIndex;
    final boolean[] latestPaginationWasRead = {false};


    InfiniteScroller(ElementsOutline elementsOutline,
                     ScrollerBehaviour scrollerBehaviour,
                     AppCompatActivity app) {
        this.elementsOutline = elementsOutline;

        this.app = app;
        items = new ArrayList<>();
        loadedNumberOfItems = 0;
        maxScrolledItemIndex = 0;
        this.scrollerBehaviour = scrollerBehaviour;

        ScrollView rootScrollView = ((ScrollView) this.elementsOutline.container.getParent());
        rootScrollView.getViewTreeObserver().addOnScrollChangedListener(
                () -> initializeOnScrollLogic(
                        rootScrollView,
                        app));

    }


    private void initializeOnScrollLogic(ScrollView rootScrollView,
                                         AppCompatActivity app) {
        int elementHeightDp = elementsOutline.getElementHeightDp();
        LinearLayout container = elementsOutline.getContainer();

        int scrolledElements = rootScrollView.getScrollY() / (int) Utils.dpToPx(elementHeightDp, app);
        float viewsPerPage = (float) rootScrollView.getHeight() / Utils.dpToPx(elementHeightDp, app);
        int scrolledPages = scrolledElements / (int) viewsPerPage;
        int totalNumberOfPages = (int) container.getChildCount() / (int) viewsPerPage;

        if (scrolledPages == totalNumberOfPages - 1) {
            if (!latestPaginationWasRead[0]) {
                scrollerBehaviour.getOnPenultimatePageWasScrolled().
                        onScrolled(
                                scrolledPages,
                                totalNumberOfPages,
                                scrolledElements
                        );
                latestPaginationWasRead[0] = true;
            }
        }
    }

    private void cleanAndPopulate() {
        elementsOutline.container.removeAllViews();
        populateWithItems(items);
    }

    protected void populateWithItems(ArrayList<T> items) {
        int i = 0;
        for (T item : items) {
            ConstraintLayout constraintLayout = prepareInnerContainer();
            createAndAddButton(i, item, constraintLayout);
            i = transactFragment(i, item, constraintLayout);
        }
    }

    private int transactFragment(int itemIndex, T item, ConstraintLayout constraintLayout) {
        FragmentTransaction transaction = app.getSupportFragmentManager().beginTransaction();
        Fragment fragment = scrollerBehaviour.getFactory().newInstance(item, itemIndex);
        transaction.add(constraintLayout.getId(), fragment, String.valueOf(item.hashCode()));
        itemIndex++;
        transaction.commit();
        return itemIndex;
    }

    private ConstraintLayout prepareInnerContainer() {
        ConstraintLayout constraintLayout = new ConstraintLayout(app);
        constraintLayout.setId(View.generateViewId());
        elementsOutline.container.addView(constraintLayout);

        return constraintLayout;
    }

    private void createAndAddButton(int i, T item, ConstraintLayout constraintLayout) {
        Button button = new Button(app);
        button.setId(View.generateViewId());

        button.setBackgroundColor(Color.TRANSPARENT);
        button.setLayoutParams(constraintLayout.getLayoutParams());
        button.setHeight(Utils.dpToPx(elementsOutline.elementHeightDp, app));

        button.setOnClickListener(
                v -> scrollerBehaviour.getOnClickListener().onClick(v, item, i));
        constraintLayout.addView(button);
    }


    public void extend(ArrayList<T> items) {
        populateWithItems(items);
        latestPaginationWasRead[0] = false;
    }

    public void populate(ArrayList<T> items) {
        this.items = new ArrayList<>();
        this.items = items;
        cleanAndPopulate();
    }


    public interface SpecificOnClickListener {
        void onClick(View view, Serializable object, int index);
    }

    public interface OnPenultimatePageWasScrolled {
        void onScrolled(int scrolledPages, int totalNumberOfPages, int scrolledElements);
    }


}
