package com.rtree.core.rtree.util;

import com.github.davidmoten.guavamini.Optional;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static com.github.davidmoten.guavamini.Optional.of;

public final class ImmutableStack<T> implements Iterable<T> {
    private static ImmutableStack<?> empty = new ImmutableStack<>();
    private final Optional<T> head;
    private final Optional<ImmutableStack<T>> tail;

    private ImmutableStack(final T head, final ImmutableStack<T> tail) {
        this(of(head), of(tail));
    }

    private ImmutableStack(Optional<T> head, Optional<ImmutableStack<T>> tail) {
        this.head = head;
        this.tail = tail;
    }

    private ImmutableStack() {
        this(Optional.absent(), Optional.absent());
    }

    public static <T> ImmutableStack<T> create(T t) {
        return new ImmutableStack<>(of(t), of(ImmutableStack.empty()));
    }

    @SuppressWarnings("unchecked")
    public static <S> ImmutableStack<S> empty() {
        return (ImmutableStack<S>) empty;
    }

    public boolean isEmpty() {
        return !head.isPresent();
    }

    public T peek() {
        return this.head.get();
    }

    public ImmutableStack<T> pop() {
        return this.tail.get();
    }

    public ImmutableStack<T> push(T value) {
        return new ImmutableStack<>(value, this);
    }

    @Override
    public Iterator<T> iterator() {
        return new StackIterator<>(this);
    }

    private static class StackIterator<U> implements Iterator<U> {
        private ImmutableStack<U> stack;

        StackIterator(final ImmutableStack<U> stack) {
            this.stack = stack;
        }

        @Override
        public boolean hasNext() {
            return !this.stack.isEmpty();
        }

        @Override
        public U next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            final U result = this.stack.peek();
            this.stack = this.stack.pop();
            return result;
        }

        @Override
        public void remove() {
        }
    }

}