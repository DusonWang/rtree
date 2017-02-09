package com.rtree.core.rtree.fbs.generated;

import com.google.flatbuffers.FlatBufferBuilder;
import com.google.flatbuffers.Table;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class EntryExtra extends Table {
    public static EntryExtra getRootAsEntry(ByteBuffer bbTmp) {
        return getRootAsEntry(bbTmp, new EntryExtra());
    }

    private static EntryExtra getRootAsEntry(ByteBuffer bbTmp, EntryExtra obj) {
        bbTmp.order(ByteOrder.LITTLE_ENDIAN);
        return obj.init(bbTmp.getInt(bbTmp.position()) + bbTmp.position(), bbTmp);
    }

    public static int createEntry(FlatBufferBuilder builder,
                                  int geometryOffset, int objectOffset) {
        builder.startObject(2);
        EntryExtra.addObject(builder, objectOffset);
        EntryExtra.addGeometry(builder, geometryOffset);
        return EntryExtra.endEntry(builder);
    }

    public static void startEntry(FlatBufferBuilder builder) {
        builder.startObject(2);
    }

    private static void addGeometry(FlatBufferBuilder builder, int geometryOffset) {
        builder.addOffset(0, geometryOffset, 0);
    }

    private static void addObject(FlatBufferBuilder builder, int objectOffset) {
        builder.addOffset(1, objectOffset, 0);
    }

    public static int createObjectVector(FlatBufferBuilder builder, byte[] data) {
        builder.startVector(1, data.length, 1);
        for (int i = data.length - 1; i >= 0; i--)
            builder.addByte(data[i]);
        return builder.endVector();
    }

    public static void startObjectVector(FlatBufferBuilder builder, int numElems) {
        builder.startVector(1, numElems, 1);
    }

    private static int endEntry(FlatBufferBuilder builder) {
        return builder.endObject();
    }

    EntryExtra init(int iTmp, ByteBuffer bbTmp) {
        bb_pos = iTmp;
        bb = bbTmp;
        return this;
    }

    public GeometryExtra geometry() {
        return geometry(new GeometryExtra());
    }

    public GeometryExtra geometry(GeometryExtra obj) {
        int o = __offset(4);
        return o != 0 ? obj.init(__indirect(o + bb_pos), bb) : null;
    }

    public byte object(int j) {
        int o = __offset(6);
        return o != 0 ? bb.get(__vector(o) + j) : 0;
    }

    public int objectLength() {
        int o = __offset(6);
        return o != 0 ? __vector_len(o) : 0;
    }

    public ByteBuffer objectAsByteBuffer() {
        return __vector_as_bytebuffer(6, 1);
    }
}
