package com.rtree.core.rtree;

import com.github.davidmoten.guavamini.Lists;
import com.github.davidmoten.guavamini.Optional;
import com.github.davidmoten.guavamini.Preconditions;
import com.github.davidmoten.guavamini.annotations.VisibleForTesting;
import com.rtree.core.rtree.geometry.HasGeometry;
import com.rtree.core.rtree.geometry.ListPair;
import com.rtree.core.rtree.geometry.Rectangle;
import com.rtree.core.rtree.util.Pair;

import java.util.ArrayList;
import java.util.List;

import static com.github.davidmoten.guavamini.Optional.absent;
import static com.github.davidmoten.guavamini.Optional.of;

public final class SplitterQuadratic implements Splitter {

    @VisibleForTesting
    private static <T extends HasGeometry> T getBestCandidateForGroup(List<T> list,
                                                                      Rectangle groupMbr) {
        Optional<T> minEntry = absent();
        Optional<Float> minArea = absent();
        for (final T entry : list) {
            final float area = groupMbr.add(entry.geometry().mbr()).area();
            if (!minArea.isPresent() || area < minArea.get()) {
                minArea = of(area);
                minEntry = of(entry);
            }
        }
        return minEntry.get();
    }

    @VisibleForTesting
    private static <T extends HasGeometry> Pair<T> worstCombination(List<T> items) {
        Optional<T> e1 = absent();
        Optional<T> e2 = absent();
        Optional<Float> maxArea = absent();
        int size = items.size();
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                T entry1 = items.get(i);
                T entry2 = items.get(j);
                final float area = entry1.geometry().mbr().add(entry2.geometry().mbr()).area();
                if (!maxArea.isPresent() || area > maxArea.get()) {
                    e1 = of(entry1);
                    e2 = of(entry2);
                    maxArea = of(area);
                }
            }
        }
        if (e1.isPresent())
            return new Pair<>(e1.get(), e2.get());
        else
            return new Pair<>(items.get(0), items.get(1));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends HasGeometry> ListPair<T> split(List<T> items, int minSize) {
        Preconditions.checkArgument(items.size() >= 2);
        final Pair<T> worstCombination = worstCombination(items);

        final List<T> group1 = Lists.newArrayList(worstCombination.value1());
        final List<T> group2 = Lists.newArrayList(worstCombination.value2());

        final List<T> remaining = new ArrayList<>(items);
        remaining.remove(worstCombination.value1());
        remaining.remove(worstCombination.value2());

        final int minGroupSize = items.size() / 2;
        while (!remaining.isEmpty()) {
            assignRemaining(group1, group2, remaining, minGroupSize);
        }
        return new ListPair<>(group1, group2);
    }

    private <T extends HasGeometry> void assignRemaining(final List<T> group1,
                                                         final List<T> group2, final List<T> remaining,
                                                         final int minGroupSize) {
        final Rectangle mbr1 = Util.mbr(group1);
        final Rectangle mbr2 = Util.mbr(group2);
        final T item1 = getBestCandidateForGroup(remaining, mbr1);
        final T item2 = getBestCandidateForGroup(remaining, mbr2);
        final boolean area1LessThanArea2 = item1.geometry().mbr().add(mbr1).area() <= item2.geometry().mbr().add(mbr2).area();
        if (area1LessThanArea2 && (group2.size() + remaining.size() - 1 >= minGroupSize) || !area1LessThanArea2 && (group1.size() + remaining.size() == minGroupSize)) {
            group1.add(item1);
            remaining.remove(item1);
        } else {
            group2.add(item2);
            remaining.remove(item2);
        }
    }
}
