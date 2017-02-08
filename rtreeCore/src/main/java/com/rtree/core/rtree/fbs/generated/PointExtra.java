package com.rtree.core.rtree.fbs.generated;

import com.google.flatbuffers.FlatBufferBuilder;
import com.google.flatbuffers.Struct;

import java.nio.ByteBuffer;

public final class PointExtra extends Struct {
    public static int createPoint_(FlatBufferBuilder builder, float x, float y) {
        builder.prep(4, 8);
        builder.putFloat(y);
        builder.putFloat(x);
        return builder.offset();
    }

    public PointExtra init(int iTmp, ByteBuffer bbTmp) {
        bb_pos = iTmp;
        bb = bbTmp;
        return this;
    }

    public float x() {
        return bb.getFloat(bb_pos);
    }

    public float y() {
        return bb.getFloat(bb_pos + 4);
    }
}
