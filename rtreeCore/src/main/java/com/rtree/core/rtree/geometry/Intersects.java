package com.rtree.core.rtree.geometry;

import rx.functions.Func2;

public final class Intersects {

    public static final Func2<Circle, Rectangle, Boolean> circleIntersectsRectangle = Circle::intersects;

    public static final Func2<Rectangle, Circle, Boolean> rectangleIntersectsCircle = (Rectangle rectangle, Circle circle) -> circleIntersectsRectangle.call(circle, rectangle);

    public static final Func2<Circle, Point, Boolean> circleIntersectsPoint = Circle::intersects;

    public static final Func2<Point, Circle, Boolean> pointIntersectsCircle = (Point point, Circle circle) -> circleIntersectsPoint.call(circle, point);

    public static final Func2<Circle, Circle, Boolean> circleIntersectsCircle = Circle::intersects;

    public static final Func2<Line, Line, Boolean> lineIntersectsLine = Line::intersects;

    public static final Func2<Rectangle, Line, Boolean> rectangleIntersectsLine = (Rectangle r, Line a) -> a.intersects(r);

    public static final Func2<Line, Rectangle, Boolean> lineIntersectsRectangle = (Line a, Rectangle r) -> rectangleIntersectsLine.call(r, a);

    public static final Func2<Circle, Line, Boolean> circleIntersectsLine = (Circle c, Line a) -> a.intersects(c);

    public static final Func2<Line, Circle, Boolean> lineIntersectsCircle = (Line a, Circle c) -> circleIntersectsLine.call(c, a);

    public static final Func2<Point, Line, Boolean> pointIntersectsLine = (Point point, Line line) -> line.intersects(point);

    public static final Func2<Line, Point, Boolean> lineIntersectsPoint = (Line line, Point point) -> pointIntersectsLine.call(point, line);

    public static final Func2<Geometry, Line, Boolean> geometryIntersectsLine = (Geometry geometry, Line line) -> {
        if (geometry instanceof Line)
            return line.intersects((Line) geometry);
        else if (geometry instanceof Rectangle)
            return line.intersects((Rectangle) geometry);
        else if (geometry instanceof Circle)
            return line.intersects((Circle) geometry);
        else return null;
    };
    public static final Func2<Geometry, Circle, Boolean> geometryIntersectsCircle = (Geometry geometry, Circle circle) -> {
        if (geometry instanceof Line)
            return circle.intersects((Line) geometry);
        else if (geometry instanceof Rectangle)
            return circle.intersects((Rectangle) geometry);
        else if (geometry instanceof Circle)
            return circle.intersects((Circle) geometry);
        else return null;
    };
    public static final Func2<Circle, Geometry, Boolean> circleIntersectsGeometry = (Circle circle, Geometry geometry) -> geometryIntersectsCircle.call(geometry, circle);

    public static final Func2<Geometry, Rectangle, Boolean> geometryIntersectsRectangle = (Geometry geometry, Rectangle r) -> {
        if (geometry instanceof Line)
            return geometry.intersects(r);
        else if (geometry instanceof Rectangle)
            return r.intersects((Rectangle) geometry);
        else if (geometry instanceof Circle)
            return geometry.intersects(r);
        else return null;
    };
    public static final Func2<Rectangle, Geometry, Boolean> rectangleIntersectsGeometry = (Rectangle r, Geometry geometry) -> geometryIntersectsRectangle.call(geometry, r);

    public static final Func2<Geometry, Point, Boolean> geometryIntersectsPoint = (Geometry geometry, Point point) -> geometryIntersectsRectangle.call(geometry, point.mbr());

    public static final Func2<Point, Geometry, Boolean> pointIntersectsGeometry = (Point point, Geometry geometry) -> geometryIntersectsPoint.call(geometry, point);

    private Intersects() {
    }
}
