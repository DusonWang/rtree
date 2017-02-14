package com.rtree.core.rtree.geometry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangds on 17/2/10.
 */
public final class Polygon implements HasGeometry, Geometry {

    private List<Point> points;  //多边形的顶点

    Polygon(List<Point> points) {
        this.points = points;
    }

    List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }

    @Override
    public Rectangle geometry() {
        if (points != null && points.size() > 2) {
            double minX = points.get(0).x();
            double minY = points.get(0).y();
            double maxX = points.get(0).x();
            double maxY = points.get(0).y();
            for (int i = 1; i < points.size(); i++) {
                minX = Math.min(minX, points.get(i).x());
                minY = Math.min(minY, points.get(i).y());
                maxX = Math.max(maxX, points.get(i).x());
                maxY = Math.max(maxY, points.get(i).y());
            }
            return RectangleImpl.create(minX, minY, maxX, maxY);
        }
        return null;
    }

    private boolean polygonsIntersect(Polygon p) {
        // 如果一个范围包含另一个范围，则返回true;
        int size = p.points.size() - 1;
        double x = p.points.get(size).x();
        double y = p.points.get(size).y();
        if (searchPoint(Point.create(x, y)))
            return true;
        x = this.points.get(size).x();
        y = this.points.get(size).y();
        if (p.searchPoint(Point.create(x, y)))
            return true;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < this.points.size() - 1; j++) {
                if (Line.linesIntersect(p.points.get(i).x(), p.points.get(i)
                                .y(), p.points.get(i + 1).x(), p.points
                                .get(i + 1).y(), this.points.get(j).x(),
                        this.points.get(j).y(), this.points.get(j + 1)
                                .x(), this.points.get(j + 1).y()))
                    return true;
            }
        }
        return false;
    }

    @Override
    public double distance(Rectangle r) {
        return r.distance(this.geometry());
    }

    @Override
    public Rectangle mbr() {
        return this.geometry();
    }

    @Override
    public boolean intersects(Rectangle r) {
        return this.polygonsIntersect(new RectangleImpl(r.x1(), r.x2(), r.y1(), r.y2()).createPolygon());
    }

    @Override
    public boolean intersects(Geometry r) {
        return false;
    }

    @Override
    public boolean searchPoint(Point point) {
        double x = point.x();
        double y = point.y();
        int nPoints = points.size();
        int hits = 0;

        double lastX = points.get(nPoints - 1).x();
        double lastY = points.get(nPoints - 1).y();
        double curX, curY;

        // Walk the edges of the polygon
        for (int i = 0; i < nPoints; lastX = curX, lastY = curY, i++) {
            curX = points.get(i).x();
            curY = points.get(i).y();

            if (curY == lastY) {
                continue;
            }

            double leftX;
            if (curX < lastX) {
                if (x >= lastX) {
                    continue;
                }
                leftX = curX;
            } else {
                if (x >= curX) {
                    continue;
                }
                leftX = lastX;
            }

            double test1, test2;
            if (curY < lastY) {
                if (y < curY || y >= lastY) {
                    continue;
                }
                if (x < leftX) {
                    hits++;
                    continue;
                }
                test1 = x - curX;
                test2 = y - curY;
            } else {
                if (y < lastY || y >= curY) {
                    continue;
                }
                if (x < leftX) {
                    hits++;
                    continue;
                }
                test1 = x - lastX;
                test2 = y - lastY;
            }

            if (test1 < (test2 / (lastY - curY) * (lastX - curX))) {
                hits++;
            }
        }
        return (hits & 1) != 0;
    }

    private static Polygon convertToPoly(String str) {
        String[] array = str.split(",");
        List<Point> corner = new ArrayList<>();
        for (int i = 0; i < array.length; i += 2) {
            Point c = Point.create(Double.parseDouble(array[i]),
                    Double.parseDouble(array[i + 1]));
            corner.add(c);
        }
        return new Polygon(corner);
    }

    public static void main(String[] args) {
        String str1 = "116.385603,39.937328,116.38616,39.93638,116.387422,39.936712,116.387476,39.93758,116.386371,39.937621";
        String str2 = "116.385805,39.937649,116.385379,39.937003,116.384894,39.937452,116.385055,39.93776";
        Polygon p1 = convertToPoly(str1);
        Polygon p2 = convertToPoly(str2);
        boolean result = p1.polygonsIntersect(p2);
        System.out.println("是否相交：" + result);
        Rectangle r = p1.geometry();
        System.out.println(r);
    }
}
