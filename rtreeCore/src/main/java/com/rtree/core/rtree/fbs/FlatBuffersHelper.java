package com.rtree.core.rtree.fbs;

import com.github.davidmoten.guavamini.Preconditions;
import com.google.flatbuffers.FlatBufferBuilder;
import com.rtree.core.rtree.Entries;
import com.rtree.core.rtree.Entry;
import com.rtree.core.rtree.Util;
import com.rtree.core.rtree.fbs.generated.*;
import com.rtree.core.rtree.geometry.*;
import rx.functions.Func1;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class FlatBuffersHelper {

    private FlatBuffersHelper() {
    }

    public static <T, S extends Geometry> int addEntries(List<Entry<T, S>> entries, FlatBufferBuilder builder, Func1<? super T, byte[]> serializer) {
        int[] entries2 = new int[entries.size()];
        for (int i = 0; i < entries.size(); i++) {
            Geometry g = entries.get(i).geometry();
            final int geom;
            final byte geomType;
            if (g instanceof Rectangle) {
                Rectangle b = (Rectangle) g;
                geom = BoxExtra.createBox(builder, b.x1(), b.y1(), b.x2(), b.y2());
                geomType = GeometryTypeExtra.Box;
            } else if (g instanceof Point) {
                Point p = (Point) g;
                geom = PointExtra.createPoint_(builder, p.x(), p.y());
                geomType = GeometryTypeExtra.Point;
            } else if (g instanceof Circle) {
                Circle c = (Circle) g;
                geom = CircleExtra.createCircle(builder, c.x(), c.y(), c.radius());
                geomType = GeometryTypeExtra.Circle;
            } else if (g instanceof Line) {
                Line c = (Line) g;
                geom = LineExtra.createLine_(builder, c.x1(), c.y1(), c.x2(), c.y2());
                geomType = GeometryTypeExtra.Line;
            } else
                throw new RuntimeException("unexpected");
            GeometryExtra.startGeometry(builder);
            if (geomType == GeometryTypeExtra.Box) {
                GeometryExtra.addBox(builder, geom);
            } else if (geomType == GeometryTypeExtra.Point) {
                GeometryExtra.addPoint(builder, geom);
            } else if (geomType == GeometryTypeExtra.Circle) {
                GeometryExtra.addCircle(builder, geom);
            } else {
                GeometryExtra.addLine(builder, geom);
            }
            GeometryExtra.addType(builder, geomType);
            int geo = GeometryExtra.endGeometry(builder);
            int obj = EntryExtra.createObjectVector(builder, serializer.call(entries.get(i).value()));
            entries2[i] = EntryExtra.createEntry(builder, geo, obj);
        }

        int ent = NodeExtra.createEntriesVector(builder, entries2);
        Rectangle mbb = Util.mbr(entries);
        int b = BoxExtra.createBox(builder, mbb.x1(), mbb.y1(), mbb.x2(), mbb.y2());
        NodeExtra.startNode(builder);
        NodeExtra.addMbb(builder, b);
        NodeExtra.addEntries(builder, ent);
        return NodeExtra.endNode_(builder);

    }

    static <T, S extends Geometry> List<Entry<T, S>> createEntries(NodeExtra node, Func1<byte[], ? extends T> deserializer) {
        int numEntries = node.entriesLength();
        List<Entry<T, S>> entries = new ArrayList<>(numEntries);
        Preconditions.checkArgument(numEntries > 0);
        EntryExtra entry = new EntryExtra();
        GeometryExtra geom = new GeometryExtra();
        for (int i = 0; i < numEntries; i++) {
            Entry<T, S> ent = createEntry(node, deserializer, entry, geom, i);
            entries.add(ent);
        }
        return entries;
    }

    @SuppressWarnings("unchecked")
    private static <T, S extends Geometry> Entry<T, S> createEntry(NodeExtra node, Func1<byte[], ? extends T> deserializer, EntryExtra entry, GeometryExtra geom, int i) {
        node.entries(entry, i);
        entry.geometry(geom);
        final Geometry g = toGeometry(geom);
        return Entries.entry(parseObject(deserializer, entry), (S) g);
    }

    static <T, S extends Geometry> Entry<T, S> createEntry(NodeExtra node, Func1<byte[], ? extends T> deserializer, int i) {
        return createEntry(node, deserializer, new EntryExtra(), new GeometryExtra(), i);
    }

    static <T> T parseObject(Func1<byte[], ? extends T> deserializer, EntryExtra entry) {
        ByteBuffer bb = entry.objectAsByteBuffer();
        if (bb == null) {
            return null;
        } else {
            byte[] bytes = Arrays.copyOfRange(bb.array(), bb.position(), bb.limit());
            return deserializer.call(bytes);
        }
    }

    @SuppressWarnings("unchecked")
    static <S extends Geometry> S toGeometry(GeometryExtra g) {
        final Geometry result;
        byte type = g.type();
        if (type == GeometryTypeExtra.Box) {
            result = createBox(g.box());
        } else if (type == GeometryTypeExtra.Point) {
            PointExtra p = g.point();
            result = Geometries.point(p.x(), p.y());
        } else if (type == GeometryTypeExtra.Circle) {
            CircleExtra c = g.circle();
            result = Geometries.circle(c.x(), c.y(), c.radius());
        } else if (type == GeometryTypeExtra.Line) {
            result = createLine(g.line());
        } else {
            result = null;
        }
        return (S) result;
    }

    static Rectangle createBox(BoxExtra b) {
        return Geometries.rectangle(b.minX(), b.minY(), b.maxX(), b.maxY());
    }

    static Line createLine(BoxExtra b) {
        return Geometries.line(b.minX(), b.minY(), b.maxX(), b.maxY());
    }

}