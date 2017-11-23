package co.ortizol.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import co.ortizol.R;
import co.ortizol.models.Contacto;

/**
 * Repreesenta el controlador para gestionar los contactos que se leen desde el proveedor de contenido.
 */
public class ContactosAdapter extends BaseAdapter {

    private final Context context;
    private final List<Contacto> contactos;

    public ContactosAdapter(Context context, List<Contacto> contactos) {
        this.context = context;
        this.contactos = contactos;
    }

    @Override
    public int getCount() {
        return contactos.size();
    }

    @Override
    public Contacto getItem(int position) {
        return contactos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        ContactoViewHolder holder = new ContactoViewHolder();

        if (row == null) {
            row = LayoutInflater.from(context).inflate(R.layout.contacto_item_layout, parent, false);
        }

        holder.lblNombreContacto = (TextView) row.findViewById(R.id.lblNombreContacto);
        holder.lblTelefonoContacto = (TextView) row.findViewById(R.id.lblTelefonoContacto);
        holder.chkAlertar = (CheckBox) row.findViewById(R.id.chkAlertar);

        final Contacto persona = contactos.get(position);

        holder.lblNombreContacto.setText(persona.getNombre());
        holder.lblTelefonoContacto.setText(persona.getTelefono());

        holder.chkAlertar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(context, context.getString(R.string.notificar_a, persona.getNombre()), Toast.LENGTH_SHORT).show();
                } else{
                    Toast.makeText(context, context.getString(R.string.no_notificar_a, persona.getNombre()), Toast.LENGTH_SHORT).show();
                }
            }
        });

        return row;
    }

    private class ContactoViewHolder {
        public TextView lblNombreContacto;
        public TextView lblTelefonoContacto;
        public CheckBox chkAlertar;
    }
}
