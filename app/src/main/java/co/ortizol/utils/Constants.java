package co.ortizol.utils;

import com.google.android.gms.location.Geofence;

public class Constants {

    private Constants(){}

    public final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    public static final long CONNECTION_TIME_OUT_MS = 100;

    public static final long GEOFENCE_EXPIRATION_TIME = Geofence.NEVER_EXPIRE;

    public static final String ANDROID_ID = "1";
    public static final double ANDROID_LATITUDE = 4.667079;
    public static final double ANDROID_LONGITUDE = -74.0569433;
    public static final float ANDROID_RADIUS_METERS = 5F;
    public static final int ANDROID_LOITERING_DELAY = 100;

}
