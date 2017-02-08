package com.rtree.core.rtree;

import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;

import java.util.Comparator;

public final class Functions {

    private Functions() {
    }

    public static <T> Func1<T, T> identity() {
        return (T t) -> t;
    }

    public static <T> Func1<T, Observable<T>> just() {
        return Observable::just;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Number> Func2<T, T, T> add() {
        return (T a, T b) -> {
            if (a instanceof Integer)
                return (T) (Number) (a.intValue() + b.intValue());
            else if (a instanceof Long)
                return (T) (Number) (a.longValue() + b.longValue());
            else if (a instanceof Double)
                return (T) (Number) (a.doubleValue() + b.doubleValue());
            else if (a instanceof Float)
                return (T) (Number) (a.floatValue() + b.floatValue());
            else if (a instanceof Byte)
                return (T) (Number) (a.byteValue() + b.byteValue());
            else if (a instanceof Short)
                return (T) (Number) (a.shortValue() + b.shortValue());
            else
                return null;
        };
    }

    public static <T extends Number> Func2<Statistics, T, Statistics> collectStats() {
        return Statistics::add;
    }

    public static <T> Func2<T, T, Integer> toFunc2(
            final Comparator<? super T> comparator) {
        return comparator::compare;
    }

    public static <T> Comparator<T> toComparator(
            final Func2<? super T, ? super T, Integer> f) {
        return f::call;
    }

    public static <T, R> Func2<T, R, Boolean> alwaysTrue2() {
        return (T t1, R t2) -> true;
    }

    public static <T, R> Func2<T, R, Boolean> alwaysFalse2() {
        return (T t1, R t2) -> false;
    }

    public static <T> Func1<T, Boolean> isNull() {
        return (T t) -> t == null;
    }

    public static <T> Func1<T, Boolean> isNotNull() {
        return (T t) -> t != null;
    }
}
