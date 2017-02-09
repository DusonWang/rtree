package com.rtree.core.rtree;


import com.github.davidmoten.guavamini.Optional;
import com.rtree.core.rtree.geometry.Geometry;
import com.rtree.core.rtree.geometry.Rectangle;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class Visualizer {

    private final RTree<?, Geometry> tree;
    private final int width;
    private final int height;
    private final Rectangle view;
    private final int maxDepth;

    Visualizer(RTree<?, Geometry> tree, int width, int height, Rectangle view) {
        this.tree = tree;
        this.width = width;
        this.height = height;
        this.view = view;
        this.maxDepth = calculateMaxDepth(tree.root());
    }

    private static <R, S extends Geometry> int calculateMaxDepth(Optional<? extends Node<R, S>> root) {
        if (!root.isPresent())
            return 0;
        else
            return calculateDepth(root.get(), 0);
    }

    private static <R, S extends Geometry> int calculateDepth(Node<R, S> node, int depth) {
        if (node instanceof Leaf)
            return depth + 1;
        else
            return calculateDepth(((NonLeaf<R, S>) node).child(0), depth + 1);
    }

    private static int rnd(double d) {
        return (int) Math.round(d);
    }

    public BufferedImage createImage() {
        final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g = (Graphics2D) image.getGraphics();
        g.setBackground(Color.white);
        g.clearRect(0, 0, width, height);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.75f));
        if (tree.root().isPresent()) {
            final List<RectangleDepth> nodeDepths = getNodeDepthsSortedByDepth(tree.root().get());
            drawNode(g, nodeDepths);
        }
        return image;
    }

    private <T, S extends Geometry> List<RectangleDepth> getNodeDepthsSortedByDepth(
            Node<T, S> root) {
        final List<RectangleDepth> list = getRectangleDepths(root, 0);
        Collections.sort(list, (RectangleDepth n1, RectangleDepth n2) -> ((Integer) n1.getDepth()).compareTo(n2.getDepth()));
        return list;
    }

    private <T, S extends Geometry> List<RectangleDepth> getRectangleDepths(Node<T, S> node, int depth) {
        final List<RectangleDepth> list = new ArrayList<>();
        list.add(new RectangleDepth(node.geometry().mbr(), depth));
        if (node instanceof Leaf) {
            final Leaf<T, S> leaf = (Leaf<T, S>) node;
            list.addAll(leaf.entries().stream().map(entry -> new RectangleDepth(entry.geometry().mbr(), depth + 2)).collect(Collectors.toList()));
        } else {
            final NonLeaf<T, S> n = (NonLeaf<T, S>) node;
            for (int i = 0; i < n.count(); i++) {
                list.addAll(getRectangleDepths(n.child(i), depth + 1));
            }
        }
        return list;
    }

    private void drawNode(Graphics2D g, List<RectangleDepth> nodes) {
        for (final RectangleDepth node : nodes) {
            final Color color = Color.getHSBColor(node.getDepth() / (maxDepth + 1f), 1f, 1f);
            g.setStroke(new BasicStroke(Math.max(0.5f, maxDepth - node.getDepth() + 1 - 1)));
            g.setColor(color);
            final Rectangle r = node.getRectangle();
            drawRectangle(g, r);
        }
    }

    private void drawRectangle(Graphics2D g, Rectangle r) {
        final double x1 = (r.x1() - view.x1()) / (view.x2() - view.x1()) * width;
        final double y1 = (r.y1() - view.y1()) / (view.y2() - view.y1()) * height;
        final double x2 = (r.x2() - view.x1()) / (view.x2() - view.x1()) * width;
        final double y2 = (r.y2() - view.y1()) / (view.y2() - view.y1()) * height;
        g.drawRect(rnd(x1), rnd(y1), Math.max(rnd(x2 - x1), 1), Math.max(rnd(y2 - y1), 1));
    }

    public void save(File file, String imageFormat) {
        ImageSaver.save(createImage(), file, imageFormat);
    }

    public void save(String filename, String imageFormat) {
        save(new File(filename), imageFormat);
    }

    public void save(String filename) {
        save(new File(filename), "PNG");
    }
}