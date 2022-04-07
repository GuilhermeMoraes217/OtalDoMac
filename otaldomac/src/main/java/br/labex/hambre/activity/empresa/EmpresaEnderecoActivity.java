package br.labex.hambre.activity.empresa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.labex.hambre.R;
import br.labex.hambre.helper.FirebaseHelper;
import br.labex.hambre.model.Endereco;

public class EmpresaEnderecoActivity extends AppCompatActivity {

    private EditText edt_logradouroEmpresa;
    private EditText edt_bairroEmpresa;
    private EditText edt_municipioEmpresa;

    private ImageButton ib_salvar;
    private ProgressBar progressBar;

    private Endereco endereco;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa_endereco);

        iniciaComponentes();

        configCliques();

        recuperaEndereco();
    }

    private void recuperaEndereco(){
        DatabaseReference enderecoRef = FirebaseHelper.getDatabaseReference()
                .child("enderecos")
                .child(FirebaseHelper.getIdFirebase());
        enderecoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot ds : snapshot.getChildren()){
                        endereco = ds.getValue(Endereco.class);
                    }
                    configDados();
                }else {
                    configSalvar(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void configSalvar(boolean progress){
        if(progress){
            progressBar.setVisibility(View.VISIBLE);
            ib_salvar.setVisibility(View.GONE);
        }else {
            progressBar.setVisibility(View.GONE);
            ib_salvar.setVisibility(View.VISIBLE);
        }
    }

    private void configDados(){
        edt_logradouroEmpresa.setText(endereco.getLogradouro());
        edt_bairroEmpresa.setText(endereco.getBairro());
        edt_municipioEmpresa.setText(endereco.getMunicipio());

        configSalvar(false);
    }

    private void validaDados() {
        String logradouro = edt_logradouroEmpresa.getText().toString().trim();
        String bairro = edt_bairroEmpresa.getText().toString().trim();
        String municipio = edt_municipioEmpresa.getText().toString().trim();

        if (!logradouro.isEmpty()) {
            if (!bairro.isEmpty()) {
                if (!municipio.isEmpty()) {

                    configSalvar(true);

                    if (endereco == null) endereco = new Endereco();
                    endereco.setLogradouro(logradouro);
                    endereco.setBairro(bairro);
                    endereco.setMunicipio(municipio);
                    endereco.salvar();
                    ocultarTeclado();
                    configSalvar(false);
                    Toast.makeText(this, "Endereço salvo.", Toast.LENGTH_SHORT).show();

                } else {
                    edt_municipioEmpresa.requestFocus();
                    edt_municipioEmpresa.setError("Informe o município.");
                }
            } else {
                edt_bairroEmpresa.requestFocus();
                edt_bairroEmpresa.setError("Informe o bairro.");
            }
        } else {
            edt_logradouroEmpresa.requestFocus();
            edt_logradouroEmpresa.setError("Informe o endereço");
        }

    }

    private void configCliques() {
        findViewById(R.id.ib_Voltar).setOnClickListener(v -> finish());
        findViewById(R.id.ib_Salvar).setOnClickListener(v -> validaDados());
    }

    private void iniciaComponentes() {
        TextView text_toolbar = findViewById(R.id.txt_Toobar);
        text_toolbar.setText("Meu endereço");

        edt_logradouroEmpresa = findViewById(R.id.edt_logradouroEmpresa);
        edt_bairroEmpresa = findViewById(R.id.edt_bairroEmpresa);
        edt_municipioEmpresa = findViewById(R.id.edt_municipioEmpresa);

        ib_salvar = findViewById(R.id.ib_Salvar);
        progressBar = findViewById(R.id.progressBar);
    }

    private void ocultarTeclado(){
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                ib_salvar.getWindowToken(), 0
        );
    }
}