package com.rtree.core.rtree.fbs.generated;

import com.google.flatbuffers.FlatBufferBuilder;
import com.google.flatbuffers.Struct;

import java.nio.ByteBuffer;

public final class CircleExtra extends Struct {
    public static int createCircle(FlatBufferBuilder builder, float x,
                                   float y, float radius) {
        builder.prep(4, 12);
        builder.putFloat(radius);
        builder.putFloat(y);
        builder.putFloat(x);
        return builder.offset();
    }

    CircleExtra init(int iTmp, ByteBuffer bbTmp) {
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

    public float radius() {
        return bb.getFloat(bb_pos + 8);
    }
}
