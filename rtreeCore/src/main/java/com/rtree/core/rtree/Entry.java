package com.rtree.core.rtree;


import com.rtree.core.rtree.geometry.Geometry;
import com.rtree.core.rtree.geometry.HasGeometry;

public interface Entry<T, S extends Geometry> extends HasGeometry {

    T value();

    @Override
    S geometry();

}