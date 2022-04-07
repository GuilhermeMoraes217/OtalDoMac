package br.labex.hambre.activity.empresa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.tsuryo.swipeablerv.SwipeLeftRightCallback;
import com.tsuryo.swipeablerv.SwipeableRecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import br.labex.hambre.R;
import br.labex.hambre.adapter.CategoriaAdapter;
import br.labex.hambre.helper.FirebaseHelper;
import br.labex.hambre.model.Categoria;

public class EmpresaCategoriaActivity extends AppCompatActivity implements CategoriaAdapter.OnClickListener {

    private CategoriaAdapter categoriaAdapter;
    private List<Categoria> categoriaList = new ArrayList<>();

    private SwipeableRecyclerView rv_Categorias;
    private ProgressBar progressBar;
    private TextView text_Info;
    private AlertDialog alertDialog;

    private Categoria categoriaSelecionada;
    private int categoriaIndex = 0;
    private Boolean novaCategoria = true;
    private int acesso = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa_categoria);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            acesso = bundle.getInt("acesso");
        }

        iniciaComponentes();
        recuperaCategorias();
        configRv();
        configCliques();
    }

    private void configCliques (){
        findViewById(R.id.ib_Voltar).setOnClickListener(view -> finish());
        findViewById(R.id.ib_Add).setOnClickListener(view -> {
            novaCategoria = true;
            showDialog();
        });
    }

    private void configRv(){
        rv_Categorias.setLayoutManager(new LinearLayoutManager(this));
        rv_Categorias.setHasFixedSize(true);
        categoriaAdapter = new CategoriaAdapter(categoriaList, this);
        rv_Categorias.setAdapter(categoriaAdapter);

        rv_Categorias.setListener(new SwipeLeftRightCallback.Listener() {
            @Override
            public void onSwipedLeft(int position) {

            }

            @Override
            public void onSwipedRight(int position) {
                dialogRemoverCategoria(categoriaList.get(position));

            }
        });


    }

    private void recuperaCategorias(){
        DatabaseReference categoriasRef = FirebaseHelper.getDatabaseReference()
                .child("categorias")
                .child(FirebaseHelper.getIdFirebase());
        categoriasRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){

                    for (DataSnapshot ds : snapshot.getChildren()){
                        Categoria categoria = ds.getValue(Categoria.class);
                        categoriaList.add(categoria);
                    }

                    text_Info.setText("");

                }else {
                    text_Info.setText("Nenhuma categoria cadastrada.");
                }

                progressBar.setVisibility(View.GONE);
                Collections.reverse(categoriaList);
                categoriaAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showDialog (){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add_categoria, null);
        builder.setView(view);

        EditText edt_CategoriaEmpresa = view.findViewById(R.id.edt_CategoriaEmpresa);
        Button btn_Fechar = view.findViewById(R.id.btn_Fechar);
        Button btn_Salvar = view.findViewById(R.id.btn_Salvar);

        if (!novaCategoria){
            edt_CategoriaEmpresa.setText(categoriaSelecionada.getNome());
        }


        btn_Salvar.setOnClickListener(v -> {

            String nomeCategoria = edt_CategoriaEmpresa.getText().toString().trim();

            if(!nomeCategoria.isEmpty()){

                if (novaCategoria){
                    Categoria categoria = new Categoria();
                    categoria.setNome(nomeCategoria);
                    categoria.salvar();

                    categoriaList.add(categoria);

                }else{

                    categoriaSelecionada.setNome(nomeCategoria);
                    categoriaList.set(categoriaIndex, categoriaSelecionada);
                    categoriaSelecionada.salvar();

                }

                if(!categoriaList.isEmpty()){
                    text_Info.setText("");
                }

                alertDialog.dismiss();
                categoriaAdapter.notifyDataSetChanged();




            }else{
                edt_CategoriaEmpresa.requestFocus();
                edt_CategoriaEmpresa.setError("Informe um nome.");
            }

        });

        btn_Fechar.setOnClickListener(view1 -> alertDialog.dismiss());

        alertDialog = builder.create();
        alertDialog.show();
    }

    private void dialogRemoverCategoria(Categoria categoria){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Remover categoria");
        builder.setMessage("Deseja remover a categoria selecionada ?");
        builder.setNegativeButton("Não", ((dialog, which) -> {
            dialog.dismiss();
            categoriaAdapter.notifyDataSetChanged();
        }));

        builder.setPositiveButton("Sim", ((dialog, which) -> {
            categoria.remover();
            categoriaList.remove(categoria);

            if(categoriaList.isEmpty()){
                text_Info.setText("Nenhuma categoria cadastrada.");
            }

            categoriaAdapter.notifyDataSetChanged();
            dialog.dismiss();
        }));



        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void iniciaComponentes (){

        TextView text_toolbar = findViewById(R.id.txt_Toobar);
        text_toolbar.setText("Categorias");

        rv_Categorias = findViewById(R.id.rv_Categorias);
        progressBar = findViewById(R.id.progressBar);
        text_Info = findViewById(R.id.text_Info);
    }

    @Override
    public void OnClick(Categoria categoria, int position) {
        if (acesso == 0){
            categoriaSelecionada = categoria;
            categoriaIndex = position;
            novaCategoria = false;

            showDialog();
        } else if(acesso ==1){
            Intent intent = new Intent();
            intent.putExtra("categoriaSelecionada", categoria);
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}