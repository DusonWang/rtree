package com.rtree.core.rtree.fbs;

import com.github.davidmoten.guavamini.Optional;
import com.github.davidmoten.guavamini.annotations.VisibleForTesting;
import com.google.flatbuffers.FlatBufferBuilder;
import com.rtree.core.rtree.*;
import com.rtree.core.rtree.fbs.generated.BoxExtra;
import com.rtree.core.rtree.fbs.generated.ContextBoxExtra;
import com.rtree.core.rtree.fbs.generated.NodeExtra;
import com.rtree.core.rtree.fbs.generated.TreeExtra;
import com.rtree.core.rtree.geometry.Geometries;
import com.rtree.core.rtree.geometry.Geometry;
import com.rtree.core.rtree.geometry.Rectangle;
import com.rtree.core.rtree.internal.LeafDefault;
import com.rtree.core.rtree.internal.NonLeafDefault;
import rx.functions.Func1;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public final class SerializerFlatBuffers<T, S extends Geometry> implements
        Serializer<T, S> {

    private final FactoryFlatBuffers<T, S> factory;

    private SerializerFlatBuffers(Func1<? super T, byte[]> serializer, Func1<byte[], ? extends T> deserializer) {
        this.factory = new FactoryFlatBuffers<>(serializer, deserializer);
    }

    public static <T, S extends Geometry> Serializer<T, S> create(Func1<? super T, byte[]> serializer, Func1<byte[], ? extends T> deserializer) {
        return new SerializerFlatBuffers<>(serializer, deserializer);
    }

    private static <T, S extends Geometry> int addNode(Node<T, S> node, FlatBufferBuilder builder, Func1<? super T, byte[]> serializer) {
        if (node instanceof Leaf) {
            Leaf<T, S> leaf = (Leaf<T, S>) node;
            return FlatBuffersHelper.addEntries(leaf.entries(), builder, serializer);
        } else {
            NonLeaf<T, S> nonLeaf = (NonLeaf<T, S>) node;
            int[] nodes = new int[nonLeaf.count()];
            for (int i = 0; i < nonLeaf.count(); i++) {
                Node<T, S> child = nonLeaf.child(i);
                nodes[i] = addNode(child, builder, serializer);
            }
            int ch = NodeExtra.createChildrenVector(builder, nodes);
            NodeExtra.startNode(builder);
            NodeExtra.addChildren(builder, ch);
            Rectangle mbb = nonLeaf.geometry().mbr();
            int b = BoxExtra.createBox(builder, mbb.x1(), mbb.y1(), mbb.x2(), mbb.y2());
            NodeExtra.addMbb(builder, b);
            return NodeExtra.endNode_(builder);
        }
    }

    private static <T, S extends Geometry> Node<T, S> toNodeDefault(NodeExtra node, Context<T, S> context, Func1<byte[], ? extends T> deserializer) {
        int numChildren = node.childrenLength();
        if (numChildren > 0) {
            List<Node<T, S>> children = new ArrayList<>(numChildren);
            for (int i = 0; i < numChildren; i++) {
                children.add(toNodeDefault(node.children(i), context, deserializer));
            }
            return new NonLeafDefault<>(children, context);
        } else {
            List<Entry<T, S>> entries = FlatBuffersHelper.createEntries(node, deserializer);
            return new LeafDefault<>(entries, context);
        }
    }

    @VisibleForTesting
    private static byte[] readFully(InputStream is, int numBytes) throws IOException {
        byte[] b = new byte[numBytes];
        int n = is.read(b);
        if (n != numBytes)
            return new byte[0];
        return b;
    }

    @Override
    public void write(RTree<T, S> tree, OutputStream os) throws IOException {
        FlatBufferBuilder builder = new FlatBufferBuilder();
        final Rectangle mbb;
        if (tree.root().isPresent()) {
            mbb = tree.root().get().geometry().mbr();
        } else {
            mbb = Geometries.rectangle(0, 0, 0, 0);
        }
        int b = BoxExtra.createBox(builder, mbb.x1(), mbb.y1(), mbb.x2(), mbb.y2());
        ContextBoxExtra.startContext(builder);
        ContextBoxExtra.addBounds(builder, b);
        ContextBoxExtra.addMinChildren(builder, tree.context().minChildren());
        ContextBoxExtra.addMaxChildren(builder, tree.context().maxChildren());
        int c = ContextBoxExtra.endContext_(builder);
        final int n;
        if (tree.root().isPresent()) {
            n = addNode(tree.root().get(), builder, factory.serializer());
        } else {
            n = 0;
        }
        TreeExtra.startTree(builder);
        TreeExtra.addContext(builder, c);
        TreeExtra.addSize(builder, tree.size());
        if (tree.size() > 0) {
            TreeExtra.addRoot(builder, n);
        }
        int t = TreeExtra.endTree(builder);
        TreeExtra.finishTree_Buffer(builder, t);
        ByteBuffer bb = builder.dataBuffer();
        os.write(bb.array(), bb.position(), bb.remaining());
    }

    @Override
    public RTree<T, S> read(InputStream is, long sizeBytes, InternalStructure structure) throws IOException {
        byte[] bytes = readFully(is, (int) sizeBytes);
        TreeExtra t = TreeExtra.getRootAsTree(ByteBuffer.wrap(bytes));
        Context<T, S> context = new Context<>(t.context().minChildren(), t.context().maxChildren(), new SelectorRStar(), new SplitterRStar(), factory);
        NodeExtra node = t.root();
        if (node == null) {
            return SerializerHelper.create(Optional.<Node<T, S>>absent(), 0, context);
        } else {
            final Node<T, S> root;
            if (structure == InternalStructure.SINGLE_ARRAY) {
                if (node.childrenLength() > 0) {
                    root = new NonLeafFlatBuffers<>(node, context, factory.deserializer());
                } else {
                    root = new LeafFlatBuffers<>(node, context, factory.deserializer());
                }
            } else {
                root = toNodeDefault(node, context, factory.deserializer());
            }
            return SerializerHelper.create(Optional.of(root), (int) t.size(), context);
        }
    }

}
