package com.example.owner.googlemapsgoogleplaces.DemoMap.MapManipulation;

import org.json.JSONArray;
import org.json.JSONException;

import android.location.Location;

public class NewWeight{

    double weight = 0;

    //returns the total weight from starting to ending coordinates (that were fetched from GetCoordinates)
    public void NewWeight(int start, int limit, JSONArray jArrCoordinates) throws JSONException{


        // 0 == 0 (limit)
        if(start == limit){
            // get the starting coordinates
            JSONArray latlngs = jArrCoordinates.getJSONArray(start);

            double lat_0 = latlngs.getDouble(0);
            double lng_0 = latlngs.getDouble(1);

            Location startNode = new Location("");
            startNode.setLatitude(lat_0);
            startNode.setLongitude(lng_0);

            // get coordinates of next node
            JSONArray latlngs1 = jArrCoordinates.getJSONArray(++start);

            double lat_1 = latlngs1.getDouble(0);
            double lng_1 = latlngs1.getDouble(1);

            Location nextCoordinates = new Location("");
            nextCoordinates.setLatitude(lat_1);
            nextCoordinates.setLongitude(lng_1);

            //saving distance
            weight += startNode.distanceTo(nextCoordinates);

        }else{
            //loop from starting coordinates to reach the limit
            for(int i = 0; i < 1; i++){

                // get starting coordinates
                JSONArray latlngs = jArrCoordinates.getJSONArray(start);

                double lat_0 = latlngs.getDouble(0);
                double lng_0 = latlngs.getDouble(1);

                Location startNode = new Location("");
                startNode.setLatitude(lat_0);
                startNode.setLongitude(lng_0);

                // get coordinates of next node
                JSONArray latlngs1 = jArrCoordinates.getJSONArray(++start);

                double lat_1 = latlngs1.getDouble(0);
                double lng_1 = latlngs1.getDouble(1);

                Location nextCoordinates = new Location("");
                nextCoordinates.setLatitude(lat_1);
                nextCoordinates.setLongitude(lng_1);


                //adding the weight
                weight += startNode.distanceTo(nextCoordinates);

                //end loop when we reach the limit
                if(start == limit) break;
                else --i;
            }
        }
    }
}