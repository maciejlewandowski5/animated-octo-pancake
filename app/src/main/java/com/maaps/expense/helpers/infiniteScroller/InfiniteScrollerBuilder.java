package com.maaps.expense.helpers.infiniteScroller;

import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.maaps.expense.helpers.AbstractFactory;

import java.io.Serializable;

public class InfiniteScrollerBuilder<T extends Serializable> {

    private final int itemHeightInDp;
    private final LinearLayout parentContainer;
    private InfiniteScroller.SpecificOnClickListener mOnClickListener;
    private InfiniteScroller.OnPenultimatePageWasScrolled mOnPenultimatePageWasScrolled;
    private final AbstractFactory fragmentFactoryMethod;

    public InfiniteScrollerBuilder(int itemHeightInDp,
                                   LinearLayout parentContainer,
                                   AbstractFactory fragmentFactoryMethod) {
        this.itemHeightInDp = itemHeightInDp;
        this.parentContainer = parentContainer;
        this.fragmentFactoryMethod = fragmentFactoryMethod;
    }

    public void onClickListener(InfiniteScroller.SpecificOnClickListener onClickListener){
        this.mOnClickListener = onClickListener;
    }

    public void onPenultimatePageWasScrolled(InfiniteScroller.OnPenultimatePageWasScrolled
                                                     onPenultimatePageWasScrolled){
        this.mOnPenultimatePageWasScrolled = onPenultimatePageWasScrolled;
    }

    public InfiniteScroller<T> buildInfiniteScroller(AppCompatActivity activity){
        ElementsOutline elementsOutline =
                new ElementsOutline(parentContainer,itemHeightInDp);
        ScrollerBehaviour scrollerBehaviour =
                new ScrollerBehaviour(
                        fragmentFactoryMethod,
                        mOnClickListener,
                        mOnPenultimatePageWasScrolled
                );

        return new InfiniteScroller<>(
                elementsOutline,
                scrollerBehaviour,
                activity);
    }
}
