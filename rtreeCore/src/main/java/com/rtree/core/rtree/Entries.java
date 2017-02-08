package com.rtree.core.rtree;


import com.rtree.core.rtree.geometry.Geometry;
import com.rtree.core.rtree.internal.EntryDefault;

public final class Entries {

    private Entries() {
    }

    public static <T, S extends Geometry> Entry<T, S> entry(T object, S geometry) {
        return EntryDefault.entry(object, geometry);
    }

}
