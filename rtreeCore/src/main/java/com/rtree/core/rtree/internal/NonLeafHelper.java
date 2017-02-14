package com.rtree.core.rtree.internal;

import com.github.davidmoten.guavamini.Optional;
import com.rtree.core.rtree.*;
import com.rtree.core.rtree.geometry.Geometry;
import com.rtree.core.rtree.geometry.ListPair;
import rx.Subscriber;
import rx.functions.Func1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.github.davidmoten.guavamini.Optional.of;

public final class NonLeafHelper {

    private NonLeafHelper() {
    }

    static <T, S extends Geometry> void search(Func1<? super Geometry, Boolean> criterion, Subscriber<? super Entry<T, S>> subscriber, NonLeaf<T, S> node) {
        if (!criterion.call(node.geometry().mbr()))
            return;
        int numChildren = node.count();
        for (int i = 0; i < numChildren; i++) {
            if (subscriber.isUnsubscribed()) {
                return;
            } else {
                Node<T, S> child = node.child(i);
                child.searchWithoutBackPressure(criterion, subscriber);
            }
        }
    }

    public static <T, S extends Geometry> List<Node<T, S>> add(Entry<? extends T, ? extends S> entry, NonLeaf<T, S> node) {
        Context<T, S> context = node.context();
        List<Node<T, S>> children = node.children();
        final Node<T, S> child = context.selector().select(entry.geometry().mbr(), children);
        List<Node<T, S>> list = child.add(entry);
        List<? extends Node<T, S>> children2 = Util.replace(children, child, list);
        if (children2.size() <= context.maxChildren())
            return Collections.singletonList(context.factory().createNonLeaf(children2, context));
        else {
            ListPair<? extends Node<T, S>> pair = context.splitter().split(children2, context.minChildren());
            return makeNonLeaves(pair, context);
        }
    }

    private static <T, S extends Geometry> List<Node<T, S>> makeNonLeaves(
            ListPair<? extends Node<T, S>> pair, Context<T, S> context) {
        List<Node<T, S>> list = new ArrayList<>();
        list.add(context.factory().createNonLeaf(pair.group1().list(), context));
        list.add(context.factory().createNonLeaf(pair.group2().list(), context));
        return list;
    }

    public static <T, S extends Geometry> NodeAndEntries<T, S> delete(Entry<? extends T, ? extends S> entry, boolean all, NonLeaf<T, S> node) {
        List<Entry<T, S>> addTheseEntries = new ArrayList<>();
        List<Node<T, S>> removeTheseNodes = new ArrayList<>();
        List<Node<T, S>> addTheseNodes = new ArrayList<>();
        int countDeleted = 0;
        List<? extends Node<T, S>> children = node.children();
        for (final Node<T, S> child : children) {
            if (entry.geometry().intersects(child.geometry().mbr())) {
                final NodeAndEntries<T, S> result = child.delete(entry, all);
                if (result.node().isPresent()) {
                    if (result.node().get() != child) {
                        addTheseNodes.add(result.node().get());
                        removeTheseNodes.add(child);
                        addTheseEntries.addAll(result.entriesToAdd());
                        countDeleted += result.countDeleted();
                        if (!all)
                            break;
                    }
                } else {
                    removeTheseNodes.add(child);
                    addTheseEntries.addAll(result.entriesToAdd());
                    countDeleted += result.countDeleted();
                    if (!all)
                        break;
                }
            }
        }
        if (removeTheseNodes.isEmpty())
            return new NodeAndEntries<>(of(node),
                    Collections.emptyList(), 0);
        else {
            List<Node<T, S>> nodes = Util.remove(children, removeTheseNodes);
            nodes.addAll(addTheseNodes);
            if (nodes.isEmpty())
                return new NodeAndEntries<>(Optional.absent(), addTheseEntries, countDeleted);
            else {
                NonLeaf<T, S> nd = node.context().factory().createNonLeaf(nodes, node.context());
                return new NodeAndEntries<>(of(nd), addTheseEntries, countDeleted);
            }
        }
    }

}
