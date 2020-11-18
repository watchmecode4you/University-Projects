package com.example.owner.googlemapsgoogleplaces.DemoMap.MapManipulation;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.util.Log;

import com.example.owner.googlemapsgoogleplaces.DemoMap.DatabaseManipulation.SQLHelper;

public class AddVertex {

    protected Cursor cursor;

    public String[][] modif_graph = new String[500][100];
    public String old_vertex = "";//[4-7]
    public int new_vertex;//10


    String vertex_aim_ = "";


    /*
     * @function
     * insert new node
     * ex path 4-7, after inserting new node (10) ->4-10-7
     * and path 7-4, after inserting new node (10) ->7-10-4
     * @parameter
     * vertex0: eg {"nodes": "4-7"} then first_vertex0 = 4
     * vertex1: eg {"nodes": "4-7"} then second_vertex1 = 7
     * index_coordinate_json: index array coordinates in JSON
     * context: MainActivity.context
     * graph [] []: array to hold graph of DB
     * example output: graph [4] [0] = 7-> 439.281
     * graph [10] [0] = 7-> 216.281
     * increase_row_id: row new DB id
     * @return
     * old_vertex = vertex0 + "-" + vertex1
     * new_vertex = new added node ex 10
     * graph [] []
     * */

    public void DoublePath(int vertex0, int vertex1,
                           int index_coordinate_json, Context context,
                           String[][] graph, int increase_row_id
    ) throws JSONException{

        // read DB
        SQLHelper dbHelper = new SQLHelper(context);
        SQLiteDatabase dbRead = dbHelper.openDataBase();
        SQLiteDatabase dbInsert = dbHelper.openDataBase();


        // CALCULATE THE ORIGINAL CONTENTS (4-7), NOT THE REVERSE (7-4)
        String pathIndex = "";

        // look for path index vertex1 (4) from graph [vertex0] [pathIndex]
        for(int l = 0; l < 100; l++){

            if(graph[vertex0][l] != null){

                String startingNode = graph[vertex0][l]; // [4][0] = 7->721.666

                // 7->721.666
                String [] explode = startingNode.split("->");

                vertex_aim_ = explode[0]; // 7

                // if 7 == 7 (node1)
                if(vertex_aim_.trim().equals( String.valueOf(vertex1).trim()) ){

                    // Path index example graph [vertex0] [pathIndex]
                    pathIndex = String.valueOf(l);
                }

            }else break;

        }// for

        // index of graph [tmp_vertex0] [tmp_pathIndex] to be edited
        int tmp_vertex0 = vertex0;
        int tmp_pathIndex = Integer.parseInt(pathIndex);

        // take the coordinates from node 4-7
        cursor = dbInsert.rawQuery("SELECT path FROM graph where start_vertex = "+ vertex0 +" and end_vertex = "+ vertex1, null);
        cursor.moveToFirst();
        cursor.moveToPosition(0);

        // --
        // get coordinates JSON
        String json_coordinates = cursor.getString(0).toString();
        JSONObject jObject = new JSONObject(json_coordinates);
        JSONArray jArrCoordinates = jObject.getJSONArray("coordinates");
        // --

        // find the maximum node, (make the new node numbering)
        cursor = dbRead.rawQuery("SELECT max(start_vertex), max(end_vertex) FROM graph", null);
        cursor.moveToFirst();
        int max_node_db		= 0;
        int max_startingNode_db 	= Integer.parseInt(cursor.getString(0).toString());
        int max_nodeAim_db = Integer.parseInt(cursor.getString(1).toString());
        if(max_startingNode_db >= max_nodeAim_db){
            max_node_db = max_startingNode_db;
        }else{
            max_node_db = max_nodeAim_db;
        }

        // broke the coordinates of BEGINNING-> CENTRAL
        //set the limit as the closest coordinate to the given latlng
        int limit = index_coordinate_json;
        NewWeight ct = new NewWeight();
        ct.NewWeight(0, limit, jArrCoordinates); // 0, middle coordinate, jSON coordinates// jArrCoodrinates is the index of closest coordinates to the given latlng//calculates the weight btw 0 and limit

        //replace array graph[4][0] = 10->888.6
        graph[tmp_vertex0][tmp_pathIndex] = (max_node_db+1)+"->"+ct.weight;



        int start_path_index = 0;
        // create and save (new record) json new coordinates to the DB
        //ex:[4-7]  insert tmp_vertex0(4) as start_path_index(0) that is start_vertex and (max_node_db + 1)(10) as limit(index_coordinate_json)(index of closest coordinate) that is end_vertex and coordinates btw 4 and index_coordinate_json that are path coordinates and weight
        createAndSave_NewJsonCoordinate(start_path_index, limit, jArrCoordinates, increase_row_id, tmp_vertex0, (max_node_db + 1), ct.weight,
                dbInsert, dbRead); // 501: new index record


        // reset weight
        ct.weight = 0;


        // broke the coordinates of CENTRAL-> END
        int start_path_index1 = index_coordinate_json;
        int limit1 = (jArrCoordinates.length() - 1); // - 1 because the array starts from 0
        ct.NewWeight(index_coordinate_json, limit1, jArrCoordinates); // coordinates from mid to end


        // new array graph[10][0] = 7->777.4
        graph[(max_node_db+1)][0] = vertex1 + "->" + ct.weight; //defined [0] because the new index in graph [] []

        // create and save (new record) json new coordinate to the DB

        //ex:[4-7]  insert (max_node_db + 1)(10) as start_path_index1(index_coordinate_json) that is start_vertex
        // and vertex1(7) as limit1(last coordinate) that is end_vertex
        // and coordinates btw index_coordinate_json(10) to 7 that are path coordinates
        // and weight
        createAndSave_NewJsonCoordinate(start_path_index1, limit1, jArrCoordinates, ++increase_row_id, (max_node_db + 1), vertex1, ct.weight,
                dbInsert, dbRead); // defined [0] because the new index in graph [] []//jArrCoordinates contains coordinated between vertices


        // reset weight
        ct.weight = 0;



        //SAME OPERATION FOR the reversed path

        String pathIndex1 = "";
        String nodes_inside_tmp2_pathIndex = "";

        // reversed, vertex0 so vertex1; example [7-4]
        int t_vertex0 = vertex1; // 7
        int t_vertex1 = vertex0; // 4

        // search the path index from graph [4] [pathIndex]
        for(int l = 0; l < 100; l++){

            if(graph[t_vertex0][l] != null){

                //get the destination vertex, example: 4-> 9585.340
                String startingNode = graph[t_vertex0][l];
                String [] explode1 = startingNode.split("->");

                nodes_inside_tmp2_pathIndex = explode1[0];

                if(nodes_inside_tmp2_pathIndex.trim().equals( String.valueOf(t_vertex1)) ){
                    pathIndex1 = String.valueOf(l);
                }

            }else break;
        }//for


        // index of graph [tmp1_vertex0] [tmp1_pathIndex] to be edited
        int tmp1_vertex0 = t_vertex0;//the starting vertex of the reversed path ex 7
        int tmp1_pathIndex = Integer.parseInt(pathIndex1);

        // take the coordinates from node 7-4
        cursor = dbRead.rawQuery("SELECT path FROM graph where start_vertex = "+t_vertex0+" and end_vertex = "+t_vertex1, null);
        cursor.moveToFirst();
        cursor.moveToPosition(0);

        // get coordinates from DB
        String json1 = cursor.getString(0).toString();
        JSONObject jObject1 = new JSONObject(json1);
        JSONArray jArrCoordinates1 = jObject1.getJSONArray("coordinates");


        // break the coordinates from BEGINNING to CENTRAL
        int index1_coordinate_json = ( (jArrCoordinates1.length()-1) - index_coordinate_json );
        ct.NewWeight(0, index1_coordinate_json, jArrCoordinates1); // 0, the initial coordinates to the middle, JSONArray coordinate

        //replace array to become graph[7][0] = 10->777.4
        graph[tmp1_vertex0][tmp1_pathIndex] = (max_node_db+1)+"->"+ct.weight;


        // create and save (new record) json new coordinate to the DB
        int start_path_index2 = 0;
        createAndSave_NewJsonCoordinate(start_path_index2, index1_coordinate_json, jArrCoordinates1, ++increase_row_id, tmp1_vertex0, (max_node_db + 1), ct.weight,
                dbInsert, dbRead); // 503 : index record


        // reset weight
        ct.weight = 0;


        // break the coordinates of CENTRAL-> END
        int limit2 = (jArrCoordinates1.length() - 1); // - 1 because the array starts from 0
        ct.NewWeight(index1_coordinate_json, limit2, jArrCoordinates1); // the middle coordinates to the end

        //replace array to become graph[10][1] = 4->888.6
        graph[(max_node_db+1)][1] = t_vertex1+"->"+ct.weight; // defined [1] because sdh there is index 0 in graph [] []

        // create and save (new record) json new coordinate to the DB
        createAndSave_NewJsonCoordinate(index1_coordinate_json, limit2, jArrCoordinates1, ++increase_row_id, (max_node_db + 1), t_vertex1, ct.weight,
                dbInsert, dbRead); // 504 : new index record



        // return
        old_vertex = vertex0 + "-" + vertex1;
        new_vertex = (max_node_db + 1);
        modif_graph = graph; // graph[][]

    }// public void DoublePath



    /*
     * @function
     * insert new node
     * ex node 5-4, after inserting new node (10) 5-10-5
     * @parameter
     * start_vertex0: eg {"nodes": "5-4"} then start_vertex0 = 5
     * end_vertex1: eg {"nodes": "5-4"} then end_vertex1 = 4
     * index_coordinate_json: index array coordinates in JSON
     * context: MainActivity.context
     * graph [] []: array to hold graph of DB
     * example output: graph [5] [0] = 4-> 439.281
     * graph [6] [0] = 1-> 216.281
     * increase_row_id: row new DB id
     * @return
     * new_vertex: final node
     * graph [] []
     * */

    public void SinglePath(int vertex0, int vertex1, int index_coordinate_json,
                           Context context, String[][] graph, int increase_row_id) throws JSONException{

        // read DB
        SQLHelper dbHelper = new SQLHelper(context);
        SQLiteDatabase dbRead = dbHelper.openDataBase();
        SQLiteDatabase dbInsert = dbHelper.openDataBase();


        //THE ORIGINAL vertex (5-4)

        String pathIndex = "";

        // search for index of end_vertex (4) from graph [vertex0] [pathIndex]
        for(int l = 0; l < 100; l++){

            if(graph[vertex0][l] != null){

                String startingNode = graph[vertex0][l]; // [5][0] = 4->721.666
                String [] explode = startingNode.split("->");

                // 6->721.666
                String value_node_array = explode[0];

                // if 4 == 4
                if( value_node_array.trim().equals(String.valueOf(vertex1).trim()) ){

                    // pathIndex; example graph[vertex0][pathIndex]
                    pathIndex = String.valueOf(l);
                }

            }else break;
        }//for


        // index of the graph [tmp2_vertex0] [tmp2_pathIndex] to be edited
        int tmp2_vertex0 = vertex0;
        int tmp2_pathIndex = Integer.parseInt(pathIndex);


        //take the coordinates from node 3-6
        cursor = dbRead.rawQuery("SELECT path FROM graph where start_vertex = "+vertex0+" and end_vertex = "+vertex1, null);
        cursor.moveToFirst();
        cursor.moveToPosition(0);


        // get coordinates from database
        String json_coordinates = cursor.getString(0).toString();
        JSONObject jObject = new JSONObject(json_coordinates);
        JSONArray jArrCoordinates = jObject.getJSONArray("coordinates");


        // find the maximum node, to increment it by 1 inorder to be used as the new index
        cursor = dbRead.rawQuery("SELECT max(start_vertex) FROM graph", null);
        cursor.moveToFirst();
        int max_node_db = Integer.parseInt(cursor.getString(0));


        // break the coordinates of BEGINNING-> CENTRAL
        int limit = index_coordinate_json;
        NewWeight ct = new NewWeight();
        ct.NewWeight(0, limit, jArrCoordinates); // 0, middle coordinates, jSON coordinates

        //replace array to become graph[5][0] = 6->888.6
        graph[tmp2_vertex0][tmp2_pathIndex] = (max_node_db+1)+"->"+ct.weight;


        int start_path_index = 0;

        // create and save (new record) json new coordinates to the DB
        createAndSave_NewJsonCoordinate(start_path_index, limit, jArrCoordinates, increase_row_id, tmp2_vertex0, (max_node_db + 1), ct.weight,
                dbInsert, dbRead); // 501 : new index record

        // reset weights
        ct.weight = 0;


        // break the coordinates of CENTRAL-> END
        int start_path_index1 = index_coordinate_json; // - 1 because the array starts from 0
        int limit1 = (jArrCoordinates.length() - 1); // - 1 because the array starts from 0
        ct.NewWeight(index_coordinate_json, limit1, jArrCoordinates); // coordinate from middle to end


        // new array graph[6][0] = 4->777.4
        graph[(max_node_db+1)][0] = vertex1 + "->" + ct.weight; //defined [0] because the new index in graph [] []

        // create and save (new record) json new coordinate to the DB
        createAndSave_NewJsonCoordinate(start_path_index1, limit1, jArrCoordinates, ++increase_row_id, (max_node_db + 1), vertex1, ct.weight,
                dbInsert, dbRead); // 502 : new index record

        // return
        old_vertex = vertex0 + "-" + vertex1;
        new_vertex = (max_node_db + 1);
        modif_graph = graph; // graph[][]
    }


    /* * @function
     * Create and save new coordinates in the form of JSON to DB
     * @parameter
     * start: start looping, eg 0
     * limit: index array coordinates, ex i [7] then limit = 7
     * jArrCoordinates: Coordinate of DB in JSONArray form
     * new_id: new record id
     * // tmp2_vertex0: tmp2_vertex0 multidimensional array, ex i [tmp2_vertex0] [tmp2_pathIndex]
     * // max_node_db: the number of max records in the graph table
     * new_weight: new weight from splitting the path coordinates
     * dbInsert: insert to the database
     * dbRead: read the database record
     * @return
     * no return
     * */

    public void createAndSave_NewJsonCoordinate(int start, int limit, JSONArray jArrCoordinates,
                                                int new_id, int start_vertex, int end_vertex, double new_weight,
                                                SQLiteDatabase dbInsert, SQLiteDatabase dbRead) throws JSONException{

        //save the new coordinate
        JSONObject json_new = new JSONObject();
        JSONArray new_root_coordinates = new JSONArray();

        // looping from the initial coordinate to the middle coordinate
        // or
        // looping from middle coordinate to final coordinate
        // then move the old coordinate to new coordinate
        for(int i = start; i <= limit; i++){

            JSONArray latlng = jArrCoordinates.getJSONArray(i);
            double new_lat = latlng.getDouble(0);
            double new_lng = latlng.getDouble(1);

            JSONArray new_list_coordinates = new JSONArray();
            new_list_coordinates.put(new_lat);
            new_list_coordinates.put(new_lng);

            // add coordinates btw start and limit
            new_root_coordinates.put(new_list_coordinates);
        }


        // vertices
        JSONArray vertices = new JSONArray();
        String joinVertices = String.valueOf(start_vertex) + '-' + String.valueOf(end_vertex);
        vertices.put(joinVertices);

        // distance_metres
        JSONArray distance_metres = new JSONArray();
        distance_metres.put(new_weight);


        // create new JSON
        json_new.put("nodes", vertices);
        json_new.put("coordinates", new_root_coordinates);
        json_new.put("distance_metres", distance_metres);

        String new_path = json_new.toString();
        System.out.println(new_path);

        // INSERT new Node
        ContentValues newCon = new ContentValues();
        newCon.put("id", new_id);
        newCon.put("start_vertex", start_vertex);
        newCon.put("end_vertex", end_vertex);
        newCon.put("path", new_path);
        newCon.put("weight", new_weight);
        dbInsert.execSQL("Insert into graph values ("+new_id+","+start_vertex+","+end_vertex+",'"+new_path+"',"+new_weight+")");

    }
}