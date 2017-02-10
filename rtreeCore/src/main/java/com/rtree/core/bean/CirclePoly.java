package com.rtree.core.bean;


import com.rtree.core.rtree.geometry.Circle;
import com.rtree.core.rtree.geometry.Geometry;
import com.rtree.core.rtree.geometry.Point;

import java.util.Collections;
import java.util.List;

public final class CirclePoly extends PolyBase {

    private Circle circle;

    private static double distLawOfCosinesRAD(double lat1, double lon1, double lat2, double lon2) {
        double dLon = lon2 - lon1;
        double cosB = (Math.sin(lat1) * Math.sin(lat2)) + (Math.cos(lat1) * Math.cos(lat2) * Math.cos(dLon));
        if (cosB < -1.0d)
            return Math.PI;
        else if (cosB >= 1.0d)
            return 0d;
        else
            return cosB;
    }

    public Geometry getGeometry() {
        return circle;
    }

    @Override
    public boolean searchPoint(Point point) {
        return circle.searchPoint(point);
    }

    public Circle getCircle() {
        return circle;
    }

    public void setCircle(Circle circle) {
        this.circle = circle;
    }

    public List<Corner> getCorner() {
        return Collections.emptyList();
    }
}
