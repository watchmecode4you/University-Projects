package com.example.owner.googlemapsgoogleplaces.DemoMap.Service;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class CheckServices extends Activity{
    private static final String TAG = "CheckServices";
    private static final int ERROR_DIALOG_REQUEST = 9001 ;

    public boolean isServiesOK(Context ctx){
        Log.d(TAG,"isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(ctx);
        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServiveOK: Google Play Services is working");
            return true ;
        }
        else if ( GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG,"isServieOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(CheckServices.this,available,ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else
            //nothing we can do
            Toast.makeText(ctx,"you can't make map requests",Toast.LENGTH_SHORT).show();
        return false ;
    }

    private void buildAlertMessageNoGps(Context ctx) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public void statusCheck(Context ctx) {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps(ctx);

        }
    }
}
