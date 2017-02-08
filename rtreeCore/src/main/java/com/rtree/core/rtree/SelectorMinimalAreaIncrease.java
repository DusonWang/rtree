package com.rtree.core.rtree;

import com.rtree.core.rtree.geometry.Geometry;
import com.rtree.core.rtree.internal.Comparators;

import java.util.List;

import static java.util.Collections.min;

public final class SelectorMinimalAreaIncrease implements Selector {

    @Override
    public <T, S extends Geometry> Node<T, S> select(Geometry g, List<? extends Node<T, S>> nodes) {
        return min(nodes, Comparators.areaIncreaseThenAreaComparator(g.mbr()));
    }
}
