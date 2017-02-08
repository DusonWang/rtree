package com.rtree.core.rtree;

import com.github.davidmoten.guavamini.Optional;
import com.rtree.core.rtree.geometry.Geometry;

public final class SerializerHelper {

    private SerializerHelper() {
    }

    public static <T, S extends Geometry> RTree<T, S> create(Optional<Node<T, S>> root, int size, Context<T, S> context) {
        return RTree.create(root, size, context);
    }

}
