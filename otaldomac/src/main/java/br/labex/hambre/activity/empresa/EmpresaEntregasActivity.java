package br.labex.hambre.activity.empresa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.labex.hambre.R;
import br.labex.hambre.helper.FirebaseHelper;
import br.labex.hambre.model.Entrega;

public class EmpresaEntregasActivity extends AppCompatActivity {

    private final List<Entrega> entregaList = new ArrayList<>();

    private Entrega domicilio = new Entrega();
    private Entrega retirada = new Entrega();
    private Entrega outros = new Entrega();

    private CheckBox cb_Domicilio;
    private CurrencyEditText edt_Domicilio;

    private CheckBox cb_Retirada;
    private CurrencyEditText edt_Retirada;

    private CheckBox cb_Outras;
    private CurrencyEditText edt_Outros;

    private ImageButton ib_Salvar;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa_entregas);
        iniciaComponentes();

        recuperaEntregas();

        configCliques();
    }

    private void iniciaComponentes(){
        TextView toolBar = findViewById(R.id.txt_Toobar);
        toolBar.setText("Entregas");

        cb_Domicilio = findViewById(R.id.cb_Domicilio);
        edt_Domicilio = findViewById(R.id.edt_Domicilio);
        edt_Domicilio.setTextLocale(new Locale("PT", "br"));

        cb_Retirada = findViewById(R.id.cb_Retirada);
        edt_Retirada = findViewById(R.id.edt_Retirada);
        edt_Retirada.setTextLocale(new Locale("PT", "br"));

        cb_Outras = findViewById(R.id.cb_Outras);
        edt_Outros = findViewById(R.id.edt_Outros);
        edt_Outros.setTextLocale(new Locale("PT", "br"));

        ib_Salvar = findViewById(R.id.ib_Salvar);
        progressBar = findViewById(R.id.progressBar);
    }


    private void configEntregas(Entrega entrega){
        switch (entrega.getDescricao()){
            case "Domicílio":
                domicilio = entrega;
                edt_Domicilio.setText(String.valueOf(domicilio.getTaxa() * 10));
                cb_Domicilio.setChecked(domicilio.getStatus());
                break;
            case "Retirada":
                retirada = entrega;
                edt_Retirada.setText(String.valueOf(retirada.getTaxa() * 10));
                cb_Retirada.setChecked(retirada.getStatus());
                break;
            case "Outros":
                outros = entrega;
                edt_Outros.setText(String.valueOf(outros.getTaxa() * 10));
                cb_Outras.setChecked(outros.getStatus());
                break;
        }

        configSalvar(false);
    }

    private void configCliques(){
        ib_Salvar.setOnClickListener(v -> validaEntregas());
        findViewById(R.id.ib_Voltar).setOnClickListener(v -> finish());
    }

    private void configSalvar(boolean progress){
        if(progress){
            progressBar.setVisibility(View.VISIBLE);
            ib_Salvar.setVisibility(View.GONE);
        }else {
            progressBar.setVisibility(View.GONE);
            ib_Salvar.setVisibility(View.VISIBLE);
        }
    }

    private void recuperaEntregas(){
        DatabaseReference entregasRef = FirebaseHelper.getDatabaseReference()
                .child("entregas")
                .child(FirebaseHelper.getIdFirebase());
        entregasRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    entregaList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Entrega entrega = ds.getValue(Entrega.class);
                        configEntregas(entrega);
                    }
                }else {
                    configSalvar(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void validaEntregas(){

        entregaList.clear();
        configSalvar(true);

        domicilio.setStatus(cb_Domicilio.isChecked());
        domicilio.setTaxa((double) edt_Domicilio.getRawValue() / 100);
        domicilio.setDescricao("Domicílio");

        retirada.setStatus(cb_Retirada.isChecked());
        retirada.setTaxa((double) edt_Retirada.getRawValue() / 100);
        retirada.setDescricao("Retirada");

        outros.setStatus(cb_Outras.isChecked());
        outros.setTaxa((double) edt_Outros.getRawValue() / 100);
        outros.setDescricao("Outros");

        entregaList.add(domicilio);
        entregaList.add(retirada);
        entregaList.add(outros);

        Entrega.salvar(entregaList);
        configSalvar(false);
        Toast.makeText(this, "Dados salvos", Toast.LENGTH_SHORT).show();

    }
}