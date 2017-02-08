package com.rtree.core.rtree;

import com.github.davidmoten.guavamini.Lists;
import com.github.davidmoten.guavamini.Optional;
import com.github.davidmoten.guavamini.annotations.VisibleForTesting;
import com.rtree.core.rtree.geometry.*;
import com.rtree.core.rtree.internal.Comparators;
import com.rtree.core.rtree.internal.NodeAndEntries;
import com.rtree.core.rtree.rx.operators.OperatorBoundedPriorityQueue;
import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;

import java.util.List;

import static com.github.davidmoten.guavamini.Optional.absent;
import static com.github.davidmoten.guavamini.Optional.of;
import static com.rtree.core.rtree.geometry.Geometries.rectangle;

public final class RTree<T, S extends Geometry> {

    public static final int MAX_CHILDREN_DEFAULT_GUTTMAN = 4;
    public static final int MAX_CHILDREN_DEFAULT_STAR = 4;
    private static final Func1<Geometry, Boolean> ALWAYS_TRUE = (Geometry rectangle) -> true;
    private final static String marginIncrement = "  ";
    private final Optional<? extends Node<T, S>> root;
    private final Context<T, S> context;
    private final int size;

    private RTree(Optional<? extends Node<T, S>> root, int size,
                  Context<T, S> context) {
        this.root = root;
        this.size = size;
        this.context = context;
    }

    private RTree() {
        this(Optional.<Node<T, S>>absent(), 0, null);
    }

    private RTree(Node<T, S> root, int size, Context<T, S> context) {
        this(of(root), size, context);
    }

    static <T, S extends Geometry> RTree<T, S> create(
            Optional<? extends Node<T, S>> root, int size, Context<T, S> context) {
        return new RTree<>(root, size, context);
    }

    public static <T, S extends Geometry> RTree<T, S> create() {
        return new Builder().create();
    }

    private static <T, S extends Geometry> int calculateDepth(
            Optional<? extends Node<T, S>> root) {
        if (!root.isPresent())
            return 0;
        else
            return calculateDepth(root.get(), 0);
    }

    private static <T, S extends Geometry> int calculateDepth(Node<T, S> node, int depth) {
        if (node instanceof Leaf)
            return depth + 1;
        else
            return calculateDepth(((NonLeaf<T, S>) node).child(0), depth + 1);
    }

    public static Builder minChildren(int minChildren) {
        return new Builder().minChildren(minChildren);
    }

    public static Builder maxChildren(int maxChildren) {
        return new Builder().maxChildren(maxChildren);
    }

    public static Builder splitter(Splitter splitter) {
        return new Builder().splitter(splitter);
    }

    public static Builder selector(Selector selector) {
        return new Builder().selector(selector);
    }

    public static Builder star() {
        return new Builder().star();
    }

    public static Func1<Geometry, Boolean> intersects(final Rectangle r) {
        return (Geometry g) -> g.intersects(r);
    }

    public int calculateDepth() {
        return calculateDepth(root);
    }

    @SuppressWarnings("unchecked")
    public RTree<T, S> add(Entry<? extends T, ? extends S> entry) {
        if (root.isPresent()) {
            List<Node<T, S>> nodes = root.get().add(entry);
            Node<T, S> node;
            if (nodes.size() == 1)
                node = nodes.get(0);
            else {
                node = context.factory().createNonLeaf(nodes, context);
            }
            return new RTree<>(node, size + 1, context);
        } else {
            Leaf<T, S> node = context.factory().createLeaf(Lists.newArrayList((Entry<T, S>) entry), context);
            return new RTree<>(node, size + 1, context);
        }
    }

    public RTree<T, S> add(T value, S geometry) {
        return add(context.factory().createEntry(value, geometry));
    }

    public RTree<T, S> add(Iterable<Entry<T, S>> entries) {
        RTree<T, S> tree = this;
        for (Entry<T, S> entry : entries)
            tree = tree.add(entry);
        return tree;
    }

    public Observable<RTree<T, S>> add(Observable<Entry<T, S>> entries) {
        return entries.scan(this, RTree<T, S>::add);
    }

    public Observable<RTree<T, S>> delete(Observable<Entry<T, S>> entries, final boolean all) {
        return entries.scan(this, (RTree<T, S> tree, Entry<T, S> entry) -> tree.delete(entry, all));
    }

    public RTree<T, S> delete(Iterable<Entry<T, S>> entries, boolean all) {
        RTree<T, S> tree = this;
        for (Entry<T, S> entry : entries)
            tree = tree.delete(entry, all);
        return tree;
    }

    public RTree<T, S> delete(Iterable<Entry<T, S>> entries) {
        RTree<T, S> tree = this;
        for (Entry<T, S> entry : entries)
            tree = tree.delete(entry);
        return tree;
    }

    public RTree<T, S> delete(T value, S geometry, boolean all) {
        return delete(context.factory().createEntry(value, geometry), all);
    }

    public RTree<T, S> delete(T value, S geometry) {
        return delete(context.factory().createEntry(value, geometry), false);
    }

    public RTree<T, S> delete(Entry<? extends T, ? extends S> entry, boolean all) {
        if (root.isPresent()) {
            NodeAndEntries<T, S> nodeAndEntries = root.get().delete(entry, all);
            if (nodeAndEntries.node().isPresent()
                    && nodeAndEntries.node().get() == root.get())
                return this;
            else
                return new RTree<>(nodeAndEntries.node(), size - nodeAndEntries.countDeleted() - nodeAndEntries.entriesToAdd().size(), context).add(nodeAndEntries.entriesToAdd());
        } else
            return this;
    }

    public RTree<T, S> delete(Entry<? extends T, ? extends S> entry) {
        return delete(entry, false);
    }

    @VisibleForTesting
    Observable<Entry<T, S>> search(Func1<? super Geometry, Boolean> condition) {
        if (root.isPresent())
            return Observable.create(new OnSubscribeSearch<>(root.get(),
                    condition));
        else
            return Observable.empty();
    }

    public Observable<Entry<T, S>> search(final Rectangle r) {
        return search(intersects(r));
    }

    public Observable<Entry<T, S>> search(final Point p) {
        return search(p.mbr());
    }

    public Observable<Entry<T, S>> search(Circle circle) {
        return search(circle, Intersects.geometryIntersectsCircle);
    }

    public Observable<Entry<T, S>> search(Line line) {
        return search(line, Intersects.geometryIntersectsLine);
    }

    public Observable<Entry<T, S>> search(final Rectangle r, final double maxDistance) {
        return search((Geometry g) -> g.distance(r) < maxDistance);
    }

    public <R extends Geometry> Observable<Entry<T, S>> search(final R g, final Func2<? super S, ? super R, Boolean> intersects) {
        return search(g.mbr()).filter((Entry<T, S> entry) -> intersects.call(entry.geometry(), g));
    }

    public <R extends Geometry> Observable<Entry<T, S>> search(final R g, final double maxDistance, final Func2<? super S, ? super R, Double> distance) {
        return search((Geometry entry) -> entry.distance(g.mbr()) < maxDistance).filter((Entry<T, S> entry) -> distance.call(entry.geometry(), g) < maxDistance);
    }

    public Observable<Entry<T, S>> search(final Point p, final double maxDistance) {
        return search(p.mbr(), maxDistance);
    }

    public Observable<Entry<T, S>> nearest(final Rectangle r, final double maxDistance, int maxCount) {
        return search(r, maxDistance).lift(new OperatorBoundedPriorityQueue<>(maxCount, Comparators.<T, S>ascendingDistance(r)));
    }

    public Observable<Entry<T, S>> nearest(final Point p, final double maxDistance, int maxCount) {
        return nearest(p.mbr(), maxDistance, maxCount);
    }

    public Observable<Entry<T, S>> entries() {
        return search(ALWAYS_TRUE);
    }

    @SuppressWarnings("unchecked")
    public Visualizer visualize(int width, int height, Rectangle view) {
        return new Visualizer((RTree<?, Geometry>) this, width, height, view);
    }

    public Visualizer visualize(int width, int height) {
        return visualize(width, height, calculateMaxView(this));
    }

    private Rectangle calculateMaxView(RTree<T, S> tree) {
        Func2<Optional<Rectangle>, Entry<T, S>, Optional<Rectangle>> func2 = (Optional<Rectangle> r, Entry<T, S> entry) -> {
            if (r.isPresent())
                return of(r.get().add(entry.geometry().mbr()));
            else
                return of(entry.geometry().mbr());
        };
        return tree.entries().reduce(Optional.<Rectangle>absent(), func2).toBlocking().single().or(rectangle(0, 0, 0, 0));
    }

    public Optional<? extends Node<T, S>> root() {
        return root;
    }

    public Optional<Rectangle> mbr() {
        if (!root.isPresent())
            return absent();
        else
            return of(root.get().geometry().mbr());
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public Context<T, S> context() {
        return context;
    }

    public String asString() {
        if (!root.isPresent())
            return "";
        else
            return asString(root.get(), "");
    }

    private String asString(Node<T, S> node, String margin) {
        StringBuilder s = new StringBuilder();
        s.append(margin);
        s.append("mbr=");
        s.append(node.geometry());
        s.append('\n');
        if (node instanceof NonLeaf) {
            NonLeaf<T, S> n = (NonLeaf<T, S>) node;
            for (int i = 0; i < n.count(); i++) {
                Node<T, S> child = n.child(i);
                s.append(asString(child, margin + marginIncrement));
            }
        } else {
            Leaf<T, S> leaf = (Leaf<T, S>) node;

            for (Entry<T, S> entry : leaf.entries()) {
                s.append(margin);
                s.append(marginIncrement);
                s.append("entry=");
                s.append(entry);
                s.append('\n');
            }
        }
        return s.toString();
    }

    public static class Builder {

        private static final double DEFAULT_FILLING_FACTOR = 0.4;
        private Optional<Integer> maxChildren = absent();
        private Optional<Integer> minChildren = absent();
        private Splitter splitter = new SplitterQuadratic();
        private Selector selector = new SelectorMinimalAreaIncrease();
        private boolean star = false;
        private Factory<Object, Geometry> factory = Factories.defaultFactory();

        private Builder() {
        }

        public Builder minChildren(int minChildren) {
            this.minChildren = of(minChildren);
            return this;
        }

        public Builder maxChildren(int maxChildren) {
            this.maxChildren = of(maxChildren);
            return this;
        }

        public Builder splitter(Splitter splitter) {
            this.splitter = splitter;
            return this;
        }

        public Builder selector(Selector selector) {
            this.selector = selector;
            return this;
        }

        public Builder star() {
            selector = new SelectorRStar();
            splitter = new SplitterRStar();
            star = true;
            return this;
        }

        @SuppressWarnings("unchecked")
        public Builder factory(Factory<?, ? extends Geometry> factory) {
            this.factory = (Factory<Object, Geometry>) factory;
            return this;
        }

        @SuppressWarnings("unchecked")
        public <T, S extends Geometry> RTree<T, S> create() {
            if (!maxChildren.isPresent())
                if (star)
                    maxChildren = of(MAX_CHILDREN_DEFAULT_STAR);
                else
                    maxChildren = of(MAX_CHILDREN_DEFAULT_GUTTMAN);
            if (!minChildren.isPresent())
                minChildren = of((int) Math.round(maxChildren.get() * DEFAULT_FILLING_FACTOR));
            return new RTree<>(Optional.<Node<T, S>>absent(), 0,
                    new Context<>(minChildren.get(), maxChildren.get(), selector, splitter, (Factory<T, S>) factory));
        }

    }

}
