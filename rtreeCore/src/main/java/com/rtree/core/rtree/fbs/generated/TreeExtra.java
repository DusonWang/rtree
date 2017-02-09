package com.rtree.core.rtree.fbs.generated;

import com.google.flatbuffers.FlatBufferBuilder;
import com.google.flatbuffers.Table;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class TreeExtra extends Table {
    public static TreeExtra getRootAsTree(ByteBuffer bbTmp) {
        return getRootAsTree(bbTmp, new TreeExtra());
    }

    private static TreeExtra getRootAsTree(ByteBuffer bbTmp, TreeExtra obj) {
        bbTmp.order(ByteOrder.LITTLE_ENDIAN);
        return obj.init(bbTmp.getInt(bbTmp.position()) + bbTmp.position(), bbTmp);
    }

    public static int createTree(FlatBufferBuilder builder, int contextOffset,
                                 int rootOffset, long size) {
        builder.startObject(3);
        TreeExtra.addSize(builder, size);
        TreeExtra.addRoot(builder, rootOffset);
        TreeExtra.addContext(builder, contextOffset);
        return TreeExtra.endTree(builder);
    }

    public static void startTree(FlatBufferBuilder builder) {
        builder.startObject(3);
    }

    public static void addContext(FlatBufferBuilder builder, int contextOffset) {
        builder.addOffset(0, contextOffset, 0);
    }

    public static void addRoot(FlatBufferBuilder builder, int rootOffset) {
        builder.addOffset(1, rootOffset, 0);
    }

    public static void addSize(FlatBufferBuilder builder, long size) {
        builder.addInt(2, (int) size, 0);
    }

    public static int endTree(FlatBufferBuilder builder) {
        return builder.endObject();
    }

    public static void finishTree_Buffer(FlatBufferBuilder builder, int offset) {
        builder.finish(offset);
    }

    private TreeExtra init(int iTmp, ByteBuffer bbTmp) {
        bb_pos = iTmp;
        bb = bbTmp;
        return this;
    }

    public ContextBoxExtra context() {
        return context(new ContextBoxExtra());
    }

    public ContextBoxExtra context(ContextBoxExtra obj) {
        int o = __offset(4);
        return o != 0 ? obj.init(__indirect(o + bb_pos), bb) : null;
    }

    public NodeExtra root() {
        return root(new NodeExtra());
    }

    private NodeExtra root(NodeExtra obj) {
        int o = __offset(6);
        return o != 0 ? obj.init(__indirect(o + bb_pos), bb) : null;
    }

    public long size() {
        int o = __offset(8);
        return o != 0 ? (long) bb.getInt(o + bb_pos) & 0xFFFFFFFFL : 0;
    }
}
