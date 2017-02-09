package com.rtree.core.util;

import com.rtree.core.rtree.geometry.Point;
import com.rtree.core.rtree.geometry.Rectangle;
import com.rtree.core.rtree.geometry.RectangleImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * 多边行
 */
public class Polygon {

    private List<Point> points;  //多边形的顶点

    private Rectangle geometry;   //多边形的外接矩形

    //计算多边形的外接矩形
    public Rectangle getGeometry() {
        if (geometry != null)
            return geometry;
        else {
            if (points != null && points.size() > 2) {
                double minX = points.get(0).x();
                double minY = points.get(0).y();
                double maxX = points.get(0).x();
                double maxY = points.get(0).y();
                for (int i = 1; i < points.size(); i++) {
                    minX = min(minX, points.get(i).x());
                    minY = min(minY, points.get(i).y());
                    maxX = max(maxX, points.get(i).x());
                    maxY = max(maxY, points.get(i).y());
                }
                return RectangleImpl.create(minX, minY, maxX, maxY);
            }
            return null;
        }
    }

    public List<Point> getCorner() {
        return points;
    }

    private void setCorner(List<Point> points) {
        this.points = points;
    }

    /**
     * 点跟多边形的关系。多边形包含点则返回true
     *
     * @param point
     * @return
     */
    private boolean searchPoint(Point point) {
        double x = point.x();
        double y = point.y();
        int npoints = points.size();
        int hits = 0;

        double lastx = points.get(npoints - 1).x();
        double lasty = points.get(npoints - 1).y();
        double curx, cury;

        // Walk the edges of the polygon
        for (int i = 0; i < npoints; lastx = curx, lasty = cury, i++) {
            curx = points.get(i).x();
            cury = points.get(i).y();

            if (cury == lasty) {
                continue;
            }

            double leftx;
            if (curx < lastx) {
                if (x >= lastx) {
                    continue;
                }
                leftx = curx;
            } else {
                if (x >= curx) {
                    continue;
                }
                leftx = lastx;
            }

            double test1, test2;
            if (cury < lasty) {
                if (y < cury || y >= lasty) {
                    continue;
                }
                if (x < leftx) {
                    hits++;
                    continue;
                }
                test1 = x - curx;
                test2 = y - cury;
            } else {
                if (y < lasty || y >= cury) {
                    continue;
                }
                if (x < leftx) {
                    hits++;
                    continue;
                }
                test1 = x - lastx;
                test2 = y - lasty;
            }

            if (test1 < (test2 / (lasty - cury) * (lastx - curx))) {
                hits++;
            }
        }
        return ((hits & 1) != 0);
    }

    private static int relativeCCW(double x1, double y1, double x2, double y2,
                                   double px, double py) {
        x2 -= x1;
        y2 -= y1;
        px -= x1;
        py -= y1;
        double ccw = px * y2 - py * x2;
        if (ccw == 0.0) {
            ccw = px * x2 + py * y2;
            if (ccw > 0.0) {
                px -= x2;
                py -= y2;
                ccw = px * x2 + py * y2;
                if (ccw < 0.0) {
                    ccw = 0.0;
                }
            }
        }
        return (ccw < 0.0) ? -1 : ((ccw > 0.0) ? 1 : 0);
    }

    /**
     * 判断两个线段是否相交
     */
    private static boolean linesIntersect(double x1, double y1, double x2,
                                          double y2, double x3, double y3, double x4, double y4) {
        return ((relativeCCW(x1, y1, x2, y2, x3, y3)
                * relativeCCW(x1, y1, x2, y2, x4, y4) <= 0) && (relativeCCW(x3,
                y3, x4, y4, x1, y1) * relativeCCW(x3, y3, x4, y4, x2, y2) <= 0));
    }

    /**
     * 判断两个范围是否相交
     */
    private boolean polygonsIntersect(Polygon p) {
        // 如果一个范围包含另一个范围，则返回true;
        double x = p.points.get(p.points.size() - 1).x();
        double y = p.points.get(p.points.size() - 1).y();
        if (searchPoint(Point.create(x, y)))
            return true;
        x = this.points.get(this.points.size() - 1).x();
        y = this.points.get(this.points.size() - 1).y();
        if (p.searchPoint(Point.create(x, y)))
            return true;
        for (int i = 0; i < p.points.size() - 1; i++) {
            for (int j = 0; j < this.points.size() - 1; j++) {
                if (linesIntersect(p.points.get(i).x(), p.points.get(i)
                                .y(), p.points.get(i + 1).x(), p.points
                                .get(i + 1).y(), this.points.get(j).x(),
                        this.points.get(j).y(), this.points.get(j + 1)
                                .x(), this.points.get(j + 1).y()))
                    return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        String str1 = "116.385603,39.937328,116.38616,39.93638,116.387422,39.936712,116.387476,39.93758,116.386371,39.937621";
        String str2 = "116.385805,39.937649,116.385379,39.937003,116.384894,39.937452,116.385055,39.93776";
        Polygon p1 = convertToPoly(str1);
        Polygon p2 = convertToPoly(str2);
        boolean result = p1.polygonsIntersect(p2);
        System.out.println("是否相交：" + result);
        Rectangle r = p1.getGeometry();
        System.out.println(r);
    }

    private static Polygon convertToPoly(String str) {
        Polygon p = new Polygon();
        String[] array = str.split(",");
        List<Point> corner = new ArrayList<>();
        for (int i = 0; i < array.length; i += 2) {
            Point c = Point.create(Double.parseDouble(array[i]),
                    Double.parseDouble(array[i + 1]));
            corner.add(c);
        }
        p.setCorner(corner);
        return p;
    }

    private double max(double a, double b) {
        if (a < b)
            return b;
        else
            return a;
    }

    private double min(double a, double b) {
        if (a < b)
            return a;
        else
            return b;
    }
}
