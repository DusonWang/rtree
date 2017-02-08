package com.rtree.core.rtree;


import com.rtree.core.rtree.geometry.Geometry;

import java.util.List;

@FunctionalInterface
public interface LeafFactory<T, S extends Geometry> {

    Leaf<T, S> createLeaf(List<Entry<T, S>> entries, Context<T, S> context);

}
