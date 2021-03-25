package com.maaps.expense.helpers.infiniteScroller;

import android.widget.LinearLayout;

public class ElementsOutline {
    LinearLayout container;
    int elementHeightDp;

    ElementsOutline(LinearLayout container, int elementHeightDp) {
        this.container = container;
        this.elementHeightDp = elementHeightDp;
    }

    public LinearLayout getContainer() {
        return container;
    }

    public int getElementHeightDp() {
        return elementHeightDp;
    }
}