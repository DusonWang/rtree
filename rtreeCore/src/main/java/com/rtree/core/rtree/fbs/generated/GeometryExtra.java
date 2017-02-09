package com.rtree.core.rtree.fbs.generated;

import com.google.flatbuffers.FlatBufferBuilder;
import com.google.flatbuffers.Table;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class GeometryExtra extends Table {
    public static GeometryExtra getRootAsGeometry(ByteBuffer bbTmp) {
        return getRootAsGeometry(bbTmp, new GeometryExtra());
    }

    private static GeometryExtra getRootAsGeometry(ByteBuffer bbTmp, GeometryExtra obj) {
        bbTmp.order(ByteOrder.LITTLE_ENDIAN);
        return obj.init(bbTmp.getInt(bbTmp.position()) + bbTmp.position(), bbTmp);
    }

    public static void startGeometry(FlatBufferBuilder builder) {
        builder.startObject(5);
    }

    public static void addType(FlatBufferBuilder builder, byte type) {
        builder.addByte(0, type, 0);
    }

    public static void addBox(FlatBufferBuilder builder, int boxOffset) {
        builder.addStruct(1, boxOffset, 0);
    }

    public static void addPoint(FlatBufferBuilder builder, int pointOffset) {
        builder.addStruct(2, pointOffset, 0);
    }

    public static void addCircle(FlatBufferBuilder builder, int circleOffset) {
        builder.addStruct(3, circleOffset, 0);
    }

    public static void addLine(FlatBufferBuilder builder, int lineOffset) {
        builder.addStruct(4, lineOffset, 0);
    }

    public static int endGeometry(FlatBufferBuilder builder) {
        return builder.endObject();
    }

    GeometryExtra init(int iTmp, ByteBuffer bbTmp) {
        bb_pos = iTmp;
        bb = bbTmp;
        return this;
    }

    public byte type() {
        int o = __offset(4);
        return o != 0 ? bb.get(o + bb_pos) : 0;
    }

    public BoxExtra box() {
        return box(new BoxExtra());
    }

    private BoxExtra box(BoxExtra obj) {
        int o = __offset(6);
        return o != 0 ? obj.init(o + bb_pos, bb) : null;
    }

    public PointExtra point() {
        return point(new PointExtra());
    }

    public PointExtra point(PointExtra obj) {
        int o = __offset(8);
        return o != 0 ? obj.init(o + bb_pos, bb) : null;
    }

    public CircleExtra circle() {
        return circle(new CircleExtra());
    }

    private CircleExtra circle(CircleExtra obj) {
        int o = __offset(10);
        return o != 0 ? obj.init(o + bb_pos, bb) : null;
    }

    public BoxExtra line() {
        return line(new BoxExtra());
    }

    private BoxExtra line(BoxExtra obj) {
        int o = __offset(12);
        return o != 0 ? obj.init(o + bb_pos, bb) : null;
    }
}
