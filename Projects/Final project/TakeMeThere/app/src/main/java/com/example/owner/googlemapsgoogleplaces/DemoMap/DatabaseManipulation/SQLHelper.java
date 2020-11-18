package com.example.owner.googlemapsgoogleplaces.DemoMap.DatabaseManipulation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.owner.googlemapsgoogleplaces.DemoMap.Utils.BuildCoord;

import static android.content.ContentValues.TAG;

/**
 * Created by one on 4/30/2018.
 */

public class SQLHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "newGraph.sqlite";
	private static final int DATABASE_VERSION = 1;
	private static String DB_PATH = "/data/data/com.app.dijkstra/databases/";
	private Context myContext;
	private Cursor cursor ;

	public String FName;
	public String LName;
	public String BuildName;
	public double Lng;
	public double Lat;
	public int Floor;
	public int nbOfBuildings;
	public ArrayList<String> customersID = new ArrayList<String>() ;

	public SQLHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		myContext=context;
	}

	public SQLiteDatabase openDataBase(){
		SQLiteDatabase db = SQLiteDatabase.openDatabase(Environment.getExternalStorageDirectory() + "/newGraph.sqlite",null,SQLiteDatabase.OPEN_READWRITE);
		return db;
	}

	//		INSERTING NEW CUSTOMER
	public void insertCustomer(String FName, String LName, String BuildName,double Lng,double Lat,int Floor,SQLiteDatabase dbInsert){
		this.FName=FName;
		this.LName=LName;
		this.BuildName=BuildName;
		this.Lng=Lng;
		this.Lat=Lat;
		this.Floor=Floor;

		try {
			String query = "Select count(*) from building where building.BuildName = '"+BuildName+"'";
			cursor = dbInsert.rawQuery(query , null);
			cursor.moveToFirst();
			if( cursor.getInt(0) > 0){
				String query2 = "Select BuildID from building where BuildName='" + BuildName + "'";
				cursor = dbInsert.rawQuery(query2, null);
				cursor.moveToNext();

				String query3 = "insert into Customers(FName,LName,Floor,BuildID) values('" + FName + "','" + LName + "'," + Floor + "," + cursor.getInt(0) + ")";
				dbInsert.execSQL(query3);
				cursor.close();
				dbInsert.close();
				return ;
			}
			else{
				String query1 = "insert into Building(BuildName,Latitude,Longitude) values('" + BuildName + "','" + Lat + "'," + Lng + ")";
				dbInsert.execSQL(query1);

				String query2 = "Select BuildID from building where BuildName='" + BuildName + "'";
				cursor = dbInsert.rawQuery(query2, null);
				cursor.moveToNext();

				String query3 = "insert into Customers(FName,LName,Floor,BuildID) values('" + FName + "','" + LName + "'," + Floor + "," + cursor.getInt(0) + ")";
				dbInsert.execSQL(query3);
			}

		}catch(SQLException ex){
			Log.d("SQLHELPER",ex.getMessage());
		}
		finally {
			cursor.close();
			dbInsert.close();
		}
	}

	//		SELECTION
	public ArrayList<String> showAllCustomers(SQLiteDatabase dbRead){
		ArrayList<String> cus = new ArrayList<String>();
		try {
			String query = "Select FName , LName, CustID from customers ";
			cursor = dbRead.rawQuery(query, null);
			while (cursor.moveToNext()) {
				cus.add(cursor.getString(0) + " " + cursor.getString(1));
				customersID.add(cursor.getString(2));
			}
		}catch(SQLException ex){
			Log.d("SQLHELPER",ex.getMessage());
		}finally {
			cursor.close();
		}
		Log.i(TAG, "showAllCustomers: "+customersID);
		return cus;
	}

	/*public ArrayList<String> showBuildings(SQLiteDatabase dbRead){
		ArrayList<String> bui = new ArrayList<String>();
		try {
			String query2 = "Select BuildName from building ";
			cursor = dbRead.rawQuery(query2, null);
			while (cursor.moveToNext()) {
				bui.add(cursor.getString(0));
			}
		}catch (SQLException ex){
			Log.d("SQLHELPER",ex.getMessage());
		}
		finally {
			cursor.close();
		}
		return bui ;
	}*/

	public ArrayList<String> showAllCustomersIds(){
		ArrayList<String> cus = new ArrayList<String>();
		cus = customersID ;
		return cus ;
	}

	public BuildCoord[] getLatLng(SQLiteDatabase dbRead){
		BuildCoord[] buildCoordinates=new BuildCoord[100]  ;


		try{
			String query = "Select count(*) from building" ;
			cursor = dbRead.rawQuery(query,null);
			cursor.moveToFirst();
			nbOfBuildings = cursor.getInt(0);

			cursor.close();

			String query2 = "Select BuildName, Latitude, Longitude from building";
			cursor = dbRead.rawQuery(query2,null);

			int k = 0 ;
			double lat , lng ;
			String name ;

			while(cursor.moveToNext()){

				name = cursor.getString(0);
				lat = cursor.getDouble(1);
				lng = cursor.getDouble(2);

				buildCoordinates[k] = new BuildCoord();
				buildCoordinates[k].setCoord(lat,lng);
				buildCoordinates[k].setBuildingName(name);

				if(++k == nbOfBuildings){
					break ;
				}
			}

		}catch(Exception ex){
			Log.d("SQLHELPER",ex.getMessage());
		}finally {
			cursor.close();
		}
		return buildCoordinates;
	}

	public double[] getLatLngSelectedCustomer(String index , SQLiteDatabase dbRead){
		double[] LatLng = new double[2] ;
		try{
			String query = "Select Latitude , Longitude from building, customers " +
					"where customers.BuildID = building.BuildID and building.BuildName ='"+index+"'";

			cursor = dbRead.rawQuery(query,null);
			cursor.moveToFirst();

			//Retreiving the coordinates of the selected building from the database
			LatLng[0]= cursor.getDouble(0);
			LatLng[1]= cursor.getDouble(1);

			Log.d("SQLHELPER_LATLNG",String.valueOf(LatLng[0])+String.valueOf(LatLng[1]));

		}catch (SQLException ex){
			Log.e("SQLHELPER",ex.getMessage());
		}
		finally{
			cursor.close();
		}
		return LatLng;
	}

    public String getBuildName(int custID){
		SQLiteDatabase dbSelect=openDataBase();
	    String query = "Select BuildName from building where BuildID in (Select BuildId from customers where custID='"+custID+"')";
        String name="";
        try {
            cursor= dbSelect.rawQuery(query,null);
            cursor.moveToFirst();
            name=cursor.getString(0);

        } catch (SQLException ex) {
            Log.e(TAG, "deleteCustomer: " + ex.getMessage());
        }
        finally {
            dbSelect.close();
        }
        return name;
    }

	//		DELETING A CUSTOMER
	public void deleteCustomer(SQLiteDatabase dbDelete, int id){
		String query = "Delete from customers where CustID ='" +id+ "'";

		try{
			dbDelete.execSQL(query);

		}catch (SQLException ex) {
            Log.e(TAG, "deleteCustomer: " + ex.getMessage());
        }
	}
	//		EDITTING A CUSTOMER
	public void editCustomer(SQLiteDatabase dbEdit, int id , String first_name , String last_name , int floor) {
		String query = "update customers set FName = '"+first_name+"' ,LName='"+last_name+"', Floor = '"+floor+"' where CustID ='"+id+"'";
		try {
			dbEdit.execSQL(query);
		} catch (SQLException ex) {
			Log.e(TAG, "deleteCustomer: " + ex.getMessage());
		}
		finally {
            dbEdit.close();
		}
	}



	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

	}
}
