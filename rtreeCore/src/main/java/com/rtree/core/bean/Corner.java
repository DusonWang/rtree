package com.rtree.core.bean;

public final class Corner {

    private double polyX;
    private double polyY;

    public Corner(double polyX, double polyY) {
        this.polyX = polyX;
        this.polyY = polyY;
    }

    double getPolyX() {
        return polyX;
    }

    public void setPolyX(double polyX) {
        this.polyX = polyX;
    }

    double getPolyY() {
        return polyY;
    }

    public void setPolyY(double polyY) {
        this.polyY = polyY;
    }
}
