package com.rtree.core.rtree.fbs.generated;

import com.google.flatbuffers.FlatBufferBuilder;
import com.google.flatbuffers.Struct;

import java.nio.ByteBuffer;

public final class LineExtra extends Struct {
    public static int createLine_(FlatBufferBuilder builder, float minX, float minY, float maxX, float maxY) {
        builder.prep(4, 16);
        builder.putFloat(maxY);
        builder.putFloat(maxX);
        builder.putFloat(minY);
        builder.putFloat(minX);
        return builder.offset();
    }

    public LineExtra init(int iTmp, ByteBuffer bbTmp) {
        bb_pos = iTmp;
        bb = bbTmp;
        return this;
    }

    public float minX() {
        return bb.getFloat(bb_pos);
    }

    public float minY() {
        return bb.getFloat(bb_pos + 4);
    }

    public float maxX() {
        return bb.getFloat(bb_pos + 8);
    }

    public float maxY() {
        return bb.getFloat(bb_pos + 12);
    }
}
