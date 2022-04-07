package br.labex.hambre.activity.usuario;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.santalu.maskara.widget.MaskEditText;

import br.labex.hambre.R;
import br.labex.hambre.model.Login;
import br.labex.hambre.model.Usuario;

public class UsuarioFinalizaCadastroActivity extends AppCompatActivity {


    private EditText edt_Nome;
    private MaskEditText edt_Telefone;
    private ProgressBar progressBar;

    private Usuario usuario;
    private Login login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_finaliza_cadastro);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            usuario = (Usuario) bundle.getSerializable("usuario");
            login = (Login) bundle.getSerializable("login");
        }

        iniciaComponetes();
    }

    public void validaDadosUsuario (View view){
        String nome = edt_Nome.getText().toString().trim();
        String telefone = edt_Telefone.getUnMasked();

        if (!nome.isEmpty()){
            if (!telefone.isEmpty()){
                if(edt_Telefone.isDone()){

                    ocultarTeclado();
                    progressBar.setVisibility(View.VISIBLE);
                    finalizaCadastro(nome, telefone);

                } else{
                    edt_Telefone.requestFocus();
                    edt_Telefone.setError("Telefone inválido.");
                }

            }else{
                edt_Telefone.requestFocus();
                edt_Telefone.setError("Informe seu telefone.");
            }
        }else{
            edt_Nome.requestFocus();
            edt_Nome.setError("Informe seu nome.");
        }

    }

    private void finalizaCadastro (String nome, String telefone){
        login.setAcesso(true);
        login.salvar();

        usuario.setNome(nome);
        usuario.setTelefone(telefone);
        usuario.salvar();

        finish();
        startActivity(new Intent(this, UsuarioHomeActivity.class));
    }

    private void iniciaComponetes (){
        edt_Nome = findViewById(R.id.edt_Nome);
        edt_Telefone = findViewById(R.id.edt_Telefone);
        progressBar = findViewById(R.id.progressBar);
    }

    private void ocultarTeclado(){
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                edt_Nome.getWindowToken(), 0
        );
    }
}