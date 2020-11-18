package com.example.owner.googlemapsgoogleplaces.DemoMap.MapManipulation;

/**
 * Created by one on 4/30/2018.
 */

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.widget.Toast;

import com.example.owner.googlemapsgoogleplaces.DemoMap.DatabaseManipulation.SQLHelper;

public class GetCoordinates extends Activity{

    // DB
    Cursor cursor;

    int fix_initial_node = 0;
    String explode_lat_only = "";
    Location userPosition = new Location("");
    ArrayList<String> a_tmp_graph = new ArrayList<String>();

    // return JSON
    JSONObject final_json = new JSONObject();



    /*
     * @function
     * get the vertices of the path that is the closest to the user
     * get the coordinates of the node between these vertices that is the closest to the user
     * @parameter
     * latx: latitude user
     * lngx: longitude user
     * context: MainActivity context
     * @return
     * JSON (index coordinates, vertex0, vertex1)
     */

    /*returns the closest start and end vertices to the user ex:[0-1] or [1-2] or .. and the coordinates between these vertices that are the closest
      and the status (double,single or no_path where the user is at the end or beginning of the vertices)*/
    public JSONObject GetNode(double latx, double lngx, Context context) throws JSONException {
        // TODO Auto-generated constructor stub

        // your coordinate
        userPosition.setLatitude(latx);
        userPosition.setLongitude(lngx);

        // for storing  paths of graph table
        List<String> doublePath = new ArrayList<>();
        List<String> doublePathIndex = new ArrayList<>();


        SQLHelper dbHelper = new SQLHelper(context);
        SQLiteDatabase db = dbHelper.openDataBase();

        // getting all records of graph table
        cursor = db.rawQuery("SELECT * FROM graph where start_vertex != '' and end_vertex != '' and path != '' and distance != ''", null);
        cursor.moveToFirst();



        // looping below for storing reversed paths only
        for (int i = 0; i < cursor.getCount(); i++){

            cursor.moveToPosition(i);

            // starting vertex
            String startVertexField = cursor.getString(1);

            // ending vertex
            String endVertexField = cursor.getString(2);

            String joinedNodes = startVertexField+","+endVertexField;
            String joinedNodesReversed = endVertexField+","+startVertexField;


            //adding all the vertices start and end in reversed only ex:[0-1],[1-0] is only entered as [1-0]
            if(doublePath.isEmpty()){

                doublePath.add(joinedNodesReversed);

                // field id in table graph
                doublePathIndex.add(cursor.getString(0));
            }else{

                if(!doublePath.contains(joinedNodes)){
                    doublePath.add(joinedNodesReversed);

                    // field id of all reversed paths
                    doublePathIndex.add(cursor.getString(0));
                }
            }
        }


        // add the id fields fetched as format "0,2,..."
        StringBuilder doublePathIndex1 = new StringBuilder();
        for(int j = 0; j < doublePathIndex.size(); j++){

            if(doublePathIndex1.length() == 0){

                // add all the id fields
                doublePathIndex1.append(doublePathIndex.get(j));
            }else{
                doublePathIndex1.append(","+doublePathIndex.get(j));
            }
        }


        // fetch all the paths that are in their original form ex:[0-1]
        cursor = db.rawQuery("SELECT * FROM graph where id in("+doublePathIndex1+")",null);
        cursor.moveToFirst();

        JSONObject obj = new JSONObject();


        // looping all records to get the coordinates that have the least distance to the user and storing their details in a JSONObject obj
        for(int k = 0; k < cursor.getCount(); k++){

            // VARIABLES MAKE SEARCH 1 DISTANCE IN 1 RECORD (1 record of its contents many coordinates)
            // saving distance user to node coordinates in meters
            List<Double> distanceNodeToUser = new ArrayList<Double>();

            cursor.moveToPosition(k);

            // get the coordinates Lat, Lng from the coordinate field (3)
            String json = cursor.getString(3);

            // manipulating JSON
            JSONObject jObject = new JSONObject(json);
            //storing coordinates of path
            JSONArray jArrCoordinates = jObject.getJSONArray("coordinates");
            //storing vertices of path
            JSONArray jArrNodes = jObject.getJSONArray("nodes");


            // get coordinate
            for(int w = 0; w < jArrCoordinates.length(); w++){
                JSONArray latlngs = jArrCoordinates.getJSONArray(w);
                Double lats = latlngs.getDouble(0);
                Double lngs = latlngs.getDouble(1);

                //SET LAT,LNG
                Location nodeCoordinates = new Location("");
                nodeCoordinates.setLatitude(lats);
                nodeCoordinates.setLongitude(lngs);

                //distance between user and node(weight in meters)
                double distance = userPosition.distanceTo(nodeCoordinates);

                //add the weight
                distanceNodeToUser.add(distance);//list

            }

            // search the index of the coordinates with the smallest weight btw the specified vertices
            int minCoordinateWeightIndex = 0;
            for(int m = 0; m < distanceNodeToUser.size(); m++){

                if(distanceNodeToUser.get(m) <= distanceNodeToUser.get(0)){
                    distanceNodeToUser.set(0, distanceNodeToUser.get(m));

                    // index of the smallest weight btw the specified vertices according to the distance relative to user
                    minCoordinateWeightIndex = m;
                }
            }


            // field id of the vertices
            int row_id = cursor.getInt(0);

            JSONObject list = new JSONObject();

            // enter the array coordinate index, the smallest weight and the number of coordinates to JSON
            list.put("row_id", row_id);
            list.put("index", minCoordinateWeightIndex);
            list.put("weight", distanceNodeToUser.get(0));
            list.put("nodes", jArrNodes.getString(0));
            list.put("count_coordinates", (jArrCoordinates.length() - 1));

            JSONArray ja = new JSONArray();
            ja.put(list);

            // Create json
            // example output :
            // {"0" : [{"row_id":17, "index":"7", "weight":"427.66", "count_coordinates":"15", "nodes":"0-1"}]}
            obj.put("" + k, ja);//store the row number and row detail list

        }//end looping



        double x = 0;
        double y = 0;
        int rowId_json = 0;
        int indexCoordinate_json = 0;
        int countCoordinate_json = 0;
        String nodes_json = "";

        // search for the smallest weight in obj
        for(int s = 0; s < obj.length(); s++){

            // first
            if(s == 0){
                JSONArray a = obj.getJSONArray("0");
                JSONObject b = a.getJSONObject(0);
                x = Double.parseDouble(b.getString("weight"));

                // row id field
                rowId_json = Integer.parseInt(b.getString("row_id"));
                // index coordinate
                indexCoordinate_json = Integer.parseInt(b.getString("index"));
                // number of coordinates
                countCoordinate_json = Integer.parseInt(b.getString("count_coordinates"));
                // nodes
                nodes_json = b.getString("nodes");

            }else{
                // second, dst
                JSONArray c = obj.getJSONArray("" + s);
                JSONObject d = c.getJSONObject(0);
                y = Double.parseDouble(d.getString("weight"));

                // get the smallest value (weight)
                if(y <= x){
                    // weight
                    x = y;

                    // row id field
                    rowId_json = Integer.parseInt(d.getString("row_id"));
                    // index coordinate around the vertex
                    indexCoordinate_json = Integer.parseInt(d.getString("index"));
                    // number of coordinates
                    countCoordinate_json = Integer.parseInt(d.getString("count_coordinates"));
                    // nodes
                    nodes_json = d.getString("nodes").toString();
                    // ==========

                }
            }

        }

        // nodes : 0-1
        String[] exp_nodes = nodes_json.split("-");


        int field_start_node = Integer.parseInt(exp_nodes[0]);
        int field_destination_node = Integer.parseInt(exp_nodes[1]);

        // if the coordinates obtained are located at the beginning or ending of the vertices then there is no need to add a new vertex
        //Check if the coordinates are at the ending or beginning of the vertices
        if(indexCoordinate_json == 0 || indexCoordinate_json == countCoordinate_json){

            //specify if the beginning or ending vertex is the closest to the user's position
            if(indexCoordinate_json == 0){


                fix_initial_node = field_start_node;
            }else if(indexCoordinate_json == countCoordinate_json){


                fix_initial_node = field_destination_node;
            }

            final_json.put("status", "path_none");

        }

        //The obtained coordinates are btw the vertices 0 - 1
        else{
            // find the double paths
            cursor = db.rawQuery("SELECT id FROM graph where start_vertex = "+ field_destination_node + " and end_vertex = " + field_start_node, null);
            cursor.moveToFirst();
            cursor.moveToPosition(0);

            int doublePath1 = cursor.getCount();

            //there are double paths [0-1] and [1-0]
            if(doublePath1 == 1){

                final_json.put("status", "path_double");

            }
            //there is a single path [0-1]
            else if(doublePath1 == 0){

                final_json.put("status", "path_single");

            }
        }


        // JSON
        final_json.put("row_id", rowId_json);

        final_json.put("start_vertex", field_start_node);
        final_json.put("end_vertex", field_destination_node);
        final_json.put("index_coordinate_json", indexCoordinate_json);
        //final_json.put("destination_node", judulTabel_simpulTujuan);
        final_json.put("explode_lat_only", explode_lat_only);

        return final_json;

    }//public

}
