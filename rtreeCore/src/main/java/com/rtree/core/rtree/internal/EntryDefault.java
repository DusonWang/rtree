package com.rtree.core.rtree.internal;

import com.github.davidmoten.guavamini.Objects;
import com.github.davidmoten.guavamini.Optional;
import com.github.davidmoten.guavamini.Preconditions;
import com.rtree.core.rtree.Entry;
import com.rtree.core.rtree.geometry.Geometry;
import com.rtree.core.rtree.util.ObjectsHelper;

public final class EntryDefault<T, S extends Geometry> implements Entry<T, S> {
    private final T value;
    private final S geometry;

    public EntryDefault(T value, S geometry) {
        Preconditions.checkNotNull(geometry);
        this.value = value;
        this.geometry = geometry;
    }

    public static <T, S extends Geometry> Entry<T, S> entry(T value, S geometry) {
        return new EntryDefault<>(value, geometry);
    }

    @Override
    public T value() {
        return value;
    }

    @Override
    public S geometry() {
        return geometry;
    }

    @Override
    public String toString() {
        return "Entry [value=" +
                value +
                ", geometry=" +
                geometry +
                "]";
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value, geometry);
    }

    @Override
    public boolean equals(Object obj) {
        @SuppressWarnings("rawtypes")
        Optional<EntryDefault> other = ObjectsHelper.asClass(obj,
                EntryDefault.class);
        return other.isPresent() && Objects.equal(value, other.get().value) && Objects.equal(geometry, other.get().geometry);
    }

}
