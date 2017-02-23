package com.rtree.core.search;

import com.rtree.core.rtree.RTree;
import com.rtree.core.rtree.geometry.Geometry;
import com.rtree.core.rtree.geometry.Point;

/**
 * Created by wangds on 17/2/14.
 */
final class RtreeSearch {

    private static RTree<String, Geometry> tree = RTree.star().maxChildren(6).create();
    private static RtreeSearch instance = new RtreeSearch();

    private boolean addRtree() {
        return false;
    }

    private boolean Search(Point point) {
        //TODO point in Rectangle,Circle,Polygon,Line
        return false;
    }

    private boolean delRtree() {
        return false;
    }
}
