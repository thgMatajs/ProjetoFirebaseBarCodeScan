package com.thgmobi.a2w.a2w.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.thgmobi.a2w.R;
import com.thgmobi.a2w.a2w.models.Equipamento;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by usuario on 01/03/2018.
 */

public class EquipamentosAdapter extends ArrayAdapter<Equipamento> {

    private final Context context;
    private final ArrayList<Equipamento> equipamentos;

    public EquipamentosAdapter(Context c, ArrayList<Equipamento> objects) {
        super(c, 0, objects);
        this.context = c;
        this.equipamentos = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;

        //verifica se a lista esta preenchida
        if (equipamentos != null){

            //inicializa objeto para montar layout
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            //monta celula a partir do xml
            view = inflater.inflate(R.layout.item_lista_equip, parent, false);

        }

        Equipamento equipamento = equipamentos.get(position);

        TextView txtNs = view.findViewById(R.id.item_lista_ns);
        txtNs.setText(equipamento.getNumeroSerie());
        TextView txtTecnico = view.findViewById(R.id.item_lista_tecnico);
        txtTecnico.setText(equipamento.getTecnico());
        TextView txtOs = view.findViewById(R.id.item_lista_os);
        txtOs.setText("OS: " + equipamento.getNumeroOs());
        return view;
    }
}
