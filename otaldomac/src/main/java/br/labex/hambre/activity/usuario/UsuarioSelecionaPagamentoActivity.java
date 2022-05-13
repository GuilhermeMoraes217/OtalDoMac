package br.labex.hambre.activity.usuario;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.labex.hambre.DAO.EmpresaDAO;
import br.labex.hambre.R;
import br.labex.hambre.adapter.SelecionaPagamentoAdapter;
import br.labex.hambre.helper.FirebaseHelper;
import br.labex.hambre.model.Pagamento;

public class UsuarioSelecionaPagamentoActivity extends AppCompatActivity implements SelecionaPagamentoAdapter.OnClickListener {private final List<Pagamento> pagamentoList = new ArrayList<>();
    private SelecionaPagamentoAdapter selecionaPagamentoAdapter;

    private RecyclerView rv_pagamentos;
    private ProgressBar progressBar;
    private TextView text_info;

    private EmpresaDAO empresaDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_seleciona_pagamento);

        empresaDAO = new EmpresaDAO(getBaseContext());

        iniciaComponentes();

        configCliques();

        configRv();

        recuperaPagamentos();

    }

    private void configRv(){
        rv_pagamentos.setLayoutManager(new LinearLayoutManager(this));
        rv_pagamentos.setHasFixedSize(true);
        selecionaPagamentoAdapter = new SelecionaPagamentoAdapter(pagamentoList, this);
        rv_pagamentos.setAdapter(selecionaPagamentoAdapter);
    }

    private void recuperaPagamentos(){
        DatabaseReference pagamentosRef = FirebaseHelper.getDatabaseReference()
                .child("recebimentos")
                .child(empresaDAO.getEmpresa().getId());
        Query formaPagamento = pagamentosRef.orderByChild("status").equalTo(true);
        formaPagamento.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot ds : snapshot.getChildren()){

                        Pagamento pagamento = ds.getValue(Pagamento.class);
                        pagamentoList.add(pagamento);
                    }
                    text_info.setText("");
                }else {
                    text_info.setText("Nenhuma forma de pagamento habilitada.");
                }

                progressBar.setVisibility(View.GONE);
                selecionaPagamentoAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void configCliques(){
        findViewById(R.id.ib_Voltar).setOnClickListener(v -> finish());
    }

    private void iniciaComponentes(){
        TextView text_toolbar = findViewById(R.id.txt_Toobar);
        text_toolbar.setText("Formas de pagamento");

        rv_pagamentos = findViewById(R.id.rv_pagamentos);
        progressBar = findViewById(R.id.progressBar);
        text_info = findViewById(R.id.text_info);
    }

    @Override
    public void OnClick(Pagamento pagamento) {
        Intent intent = new Intent();
        intent.putExtra("pagamentoSelecionado", pagamento);
        setResult(RESULT_OK, intent);
        finish();
    }
}