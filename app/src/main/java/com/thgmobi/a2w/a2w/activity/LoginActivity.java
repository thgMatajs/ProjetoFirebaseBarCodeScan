package com.thgmobi.a2w.a2w.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.thgmobi.a2w.R;
import com.thgmobi.a2w.a2w.models.Usuario;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail;
    private EditText edtSenha;

    private ProgressBar progressBarLogin;

    private Button btnEntrar;
    private TextView tvCadastar;

    private Context context;

    private Usuario usuario;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inicializaViews();
        eventosDeClick();
    }

    private void inicializaViews() {


        context = getBaseContext();

        edtEmail = findViewById(R.id.login_edt_email);
        edtSenha = findViewById(R.id.login_edt_senha);
        btnEntrar = findViewById(R.id.login_btn_entrar);
        tvCadastar = findViewById(R.id.login_tv_cadastrar);

        progressBarLogin = findViewById(R.id.login_progressbar);
        progressBarLogin.setVisibility(View.INVISIBLE);

    }

    private void eventosDeClick() {

        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                usuario = new Usuario();

                usuario.setEmail(edtEmail.getText().toString());
                usuario.setSenha(edtSenha.getText().toString());

                btnEntrar.setVisibility(View.INVISIBLE);
                progressBarLogin.setVisibility(View.VISIBLE);

                if (usuario.getEmail().isEmpty() || usuario.getSenha().isEmpty()){
                    btnEntrar.setVisibility(View.VISIBLE);
                    progressBarLogin.setVisibility(View.INVISIBLE);

                    chamaToast("E-mail ou senha invalidos!");

                }else {
                    validarLogin();
                }

            }
        });

        tvCadastar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent vaiParaTelaCadastro = new Intent(context, CadastroActivity.class);
                startActivity(vaiParaTelaCadastro);
            }
        });
    }

    private void chamaToast(String mensagem) {
        Toast.makeText(context, mensagem, Toast.LENGTH_SHORT).show();
    }

    private void validarLogin() {
        firebaseAuth.signInWithEmailAndPassword(usuario.getEmail(), usuario.getSenha())
        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){

                    Intent vaiParaTelaMain = new Intent(context, MainActivity.class);
                    startActivity(vaiParaTelaMain);
                    finish();

                }else {
                    chamaToast("Erro ao efetuar o login!");
                    btnEntrar.setVisibility(View.VISIBLE);
                    progressBarLogin.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
}
