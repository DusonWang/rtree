package com.rtree.core.bean;


import com.rtree.core.rtree.geometry.Point;
import com.rtree.core.rtree.geometry.Polygon;

public final class PolygonPoly extends PolyBase {

    private Polygon polygon;

    public Polygon getPolygon() {
        return polygon;
    }

    public void setPolygon(Polygon polygon) {
        this.polygon = polygon;
    }

    public boolean searchPoint(Point point) {
        return polygon.searchPoint(point);
    }
}