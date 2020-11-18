package com.example.owner.googlemapsgoogleplaces.DemoMap.DatabaseManipulation;

import android.os.AsyncTask;
import android.content.Context;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ExecutionException;

import com.example.owner.googlemapsgoogleplaces.DemoMap.Utils.BuildCoord;

public class MSSQLDatabase {



    Connection conn;
    //String ip="150.150.150.103";
    String ip = "192.168.43.56";
    String db="project";
    String un="root";
    String pass="";

    String FName,LName,BuildName;
    double Lng,Lat;
    int Floor;

    int size=0;
    BuildCoord[] buildCoord;



    public MSSQLDatabase(Context c){



    }

    public BuildCoord[] getLatLng(){

        try {

            GetLatLng getLatLng=new GetLatLng();
            getLatLng.execute();
            buildCoord=getLatLng.get();


        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return buildCoord;
    }

    public void insertCustomer(String FName, String LName, String BuildName,double Lng,double Lat,int Floor){

        this.FName=FName;
        this.LName=LName;
        this.BuildName=BuildName;
        this.Lng=Lng;
        this.Lat=Lat;
        this.Floor=Floor;

        InsertCust insertCust=new InsertCust();
        insertCust.execute();

    }

    public Connection connections(String user, String password, String database, String serverIP) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        String connectionURL = "";

        try {
            Class.forName("com.mysql.jdbc.Driver");
            connectionURL = "jdbc:mysql://"+serverIP+":3306/"+database+"";
            connection = DriverManager.getConnection(connectionURL,user,password);
        } catch (SQLException se) {
            Log.e("error1: ", se.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("error2: ", e.getMessage());
        } catch (Exception e) {
            Log.e("error3: ", e.getMessage());
        }

        return connection;
    }



    private class GetLatLng extends AsyncTask<String, String, BuildCoord[]> {

        @Override
        protected BuildCoord[] doInBackground(String... strings) {


            try{
                conn=connections(un,pass,db,ip);
                Log.d("Connection Status" , "done") ;

                ResultSet res;
                int k=0;

                double lat,lng;


                String query1="Select count(*) from building";
                String query2="Select Latitude,Longitude from building";
                Statement statement=conn.createStatement();


                res=statement.executeQuery(query1);
                res.next();
                size = res.getInt(1);


                Log.e("Size",String.valueOf(res.getInt(1)));

                buildCoord=new BuildCoord[size];

                res=statement.executeQuery(query2);


                while(res.next()){
                    lat=res.getDouble(1);
                    lng=res.getDouble(2);
                    buildCoord[k]=new BuildCoord();
                    buildCoord[k].setCoord(lat,lng);

                    k++;
                }



            } catch (SQLException e) {
                Log.e("errorr",e.getMessage());
            }

            return buildCoord;
        }
    }

    private class InsertCust extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... strings) {

            try {
                conn = connections(un, pass, db, ip);

                int res;
                ResultSet rs;

                String query1="insert into Building(BuildName,Latitude,Longitude) values('"+BuildName+"','"+Lat+"',"+Lng+")";
                Statement statement=conn.createStatement();
                res=statement.executeUpdate(query1);
                String query2="Select BuildID from building where BuildName='"+BuildName+"'";
                rs=statement.executeQuery(query2);
                rs.next();
                String query3="insert into Customers(FName,LName,Floor,BuildID) values('"+FName+"','"+LName+"',"+Floor+","+rs.getInt(1)+")";
                res=statement.executeUpdate(query3);

                conn.close();

                Log.e("Success","suc");

            }catch (Exception e){
                Log.e("Fail1",e.getMessage());
            }

            return null;
        }
    }
}
