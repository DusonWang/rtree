package com.rtree.core.rtree.fbs.generated;

import com.google.flatbuffers.FlatBufferBuilder;
import com.google.flatbuffers.Table;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class NodeExtra extends Table {
    public static NodeExtra getRootAsNode(ByteBuffer bbTmp) {
        return getRootAsNode(bbTmp, new NodeExtra());
    }

    public static NodeExtra getRootAsNode(ByteBuffer bbTmp, NodeExtra obj) {
        bbTmp.order(ByteOrder.LITTLE_ENDIAN);
        return obj.init(bbTmp.getInt(bbTmp.position()) + bbTmp.position(), bbTmp);
    }

    public static void startNode(FlatBufferBuilder builder) {
        builder.startObject(3);
    }

    public static void addMbb(FlatBufferBuilder builder, int mbbOffset) {
        builder.addStruct(0, mbbOffset, 0);
    }

    public static void addChildren(FlatBufferBuilder builder, int childrenOffset) {
        builder.addOffset(1, childrenOffset, 0);
    }

    public static int createChildrenVector(FlatBufferBuilder builder, int[] data) {
        builder.startVector(4, data.length, 4);
        for (int i = data.length - 1; i >= 0; i--)
            builder.addOffset(data[i]);
        return builder.endVector();
    }


    public static void addEntries(FlatBufferBuilder builder, int entriesOffset) {
        builder.addOffset(2, entriesOffset, 0);
    }

    public static int createEntriesVector(FlatBufferBuilder builder, int[] data) {
        builder.startVector(4, data.length, 4);
        for (int i = data.length - 1; i >= 0; i--)
            builder.addOffset(data[i]);
        return builder.endVector();
    }

    public static int endNode_(FlatBufferBuilder builder) {
        return builder.endObject();
    }

    public NodeExtra init(int iTmp, ByteBuffer bbTmp) {
        bb_pos = iTmp;
        bb = bbTmp;
        return this;
    }

    public BoxExtra mbb() {
        return mbb(new BoxExtra());
    }

    public BoxExtra mbb(BoxExtra obj) {
        int o = __offset(4);
        return o != 0 ? obj.init(o + bb_pos, bb) : null;
    }

    public NodeExtra children(int j) {
        return children(new NodeExtra(), j);
    }

    public NodeExtra children(NodeExtra obj, int j) {
        int o = __offset(6);
        return o != 0 ? obj.init(__indirect(__vector(o) + j * 4), bb) : null;
    }

    public int childrenLength() {
        int o = __offset(6);
        return o != 0 ? __vector_len(o) : 0;
    }

    public EntryExtra entries(int j) {
        return entries(new EntryExtra(), j);
    }

    public EntryExtra entries(EntryExtra obj, int j) {
        int o = __offset(8);
        return o != 0 ? obj.init(__indirect(__vector(o) + j * 4), bb) : null;
    }

    public int entriesLength() {
        int o = __offset(8);
        return o != 0 ? __vector_len(o) : 0;
    }
}
