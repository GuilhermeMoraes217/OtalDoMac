package br.labex.hambre.activity.empresa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.tsuryo.swipeablerv.SwipeLeftRightCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.labex.hambre.R;
import br.labex.hambre.adapter.AddMaisAdapter;
import br.labex.hambre.adapter.ProdutoAdapterEmpresa;
import br.labex.hambre.fragment.empresa.AddMais;
import br.labex.hambre.helper.FirebaseHelper;
import br.labex.hambre.model.Produto;

public class EmpresaAddMaisActivity extends AppCompatActivity implements AddMaisAdapter.OnClickListener {

    private List<Produto> produtoList = new ArrayList<>();
    private List<String> addMaisList = new ArrayList<>();
    private AddMaisAdapter addMaisAdapter;

    private RecyclerView rv_Produtos;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa_add_mais);
        iniciaComponentes();

        configRv();

        recuperaProdutos();

        configCliques();

    }

    private void configCliques (){
        findViewById(R.id.ib_Voltar).setOnClickListener(view -> finish());

    }

    private void configRv(){
        rv_Produtos.setLayoutManager(new LinearLayoutManager(this));
        rv_Produtos.setHasFixedSize(true);
        addMaisAdapter = new AddMaisAdapter(produtoList, addMaisList, this, this);
        rv_Produtos.setAdapter(addMaisAdapter);

    }

    private void recuperaProdutos(){

        DatabaseReference produtosRef = FirebaseHelper.getDatabaseReference()
                .child("produtos")
                .child(FirebaseHelper.getIdFirebase());
        produtosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    produtoList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Produto produto = ds.getValue(Produto.class);
                        produtoList.add(produto);
                    }

                    recuperaItens();
                }

                progressBar.setVisibility(View.GONE);
                Collections.reverse(produtoList);
                addMaisAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void recuperaItens(){
        DatabaseReference addMaisRef = FirebaseHelper.getDatabaseReference()
                .child("addMais")
                .child(FirebaseHelper.getIdFirebase());
        addMaisRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot ds : snapshot.getChildren()){
                        String idproduto = ds.getValue(String.class);
                        addMaisList.add(idproduto);

                    }

                    configProdutos();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void configProdutos(){
        for (Produto produto : produtoList){
            if (addMaisList.contains(produto.getId())){
                produto.setAddMais(true);
            }

        }

        addMaisAdapter.notifyDataSetChanged();
    }

    private void iniciaComponentes (){
        TextView textToolsBar = findViewById(R.id.txt_Toobar);
        textToolsBar.setText("Adicione ao carrinho");
        rv_Produtos = findViewById(R.id.rv_Produtos);
        progressBar = findViewById(R.id.progressBar);
    }

    @Override
    public void OnClick(String idProduto, Boolean status) {
        if (status){
            if (!addMaisList.contains(idProduto)) addMaisList.add(idProduto);

        }else {
            addMaisList.remove(idProduto);
        }
        AddMais.salvar(addMaisList);
    }
}