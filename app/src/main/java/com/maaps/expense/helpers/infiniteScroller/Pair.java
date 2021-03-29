package com.maaps.expense.helpers.infiniteScroller;

import java.util.Objects;

public class Pair<T,K> {
    T first;
    K second;

    public Pair(T first, K second) {
        this.first = first;
        this.second = second;
    }

    public Pair(T first) {
        this.first = first;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(first, pair.first);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }
}
