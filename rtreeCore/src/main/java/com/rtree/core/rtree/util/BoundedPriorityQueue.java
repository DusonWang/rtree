package com.rtree.core.rtree.util;

import com.github.davidmoten.guavamini.Preconditions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class BoundedPriorityQueue<T> {

    private final PriorityQueue<T> queue;
    private final Comparator<? super T> comparator;
    private final int maxSize;

    public BoundedPriorityQueue(final int maxSize, final Comparator<? super T> comparator) {
        Preconditions.checkArgument(maxSize > 0, "maxSize must be > 0");
        Preconditions.checkNotNull(comparator, "comparator cannot be null");
        this.queue = new PriorityQueue<>(reverse(comparator));
        this.comparator = comparator;
        this.maxSize = maxSize;
    }

    private static <T> Comparator<T> reverse(final Comparator<T> comparator) {
        return (T o1, T o2) -> comparator.compare(o2, o1);
    }

    public static <T> BoundedPriorityQueue<T> create(final int maxSize, final Comparator<? super T> comparator) {
        return new BoundedPriorityQueue<>(maxSize, comparator);
    }

    public void add(final T t) {
        if (t == null) {
            return;
        }
        if (queue.size() >= maxSize) {
            final T maxElement = queue.peek();
            if (comparator.compare(maxElement, t) < 1) {
                return;
            } else {
                queue.poll();
            }
        }
        queue.add(t);
    }

    public List<T> asList() {
        return Collections.unmodifiableList(new ArrayList<>(queue));
    }

    public List<T> asOrderedList() {
        List<T> list = new ArrayList<>(queue);
        Collections.sort(list, comparator);
        return list;
    }

}