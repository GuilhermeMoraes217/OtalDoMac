package br.labex.hambre.activity.empresa;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import br.labex.hambre.R;
import br.labex.hambre.helper.FirebaseHelper;
import br.labex.hambre.model.Categoria;
import br.labex.hambre.model.Produto;

public class EmpresaFormProdutosActivity extends AppCompatActivity {
    private final int REQUEST_CATEGORIA = 100;
    private final int REQUEST_GALERIA = 200;
    private String caminhoImagem;

    private ImageView img_Produto;
    private EditText edt_NomeProduto;
    private CurrencyEditText edt_ValorProduto;
    private CurrencyEditText edt_ValorProdutoAntigo;
    private Button btn_Categoria;
    private EditText edt_Descricao;
    private LinearLayout l_edt_Descricao;

    private Categoria categoriaSelecionada = null;
    private Produto produto;
    private Boolean novoProduto = true;
    private TextView text_ToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa_form_produtos);

        iniciaComponentes();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            produto = (Produto) bundle.getSerializable("produtoSelecionado");
            configDados();
        }

        configCliques();
    }
    private void iniciaComponentes(){

        text_ToolBar = findViewById(R.id.txt_Toobar);
        text_ToolBar.setText("Novo produto");

        img_Produto = findViewById(R.id.img_Produto);
        edt_NomeProduto = findViewById(R.id.edt_NomeProduto);

        edt_ValorProduto = findViewById(R.id.edt_ValorProduto);
        edt_ValorProduto.setLocale(new Locale("PT","br"));

        edt_ValorProdutoAntigo = findViewById(R.id.edt_ValorProdutoAntigo);
        edt_ValorProdutoAntigo.setLocale(new Locale("PT","br"));


        btn_Categoria = findViewById(R.id.btn_Categoria);
        edt_Descricao = findViewById(R.id.edt_Descricao);
        l_edt_Descricao = findViewById(R.id.l_edt_Descricao);
    }

    private void configDados (){
        Picasso.get().load(produto.getUrlImagem()).into(img_Produto);
        edt_NomeProduto.setText(produto.getNome());
        edt_ValorProduto.setText(String.valueOf(produto.getValor()));
        edt_ValorProdutoAntigo.setText(String.valueOf(produto.getValorAntigo()));
        edt_Descricao.setText(produto.getDescricao());


        recuperaCategoria();
        novoProduto = false;
        text_ToolBar.setText("Editar produto");
    }

    private void recuperaCategoria(){
        DatabaseReference categoriasRef = FirebaseHelper.getDatabaseReference()
                .child("categorias")
                .child(FirebaseHelper.getIdFirebase())
                .child(produto.getIdCategoria());
        categoriasRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Categoria categoria = snapshot.getValue(Categoria.class);
                if(categoria != null){
                    btn_Categoria.setText(categoria.getNome());
                    categoriaSelecionada = categoria;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void configCliques (){
        findViewById(R.id.ib_Voltar).setOnClickListener(view -> finish());

        btn_Categoria.setOnClickListener(view -> {
            Intent intent = new Intent(this, EmpresaCategoriaActivity.class);
            intent.putExtra("acesso", 1);
            startActivityForResult(intent, REQUEST_CATEGORIA);
        });

        l_edt_Descricao.setOnClickListener(view -> {
            mostrarTeclado();
            edt_Descricao.requestFocus();

        });

        img_Produto.setOnClickListener(view -> verificaPermissaoGaleria());
    }

    public void validaDados (View view){

        String nome = edt_NomeProduto.getText().toString().trim();
        double valor = (double) edt_ValorProduto.getRawValue() / 100;
        double valorAntigo = (double) edt_ValorProdutoAntigo.getRawValue() / 100;
        String descricao = edt_Descricao.getText().toString().trim();

        if(!nome.isEmpty()){
            if(valor > 0){
                if (categoriaSelecionada!=null){

                    if (!descricao.isEmpty()){

                        ocultarTeclado();

                        if(produto ==null) produto = new Produto();

                        produto.setIdEmpresa(FirebaseHelper.getIdFirebase());

                        produto.setNome(nome);
                        produto.setValor(valor);
                        produto.setValorAntigo(valorAntigo);
                        produto.setIdCategoria(categoriaSelecionada.getId());
                        produto.setDescricao(descricao);

                        if (novoProduto){
                            if (caminhoImagem!=null){
                                salvarImagemFireBase();
                            }else {
                                ocultarTeclado();
                                Snackbar.make(
                                        img_Produto,
                                        "Selecione uma imagem.",
                                        Snackbar.LENGTH_SHORT
                                ).show();
                            }
                        }else{
                            if(caminhoImagem!=null){
                                salvarImagemFireBase();
                            }else {
                                produto.salvar();
                                Toast.makeText(this, "Produto Salvo", Toast.LENGTH_SHORT).show();
                                finish(); // AUTERADOOOOOOOOOO -------------------------------------
                            }
                        }

                    }else {
                        edt_Descricao.requestFocus();
                        edt_Descricao.setError("Informe uma descrição.");
                    }

                }else {
                    ocultarTeclado();
                    erroSalvarProduto("Selecione uma categoria para o produto.");
                }

            }else{
                edt_ValorProduto.requestFocus();
                edt_ValorProduto.setError("Informe um valor válido.");
            }
        }else{

            edt_NomeProduto.requestFocus();
            edt_NomeProduto.setError("Informe um nome.");
        }

    }

    private void erroSalvarProduto(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Atenção");
        builder.setMessage(msg);
        builder.setPositiveButton("OK", ((dialog, which) -> {
            dialog.dismiss();
        }));

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void salvarImagemFireBase (){

        StorageReference storageReference = FirebaseHelper.getStorageReference()
                .child("imagens")
                .child("produtos")
                .child(FirebaseHelper.getIdFirebase())
                .child(produto.getId() + ".JPEG");

        UploadTask uploadTask = storageReference.putFile(Uri.parse(caminhoImagem));
        uploadTask.addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnCompleteListener(task -> {

            produto.setUrlImagem(task.getResult().toString());
            produto.salvar();
            if (novoProduto){
                finish();
            }

        })).addOnFailureListener(e -> erroSalvarProduto(e.getMessage()));

    }

    public void abrirGaleria (){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_GALERIA);
    }

    public void verificaPermissaoGaleria(){
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                abrirGaleria();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(getBaseContext(),
                        "Permissão negada.",
                        Toast.LENGTH_LONG).show();
            }

        };

        TedPermission.create()
                .setPermissionListener(permissionlistener)
                .setDeniedTitle("Permissão negada.")
                .setDeniedMessage("Se você não aceitar a permissão não poderá acessar a Galeria do dispositivo, deseja ativar a permissão agora ?")
                .setDeniedCloseButtonText("Não")
                .setGotoSettingButtonText("Sim")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();    }

    private void ocultarTeclado(){
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                edt_NomeProduto.getWindowToken(), 0
        );
    }

    private void mostrarTeclado (){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if (requestCode == REQUEST_CATEGORIA){
                categoriaSelecionada = (Categoria) data.getSerializableExtra("categoriaSelecionada");
                btn_Categoria.setText(categoriaSelecionada.getNome());
            }else if (requestCode==REQUEST_GALERIA){

                Bitmap bitmap;

                Uri imagemSelecionada = data.getData();
                caminhoImagem = data.getData().toString();

                if(Build.VERSION.SDK_INT < 28){
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagemSelecionada);
                        img_Produto.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else {
                    ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), imagemSelecionada);
                    try {
                        bitmap = ImageDecoder.decodeBitmap(source);
                        img_Produto.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

        }

    }
}