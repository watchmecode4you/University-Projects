package com.example.owner.googlemapsgoogleplaces.DemoMap.Utils;

import com.google.android.gms.maps.model.LatLng;

public class BuildCoord {
    private double lat,lng;
    private String buildingName ;

    public BuildCoord(){
        lat=0;
        lng=0;
    }

    public void setCoord(double lat1,double lng1){
        lat=lat1;
        lng=lng1;
    }

    public double getLat(){

        return lat;
    }

    public double getLng(){

        return lng;
    }

    public void setBuildingName(String buildingName){

        this.buildingName = buildingName ;
    }

    public String getBuildName(){

        return buildingName ;
    }

    public LatLng getLatLng(){
        LatLng latLng=new LatLng(lat,lng);
        return latLng;
    }
}
