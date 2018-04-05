package com.thgmobi.a2w.a2w.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.thgmobi.a2w.R;
import com.thgmobi.a2w.a2w.adapters.EquipamentosAdapter;
import com.thgmobi.a2w.a2w.models.Equipamento;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Context context;

    private Toolbar toolbarMain;
    private FloatingActionButton fabScan;
    private ListView lvEquipamentos;

    private Equipamento equipamento;
    private ArrayList<Equipamento> equipamentos;
    private ArrayAdapter<Equipamento> eAdapter;

    private DatabaseReference firebase = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

    private String codigoDeBarra;

    private String NOME_DB_EQUIPAMENTOS = "equipamentos";
    private String NOME_CAMPO_NS = "numeroSerie";

    private final Activity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inicializaViews();
        acoesDeClick();

        setSupportActionBar(toolbarMain);
        setTitle("A2W");

        verificaUsuarioLogado();
        pesquisaEquipamentos("");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_buscar: {

                IniciarScan(activity);

                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    //chama a tela de scan
    private void IniciarScan(Activity activity) {

        IntentIntegrator integrator = new IntentIntegrator(activity);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Por favor, posicione o codigo de barra no centro da tela!");
        integrator.setCameraId(0);
        integrator.initiateScan();
        integrator.setOrientationLocked(true);
    }

    //
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null){

            if (result.getContents() != null){

                codigoDeBarra = result.getContents();



                pesquisaEquipamentos(codigoDeBarra);

            }

        }else{
            chamaToast("Erro ao Escanear!");
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void pesquisaEquipamentos(String codigoDeBarra) {

        Query query;

        if (codigoDeBarra == "" || codigoDeBarra == null){
            query = firebase.child(NOME_DB_EQUIPAMENTOS).orderByChild(NOME_CAMPO_NS);
        }else{

            query = firebase.child(NOME_DB_EQUIPAMENTOS).orderByChild(NOME_CAMPO_NS)
                    .equalTo(codigoDeBarra).limitToFirst(1);
        }

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                equipamentos.clear();

                for (DataSnapshot dados: dataSnapshot.getChildren()){

                    Equipamento equipamento = dados.getValue(Equipamento.class);
                    equipamentos.add(equipamento);
                }

                eAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void inicializaViews() {
        context = getBaseContext();

        toolbarMain = findViewById(R.id.toolbar);
        fabScan = findViewById(R.id.fab_scanner);
        lvEquipamentos = findViewById(R.id.lv_equipamentos);

        equipamentos = new ArrayList<>();
        eAdapter = new EquipamentosAdapter(MainActivity.this, equipamentos);
        lvEquipamentos.setAdapter(eAdapter);

    }

    private void verificaUsuarioLogado() {
        if (firebaseUser == null){

            Intent vaiParaTelaLogin = new Intent(context, LoginActivity.class);
            startActivity(vaiParaTelaLogin);
            finish();
        }else{

            chamaToast("Bem vindo de volta: " + firebaseUser.getEmail() + " !");
        }

    }

    private void acoesDeClick(){

        fabScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chamaFormularioEquip = new Intent(context, FormularioEquipActivity.class);
                startActivity(chamaFormularioEquip);
            }
        });
    }

    private void chamaToast(String mensagem) {
        Toast.makeText(context, mensagem, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        pesquisaEquipamentos(codigoDeBarra);
    }
}
