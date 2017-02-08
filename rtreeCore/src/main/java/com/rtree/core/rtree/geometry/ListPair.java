package com.rtree.core.rtree.geometry;

import java.util.List;

public final class ListPair<T extends HasGeometry> {
    private final Group<T> group1;
    private final Group<T> group2;
    private final float marginSum;
    private float areaSum = -1;

    public ListPair(List<T> list1, List<T> list2) {
        this.group1 = new Group<>(list1);
        this.group2 = new Group<>(list2);
        this.marginSum = group1.geometry().mbr().perimeter()
                + group2.geometry().mbr().perimeter();
    }

    public Group<T> group1() {
        return group1;
    }

    public Group<T> group2() {
        return group2;
    }

    public float areaSum() {
        if (Float.compare(areaSum, -1) == 0)
            areaSum = group1.geometry().mbr().area()
                    + group2.geometry().mbr().area();
        return areaSum;
    }

    public float marginSum() {
        return marginSum;
    }

}
