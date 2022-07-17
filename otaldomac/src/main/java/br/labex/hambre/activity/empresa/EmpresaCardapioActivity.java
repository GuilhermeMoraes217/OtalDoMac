package br.labex.hambre.activity.empresa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import br.labex.hambre.R;
import br.labex.hambre.activity.autenticacao.LoginActivity;
import br.labex.hambre.adapter.CardapioAdapter;
import br.labex.hambre.helper.FirebaseHelper;
import br.labex.hambre.helper.GetMask;
import br.labex.hambre.model.Categoria;
import br.labex.hambre.model.CategoriaCardapio;
import br.labex.hambre.model.Empresa;
import br.labex.hambre.model.Favorito;
import br.labex.hambre.model.Produto;

public class EmpresaCardapioActivity extends AppCompatActivity {

    private CardapioAdapter cardapioAdapter;

    private List<Produto> produtoList = new ArrayList<>();
    private List<Categoria> categoriaList = new ArrayList<>();
    private List<String> idsCategoriaList = new ArrayList<>();
    private List<CategoriaCardapio> categoriaCardapioList = new ArrayList<>();

    private final List<String> favoritoList = new ArrayList<>();
    private final Favorito favorito = new Favorito();

    private ImageView img_logo_empresa;
    private ImageView img_logo_empresa2;
    private TextView text_empresa;
    private TextView text_categoria_empresa;
    private TextView text_tempo_minimo;
    private TextView text_tempo_maximo;
    private TextView text_taxa_entrega;
    private LikeButton btn_like;

    private RecyclerView rv_categorias;
    private ProgressBar progressBar;
    private TextView text_info;

    private Empresa empresa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa_cardapio);

        iniciaComponentes();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            empresa = (Empresa) bundle.getSerializable("empresaSelecionada");
            configDados();
        }

        configRv();

        configCliques();

        recuperaFavorito();

        recuperaProdutos();
    }

    private void configRv(){
        rv_categorias.setLayoutManager(new LinearLayoutManager(this));
        rv_categorias.setHasFixedSize(true);
        cardapioAdapter = new CardapioAdapter(categoriaCardapioList, this);
        rv_categorias.setAdapter(cardapioAdapter);
    }

    private void recuperaProdutos(){
        DatabaseReference produtosRef = FirebaseHelper.getDatabaseReference()
                .child("produtos")
                .child(empresa.getId());
        produtosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    produtoList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Produto produto = ds.getValue(Produto.class);
                        produtoList.add(produto);
                        configListCategoria(produto);
                    }

                    if(!idsCategoriaList.isEmpty()){
                        recuperaCategorias();
                    }

                    text_info.setText("");
                }else {
                    progressBar.setVisibility(View.GONE);
                    text_info.setText("Nenhum produto cadastrado.");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void configListCategoria(Produto produto) {
        boolean contem = false;
        for (String idCategoria : idsCategoriaList){
            if (idCategoria.equals(produto.getIdCategoria())) {
                contem = true;
                break;
            }
        }
        if(!contem) idsCategoriaList.add(produto.getIdCategoria());
    }

    private void recuperaCategorias(){

        //throw new RuntimeException("Test Crash"); // Force a crash
        for (String idCategoria : idsCategoriaList){
            DatabaseReference categoriasRef = FirebaseHelper.getDatabaseReference()
                    .child("categorias")
                    .child(empresa.getId())
                    .child(idCategoria);
            categoriasRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Categoria categoria = snapshot.getValue(Categoria.class);
                    categoriaList.add(categoria);

                    if(categoriaList.size() == idsCategoriaList.size()){
                        produtoPorCategoria();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


    }

    private void produtoPorCategoria(){
        List<Produto> produtoListTemp = new ArrayList<>();

        for (Categoria categoria : categoriaList){
            for(Produto produto : produtoList){
                if(categoria.getId().equals(produto.getIdCategoria())){
                    produtoListTemp.add(produto);
                }
            }

            categoriaCardapioList.add(new CategoriaCardapio(categoria.getNome(), produtoListTemp));

            cardapioAdapter.notifyDataSetChanged();

            progressBar.setVisibility(View.GONE);

            produtoListTemp = new ArrayList<>();
        }
    }

    private void configDados(){
        Picasso.get().load(empresa.getUrlLogo()).into(img_logo_empresa);
        Picasso.get().load(empresa.getUrlLogo()).into(img_logo_empresa2);

        text_empresa.setText(empresa.getNome());
        text_categoria_empresa.setText(empresa.getCategoria());
        text_tempo_minimo.setText(empresa.getTempMinEntrega() + "-");
        text_tempo_maximo.setText(empresa.getTempMaxEntrega() + " min");

        if(empresa.getTaxaEntrega() > 0){
            text_taxa_entrega.setText(getString(R.string.text_Valor, GetMask.getValor(empresa.getTaxaEntrega())));
        }else {
            text_taxa_entrega.setTextColor(Color.parseColor("#2ED67E"));
            text_taxa_entrega.setText("ENTREGA GRÁTIS");
        }
    }

    private void recuperaFavorito(){
        if(FirebaseHelper.getAutenticado()){
            DatabaseReference favoritosRef = FirebaseHelper.getDatabaseReference()
                    .child("favoritos")
                    .child(FirebaseHelper.getIdFirebase());
            favoritosRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        for (DataSnapshot ds : snapshot.getChildren()){
                            String idFavorito = ds.getValue(String.class);
                            favoritoList.add(idFavorito);
                        }

                        if(favoritoList.contains(empresa.getId())){
                            btn_like.setLiked(true);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void configCliques(){
        findViewById(R.id.ib_Voltar).setOnClickListener(v -> finish());

        btn_like.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                configFavorito();
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                configFavorito();
            }
        });
    }

    private void configFavorito(){
        if(FirebaseHelper.getAutenticado()){
            if(!favoritoList.contains(empresa.getId())){
                favoritoList.add(empresa.getId());
            }else {
                favoritoList.remove(empresa.getId());
            }

            favorito.setFavoritoList(favoritoList);
            favorito.salvar();
        }else {
            btn_like.setLiked(false);
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    private void iniciaComponentes(){
        TextView text_toolbar = findViewById(R.id.txt_Toobar);
        text_toolbar.setText("Cardápio");

        img_logo_empresa = findViewById(R.id.img_logo_empresa);
        img_logo_empresa2 = findViewById(R.id.img_logo_empresa2);
        text_empresa = findViewById(R.id.text_empresa);
        text_categoria_empresa = findViewById(R.id.text_categoria_empresa);
        text_tempo_minimo = findViewById(R.id.text_tempo_minimo);
        text_tempo_maximo = findViewById(R.id.text_tempo_maximo);
        text_taxa_entrega = findViewById(R.id.text_taxa_entrega);
        btn_like = findViewById(R.id.btn_Like);
        rv_categorias = findViewById(R.id.rv_categorias);
        progressBar = findViewById(R.id.progressBar);
        text_info = findViewById(R.id.text_info);
    }

}