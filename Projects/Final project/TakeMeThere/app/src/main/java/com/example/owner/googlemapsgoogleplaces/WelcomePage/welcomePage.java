package com.example.owner.googlemapsgoogleplaces.WelcomePage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.example.owner.googlemapsgoogleplaces.DemoMap.MapActivity;
import com.example.owner.googlemapsgoogleplaces.DemoMap.Service.CheckServices;
import com.example.owner.googlemapsgoogleplaces.DemoMap.new_delivery;
import com.example.owner.googlemapsgoogleplaces.R;

import java.io.IOException;

public class welcomePage extends AppCompatActivity {

    CheckServices checkServices ;
    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);

        Context ctx = getApplicationContext();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent  = new Intent(welcomePage.this , popup_window.class);
               startActivity(intent);
            }
        });



        checkServices = new CheckServices();
        if(checkServices.isServiesOK(ctx)){
            try{
                start();
            }catch(IOException e){
                e.printStackTrace();
            }
        }

    }
    //Method for the first cardView
    public void explain(View view) {
        Intent intent = new Intent(welcomePage.this , MapActivity.class);
        startActivity(intent);
    }

    //If all the services are checked and set to go, we initialize our imageView for onClick use
    public void start() throws IOException{
        ImageView btnMap = (ImageView) findViewById(R.id.btnMap);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1  = new Intent(welcomePage.this, new_delivery.class);
                startActivity(intent1);
            }
        });
    }
}
