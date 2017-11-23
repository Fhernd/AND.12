package co.ortizol.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.login.widget.LoginButton;

import co.ortizol.MainActivity;
import co.ortizol.R;

/**
 * Representa la lógica de la interfaz para el inicio de sesión.
 */
public class InicioSesionFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = InicioSesionFragment.class.getSimpleName();

    public InicioSesionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inicio_sesion, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LoginButton loginButton = (LoginButton) view.findViewById(R.id.lbnFacebook);
        loginButton.setReadPermissions("public_profile", "email");
        loginButton.registerCallback(((MainActivity) getActivity()).getCallbackManager(), (MainActivity) getActivity());

        view.findViewById(R.id.sibIniciarSesion).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        ((MainActivity) getActivity()).signIn();
    }
}
