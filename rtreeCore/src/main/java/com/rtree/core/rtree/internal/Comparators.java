package com.rtree.core.rtree.internal;


import com.rtree.core.rtree.Entry;
import com.rtree.core.rtree.geometry.Geometry;
import com.rtree.core.rtree.geometry.HasGeometry;
import com.rtree.core.rtree.geometry.Rectangle;

import java.util.Comparator;
import java.util.List;

public final class Comparators {

    private Comparators() {
    }

    public static <T extends HasGeometry> Comparator<HasGeometry> overlapAreaThenAreaIncreaseThenAreaComparator(
            final Rectangle r, final List<T> list) {
        return (HasGeometry g1, HasGeometry g2) -> Float.compare(overlapArea(r, list, g1), overlapArea(r, list, g2)) == 0 ? (Float.compare(areaIncrease(r, g1), areaIncrease(r, g2)) == 0 ? Float.compare(area(r, g1), area(r, g2)) : Float.compare(areaIncrease(r, g1), areaIncrease(r, g2))) : Float.compare(overlapArea(r, list, g1), overlapArea(r, list, g2));
    }

    private static float area(final Rectangle r, HasGeometry g1) {
        return g1.geometry().mbr().add(r).area();
    }

    public static <T extends HasGeometry> Comparator<HasGeometry> areaIncreaseThenAreaComparator(
            final Rectangle r) {
        return (HasGeometry g1, HasGeometry g2) -> Float.compare(areaIncrease(r, g1), areaIncrease(r, g2)) == 0 ? Float.compare(area(r, g1), area(r, g2)) : Float.compare(areaIncrease(r, g1), areaIncrease(r, g2));
    }

    private static float overlapArea(Rectangle r, List<? extends HasGeometry> list, HasGeometry g) {
        Rectangle gPlusR = g.geometry().mbr().add(r);
        float m = 0;
        for (HasGeometry other : list) {
            if (other != g) {
                m += gPlusR.intersectionArea(other.geometry().mbr());
            }
        }
        return m;
    }

    private static float areaIncrease(Rectangle r, HasGeometry g) {
        Rectangle gPlusR = g.geometry().mbr().add(r);
        return gPlusR.area() - g.geometry().mbr().area();
    }

    public static <T, S extends Geometry> Comparator<Entry<T, S>> ascendingDistance(
            final Rectangle r) {
        return (Entry<T, S> e1, Entry<T, S> e2) -> Double.compare(e1.geometry().distance(r), e2.geometry().distance(r));
    }

}
