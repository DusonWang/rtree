package com.rtree.core.data;


import com.rtree.core.rtree.geometry.Geometry;
import com.rtree.core.rtree.geometry.Point;

import java.io.Serializable;
import java.util.List;

public abstract class MyPolyBase implements Serializable {

    private String type;
    private Geometry geometry;
    private String id;
    private String rest_id;

    public String getRest_id() {
        return rest_id;
    }

    public void setRest_id(String rest_id) {
        this.rest_id = rest_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public abstract boolean searchPoint(Point point);

    public abstract List<Corner> getCorner();
}
