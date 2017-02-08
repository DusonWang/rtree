package com.rtree.core.rtree.fbs.generated;

public final class GeometryTypeExtra {
    public static final byte Point = 0;
    public static final byte Box = 1;
    public static final byte Circle = 2;
    public static final byte Line = 3;
    private static final String[] names = {"Point", "Box", "Circle", "Line",};

    private GeometryTypeExtra() {
    }

    public static String name(int e) {
        return names[e];
    }
}
