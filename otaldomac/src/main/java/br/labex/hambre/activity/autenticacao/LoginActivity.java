package br.labex.hambre.activity.autenticacao;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import br.labex.hambre.R;
import br.labex.hambre.activity.empresa.EmpresaFinalizaCadastroActivity;
import br.labex.hambre.activity.empresa.EmpresaHomeActivity;
import br.labex.hambre.activity.usuario.UsuarioFinalizaCadastroActivity;
import br.labex.hambre.activity.usuario.UsuarioHomeActivity;
import br.labex.hambre.helper.FirebaseHelper;
import br.labex.hambre.model.Empresa;
import br.labex.hambre.model.Login;
import br.labex.hambre.model.Usuario;

public class LoginActivity extends AppCompatActivity {


    private EditText edt_Email;
    private EditText edt_Senha;
    private ProgressBar progressBar;
    private Login login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(br.labex.hambre.R.layout.activity_login);

        configCliques();
        iniciacomponentes();
    }

    private void configCliques(){
        findViewById(R.id.txt_CriarConta).setOnClickListener(view ->
                startActivity(new Intent(this, CriarContaActivity.class)));

        findViewById(R.id.ib_Voltar).setOnClickListener(v -> finish());
        findViewById(R.id.txt_RecuperarConta).setOnClickListener(v ->
                startActivity(new Intent(this, RecuperaContaActivity.class)));
    }

    public void validaDadosLogin(View view){
        String email = edt_Email.getText().toString();
        String senha = edt_Senha.getText().toString();

        if(!email.isEmpty()){
            if(!senha.isEmpty()){
                ocultarTeclado();
                progressBar.setVisibility(View.VISIBLE);
                logar (email,senha);

            }else {
                edt_Senha.requestFocus();
                edt_Senha.setError("Informe sua senha.");
            }
        }else {
            edt_Email.requestFocus();
            edt_Email.setError("Informe seu E-mail.");
        }
    }

    private void logar (String email, String senha){
        FirebaseHelper.getAuth().signInWithEmailAndPassword(
                email, senha
        ).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                verificaCadastro(task.getResult().getUser().getUid());
            }else{
                progressBar.setVisibility(View.GONE);
                erroAutenticacao(FirebaseHelper.validaErros(task.getException().getMessage()));
            }
        });
    }

    private void verificaCadastro(String idUser){
        DatabaseReference loginRef = FirebaseHelper.getDatabaseReference()
                .child("login")
                .child(idUser);
        loginRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                login = snapshot.getValue(Login.class);
                verificaAcesso(login);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void verificaAcesso(Login login){

        if(login != null){

            if(login.getTipo().equals("U")){
                if (login.getAcesso()){
                    setResult( RESULT_OK, new Intent());
                    finish();

                }else{
                    recuperaUsuario();
                }
            } else { // else if (login.getTipo().equals("E")) (CASO QUEIRA COLOCAR ALEM DE USUARIO E EMPRESA)
                if (login.getAcesso()){
                    finish();
                    startActivity(new Intent(getBaseContext(), EmpresaHomeActivity.class));

                }else{

                    recuperaEmpresa();
                }
            }

        }

    }

    private void recuperaUsuario(){
        DatabaseReference usuarioRef = FirebaseHelper.getDatabaseReference()
                .child("usuarios")
                .child(login.getId());
        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);
                if (usuario != null){
                    finish();
                    Intent intent = new Intent(getBaseContext(), UsuarioFinalizaCadastroActivity.class);
                    intent.putExtra("login", login);
                    intent.putExtra("usuario", usuario);
                    startActivity(intent);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void recuperaEmpresa(){
        DatabaseReference empresaRef = FirebaseHelper.getDatabaseReference()
                .child("empresas")
                .child(login.getId());
        empresaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Empresa empresa = snapshot.getValue(Empresa.class);
                if (empresa != null){
                    finish();
                    Intent intent = new Intent(getBaseContext(), EmpresaFinalizaCadastroActivity.class);
                    intent.putExtra("login", login);
                    intent.putExtra("empresa", empresa);
                    startActivity(intent);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void erroAutenticacao(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Atenção");
        builder.setMessage(msg);
        builder.setPositiveButton("OK", ((dialog, which) -> {
            dialog.dismiss();
        }));

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void iniciacomponentes(){
        TextView text_Toobar = findViewById(R.id.txt_Toobar);
        text_Toobar.setText("Login");

        edt_Email = findViewById(R.id.edt_Email);
        edt_Senha = findViewById(R.id.edt_Senha);
        progressBar = findViewById(R.id.progressBar);
    }

    private void ocultarTeclado(){
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                edt_Email.getWindowToken(), 0
        );
    }

}