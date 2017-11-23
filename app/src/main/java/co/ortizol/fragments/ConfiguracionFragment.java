package co.ortizol.fragments;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import co.ortizol.R;
import co.ortizol.adapters.ContactosAdapter;
import co.ortizol.models.Contacto;

/**
 * Representa el controlador para gestionar los contactos que se notificarán.
 */
public class ConfiguracionFragment extends Fragment {

    private static final String TAG = ConfiguracionFragment.class.getSimpleName();

    private ListView lvwContactos;
    private ProgressDialog pdgProgressDialog;
    private Handler htdBarHandler;

    private ArrayList<Contacto> contactos;

    private Cursor cursor;
    int counter = 0;

    private static final int REQUEST_CODE_PERMISOS = 1;
    private static final String[] PERMISOS = {
            Manifest.permission.READ_CONTACTS
    };

    public ConfiguracionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        int leer = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS);
        if (leer == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(getActivity(), PERMISOS, REQUEST_CODE_PERMISOS);

        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_configuracion, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        contactos = new ArrayList<>();
        pdgProgressDialog = new ProgressDialog(getContext());
        pdgProgressDialog.setMessage("Leyendo");
        pdgProgressDialog.setCancelable(false);
        pdgProgressDialog.show();

        lvwContactos = (ListView) view.findViewById(R.id.lvwContactos);
        htdBarHandler = new Handler();

        new Thread(new Runnable() {
            @Override
            public void run() {
                obtenerContactos();
            }
        }).start();
    }

    private void obtenerContactos() {
        String nombre;
        String telefono = null;

        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String _ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

        Uri PHONE_CONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String PHONE_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

        ContentResolver contentResolver = getContext().getContentResolver();

        cursor = contentResolver.query(CONTENT_URI, null, null, null, null);

        if (cursor.getCount() > 0) {
            counter = 0;

            while (cursor.moveToNext()) {

                htdBarHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        pdgProgressDialog.setMessage("Leyendo: " + counter++ + "/" + cursor.getCount());
                    }
                });

                String contactId = cursor.getString(cursor.getColumnIndex(_ID));
                nombre = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
                int numeroTelefonos = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));

                if (numeroTelefonos > 0) {

                    Cursor csrPhone = contentResolver.query(PHONE_CONTENT_URI, null, PHONE_CONTACT_ID + " = ?", new String[]{contactId}, null);

                    while (csrPhone.moveToNext()) {
                        telefono = csrPhone.getString(csrPhone.getColumnIndex(NUMBER));
                    }

                    csrPhone.close();
                }

                contactos.add(new Contacto(nombre, telefono));
            }

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    ContactosAdapter adapter = new ContactosAdapter(getContext(), contactos);
                    lvwContactos.setAdapter(adapter);
                }
            });

            htdBarHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    pdgProgressDialog.cancel();
                }
            }, 500);
        }
    }
}
