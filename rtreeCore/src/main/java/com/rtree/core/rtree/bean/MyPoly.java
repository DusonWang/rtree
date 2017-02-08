package com.rtree.core.rtree.bean;


import com.rtree.core.rtree.geometry.Point;
import com.rtree.core.rtree.geometry.Rectangle;

import java.util.List;

public class MyPoly extends MyPolyBase {

    private static final long serialVersionUID = 1L;
    private List<Corner> corner;
    private Rectangle geometry;

    public Rectangle getGeometry() {
        return geometry;
    }

    public void setGeometry(Rectangle geometry) {
        this.geometry = geometry;
    }

    public List<Corner> getCorner() {
        return corner;
    }

    public void setCorner(List<Corner> corner) {
        this.corner = corner;
    }

    public boolean searchPoint(Point point) {
        double x = point.x();
        double y = point.y();
        int pointLen = corner.size();
        int hits = 0;

        double lastX = corner.get(pointLen - 1).getPolyX();
        double lastY = corner.get(pointLen - 1).getPolyY();
        double curX, curY;

        // Walk the edges of the polygon
        for (int i = 0; i < pointLen; lastX = curX, lastY = curY, i++) {

            curX = corner.get(i).getPolyX();
            curY = corner.get(i).getPolyY();

            if (Double.compare(x, curX) == 0
                    && Double.compare(y, curY) == 0)
                return true;

            if (Double.compare(curY, lastY) == 0) {
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
}