package co.ortizol;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONException;
import org.json.JSONObject;

import co.ortizol.fragments.InicioFragment;
import co.ortizol.fragments.InicioSesionFragment;

/**
 * Representa la actividad principal y host de fragmentos de la aplicación.
 */
public class MainActivity extends AppCompatActivity implements FacebookCallback<LoginResult>, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    public Toolbar getToolbar() {
        return toolbar;
    }

    private Toolbar toolbar;

    private CallbackManager callbackManager;

    private ProfileTracker profileTracker;

    public static final int SIGN_IN_GOOGLE_REQUEST_CODE = 1;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setSubtitle(getString(R.string.inicio_sesion));
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.layContenedor, new InicioSesionFragment(), "INICIO_SESION_FRAGMENT")
                .commit();

        callbackManager = CallbackManager.Factory.create();

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                if (currentProfile != null) {
                    GraphRequest request = GraphRequest.newMeRequest(
                            AccessToken.getCurrentAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject object, GraphResponse response) {
                                    try {
                                        String email = object.getString("email");
                                        InicioFragment frgInicio = InicioFragment.getInstance(email);

                                        toolbar.setSubtitle(getString(R.string.inicio));
                                        getSupportFragmentManager().beginTransaction()
                                                .replace(R.id.layContenedor, frgInicio)
                                                .addToBackStack(null)
                                                .commit();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Log.e(TAG, "onCompleted: Error", e);
                                    }
                                }
                            });

                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,name,link,email");

                    request.setParameters(parameters);
                    request.executeAsync();
                }
            }
        };
        profileTracker.startTracking();

        if (mGoogleApiClient == null) {

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_IN_GOOGLE_REQUEST_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onSuccess(LoginResult loginResult) {
        Log.i(TAG, "onSuccess: " + loginResult.getAccessToken());
    }

    @Override
    public void onCancel() {
        Log.i(TAG, "onCancel: Se ha cancelado la operación de inicio de sesión");
    }

    @Override
    public void onError(FacebookException error) {
        Log.i(TAG, "onError: " + error.getMessage());
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, SIGN_IN_GOOGLE_REQUEST_CODE);
    }

    public void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult: " + result.isSuccess());
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            InicioFragment frgInicio = InicioFragment.getInstance(account.getEmail());

            toolbar.setSubtitle(getString(R.string.inicio));
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.layContenedor, frgInicio)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        Log.i(TAG, "onResult: Disconnect with Google");
                    }
                });
    }

    public CallbackManager getCallbackManager() {
        return callbackManager;
    }
}
