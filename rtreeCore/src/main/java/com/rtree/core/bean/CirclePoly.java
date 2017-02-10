package com.rtree.core.bean;


import com.rtree.core.rtree.geometry.Circle;
import com.rtree.core.rtree.geometry.Geometry;
import com.rtree.core.rtree.geometry.Point;

import java.util.Collections;
import java.util.List;

public final class CirclePoly extends PolyBase {

    private Circle circle;
    private static final long serialVersionUID = 1L;
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
