package com.rtree.core.rtree;

import com.rtree.core.rtree.geometry.Geometry;
import com.rtree.core.rtree.internal.FactoryDefault;

final class Factories {

    private Factories() {
    }

    static <T, S extends Geometry> Factory<T, S> defaultFactory() {
        return FactoryDefault.instance();
    }
}
