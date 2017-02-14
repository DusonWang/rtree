package com.rtree.core.search;

import com.rtree.core.rtree.RTree;
import com.rtree.core.rtree.geometry.Geometry;

/**
 * Created by wangds on 17/2/14.
 */
final class RtreeSearch {

    private static RTree<String, Geometry> tree = RTree.star().maxChildren(6).create();
    private static RtreeSearch instance = new RtreeSearch();
// "{\"type\": \"FeatureCollection\", \"features\": [{\"geometry\": {\"type\": \"Polygon\", \"coordinates\": [[[121.417757, 31.025818], [121.446424, 31.034644], [121.450716, 31.023097], [121.421962, 31.015006], [121.417757, 31.025818]]]}, \"type\": \"Feature\", \"id\": \"aeac6eb6-84a5-11e5-8767-b82a72da8aa2\"}]}"

    private boolean addRtree() {
        return false;
    }

    private boolean Search() {
        return false;
    }

    private boolean delRtree() {
        return false;
    }
}
