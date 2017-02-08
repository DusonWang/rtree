package com.rtree.core.rtree;


import com.rtree.core.rtree.geometry.Geometry;

import java.util.List;

@FunctionalInterface
public interface NonLeafFactory<T, S extends Geometry> {

    NonLeaf<T, S> createNonLeaf(List<? extends Node<T, S>> children, Context<T, S> context);
}
