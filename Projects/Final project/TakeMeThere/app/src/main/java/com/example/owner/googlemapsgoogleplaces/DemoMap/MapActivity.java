package com.example.owner.googlemapsgoogleplaces.DemoMap;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.owner.googlemapsgoogleplaces.DemoMap.DatabaseManipulation.GraphToArray;
import com.example.owner.googlemapsgoogleplaces.DemoMap.MapManipulation.AddVertex;
import com.example.owner.googlemapsgoogleplaces.DemoMap.MapManipulation.Dijkstra;
import com.example.owner.googlemapsgoogleplaces.DemoMap.MapManipulation.GetCoordinates;
import com.example.owner.googlemapsgoogleplaces.DemoMap.Service.CheckServices;
import com.example.owner.googlemapsgoogleplaces.R;
import com.example.owner.googlemapsgoogleplaces.WelcomePage.welcomePage;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.example.owner.googlemapsgoogleplaces.DemoMap.DatabaseManipulation.SQLHelper;
import com.example.owner.googlemapsgoogleplaces.DemoMap.DatabaseManipulation.MSSQLDatabase;
import com.example.owner.googlemapsgoogleplaces.DemoMap.Utils.BuildCoord;

/**
 * Created by Owner on 3/18/2018.
 */

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback,GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnCameraChangeListener, GoogleMap.OnMarkerClickListener {

    private static final String TAG = "MapActivity";
    private static final String FINE_LOCATION  = Manifest.permission.ACCESS_FINE_LOCATION ;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION ;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234 ;
    private static final float DEFAULT_ZOOM = 15f ;
    //LatLngBounds (LatLng southwest, LatLng northeast)
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(-40,-168), new LatLng(71,136));
    private static final LatLngBounds LAT_LNG_BOUNDS2 = new LatLngBounds(new LatLng(33.8517,35.5067), new LatLng(33.8786,35.5288));

    //school coordinates 33.85994, 35.52033
    public static final LatLng SCHOOL = new LatLng((double) 33.86546, (double)35.52268);
    public LatLng destination = null ;
    private String path = Environment.getExternalStorageDirectory()+"/graph.sqlite";


    //widgets
    private AutoCompleteTextView mSearchText ;
    private ImageView mGps;


    //vars
    private boolean mLocationPermissionGranted = false ;
    public GoogleMap mMap ;
    public FusedLocationProviderClient mFusedLocationProviderClient ;
    private PlaceAutocompleteAdapter mAdapter;
    protected GeoDataClient mGeoDataClient;
    private Cursor cursor ;
    private SQLiteDatabase db ;
    private SQLHelper sqlHelper=new SQLHelper(this);
    private String[][] graphArrayFromDatabase;

    public int MAX_ROW = 0;
    public int MAX_ROW_1 = 0;
    public int __global_vertex_start;
    public int __global_vertex_end;
    public String __global_old_vertex_start;
    public String __global_old_vertex_end;

    public BuildCoord[] buildCoords;

    private boolean addCustomer ;
    private String index ;


    int MarkerSize=0;
    Marker[] marker;

    double SelectedCustLocationLat;
    double SelectedCustLocationLng;

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        mMap = googleMap;
        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnCameraChangeListener(this);
        mMap.setOnMarkerClickListener(this);
        boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,R.raw.dark));

        if (!success) {
            Log.e(TAG, "Style parsing failed.");
        }
        ArrayList<Integer> custId;

        SQLiteDatabase dbRead = sqlHelper.openDataBase();

        buildCoords = sqlHelper.getLatLng(dbRead);

        marker=new Marker[buildCoords.length];

        for(int i=0;i<sqlHelper.nbOfBuildings;i++){

            marker[MarkerSize] = mMap.addMarker(
                    new MarkerOptions()
                    .position(buildCoords[i].getLatLng())
                    .title(buildCoords[i].getBuildName())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.home))
            );
            MarkerSize++;

        }

        checkSelectedCustomer();

        /*
        mssqlDatabase=new MSSQLDatabase(this);


        buildCoords=mssqlDatabase.getLatLng();

        mMap.setOnCameraChangeListener(this);

        marker=new Marker[buildCoords.length];
        Log.e("Failss",String.valueOf(buildCoords.length));

        for(int i=0;i<buildCoords.length;i++){

                marker[MarkerSize] = mMap.addMarker(new MarkerOptions().position(buildCoords[i].getLatLng()).title("University").
                        icon(BitmapDescriptorFactory.fromResource(R.drawable.home)));
                 MarkerSize++;

        }

        Bundle bool=this.getIntent().getExtras();
        addCustomer=bool.getBoolean("AddCustomer");
        */
        if (mLocationPermissionGranted) {
            getSchoolLocation(SCHOOL);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            init();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map) ;
        mSearchText = (AutoCompleteTextView) findViewById(R.id.input_search);

        mGps = (ImageView) findViewById(R.id.ic_gps);

        Log.d(TAG,"onMapReady: map is ready");

        getLocationPermission();

    }

    //first method to be invoked
    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission : getting location permissions.");
        String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this, permission, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permission, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    //second method to be invoked
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG,"onRequestPermissionsResult: called.");
        mLocationPermissionGranted = false ;
        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length>0){
                    for(int i = 0 ; i <grantResults.length; i++){
                        if ( grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionGranted = false ;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed!");
                            return ;
                        }
                    }
                    Log.d(TAG,"onRequestPermissionResult: permission granted!");
                    mLocationPermissionGranted = true ;
                    //initialize our map
                    initMap();
                }
            }
        }
    }

    //third method to be invoked
    private void initMap(){
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapActivity.this);

    }

    //responsible for triggering the search for a certain location upon pressing the enter part on the keyboard
    public void init(){
        Log.d(TAG,"init: initialization");

        mGeoDataClient = Places.getGeoDataClient(this,null);

        AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(Place.TYPE_COUNTRY)
                .setCountry("LB")
                .build();

        mAdapter = new PlaceAutocompleteAdapter(this,mGeoDataClient,LAT_LNG_BOUNDS2,autocompleteFilter);
        mSearchText.setAdapter(mAdapter);
        mSearchText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                geolocate();
                hideSoftKeyboard();
            }
        });

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == event.ACTION_DOWN
                        || event.getAction() == event.KEYCODE_ENTER){
                    //execute searching method
                    geolocate();
                }
                return false;
            }
        });
        hideSoftKeyboard();
        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"onClick: clicked gps icon: ");
                getDeviceLocation();
            }
        });
    }

    private void geolocate(){
        Log.d(TAG,"geolocat: geolocating...");
        String searchString = mSearchText.getText().toString();
        Geocoder geocoder = new Geocoder(MapActivity.this); //converting the address into LatLng
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(searchString,1);
        }catch(IOException e){
            Log.e(TAG,"geolocate: IOExepction:" + e.getMessage());
        }
        if(list.size() > 0 ){
            Address address = list.get(0);

            Log.d(TAG,"geolocate: found a location: "+ address.toString());
            //Toast.makeText(this,address.toString(), Toast.LENGTH_SHORT).show();

            moveCamera(new LatLng(address.getLatitude(),address.getLongitude()),DEFAULT_ZOOM,address.getAddressLine(0)) ;
        }
    }

    private void getDeviceLocation(){
        Log.d(TAG, " getDeviceLocation: Getting the device's current location") ;
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try
        {
            if(mLocationPermissionGranted){
                com.google.android.gms.tasks.Task<Location> location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<Location> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG,"onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();

                            //saving the coordinates of my location for later use ( i was trying to check the polyLine method)
                            //loc = (Location) task.getResult();

                            moveCamera(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()),DEFAULT_ZOOM,"My location");

                        }
                        else{
                            Log.d(TAG,"onComplete: current location is null");
                            Toast.makeText(MapActivity.this, "could not get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch(SecurityException e)
        {
            Log.e(TAG,"getDeviceLocation: SecurityException: "+e.getMessage());
        }
    }

    private void getSchoolLocation(LatLng school){
        Log.d(TAG,"getSchoolLocation: Getting the school's location");
        try{
            moveCamera(school,DEFAULT_ZOOM,"University");

            MarkerOptions markerUni = new MarkerOptions().position(school).title("University").
                    icon(BitmapDescriptorFactory.fromResource(R.drawable.home));

            mMap.addMarker(markerUni);
        }catch (SecurityException e){
            Log.e(TAG,"getDeviceLocation: SecurityException: "+e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng , float zoom , String title){
        Log.d(TAG,"moveCamera: moving the camera to: lat:  "+latLng.latitude + ", lng: "+ latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));

        if(!title.equals("My location")){
            MarkerOptions options = new MarkerOptions().position(latLng).title(title); //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            mMap.addMarker(options);
        }
        hideSoftKeyboard();
    }

    private void hideSoftKeyboard(){
        //Mitch's method
        //this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    //Using geoDataclient instead of GoogleApiClient Mitche's way
    //https://github.com/googlesamples/android-play-places/blob/master/PlaceCompleteAdapter/Application/src/main/java/com/example/google/playservices/placecomplete/MainActivity.java

    @Override
    public boolean onMarkerClick(Marker marker) {
        //school's coordinates (or Supermarket)
        double school_lat = SCHOOL.latitude;
        double school_lon = SCHOOL.longitude;

        Context ctx = getApplicationContext();
        try {

            Toast.makeText(ctx, "Getting the path", Toast.LENGTH_SHORT).show();
            //Getting the path leading to the customer's building location
            startingScript(school_lat,school_lon,SelectedCustLocationLat,SelectedCustLocationLng,ctx);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Toast.makeText(this, marker.getTitle() + " is clicked ", Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public void onMapClick(final LatLng point) {

        //upon button click it checks if the user clicked on the map to set a destination
        //if the user chose a destination

        Intent intent = getIntent() ;
        addCustomer = intent.getBooleanExtra("addCustomer",false);

        if(addCustomer){

            AlertDialog.Builder mBuilder=new AlertDialog.Builder(MapActivity.this);
            View mView=getLayoutInflater().inflate(R.layout.dialog_add,null);
            final EditText fnameTxt=(EditText)mView.findViewById(R.id.firstNameET);
            final EditText lnameTxt=(EditText)mView.findViewById(R.id.lastNameET);
            final EditText floorTxt=(EditText)mView.findViewById(R.id.floorET);
            final EditText buildingTXT=(EditText)mView.findViewById(R.id.buildingET);
            Button addCustomerBtn=(Button)mView.findViewById(R.id.addCustomerBtn);

            addCustomerBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SQLiteDatabase dbInsert = sqlHelper.openDataBase();

                    sqlHelper.insertCustomer(fnameTxt.getText().toString(),lnameTxt.getText().toString(),
                            buildingTXT.getText().toString(),point.longitude,point.latitude,Integer.parseInt(floorTxt.getText().toString()),dbInsert);

                    Intent intent1  = new Intent(MapActivity.this, welcomePage.class);
                    startActivity(intent1);

                }
            });


            mBuilder.setView(mView);
            AlertDialog dialog=mBuilder.create();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }
        else {
            MarkerOptions marker1 = new MarkerOptions().position(new LatLng(point.latitude, point.longitude)).title("Coordinates are : " + point.latitude + "," + point.longitude);
            mMap.addMarker(marker1);
            //              saving  coordinates of the marker's location for later use ( i was trying to check the polyLine method
            destination = point;

            //school's coordinates
            double school_lat = SCHOOL.latitude;
            double school_lon = SCHOOL.longitude;


            if (destination != null) {
                //coordinates of the selected destination
                double destination_lat = destination.latitude;
                double destination_long = destination.longitude;

                try {
                    Context ctx = getApplicationContext();
                    startingScript(school_lat, school_lon, destination_lat, destination_long, ctx);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Tap on the map to determine your destination", Toast.LENGTH_LONG).show();
                return;
            }
        }

    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }

    public void startingScript(double school_lat, double school_lon, double destination_lat, double destination_lon, Context ctx) throws JSONException {

        // delete temporary record DB
        deleteTemporaryRecord();

        // reset google map
        mMap.clear();

        // convert graph from DB to Array; graph[][]
        GraphToArray DBGraph = new GraphToArray();
        graphArrayFromDatabase = DBGraph.ConvertToArray(ctx); // return graph[][] Array

        for(int i = 0 ; i<100 ; i++){
            for(int j = 0 ; j<100 ; j++){
                if(graphArrayFromDatabase[i][j] != null)
                    Log.i(TAG, "A["+i+"]["+j+"] = "+graphArrayFromDatabase[i][j]);
            }
        }

        // get max++ row temporary DB
        maxRowDB();

        // GET COORDINATE around start Vertex
        // the initial coordinate is then converted to the initial vertex
        // return __global_vertex_start, graphArrayFromDatabase[][]
        // ==========================================
        GetCoordinates BeginningPath = new GetCoordinates();
        getVertexStartEndPath(BeginningPath, school_lat, school_lon, "start");

        // GET COORDINATE end DI SEKITAR SIMPUL
        // final coordinate then converted to final vertex
        // return ___global_vertex_end, graphArrayFromDatabase[][]
        // ==========================================
        GetCoordinates EndPath = new GetCoordinates();
        getVertexStartEndPath(EndPath, destination_lat, destination_lon, "end");

        // ALGORITMA DIJKSTRA
        // ==========================================
        Dijkstra algo = new Dijkstra();
        algo.shortestPath(graphArrayFromDatabase, __global_vertex_start, __global_vertex_end);

        // no result for algoritm dijkstra
        if (algo.status == "die") {

            Toast.makeText(ctx, "Your location is close to the destination location", Toast.LENGTH_LONG).show();

        } else {
            // shortest path return; example 1-> 5-> 6-> 7
            String[] exp = algo.shortest_path1.split("->");

            // DRAW GENERAL PUBLIC TRANSPORT ROAD
            // =========================================
            //mapActivity.drawPath(algo.shortest_path1,exp);
            drawPath(algo.shortest_path1, exp);
        }

    }

    public void drawPath(String alg, String[] exp) throws JSONException{

        int start = 0;

        // THE IMAGE OF THE PALMS
        // ======================

        db = sqlHelper.openDataBase();

        for(int i = 0; i < exp.length-1; i++){

            ArrayList<LatLng> lat_lng = new ArrayList<LatLng>();

            String query ="SELECT path FROM graph where start_vertex ="+exp[start]+" and end_vertex ="+exp[(++start)];
            cursor = db.rawQuery(query, null);
            cursor.moveToFirst();



            // get the coordinates Lat, Lng from the coordinate field (3)
            String json = cursor.getString(0).toString();

            // get JSON
            JSONObject jObject = new JSONObject(json);
            JSONArray jArrCoordinates = jObject.getJSONArray("coordinates");

            // get coordinate JSON
            for(int w = 0; w < jArrCoordinates.length(); w++){

                JSONArray latlngs = jArrCoordinates.getJSONArray(w);
                Double lats = latlngs.getDouble(0);
                Double lngs = latlngs.getDouble(1);

                System.out.println("["+lats+","+lngs+"]");

                lat_lng.add( new LatLng(lats, lngs) );

            }
            // create a route
            PolylineOptions line = new PolylineOptions();
            line.addAll(lat_lng).width(5).color(0xff4b9efa).geodesic(true);
            mMap.addPolyline(line);
        }
        db.close();

        // MAKE A MARKER FOR YOUR POSITION AND DESTINATION POSITION
        // ======================
        // your position

        mMap.addMarker(new MarkerOptions()
                .position(SCHOOL)
                .title("University of Ain El Remmeneh")
                .snippet("University")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        // destination position
        mMap.addMarker(new MarkerOptions()
                .position(destination)
                .title("Destination position")
                .snippet("Destination position")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
    }

    //get the starting vertex and ending vertex, new vertex and the graph with the added vertex(using AddVertex)
    public void getVertexStartEndPath(GetCoordinates objects, double latx, double lngx, String statusObject) throws JSONException{

        // return JSON that contains start and end vertices, and the closest coordinate to the user btw those vertices
        JSONObject jstart = objects.GetNode(latx, lngx, this);

        // index JSON
        String status = jstart.getString("status");
        int start_vertex0 = jstart.getInt("start_vertex");
        int start_vertex1 = jstart.getInt("end_vertex");
        int index_coordinate_json = jstart.getInt("index_coordinate_json");


        int fix_vertex_start = 0;

        // if the coordinates are on the start or end vertex
        // then there's no need to add new vertices
        if(status.equals("path_none")){

            // specify a start or end vertex close to the user's position
            if(index_coordinate_json == 0){ // start
                fix_vertex_start = start_vertex0;
            }else{ // end
                fix_vertex_start = start_vertex1;
            }
            //specify if these vertices are the user's(start or src) or the (end or dst)
            if(statusObject == "start"){

                // return
                __global_old_vertex_start = start_vertex0 + "-" + start_vertex1;
                __global_vertex_start = fix_vertex_start;
            }else{

                // return
                __global_old_vertex_end = start_vertex0 + "-" + start_vertex1;
                __global_vertex_end = fix_vertex_start;
            }


        }
        // if the coordinates are between the vertices
        // then need to add new vertex
        else if(status.equals("path_double")){

            // return
            //specify if these vertices are the user's(start or src) or the (end or dst)
            if(statusObject == "start"){

                // find the vertex (5-4) and (4-5) and add a new vertex
                AddVertex addVertex3 = new AddVertex();
                addVertex3.DoublePath(start_vertex0, start_vertex1, index_coordinate_json,
                        this, graphArrayFromDatabase, 401
                ); // 401 : row id


                // return
                __global_old_vertex_start = addVertex3.old_vertex;
                __global_vertex_start = addVertex3.new_vertex;
                graphArrayFromDatabase = addVertex3.modif_graph;
                Log.i("start",String.valueOf(__global_vertex_start));

            }else{

                // find the vertex (5-4) and (4-5) and add a new vertex
                AddVertex addVertex3 = new AddVertex();
                addVertex3.DoublePath(start_vertex0, start_vertex1, index_coordinate_json,
                        this, graphArrayFromDatabase, 501
                ); // 501 : row id


                // return
                __global_old_vertex_end = addVertex3.old_vertex;
                __global_vertex_end = addVertex3.new_vertex;
                graphArrayFromDatabase = addVertex3.modif_graph;
                Log.i("end",String.valueOf(__global_vertex_end));


            }

        }

        else if(status.equals("path_single")){

            if(statusObject.equals("start")){

                // find the vertex (5-4) and (4-5) and add a new vertex
                AddVertex addVertex1 = new AddVertex();
                addVertex1.SinglePath(start_vertex0, start_vertex1, index_coordinate_json,
                        this, graphArrayFromDatabase, 401
                ); // 401 : row id


                // return
                __global_old_vertex_start = addVertex1.old_vertex;
                __global_vertex_start = addVertex1.new_vertex;
                graphArrayFromDatabase = addVertex1.modif_graph;

            }else{


                AddVertex addVertex1 = new AddVertex();
                addVertex1.SinglePath(start_vertex0, start_vertex1, index_coordinate_json,
                        this, graphArrayFromDatabase, 501
                ); // 501 : row id


                // return
                __global_old_vertex_end = addVertex1.old_vertex;
                __global_vertex_end = addVertex1.new_vertex;
                graphArrayFromDatabase = addVertex1.modif_graph;
            }
        }
    }

    public void deleteTemporaryRecord() {

        // delete DB
        db = sqlHelper.openDataBase();

        Log.d("convertToArray:", "Database openned!");

        // delete temporary record DB
        for (int i = 0; i < 4; i++) {
            // remove the additional start vertex, start db id 401,402,403,404
            String deleteQuery_ = "DELETE FROM graph where id ='" + (401 + i) + "'";
            db.execSQL(deleteQuery_);

            // remove the additional destination vertex, from db id 501,502,503,504
            String deleteQuery = "DELETE FROM graph where id ='" + (501 + i) + "'";
            db.execSQL(deleteQuery);
        }
        db.close();
    }

    //get the max vertex number and increment it for usage as the new vertices
    public void maxRowDB() {

        db = sqlHelper.openDataBase();
        String query = "SELECT max(start_vertex), max(end_vertex) FROM graph";

        cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        int max_Vertexs_db = 0;
        int max_startVertex_db = Integer.parseInt(cursor.getString(0));
        int max_destinationVertex_db = Integer.parseInt(cursor.getString(1));

        if (max_startVertex_db >= max_destinationVertex_db) {
            max_Vertexs_db = max_startVertex_db;
        } else {
            max_Vertexs_db = max_destinationVertex_db;
        }

        // return
        MAX_ROW = (max_Vertexs_db + 1);
        MAX_ROW_1 = (max_Vertexs_db + 2);

        db.close();
    }

    public void hideMarker(MarkerOptions marker){
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        Log.i("Marker","mark");

        int i;

        if(cameraPosition.zoom<16){
            for(i=0;i<MarkerSize;i++)
                marker[i].setVisible(false);
        }else{
            for(i=0;i<MarkerSize;i++)
                marker[i].setVisible(true);

        }

    }

    public void  checkSelectedCustomer(){
        Intent intent = getIntent() ;
        index = intent.getStringExtra("value");
        double[] LatLngArray ;

        if(index != null){
            SQLiteDatabase dbRead = sqlHelper.openDataBase();
            LatLngArray = sqlHelper.getLatLngSelectedCustomer(index,dbRead);

            SelectedCustLocationLat = LatLngArray[0];
            SelectedCustLocationLng = LatLngArray[1];

            LatLng custLocation = new LatLng(SelectedCustLocationLat,SelectedCustLocationLng);

            try{
                moveCamera(custLocation,DEFAULT_ZOOM,"Customer's location");
                mMap.clear();
                mMap.addMarker(new MarkerOptions()
                        .position(custLocation)
                        .title("Customer's location")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_name)));

            }catch (SecurityException e){
                Log.e(TAG,"getDeviceLocation: SecurityException: "+e.getMessage());
            }
        }
        return ;
    }
}
