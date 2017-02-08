package com.rtree.core.rtree;

import com.github.davidmoten.guavamini.annotations.VisibleForTesting;
import com.rtree.core.rtree.geometry.Geometry;
import com.rtree.core.rtree.util.ImmutableStack;
import rx.Observable.OnSubscribe;
import rx.Producer;
import rx.Subscriber;
import rx.functions.Func1;
import rx.internal.operators.BackpressureUtils;

import java.util.concurrent.atomic.AtomicLong;

final class OnSubscribeSearch<T, S extends Geometry> implements
        OnSubscribe<Entry<T, S>> {

    private final Node<T, S> node;
    private final Func1<? super Geometry, Boolean> condition;

    OnSubscribeSearch(Node<T, S> node, Func1<? super Geometry, Boolean> condition) {
        this.node = node;
        this.condition = condition;
    }

    @Override
    public void call(Subscriber<? super Entry<T, S>> subscriber) {
        subscriber.setProducer(new SearchProducer<>(node, condition, subscriber));
    }

    @VisibleForTesting
    static class SearchProducer<T, S extends Geometry> implements Producer {

        private final Subscriber<? super Entry<T, S>> subscriber;
        private final Node<T, S> node;
        private final Func1<? super Geometry, Boolean> condition;
        private final AtomicLong requested = new AtomicLong(0);
        private volatile ImmutableStack<NodePosition<T, S>> stack;

        SearchProducer(Node<T, S> node, Func1<? super Geometry, Boolean> condition, Subscriber<? super Entry<T, S>> subscriber) {
            this.node = node;
            this.condition = condition;
            this.subscriber = subscriber;
            stack = ImmutableStack.create(new NodePosition<>(node, 0));
        }

        @Override
        public void request(long n) {
            try {
                if (n <= 0 || requested.get() == Long.MAX_VALUE) {
                } else if (n == Long.MAX_VALUE && requested.compareAndSet(0, Long.MAX_VALUE)) {
                    requestAll();
                } else
                    requestSome(n);
            } catch (RuntimeException e) {
                subscriber.onError(e);
            }
        }

        private void requestAll() {
            node.searchWithoutBackPressure(condition, subscriber);
            if (!subscriber.isUnsubscribed())
                subscriber.onCompleted();
        }

        private void requestSome(long n) {

            long previousCount = BackpressureUtils.getAndAddRequest(requested, n);
            if (previousCount == 0) {
                ImmutableStack<NodePosition<T, S>> st = stack;
                while (true) {
                    long r = requested.get();
                    st = BackPressure.search(condition, subscriber, st, r);
                    if (st.isEmpty()) {
                        stack = null;
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onCompleted();
                        }
                        return;
                    } else {
                        stack = st;
                        if (requested.addAndGet(-r) == 0)
                            return;
                    }
                }

            }
        }
    }

}
