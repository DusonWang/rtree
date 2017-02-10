package com.rtree.core.bean;


import com.rtree.core.rtree.geometry.Point;
import com.rtree.core.rtree.geometry.Polygon;

public class PolygonPoly extends PolyBase {

    private static final long serialVersionUID = 1L;

    public Polygon getPolygon() {
        return polygon;
    }

    public void setPolygon(Polygon polygon) {
        this.polygon = polygon;
    }

    private Polygon polygon;


    public boolean searchPoint(Point point) {
        return polygon.searchPoint(point);
    }
}