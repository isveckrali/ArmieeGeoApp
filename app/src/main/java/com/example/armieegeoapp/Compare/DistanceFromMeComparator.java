package com.example.armieegeoapp.Compare;

import com.example.armieegeoapp.Models.GeoListingModel;

import java.util.Comparator;

public class DistanceFromMeComparator implements Comparator<GeoListingModel> {
    GeoListingModel me;

    //Sort list which coming with contructor by current user location
    public DistanceFromMeComparator(GeoListingModel me) {
        this.me = me;
    }
    private Double distanceFromMe(GeoListingModel p) {
        double theta = p.getLongitude() - me.getLongitude();
        double dist = Math.sin(deg2rad(p.getLatitude())) * Math.sin(deg2rad(me.getLatitude()))
                + Math.cos(deg2rad(p.getLatitude())) * Math.cos(deg2rad(me.getLatitude()))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        return dist;
    }

    private double deg2rad(double deg) { return (deg * Math.PI / 180.0); }
    private double rad2deg(double rad) { return (rad * 180.0 / Math.PI); }

    @Override
    public int compare(GeoListingModel p1, GeoListingModel p2) {
        return distanceFromMe(p1).compareTo(distanceFromMe(p2));
    }
}
