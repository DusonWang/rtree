package com.rtree.core.rtree.geometry;

import com.github.davidmoten.guavamini.Objects;
import com.github.davidmoten.guavamini.Optional;
import com.rtree.core.rtree.util.ObjectsHelper;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.operation.predicate.RectangleIntersects;
import com.vividsolutions.jts.util.GeometricShapeFactory;

import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Float;
import java.util.List;

public final class Line implements Geometry {

    private float x1;
    private float y1;
    private float x2;
    private float y2;

    Line(float x1, float y1, float x2, float y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    static Line create(float x1, float y1, float x2, float y2) {
        return new Line(x1, y1, x2, y2);
    }

    static Line create(double x1, double y1, double x2, double y2) {
        return new Line((float) x1, (float) y1, (float) x2, (float) y2);
    }

    private static int relativeCCW(double x1, double y1, double x2, double y2,
                                   double px, double py) {
        x2 -= x1;
        y2 -= y1;
        px -= x1;
        py -= y1;
        double ccw = px * y2 - py * x2;
        if (ccw == 0.0d) {
            ccw = px * x2 + py * y2;
            if (ccw > 0.0d) {
                px -= x2;
                py -= y2;
                ccw = px * x2 + py * y2;
                if (ccw < 0.0d) {
                    ccw = 0.0d;
                }
            }
        }
        return ccw < 0.0d ? -1 : ccw > 0.0d ? 1 : 0;
    }

    /**
     * 判断两个线段是否相交
     */
    static boolean linesIntersect(double x1, double y1, double x2, double y2,
                                  double x3, double y3, double x4, double y4) {
        return relativeCCW(x1, y1, x2, y2, x3, y3) * relativeCCW(x1, y1, x2, y2, x4, y4) <= 0
                && relativeCCW(x3, y3, x4, y4, x1, y1) * relativeCCW(x3, y3, x4, y4, x2, y2) <= 0;
    }

    @Override
    public double distance(Rectangle r) {
        if (r.contains(x1, y1) || r.contains(x2, y2)) {
            return 0;
        } else {
            double d1 = distance(r.x1(), r.y1(), r.x1(), r.y2());
            if (Double.compare(d1, 0) == 0)
                return 0;
            double d2 = distance(r.x1(), r.y2(), r.x2(), r.y2());
            if (Double.compare(d2, 0) == 0)
                return 0;
            double d3 = distance(r.x2(), r.y2(), r.x2(), r.y1());
            double d4 = distance(r.x2(), r.y1(), r.x1(), r.y1());
            return Math.min(d1, Math.min(d2, Math.min(d3, d4)));
        }
    }

    private double distance(float x1, float y1, float x2, float y2) {
        Float line = new Float(x1, y1, x2, y2);
        double d1 = line.ptSegDist(this.x1, this.y1);
        double d2 = line.ptSegDist(this.x2, this.y2);
        Float line2 = new Float(this.x1, this.y1, this.x2, this.y2);
        double d3 = line2.ptSegDist(x1, y1);
        if (Double.compare(d3, 0) == 0)
            return 0;
        double d4 = line2.ptSegDist(x2, y2);
        if (Double.compare(d4, 0) == 0)
            return 0;
        else
            return Math.min(d1, Math.min(d2, Math.min(d3, d4)));

    }

    @Override
    public Rectangle mbr() {
        return Geometries.rectangle(Math.min(x1, x2), Math.min(y1, y2), Math.max(x1, x2), Math.max(y1, y2));
    }

    @Override
    public boolean intersects(Rectangle r) {
        GeometryFactory gf = new GeometryFactory();
        GeometricShapeFactory f = new GeometricShapeFactory(gf);
        f.setBase(new Coordinate(r.x1(), r.y1()));
        f.setWidth(r.x2() - r.x1());
        f.setHeight(r.y2() - r.y1());
        Polygon rect = f.createRectangle();
        LineSegment line = new LineSegment(x1, y1, x2, y2);
        return RectangleIntersects.intersects(rect, line.toGeometry(gf));
    }

    @Override
    public boolean intersectGeometry(Geometry r) {
        return false;
    }

    @Override
    public boolean searchPoint(Point point) {
        return Math.sqrt(Math.pow(point.x() - x1, 2) + Math.pow(point.y() - y1, 2))
                + Math.sqrt(Math.pow(point.x() - x2, 2 + Math.pow(point.y() - y2, 2))) ==
                Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    public float x1() {
        return x1;
    }

    public float y1() {
        return y1;
    }

    public float x2() {
        return x2;
    }

    public float y2() {
        return y2;
    }

    boolean intersects(Line b) {
        Line2D line1 = new Float(x1, y1, x2, y2);
        Line2D line2 = new Float(b.x1(), b.y1(), b.x2(), b.y2());
        return line2.intersectsLine(line1);
    }

    boolean intersects(Point point) {
        return intersects(point.mbr());
    }

    boolean intersects(Circle circle) {
        Vector c = Vector.create(circle.x(), circle.y());
        Vector a = Vector.create(x1, y1);
        Vector cMinusA = c.minus(a);
        float radiusSquared = circle.radius() * circle.radius();
        if (Double.compare(x1, x2) == 0 && Double.compare(y1, y2) == 0) {
            return cMinusA.modulusSquared() <= radiusSquared;
        } else {
            Vector b = Vector.create(x2, y2);
            Vector bMinusA = b.minus(a);
            float bMinusAModulus = bMinusA.modulus();
            float lambda = cMinusA.dot(bMinusA) / bMinusAModulus;
            if (lambda >= 0 && lambda <= bMinusAModulus) {
                Vector dMinusA = bMinusA.times(lambda / bMinusAModulus);
                return cMinusA.modulusSquared() - dMinusA.modulusSquared() <= radiusSquared;
            } else {
                return cMinusA.modulusSquared() <= radiusSquared || c.minus(b).modulusSquared() <= radiusSquared;
            }
        }
    }

    boolean intersects(com.rtree.core.rtree.geometry.Polygon polygon) {
        List<Point> points = polygon.getPoints();
        int size = points.size() - 1;
        double x = points.get(size).x();
        double y = points.get(size).y();
        if (searchPoint(Point.create(x, y)))
            return true;
        x = points.get(size).x();
        y = points.get(size).y();
        if (polygon.searchPoint(Point.create(x, y)))
            return true;
        for (int i = 0; i < size; i++) {
            if (linesIntersect(points.get(i).x(), points.get(i).y(), points.get(i + 1).x(), points.get(i + 1).y(), x1, y1, x2, y2))
                return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(x1, y1, x2, y2);
    }

    @Override
    public boolean equals(Object obj) {
        Optional<Line> other = ObjectsHelper.asClass(obj, Line.class);
        return other.isPresent() && Objects.equal(x1, other.get().x1) && Objects.equal(x2, other.get().x2) && Objects.equal(y1, other.get().y1) && Objects.equal(y2, other.get().y2);
    }

    private static final class Vector {
        final float x;
        final float y;

        Vector(float x, float y) {
            this.x = x;
            this.y = y;
        }

        static Vector create(float x, float y) {
            return new Vector(x, y);
        }

        float dot(Vector v) {
            return x * v.x + y * v.y;
        }

        Vector times(float value) {
            return create(value * x, value * y);
        }

        Vector minus(Vector v) {
            return create(x - v.x, y - v.y);
        }

        float modulus() {
            return (float) Math.sqrt(x * x + y * y);
        }

        float modulusSquared() {
            return x * x + y * y;
        }

    }

}
