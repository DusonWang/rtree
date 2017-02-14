package com.rtree.core.data;

import com.rtree.core.bean.AreaGeojson;
import com.rtree.core.util.JsonMapperUtils;

/**
 * Created by wangds on 17/2/9.
 */
public class GeoJsonData {
    public static void main(String[] args) {
        String json ="{\"type\": \"FeatureCollection\", \"features\": [{\"geometry\": {\"type\": \"Polygon\", \"coordinates\": [[[121.417757, 31.025818], [121.446424, 31.034644], [121.450716, 31.023097], [121.421962, 31.015006], [121.417757, 31.025818]]]}, \"type\": \"Feature\", \"id\": \"aeac6eb6-84a5-11e5-8767-b82a72da8aa2\"}]}";
        System.out.println(JsonMapperUtils.jsonSerialize(JsonMapperUtils.jsonDeserialize(json,AreaGeojson.class)));
    }
}
