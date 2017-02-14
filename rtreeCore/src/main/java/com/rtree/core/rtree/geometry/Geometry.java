package com.rtree.core.rtree.geometry;


public interface Geometry {

    double distance(Rectangle r);

    Rectangle mbr();

    boolean intersects(Rectangle r);

    boolean intersects(Geometry r);

    boolean searchPoint(Point point);
}
