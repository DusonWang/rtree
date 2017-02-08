package com.rtree.core.rtree.fbs;

import com.github.davidmoten.guavamini.Preconditions;
import com.rtree.core.rtree.*;
import com.rtree.core.rtree.fbs.generated.BoxExtra;
import com.rtree.core.rtree.fbs.generated.EntryExtra;
import com.rtree.core.rtree.fbs.generated.GeometryExtra;
import com.rtree.core.rtree.fbs.generated.NodeExtra;
import com.rtree.core.rtree.geometry.Geometries;
import com.rtree.core.rtree.geometry.Geometry;
import com.rtree.core.rtree.internal.NodeAndEntries;
import com.rtree.core.rtree.internal.NonLeafHelper;
import rx.Subscriber;
import rx.functions.Func1;

import java.util.ArrayList;
import java.util.List;

import static com.rtree.core.rtree.fbs.FlatBuffersHelper.*;

public final class NonLeafFlatBuffers<T, S extends Geometry> implements NonLeaf<T, S> {

    private final NodeExtra node;
    private final Context<T, S> context;
    private final Func1<byte[], ? extends T> deserializer;

    NonLeafFlatBuffers(NodeExtra node, Context<T, S> context,
                       Func1<byte[], ? extends T> deserializer) {
        Preconditions.checkNotNull(node);
        this.node = node;
        this.context = context;
        this.deserializer = deserializer;
    }

    @SuppressWarnings("unchecked")
    private static <T, S extends Geometry> void searchWithoutBackPressure(
            NodeExtra node, Func1<? super Geometry, Boolean> criterion,
            Subscriber<? super Entry<T, S>> subscriber,
            Func1<byte[], ? extends T> deserializer, EntryExtra entry,
            GeometryExtra geometry, BoxExtra box) {
        node.mbb(box);
        if (!criterion.call(Geometries.rectangle(box.minX(), box.minY(), box.maxX(), box.maxY())))
            return;
        int numChildren = node.childrenLength();
        NodeExtra child = new NodeExtra();
        if (numChildren > 0) {
            for (int i = 0; i < numChildren; i++) {
                if (subscriber.isUnsubscribed())
                    return;
                node.children(child, i);
                searchWithoutBackPressure(child, criterion, subscriber, deserializer, entry, geometry, box);
            }
        } else {
            int numEntries = node.entriesLength();
            for (int i = 0; i < numEntries; i++) {
                if (subscriber.isUnsubscribed())
                    return;
                node.entries(entry, i);
                entry.geometry(geometry);
                final Geometry g = toGeometry(geometry);
                if (criterion.call(g)) {
                    T t = parseObject(deserializer, entry);
                    Entry<T, S> ent = Entries.entry(t, (S) g);
                    subscriber.onNext(ent);
                }
            }
        }

    }

    @Override
    public List<Node<T, S>> add(Entry<? extends T, ? extends S> entry) {
        return NonLeafHelper.add(entry, this);
    }

    @Override
    public NodeAndEntries<T, S> delete(Entry<? extends T, ? extends S> entry, boolean all) {
        return NonLeafHelper.delete(entry, all, this);
    }

    @Override
    public void searchWithoutBackPressure(
            Func1<? super Geometry, Boolean> criterion,
            Subscriber<? super Entry<T, S>> subscriber) {
        searchWithoutBackPressure(node, criterion, subscriber, deserializer, new EntryExtra(), new GeometryExtra(), new BoxExtra());
    }

    private List<Node<T, S>> createChildren() {
        int numChildren = node.childrenLength();
        List<Node<T, S>> children = new ArrayList<>(numChildren);
        for (int i = 0; i < numChildren; i++) {
            NodeExtra child = node.children(i);
            if (child.childrenLength() > 0) {
                children.add(new NonLeafFlatBuffers<>(child, context, deserializer));
            } else {
                children.add(new LeafFlatBuffers<>(child, context, deserializer));
            }
        }
        return children;
    }

    @Override
    public int count() {
        return node.childrenLength();
    }

    @Override
    public Context<T, S> context() {
        return context;
    }

    @Override
    public Geometry geometry() {
        return FlatBuffersHelper.createBox(node.mbb());
    }

    @Override
    public Node<T, S> child(int i) {
        NodeExtra child = node.children(i);
        if (child.childrenLength() > 0)
            return new NonLeafFlatBuffers<>(child, context, deserializer);
        else
            return new LeafFlatBuffers<>(child, context, deserializer);
    }

    @Override
    public List<Node<T, S>> children() {
        return createChildren();
    }

    @Override
    public String toString() {
        return "Node [" + (node.childrenLength() > 0 ? "NonLeaf" : "Leaf")
                + "," + createBox(node.mbb()).toString() + "]";
    }

}
