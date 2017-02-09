package com.rtree.core.rtree.internal;


import com.github.davidmoten.guavamini.Optional;
import com.rtree.core.rtree.Entry;
import com.rtree.core.rtree.Node;
import com.rtree.core.rtree.geometry.Geometry;

import java.util.List;

public final class NodeAndEntries<T, S extends Geometry> {

    private final Optional<? extends Node<T, S>> node;
    private final List<Entry<T, S>> entries;
    private final int count;

    NodeAndEntries(Optional<? extends Node<T, S>> node,
                   List<Entry<T, S>> entries, int countDeleted) {
        this.node = node;
        this.entries = entries;
        this.count = countDeleted;
    }

    public Optional<? extends Node<T, S>> node() {
        return node;
    }

    public List<Entry<T, S>> entriesToAdd() {
        return entries;
    }

    public int countDeleted() {
        return count;
    }


}
