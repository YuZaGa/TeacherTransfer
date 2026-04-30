package com.teachertransfer.util;

import com.github.davidmoten.geo.GeoHash;
import com.github.davidmoten.geo.LatLong;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GeohashUtil {

    private static final int DEFAULT_PRECISION = 6;

    public static String encode(double lat, double lng) {
        return GeoHash.encodeHash(lat, lng, DEFAULT_PRECISION);
    }

    public static String encode(double lat, double lng, int precision) {
        return GeoHash.encodeHash(lat, lng, precision);
    }

    public static Set<String> getNeighborHashes(String geohash) {
        Set<String> neighbors = new HashSet<>(GeoHash.neighbours(geohash));
        neighbors.add(geohash);
        return neighbors;
    }

    public static Set<String> encodeWithNeighbors(double lat, double lng) {
        String center = encode(lat, lng);
        return getNeighborHashes(center);
    }

    public static LatLong decode(String geohash) {
        return GeoHash.decodeHash(geohash);
    }
}
