package com.teachertransfer.util;

import com.github.davidmoten.geo.GeoHash;

public class GeohashUtil {
    
    // Default precision of 6 is approx 1.2km x 0.6km
    private static final int DEFAULT_PRECISION = 6;
    
    public static String encode(double lat, double lng) {
        return GeoHash.encodeHash(lat, lng, DEFAULT_PRECISION);
    }
    
    public static String encode(double lat, double lng, int precision) {
        return GeoHash.encodeHash(lat, lng, precision);
    }
}
