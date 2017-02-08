package com.rtree.core.rtree;

import com.rtree.core.rtree.geometry.Geometry;
import com.rtree.core.rtree.internal.FactoryDefault;

public final class Factories {

    private Factories() {
    }

    public static <T, S extends Geometry> Factory<T, S> defaultFactory() {
        return FactoryDefault.instance();
    }
}
