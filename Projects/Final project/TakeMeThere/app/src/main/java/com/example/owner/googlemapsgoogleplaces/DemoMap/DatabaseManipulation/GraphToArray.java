package com.example.owner.googlemapsgoogleplaces.DemoMap.DatabaseManipulation;

/**
 * Created by one on 4/30/2018.
 */

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

public class GraphToArray {

	SQLHelper sqlHelper;
	SQLiteDatabase db;
	protected Cursor cursor;

	String[][] graph =new String[500][100];



	public String[][] ConvertToArray(Context mainContext){

		sqlHelper=new SQLHelper(mainContext);
		db=sqlHelper.openDataBase();


		cursor=db.rawQuery("Select * from graph order by start_vertex,end_vertex asc",null);
		cursor.moveToFirst();

		String initialVertex="";
		int pathIndex=0;


		for(int i=0;i<cursor.getCount();i++) {

			cursor.moveToPosition(i);

			int start_vertex = cursor.getInt(1);
			int end_vertex = cursor.getInt(2);

			if (initialVertex == "") {

				initialVertex = String.valueOf(start_vertex);

			} else if (initialVertex != String.valueOf(start_vertex)) {

				initialVertex = String.valueOf(start_vertex);
				pathIndex = 0;

			}
			String destinationAndWeight="";
			//no paths
			if (cursor.getString(2)=="") {
				destinationAndWeight = ";";
			} else {
				destinationAndWeight = String.valueOf(end_vertex) + "->" + cursor.getString(4);

			}

			graph[start_vertex][pathIndex] = destinationAndWeight;
			pathIndex++;
		}
		return graph;
	}
}
