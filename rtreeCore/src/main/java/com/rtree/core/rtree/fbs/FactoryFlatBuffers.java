package com.rtree.core.rtree.fbs;

import com.github.davidmoten.guavamini.Preconditions;
import com.rtree.core.rtree.*;
import com.rtree.core.rtree.geometry.Geometry;
import com.rtree.core.rtree.internal.NonLeafDefault;
import rx.functions.Func1;

import java.util.List;

public final class FactoryFlatBuffers<T, S extends Geometry> implements
        Factory<T, S> {
    private final Func1<? super T, byte[]> serializer;
    private final Func1<byte[], ? extends T> deserializer;

    public FactoryFlatBuffers(Func1<? super T, byte[]> serializer, Func1<byte[], ? extends T> deserializer) {
        Preconditions.checkNotNull(serializer);
        Preconditions.checkNotNull(deserializer);
        this.serializer = serializer;
        this.deserializer = deserializer;
    }

    @Override
    public Leaf<T, S> createLeaf(List<Entry<T, S>> entries, Context<T, S> context) {
        return new LeafFlatBuffers<>(entries, context, serializer, deserializer);
    }

    @Override
    public NonLeaf<T, S> createNonLeaf(List<? extends Node<T, S>> children, Context<T, S> context) {
        return new NonLeafDefault<>(children, context);
    }

    @Override
    public Entry<T, S> createEntry(T value, S geometry) {
        return Entries.entry(value, geometry);
    }

    public Func1<? super T, byte[]> serializer() {
        return serializer;
    }

    public Func1<byte[], ? extends T> deserializer() {
        return deserializer;
    }

}
