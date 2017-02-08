package com.rtree.core.rtree;

import com.rtree.core.rtree.geometry.HasGeometry;
import com.rtree.core.rtree.geometry.ListPair;

import java.util.List;

@FunctionalInterface
public interface Splitter {

    <T extends HasGeometry> ListPair<T> split(List<T> items, int minSize);
}
