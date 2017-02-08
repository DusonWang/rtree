package com.rtree.core.rtree.fbs.generated;

import com.google.flatbuffers.FlatBufferBuilder;
import com.google.flatbuffers.Table;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class ContextBoxExtra extends Table {
    public static ContextBoxExtra getRootAsContext(ByteBuffer bbTmp) {
        return getRootAsContext(bbTmp, new ContextBoxExtra());
    }

    public static ContextBoxExtra getRootAsContext(ByteBuffer bbTmp, ContextBoxExtra obj) {
        bbTmp.order(ByteOrder.LITTLE_ENDIAN);
        return obj.init(bbTmp.getInt(bbTmp.position()) + bbTmp.position(), bbTmp);
    }

    public static void startContext(FlatBufferBuilder builder) {
        builder.startObject(3);
    }

    public static void addBounds(FlatBufferBuilder builder, int boundsOffset) {
        builder.addStruct(0, boundsOffset, 0);
    }

    public static void addMinChildren(FlatBufferBuilder builder, int minChildren) {
        builder.addInt(1, minChildren, 0);
    }

    public static void addMaxChildren(FlatBufferBuilder builder, int maxChildren) {
        builder.addInt(2, maxChildren, 0);
    }

    public static int endContext_(FlatBufferBuilder builder) {
        return builder.endObject();
    }

    public ContextBoxExtra init(int iTmp, ByteBuffer bbTmp) {
        bb_pos = iTmp;
        bb = bbTmp;
        return this;
    }

    public BoxExtra bounds() {
        return bounds(new BoxExtra());
    }

    public BoxExtra bounds(BoxExtra obj) {
        int o = __offset(4);
        return o != 0 ? obj.init(o + bb_pos, bb) : null;
    }

    public int minChildren() {
        int o = __offset(6);
        return o != 0 ? bb.getInt(o + bb_pos) : 0;
    }

    public int maxChildren() {
        int o = __offset(8);
        return o != 0 ? bb.getInt(o + bb_pos) : 0;
    }
}
