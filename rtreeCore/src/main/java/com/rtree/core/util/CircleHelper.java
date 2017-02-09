package com.rtree.core.util;

import com.rtree.core.rtree.geometry.Rectangle;
import com.rtree.core.rtree.geometry.RectangleImpl;
import com.spatial4j.core.distance.DistanceUtils;

public final class CircleHelper {

    /**
     * 圆心距离（单位：米） 转化成圆度数
     */
    public static double getCircleDegreesByDis(double dis) {
        return DistanceUtils.dist2Degrees(dis / 1000.0, DistanceUtils.EARTH_MEAN_RADIUS_KM);
    }

    public static Rectangle rectangleFromCircle(double lon, double lat, double radius) {
        double distDEG = getCircleDegreesByDis(radius);
        double minX;
        double maxX;
        double minY;
        double maxY;
        if (distDEG == 0) {
            minX = lon;
            maxX = lon;
            minY = lat;
            maxY = lat;
        } else if (distDEG >= 180) {// distance is >= opposite side of the globe
            minX = -180;
            maxX = 180;
            minY = -90;
            maxY = 90;
        } else {

            // --calc latitude bounds
            maxY = lat + distDEG;
            minY = lat - distDEG;

            if (maxY >= 90 || minY <= -90) {// touches either pole
                // we have special logic for longitude
                minX = -180;
                maxX = 180;// world wrap: 360 deg
                if (maxY <= 90 && minY >= -90) {// doesn't pass either pole: 180
                    // deg
                    minX = DistanceUtils.normLonDEG(lon - 90);
                    maxX = DistanceUtils.normLonDEG(lon + 90);
                }
                if (maxY > 90)
                    maxY = 90;
                if (minY < -90)
                    minY = -90;
            } else {
                // --calc longitude bounds
                double lon_delta_deg = DistanceUtils.calcBoxByDistFromPt_deltaLonDEG(lat, lon, distDEG);

                minX = DistanceUtils.normLonDEG(lon - lon_delta_deg);
                maxX = DistanceUtils.normLonDEG(lon + lon_delta_deg);
            }
        }
        return RectangleImpl.create(minX, minY, maxX, maxY);
    }

}