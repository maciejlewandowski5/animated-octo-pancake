package com.maaps.expense.helpers.infiniteScroller;

import com.maaps.expense.helpers.AbstractFactory;

public class ScrollerBehaviour {
    AbstractFactory factory;
    InfiniteScroller.SpecificOnClickListener onClickListener;
    InfiniteScroller.OnPenultimatePageWasScrolled onPenultimatePageWasScrolled;

    ScrollerBehaviour(AbstractFactory fragmentFactory, InfiniteScroller.SpecificOnClickListener onClickListener, InfiniteScroller.OnPenultimatePageWasScrolled onPenultimatePageWasScrolled) {
        this.factory = fragmentFactory;
        this.onClickListener = onClickListener;
        this.onPenultimatePageWasScrolled = onPenultimatePageWasScrolled;
    }

    public AbstractFactory getFactory() {
        return factory;
    }

    public InfiniteScroller.SpecificOnClickListener getOnClickListener() {
        return onClickListener;
    }

    public InfiniteScroller.OnPenultimatePageWasScrolled getOnPenultimatePageWasScrolled() {
        return onPenultimatePageWasScrolled;
    }
}