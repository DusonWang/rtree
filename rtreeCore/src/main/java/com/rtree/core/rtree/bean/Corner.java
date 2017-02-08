package com.rtree.core.rtree.bean;

import java.io.Serializable;

public class Corner implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private double polyX;
    private double polyY;

    public Corner(double polyX, double polyY) {
        this.polyX = polyX;
        this.polyY = polyY;
    }

    public double getPolyX() {
        return polyX;
    }

    public void setPolyX(double polyX) {
        this.polyX = polyX;
    }

    public double getPolyY() {
        return polyY;
    }

    public void setPolyY(double polyY) {
        this.polyY = polyY;
    }
}
