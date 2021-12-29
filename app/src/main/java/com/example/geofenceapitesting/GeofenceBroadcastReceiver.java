package com.example.geofenceapitesting;

import static android.content.ContentValues.TAG;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "GeofenceBroadcastReceiv";
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
     //   Toast.makeText(context, "Geofence Triggered", Toast.LENGTH_SHORT).show();
        NotificationHelper notificationHelper = new NotificationHelper(context);
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if(geofencingEvent.hasError()){
            Log.d(TAG, "on failure error receiving geofence event" );
            return;
        }
        List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();
        int transitionType = geofencingEvent.getGeofenceTransition();
        for(Geofence geofence: geofenceList){

            switch (transitionType) {
                case Geofence.GEOFENCE_TRANSITION_ENTER:
                    Toast.makeText(context, "geofence_Transition_Enter", Toast.LENGTH_SHORT).show();
                    notificationHelper.sendHighPriorityNotification("Arrived at " + geofence.getRequestId(),"geofence_Transition_Enter",MapsActivity.class);
                    break;
                case Geofence.GEOFENCE_TRANSITION_DWELL:
                    Toast.makeText(context, "geofence_Transition_Dwell", Toast.LENGTH_SHORT).show();
                    notificationHelper.sendHighPriorityNotification("Now Dwelling in " + geofence.getRequestId(),"geofence_Transition_Dwell",MapsActivity.class);
                    break;
                case Geofence.GEOFENCE_TRANSITION_EXIT:
                    Toast.makeText(context, "geofence_Transition_Exit", Toast.LENGTH_SHORT).show();
                    notificationHelper.sendHighPriorityNotification("Exiting From " + geofence.getRequestId() ,"geofence_Transition_Exit",MapsActivity.class);
                    break;
            }
        }
        //Location location = geofencingEvent.getTriggeringLocation(); isse kaha pe trigger hua..wo location mil jayegi




    }
}