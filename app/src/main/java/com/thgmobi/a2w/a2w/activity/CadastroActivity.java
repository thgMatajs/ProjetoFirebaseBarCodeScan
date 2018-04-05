package com.thgmobi.a2w.a2w.activity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.thgmobi.a2w.R;
import com.thgmobi.a2w.a2w.models.Usuario;

public class CadastroActivity extends AppCompatActivity {

    private Context context;
    private Usuario usuario;


    private EditText edtNome;
    private EditText edtEmail;
    private EditText edtSenha;

    private ProgressBar progressBar;
    private Button btnSalvar;
    private Button btnCancelar;

    private DatabaseReference firebaseReference = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        inicializaViews();
        acoesDeClick();
    }

    private void inicializaViews() {
        edtNome = findViewById(R.id.cadastro_edt_nome);
        edtEmail = findViewById(R.id.cadastro_edt_email);
        edtSenha = findViewById(R.id.cadastro_edt_senha);
        btnSalvar = findViewById(R.id.cadastro_btn_salvar);
        btnCancelar = findViewById(R.id.cadastro_btn_cancelar);
        progressBar = findViewById(R.id.cadastro_progressbar);

        progressBar.setVisibility(View.INVISIBLE);

        context = getBaseContext();
    }

    private void acoesDeClick() {

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                usuario = new Usuario();
                usuario.setNome(edtNome.getText().toString());
                usuario.setEmail(edtEmail.getText().toString());
                usuario.setSenha(edtSenha.getText().toString());

                if (usuario.getNome().isEmpty() || usuario.getEmail().isEmpty() || usuario.getSenha().isEmpty()){
                    chamaToast("Todos os campos são obrigatorios!");
                    btnSalvar.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }else{
                    cadastarUsuario();
                    btnSalvar.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
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

    private void cadastarUsuario() {

        firebaseAuth.createUserWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(CadastroActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){
                    chamaToast("Cadastro efetuado com sucesso");
                    usuario.setId(task.getResult().getUser().getUid());
                    usuario.salvarUsuario();
                    firebaseAuth.signOut();
                    progressBar.setVisibility(View.GONE);
                    finish();
                }else {

                    btnSalvar.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);

                    String msgErroException = "";

                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        msgErroException = "Senha não aceita, tente outra com no minimo 8 digitos!";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        msgErroException = "E-mail invalido, verifique se digitou corretamente!";
                    } catch (FirebaseAuthUserCollisionException e) {
                        msgErroException = "E-mail ja esta em uso, tente um novo!";
                    } catch (Exception e) {
                        msgErroException = "Erro ao cadastrar";
                        e.printStackTrace();
                    }
                    chamaToast(msgErroException);
                }
            }
        });

    }

    private void chamaToast(String mensagem) {
        Toast.makeText(context, mensagem, Toast.LENGTH_SHORT).show();
    }
}
