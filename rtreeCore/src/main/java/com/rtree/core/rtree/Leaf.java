package com.rtree.core.rtree;


import com.rtree.core.rtree.geometry.Geometry;

import java.util.List;

public interface Leaf<T, S extends Geometry> extends Node<T, S> {

    List<Entry<T, S>> entries();

    Entry<T, S> entry(int i);

}