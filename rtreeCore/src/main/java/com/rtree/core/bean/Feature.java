package com.rtree.core.bean;

public class Feature {
    private GeojsonGeometries geometry;
    private String type;
    private String id;

    public GeojsonGeometries getGeometry() {
        return geometry;
    }

    public void setGeometry(GeojsonGeometries geometry) {
        this.geometry = geometry;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
