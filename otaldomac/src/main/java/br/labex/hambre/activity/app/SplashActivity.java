package br.labex.hambre.activity.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import br.labex.hambre.DAO.ItemPedidoDAO;
import br.labex.hambre.R;
import br.labex.hambre.activity.empresa.EmpresaFinalizaCadastroActivity;
import br.labex.hambre.activity.empresa.EmpresaHomeActivity;
import br.labex.hambre.activity.usuario.UsuarioFinalizaCadastroActivity;
import br.labex.hambre.activity.usuario.UsuarioHomeActivity;
import br.labex.hambre.helper.FirebaseHelper;
import br.labex.hambre.model.Empresa;
import br.labex.hambre.model.Login;
import br.labex.hambre.model.Usuario;

public class SplashActivity extends AppCompatActivity {

    int tempoDeEspera = 1000 * 2;


    private Login login;
    private Usuario usuario;
    private Empresa empresa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        limparSQLite();

        verificaAutenticacao();

    }
    private void limparSQLite(){
        ItemPedidoDAO itemPedidoDAO = new ItemPedidoDAO(getBaseContext());
        itemPedidoDAO.limparCarrinho();
    }

    private void verificaAutenticacao(){
        if(FirebaseHelper.getAutenticado()){

            verificaCadastro();

        }else{
            finish();
            startActivity(new Intent(this, UsuarioHomeActivity.class));
        }
    }

    private void verificaCadastro(){
        DatabaseReference loginRef = FirebaseHelper.getDatabaseReference()
                .child("login")
                .child(FirebaseHelper.getIdFirebase());
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

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(login != null){

                    if(login.getTipo().equals("U")){
                        if (login.getAcesso()){
                            finish();
                            startActivity(new Intent(getBaseContext(), UsuarioHomeActivity.class));

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
        }, tempoDeEspera);
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

}