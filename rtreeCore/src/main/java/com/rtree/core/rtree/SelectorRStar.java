package com.rtree.core.rtree;


import com.rtree.core.rtree.geometry.Geometry;

import java.util.List;

public final class SelectorRStar implements Selector {

    private static final Selector overlapAreaSelector = new SelectorMinimalOverlapArea();
    private static final Selector areaIncreaseSelector = new SelectorMinimalAreaIncrease();

    @Override
    public <T, S extends Geometry> Node<T, S> select(Geometry g, List<? extends Node<T, S>> nodes) {
        boolean leafNodes = nodes.get(0) instanceof Leaf;
        if (leafNodes)
            return overlapAreaSelector.select(g, nodes);
        else
            return areaIncreaseSelector.select(g, nodes);
    }

}
