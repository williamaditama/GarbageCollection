package com.example.william.garbagecollection;

import android.location.Location;

import com.tomtom.online.sdk.common.location.LatLng;

import java.util.ArrayList;

/**
 * Created by willi on 02/03/2018.
 */

public class GarbageCan {
    private LatLng location;
    private double size; //In Gallons
    //TODO Find a way to get garbage locations in Location

    public GarbageCan(LatLng location, double size) {
        this.location = location;
        this.size = size;
    }

//    public static GarbageCan getNearest(Location loc, ArrayList<GarbageCan> array) {
//        //TODO
//        return new GarbageCan(new LatLng(0, 0), 5.);
//    }

    public LatLng getLocation() {
        return this.location;
    }

}
