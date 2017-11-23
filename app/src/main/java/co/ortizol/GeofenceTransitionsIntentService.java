package co.ortizol;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.LocationServices;

import java.util.concurrent.TimeUnit;

import static co.ortizol.utils.Constants.CONNECTION_TIME_OUT_MS;

public class GeofenceTransitionsIntentService extends IntentService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;

    public GeofenceTransitionsIntentService() {
        super(GeofenceTransitionsIntentService.class.getName());
    }

    public void onCreate() {
        super.onCreate();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent.hasError()) {
            Toast.makeText(GeofenceTransitionsIntentService.this, "Error conectando con geofence", Toast.LENGTH_SHORT).show();
        } else {
            int transitionType = geofencingEvent.getGeofenceTransition();
            if (Geofence.GEOFENCE_TRANSITION_ENTER == transitionType) {

                mGoogleApiClient.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);

                Toast.makeText(GeofenceTransitionsIntentService.this, getApplicationContext().getString(R.string.ingreso_zona_insegura), Toast.LENGTH_SHORT).show();

                mGoogleApiClient.disconnect();
            } else if (Geofence.GEOFENCE_TRANSITION_EXIT == transitionType) {
                mGoogleApiClient.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);

                Toast.makeText(GeofenceTransitionsIntentService.this, getApplicationContext().getString(R.string.salida_zona_insegura), Toast.LENGTH_SHORT).show();

                mGoogleApiClient.disconnect();
            } else if (Geofence.GEOFENCE_TRANSITION_DWELL == transitionType) {
                mGoogleApiClient.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);

                Toast.makeText(GeofenceTransitionsIntentService.this, getApplicationContext().getString(R.string.maximo_tiempo_zona_insegura), Toast.LENGTH_SHORT).show();
                mGoogleApiClient.disconnect();
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
