package com.rtree.core.rtree;


import com.rtree.core.rtree.geometry.Geometry;

public interface Factory<T, S extends Geometry> extends LeafFactory<T, S>,
    NonLeafFactory<T, S>, EntryFactory<T, S> {
}
