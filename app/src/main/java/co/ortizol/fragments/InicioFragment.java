package co.ortizol.fragments;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

import co.ortizol.GeofenceTransitionsIntentService;
import co.ortizol.R;
import co.ortizol.SimpleGeofence;

import static co.ortizol.utils.Constants.ANDROID_ID;
import static co.ortizol.utils.Constants.ANDROID_LATITUDE;
import static co.ortizol.utils.Constants.ANDROID_LOITERING_DELAY;
import static co.ortizol.utils.Constants.ANDROID_LONGITUDE;
import static co.ortizol.utils.Constants.ANDROID_RADIUS_METERS;
import static co.ortizol.utils.Constants.CONNECTION_FAILURE_RESOLUTION_REQUEST;
import static co.ortizol.utils.Constants.GEOFENCE_EXPIRATION_TIME;

/**
 * Representa el fragmento que se encarga de mostrar la información del usuario que acaba de iniciar sesión.
 */
public class InicioFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private SimpleGeofence mAndroidGeofence;

    List<Geofence> mGeofence;

    private PendingIntent mGeofenceRequestIntent;

    private GoogleApiClient mApiClient;

    private static final String[] PERMISOS = {
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    public static final String EMAIL = "email";

    public static InicioFragment getInstance(String email) {
        InicioFragment frgInicio = new InicioFragment();

        Bundle bdlArgumentos = new Bundle();
        bdlArgumentos.putString(EMAIL, email);

        frgInicio.setArguments(bdlArgumentos);

        return frgInicio;
    }

    private TextView lblEmailSesionIniciada;

    public InicioFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        int leer = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
        if (leer == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(getActivity(), PERMISOS, 1);
        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inicio, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lblEmailSesionIniciada = (TextView) view.findViewById(R.id.lblEmailSesionIniciada);

        lblEmailSesionIniciada.setText(getArguments().getString(EMAIL));

        view.findViewById(R.id.fabConfigurarContactosAlerta).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.layContenedor, new ConfiguracionFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        if (!disponiblesGooglePlayServices()) {
            Toast.makeText(getActivity(), "Servicios de Google Play no disponibles", Toast.LENGTH_SHORT).show();
            return;
        }

        mApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mApiClient.connect();

        mGeofence = new ArrayList<Geofence>();

        crearGeofences();
    }

    public void crearGeofences() {

        mAndroidGeofence = new SimpleGeofence(
                ANDROID_ID,
                ANDROID_LATITUDE,
                ANDROID_LONGITUDE,
                ANDROID_RADIUS_METERS,
                GEOFENCE_EXPIRATION_TIME,
                Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT | Geofence.GEOFENCE_TRANSITION_DWELL,
                ANDROID_LOITERING_DELAY
        );

        mGeofence.add(mAndroidGeofence.toGeofence());

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        int leer = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        if (leer == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(getActivity(), PERMISOS, 1);
        }

        mGeofenceRequestIntent = getGeofenceTransitionPendingIntent();
        LocationServices.GeofencingApi.addGeofences(mApiClient, mGeofence, mGeofenceRequestIntent);

        Toast.makeText(getActivity(), getString(R.string.servicio_alertas_iniciado), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(getActivity(), CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                Toast.makeText(getActivity(), "Error ejecutando Google Play Service", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "Ocurrió un error", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean disponiblesGooglePlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getContext());
        if (ConnectionResult.SUCCESS == resultCode) {
            Toast.makeText(getActivity(), getString(R.string.inicio_servicio_alertas), Toast.LENGTH_SHORT).show();
            return true;
        } else return false;
    }

    private PendingIntent getGeofenceTransitionPendingIntent() {
        Intent intent = new Intent(getContext(), GeofenceTransitionsIntentService.class);
        return PendingIntent.getService(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
