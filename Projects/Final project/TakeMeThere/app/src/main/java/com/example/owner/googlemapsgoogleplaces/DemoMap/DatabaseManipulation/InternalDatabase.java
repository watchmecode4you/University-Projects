package com.example.owner.googlemapsgoogleplaces.DemoMap.DatabaseManipulation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.maps.model.SquareCap;

public class InternalDatabase extends SQLiteOpenHelper {

    public static final String DATABASE_NAME="Customer.db";

    public static final String Customer_Table="Customers";
    public static final String Cust_Col_ID="CustID";
    public static final String Cust_Col_FName="FName";
    public static final String Cust_Col_LName="LName";
    public static final String Cust_Col_Floor="Floor";
    public static final String Cust_Col_BldID="BuildingID";


    public static final String Building_Table="Buildings";
    public static final String Building_Col_ID="BuildingID";
    public static final String Building_Col_Name="BuildingName";
    public static final String Building_Col_Lat="Latitude";
    public static final String Building_Col_Lng="Longitude";

    public InternalDatabase(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.i("SSock","fwfewfwefw");

        db.execSQL("create table "+Building_Table+"("
                +Building_Col_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                +Building_Col_Name+" TEXT, "
                +Building_Col_Lat+" REAL, "
                +Building_Col_Lng+" REAL)");


        db.execSQL("create table "+Customer_Table+"("
                +Cust_Col_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                +Cust_Col_FName+" TEXT, "
                +Cust_Col_LName+" TEXT,"
                +Cust_Col_Floor+" INTEGER,"
                +Cust_Col_BldID+" INTEGER,"
                +" FOREIGN KEY ("+Cust_Col_BldID+") REFERENCES "+Building_Table+" ("+Building_Col_ID+"))");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        db.execSQL("DROP TABLE IF EXISTS "+Customer_Table);
        db.execSQL("DROP TABLE IF EXISTS "+Building_Table);

        onCreate(db);

    }

    public boolean insertDataCust(String fname,String lname,int floor,int bldID){

        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(Cust_Col_FName,fname);
        contentValues.put(Cust_Col_LName,lname);
        contentValues.put(Cust_Col_Floor,floor);
        contentValues.put(Cust_Col_BldID,bldID);
        long result=db.insert(Customer_Table,null,contentValues);

        if(result==-1){
            return false;
        }else{
            return true;
        }

    }
    public boolean insertDataBld(String name,double lat,double lng){

        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(Building_Col_Name,name);
        contentValues.put(Building_Col_Lng,lng);
        contentValues.put(Building_Col_Lat,lat);
        long result=db.insert(Building_Table,null,contentValues);

        if(result==-1){
            return false;
        }else{
            return true;
        }

    }

    public int GetBuildingId(String name){

        SQLiteDatabase db=this.getWritableDatabase();
        int id;
        Cursor cursor;
        cursor=db.rawQuery("Select "+Building_Col_ID+" from "+Building_Table+" where "+Building_Col_Name+"='"+name+"'",null);

        return cursor.getInt(0);
    }
}
