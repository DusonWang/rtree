package com.rtree.core.data;


import com.rtree.core.rtree.geometry.Circle;
import com.rtree.core.rtree.geometry.Geometry;
import com.rtree.core.rtree.geometry.Point;

import java.util.Collections;
import java.util.List;

public class MyCirclePolyzer extends MyPolyBase {

    private static final long serialVersionUID = 1L;
    private Circle circle;

    public static double distLawOfCosinesRAD(double lat1, double lon1, double lat2, double lon2) {
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
        double lng = this.circle.x();
        double lat = this.circle.y();
        double px = point.x();
        double py = point.y();
        return Double.compare(lng, px) == 0 && Double.compare(lat, py) == 0 || distLawOfCosinesRAD(lat, lng, px, py) >= this.circle.radius();
    }

    public Circle getCircle() {
        return circle;
    }

    public void setCircle(Circle circle) {
        this.circle = circle;
    }

    @Override
    public List<Corner> getCorner() {
        return Collections.emptyList();
    }
}
