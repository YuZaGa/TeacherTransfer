package com.teachertransfer.util;

import com.github.davidmoten.geo.GeoHash;
import com.github.davidmoten.geo.LatLong;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GeohashUtil {

    public static String encode(double lat, double lng) {
        return GeoHash.encodeHash(lat, lng, 6);
    }

    public static String encode(double lat, double lng, int precision) {
        return GeoHash.encodeHash(lat, lng, precision);
    }

    public static int precisionForRadius(int radiusKm) {
        if (radiusKm <= 2) return 6;
        if (radiusKm <= 50) return 5;
        return 4;
    }

    public static int ringsForRadius(int radiusKm) {
        if (radiusKm <= 3) return 1;
        if (radiusKm <= 12) return 2;
        if (radiusKm <= 25) return 3;
        if (radiusKm <= 50) return 4;
        return 1;
    }

    public static Set<String> getNeighborHashes(String geohash) {
        Set<String> neighbors = new HashSet<>(GeoHash.neighbours(geohash));
        neighbors.add(geohash);
        return neighbors;
    }

    public static Set<String> getNeighborHashes(String geohash, int rings) {
        Set<String> all = new HashSet<>();
        all.add(geohash);
        Set<String> frontier = new HashSet<>();
        frontier.add(geohash);
        for (int r = 0; r < rings; r++) {
            Set<String> next = new HashSet<>();
            for (String h : frontier) {
                next.addAll(GeoHash.neighbours(h));
            }
            frontier = next;
            frontier.removeAll(all);
            all.addAll(next);
        }
        return all;
    }

    public static Set<String> encodeWithNeighbors(double lat, double lng) {
        return encodeWithNeighbors(lat, lng, 10);
    }

    public static Set<String> encodeWithNeighbors(double lat, double lng, int radiusKm) {
        int precision = precisionForRadius(radiusKm);
        String center = GeoHash.encodeHash(lat, lng, precision);
        return getNeighborHashes(center, ringsForRadius(radiusKm));
    }

    public static LatLong decode(String geohash) {
        return GeoHash.decodeHash(geohash);
    }
}
