package com.rtree.core.rtree.util;

import com.github.davidmoten.guavamini.Optional;

public final class ObjectsHelper {

    private ObjectsHelper() {
    }

    @SuppressWarnings("unchecked")
    public static <T> Optional<T> asClass(Object object, Class<T> cls) {
        if (object == null)
            return Optional.absent();
        else if (object.getClass() != cls)
            return Optional.absent();
        else
            return Optional.of((T) object);
    }

}
