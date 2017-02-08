package com.rtree.core.rtree;


import com.rtree.core.rtree.geometry.Geometry;

import java.util.List;

@FunctionalInterface
public interface Selector {

    <T, S extends Geometry> Node<T, S> select(Geometry g, List<? extends Node<T, S>> nodes);

}
