package com.rtree.core.rtree;


import com.rtree.core.rtree.geometry.Geometry;

public interface EntryFactory<T, S extends Geometry> {

    Entry<T, S> createEntry(T value, S geometry);

}
