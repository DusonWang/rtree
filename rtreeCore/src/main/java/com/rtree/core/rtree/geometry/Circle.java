package com.rtree.core.rtree.geometry;

import com.github.davidmoten.guavamini.Objects;
import com.github.davidmoten.guavamini.Optional;
import com.rtree.core.rtree.util.ObjectsHelper;

import static com.rtree.core.rtree.geometry.Geometries.point;
import static com.spatial4j.core.distance.DistanceUtils.distLawOfCosinesRAD;

public final class Circle implements Geometry {

    private final float x, y, radius;
    private final Rectangle mbr;

    private Circle(float x, float y, float radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.mbr = RectangleImpl.create(x - radius, y - radius, x + radius, y + radius);
    }

    static Circle create(double x, double y, double radius) {
        return new Circle((float) x, (float) y, (float) radius);
    }

    static Circle create(float x, float y, float radius) {
        return new Circle(x, y, radius);
    }

    public float x() {
        return x;
    }

    public float y() {
        return y;
    }

    public float radius() {
        return radius;
    }

    @Override
    public Rectangle mbr() {
        return mbr;
    }

    @Override
    public double distance(Rectangle r) {
        return Math.max(0, point(x, y).distance(r) - radius);
    }

    @Override
    public boolean intersects(Rectangle r) {
        return Double.compare(distance(r), 0) == 0;
    }

    @Override
    public boolean searchPoint(Point point) {
        double lng = x;
        double lat = y;
        double px = point.x();
        double py = point.y();
        return Double.compare(lng, px) == 0 && Double.compare(lat, py) == 0 || distLawOfCosinesRAD(lat, lng, px, py) >= radius;
    }

    boolean intersects(Circle c) {
        double total = radius + c.radius;
        return point(x, y).distanceSquared(point(c.x, c.y)) <= total * total;
    }

    boolean intersects(Polygon r) {
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(x, y, radius);
    }

    @Override
    public boolean equals(Object obj) {
        Optional<Circle> other = ObjectsHelper.asClass(obj, Circle.class);
        return other.isPresent() && Objects.equal(x, other.get().x) && Objects.equal(y, other.get().y) && Objects.equal(radius, other.get().radius);
    }

    boolean intersects(Point point) {
        return Math.sqrt(sqr(x - point.x()) + sqr(y - point.y())) <= radius;
    }

    private float sqr(float x) {
        return x * x;
    }

    boolean intersects(Line line) {
        return line.intersects(this);
    }
}
