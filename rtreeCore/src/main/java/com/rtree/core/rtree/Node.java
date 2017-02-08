package com.rtree.core.rtree;


import com.rtree.core.rtree.geometry.Geometry;
import com.rtree.core.rtree.geometry.HasGeometry;
import com.rtree.core.rtree.internal.NodeAndEntries;
import rx.Subscriber;
import rx.functions.Func1;

import java.util.List;

public interface Node<T, S extends Geometry> extends HasGeometry {

    List<Node<T, S>> add(Entry<? extends T, ? extends S> entry);

    NodeAndEntries<T, S> delete(Entry<? extends T, ? extends S> entry, boolean all);

    void searchWithoutBackPressure(Func1<? super Geometry, Boolean> criterion, Subscriber<? super Entry<T, S>> subscriber);

    int count();

    Context<T, S> context();

}
