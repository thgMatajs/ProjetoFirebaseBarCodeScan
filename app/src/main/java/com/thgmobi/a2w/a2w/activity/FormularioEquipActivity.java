package com.thgmobi.a2w.a2w.activity;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.thgmobi.a2w.R;
import com.thgmobi.a2w.a2w.models.Equipamento;

public class FormularioEquipActivity extends AppCompatActivity {

    private Context context;
    private Equipamento equipamento;

    private String idEquipamento;

    private EditText edtNs;
    private EditText edtTipo;
    private EditText edtOs;
    private EditText edtTecnico;
    private EditText edtDefeito;

    private Button btnSalvar;
    private Button btnCancelar;
    private ImageButton ibScan;

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_equip);

        idEquipamento = databaseReference.push().getKey();

        inicializaViews();
        eventosDeClick();

    }

    private void inicializaViews() {
        context = getBaseContext();

        edtNs = findViewById(R.id.formulario_equip_edt_ns);
        edtTipo = findViewById(R.id.formulario_equip_edt_tipo);
        edtOs = findViewById(R.id.formulario_equip_edt_os);
        edtTecnico = findViewById(R.id.formulario_equip_edt_tecnico);
        edtDefeito = findViewById(R.id.formulario_equip_edt_defeito);

        btnSalvar = findViewById(R.id.formulario_equip_btn_salvar);
        btnCancelar = findViewById(R.id.formulario_equip_btn_cancelar);
        ibScan = findViewById(R.id.formulario_equip_ib_scan);

    }

    private void eventosDeClick() {

        ibScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                IntentIntegrator integrator = new IntentIntegrator(FormularioEquipActivity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrator.setPrompt("Por favor, posicione o codigo de barra no centro da tela!");
                integrator.setCameraId(0);
                integrator.initiateScan();
                integrator.setOrientationLocked(true);
            }
        });

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                inicializaEquipamento();

                if (equipamento.getDefeito().isEmpty() || equipamento.getNumeroOs().isEmpty() ||
                        equipamento.getNumeroSerie().isEmpty() || equipamento.getTecnico().isEmpty() ||
                        equipamento.getTipo().isEmpty()){

                    chamaToast("Preencha todos os campos!");

                }else {


                    databaseReference.child("equipamentos").child(idEquipamento).setValue(equipamento);

                    limpaCampos();

                    chamaToast("Equipamento cadastrado com sucesso!");
                }

            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void chamaToast(String mensagem) {
        Toast.makeText(context, mensagem, Toast.LENGTH_SHORT).show();
    }

    private void inicializaEquipamento(){

        equipamento = new Equipamento();

        equipamento.setId(idEquipamento);
        equipamento.setIdUsuario(firebaseUser.getUid());
        equipamento.setNumeroSerie(edtNs.getText().toString());
        equipamento.setTipo(edtTipo.getText().toString());
        equipamento.setTecnico(edtTecnico.getText().toString());
        equipamento.setNumeroOs(edtOs.getText().toString());
        equipamento.setDefeito(edtDefeito.getText().toString());

    }

    private void limpaCampos(){

        edtNs.setText("");
        edtTipo.setText("");
        edtDefeito.setText("");
        edtTecnico.setText("");
        edtOs.setText("");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null){

            if (result.getContents() != null){

                edtNs.setText(result.getContents());
            }

        }else{
            chamaToast("Erro ao Escanear!");
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
