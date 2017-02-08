package com.rtree.core.rtree.fbs;

import com.google.flatbuffers.FlatBufferBuilder;
import com.rtree.core.rtree.Context;
import com.rtree.core.rtree.Entry;
import com.rtree.core.rtree.Leaf;
import com.rtree.core.rtree.Node;
import com.rtree.core.rtree.fbs.generated.BoxExtra;
import com.rtree.core.rtree.fbs.generated.NodeExtra;
import com.rtree.core.rtree.geometry.Geometries;
import com.rtree.core.rtree.geometry.Geometry;
import com.rtree.core.rtree.internal.LeafHelper;
import com.rtree.core.rtree.internal.NodeAndEntries;
import rx.Subscriber;
import rx.functions.Func1;

import java.util.List;

final class LeafFlatBuffers<T, S extends Geometry> implements Leaf<T, S> {

    private final NodeExtra node;
    private final Context<T, S> context;
    private final Func1<byte[], ? extends T> deserializer;

    LeafFlatBuffers(List<Entry<T, S>> entries, Context<T, S> context, Func1<? super T, byte[]> serializer, Func1<byte[], ? extends T> deserializer) {
        this(createNode(entries, serializer), context, deserializer);
    }

    LeafFlatBuffers(NodeExtra node, Context<T, S> context, Func1<byte[], ? extends T> deserializer) {
        this.context = context;
        this.deserializer = deserializer;
        this.node = node;
    }

    private static <T, S extends Geometry> NodeExtra createNode(List<Entry<T, S>> entries, Func1<? super T, byte[]> serializer) {
        FlatBufferBuilder builder = new FlatBufferBuilder(0);
        builder.finish(FlatBuffersHelper.addEntries(entries, builder, serializer));
        return NodeExtra.getRootAsNode(builder.dataBuffer());
    }

    @Override
    public List<Node<T, S>> add(Entry<? extends T, ? extends S> entry) {
        return LeafHelper.add(entry, this);
    }

    @Override
    public NodeAndEntries<T, S> delete(Entry<? extends T, ? extends S> entry, boolean all) {
        return LeafHelper.delete(entry, all, this);
    }

    @Override
    public void searchWithoutBackPressure(
            Func1<? super Geometry, Boolean> condition,
            Subscriber<? super Entry<T, S>> subscriber) {
        LeafHelper.search(condition, subscriber, this);
    }

    @Override
    public int count() {
        return node.entriesLength();
    }

    @Override
    public Context<T, S> context() {
        return context;
    }

    @Override
    public Geometry geometry() {
        BoxExtra b = node.mbb();
        return Geometries.rectangle(b.minX(), b.minY(), b.maxX(), b.maxY());
    }

    @Override
    public List<Entry<T, S>> entries() {
        return FlatBuffersHelper.createEntries(node, deserializer);
    }

    @Override
    public Entry<T, S> entry(int i) {
        return FlatBuffersHelper.createEntry(node, deserializer, i);
    }

}
