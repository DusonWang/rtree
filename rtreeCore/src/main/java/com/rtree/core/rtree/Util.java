package com.rtree.core.rtree;

import com.github.davidmoten.guavamini.Preconditions;
import com.rtree.core.rtree.geometry.Geometries;
import com.rtree.core.rtree.geometry.HasGeometry;
import com.rtree.core.rtree.geometry.Rectangle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public final class Util {

    private Util() {
    }

    public static Rectangle mbr(Collection<? extends HasGeometry> items) {
        Preconditions.checkArgument(!items.isEmpty());
        float minX1 = Float.MAX_VALUE;
        float minY1 = Float.MAX_VALUE;
        float maxX2 = -Float.MAX_VALUE;
        float maxY2 = -Float.MAX_VALUE;
        for (final HasGeometry item : items) {
            Rectangle r = item.geometry().mbr();
            if (r.x1() < minX1)
                minX1 = r.x1();
            if (r.y1() < minY1)
                minY1 = r.y1();
            if (r.x2() > maxX2)
                maxX2 = r.x2();
            if (r.y2() > maxY2)
                maxY2 = r.y2();
        }
        return Geometries.rectangle(minX1, minY1, maxX2, maxY2);
    }

    public static <T> List<T> add(List<T> list, T element) {
        final ArrayList<T> result = new ArrayList<>(list.size() + 2);
        result.addAll(list);
        result.add(element);
        return result;
    }

    public static <T> List<T> remove(List<? extends T> list,
                                     List<? extends T> elements) {
        final ArrayList<T> result = new ArrayList<>(list);
        result.removeAll(elements);
        return result;
    }

    public static <T> List<? extends T> replace(List<? extends T> list, T element, List<T> replacements) {
        List<T> list2 = list.stream().filter(node -> node != element).collect(Collectors.toList());
        list2.addAll(replacements);
        return list2;
    }

}