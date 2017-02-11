package com.rtree.core.rtree;

import com.github.davidmoten.guavamini.Preconditions;
import com.github.davidmoten.guavamini.annotations.VisibleForTesting;
import com.rtree.core.rtree.geometry.HasGeometry;
import com.rtree.core.rtree.geometry.ListPair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class SplitterRStar implements Splitter {

    private static final Comparator<HasGeometry> INCREASING_X_LOWER = (HasGeometry n1, HasGeometry n2) -> Float.compare(n1.geometry().mbr().x1(), n2.geometry().mbr().x1());

    private static final Comparator<HasGeometry> INCREASING_X_UPPER = (HasGeometry n1, HasGeometry n2) -> Float.compare(n1.geometry().mbr().y1(), n2.geometry().mbr().y1());

    private static final Comparator<HasGeometry> INCREASING_Y_LOWER = (HasGeometry n1, HasGeometry n2) -> Float.compare(n1.geometry().mbr().y1(), n2.geometry().mbr().y1());

    private static final Comparator<HasGeometry> INCREASING_Y_UPPER = (HasGeometry n1, HasGeometry n2) -> Float.compare(n1.geometry().mbr().y2(), n2.geometry().mbr().y2());

    private final Comparator<ListPair<?>> comparator;

    public SplitterRStar() {
        this.comparator = (ListPair<?> p1, ListPair<?> p2) -> Float.compare(overlap(p1), overlap(p2)) == 0 ? Float.compare(p1.areaSum(), p2.areaSum()) : Float.compare(overlap(p1), overlap(p2));
    }

    private static Comparator<HasGeometry> comparator(SortType sortType) {
        switch (sortType) {
            case X_LOWER:
                return INCREASING_X_LOWER;
            case X_UPPER:
                return INCREASING_X_UPPER;
            case Y_LOWER:
                return INCREASING_Y_LOWER;
            case Y_UPPER:
                return INCREASING_Y_UPPER;
            default:
                throw new IllegalArgumentException("unknown SortType " + sortType);
        }
    }

    private static <T extends HasGeometry> float marginValueSum(
            List<ListPair<T>> list) {
        float sum = 0;
        for (ListPair<T> p : list)
            sum += p.marginSum();
        return sum;
    }

    @VisibleForTesting
    private static <T extends HasGeometry> List<ListPair<T>> getPairs(int minSize, List<T> list) {
        List<ListPair<T>> pairs = new ArrayList<>(list.size() - 2 * minSize + 1);
        for (int i = minSize; i < list.size() - minSize + 1; i++) {
            List<T> list1 = list.subList(0, i);
            List<T> list2 = list.subList(i, list.size());
            ListPair<T> pair = new ListPair<>(list1, list2);
            pairs.add(pair);
        }
        return pairs;
    }

    private static float overlap(ListPair<? extends HasGeometry> pair) {
        return pair.group1().geometry().mbr()
                .intersectionArea(pair.group2().geometry().mbr());
    }

    @Override
    public <T extends HasGeometry> ListPair<T> split(List<T> items, int minSize) {
        Preconditions.checkArgument(!items.isEmpty());

        List<ListPair<T>> pairs = null;
        float lowestMarginSum = Float.MAX_VALUE;
        List<T> list = null;
        for (SortType sortType : SortType.values()) {
            if (list == null) {
                list = new ArrayList<>(items);
            }
            list.sort(comparator(sortType));
            List<ListPair<T>> p = getPairs(minSize, list);
            float marginSum = marginValueSum(p);
            if (marginSum < lowestMarginSum) {
                lowestMarginSum = marginSum;
                pairs = p;
                list = null;
            }
        }
        assert pairs != null;
        return Collections.min(pairs, comparator);
    }

    private enum SortType {
        X_LOWER,
        X_UPPER,
        Y_LOWER,
        Y_UPPER;
    }

}
