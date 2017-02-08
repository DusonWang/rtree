package com.rtree.core.rtree.rx.operators;

import com.rtree.core.rtree.util.BoundedPriorityQueue;
import rx.Observable.Operator;
import rx.Subscriber;

import java.util.Comparator;
import java.util.List;

public final class OperatorBoundedPriorityQueue<T> implements Operator<T, T> {

    private final int maximumSize;
    private final Comparator<? super T> comparator;

    public OperatorBoundedPriorityQueue(int maximumSize, Comparator<? super T> comparator) {
        this.maximumSize = maximumSize;
        this.comparator = comparator;
    }

    @Override
    public Subscriber<? super T> call(final Subscriber<? super T> child) {
        final BoundedPriorityQueue<T> q = new BoundedPriorityQueue<>(maximumSize, comparator);
        return new Subscriber<T>(child) {
            @Override
            public void onStart() {
                request(Long.MAX_VALUE);
            }

            @Override
            public void onCompleted() {
                List<T> list = q.asOrderedList();
                for (T t : list) {
                    if (isUnsubscribed()) {
                        return;
                    } else {
                        child.onNext(t);
                    }
                }
                if (!isUnsubscribed()) {
                    child.onCompleted();
                }
            }

            @Override
            public void onError(Throwable t) {
                if (!isUnsubscribed())
                    child.onError(t);
            }

            @Override
            public void onNext(T t) {
                if (!isUnsubscribed())
                    q.add(t);
            }
        };
    }

}
