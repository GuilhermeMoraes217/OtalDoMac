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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.santalu.maskara.widget.MaskEditText;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import br.labex.hambre.R;
import br.labex.hambre.helper.FirebaseHelper;
import br.labex.hambre.model.Empresa;

public class EmpresaConfigActivity extends AppCompatActivity {


    private final int REQUEST_GALERIA = 100;
    private Empresa empresa;
    private ImageView img_logo;
    private EditText edt_Nome;
    private MaskEditText edt_Telefone;
    private CurrencyEditText edt_TaxaEntrega;
    private CurrencyEditText edt_PedidoMinimo;
    private EditText edt_TempoMinimo;
    private EditText edt_TempoMaximo;
    private EditText edt_Categoria;
    private ProgressBar progressBar;
    private ImageButton ib_Salvar;

    private String caminhoLogo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa_config);
        iniciaComponetes();

        recuperaEmpresa();
        configCliques();
    }
    private void configCliques (){
        findViewById(R.id.ib_Voltar).setOnClickListener(view -> finish());
        ib_Salvar.setOnClickListener(view -> validaDadosEmpresa());
    }

    private void recuperaEmpresa(){
        DatabaseReference empresaRef = FirebaseHelper.getDatabaseReference()
                .child("empresas")
                .child(FirebaseHelper.getIdFirebase());
        empresaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                empresa = snapshot.getValue(Empresa.class);
                configDados();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void configDados(){
        Picasso.get().load(empresa.getUrlLogo()).into(img_logo);
        edt_Nome.setText(empresa.getNome());
        edt_Telefone.setText(empresa.getTelefone());
        edt_TaxaEntrega.setText(String.valueOf(empresa.getTaxaEntrega()));
        edt_PedidoMinimo.setText(String.valueOf(empresa.getPedidoMinimo()));
        edt_TempoMinimo.setText(String.valueOf(empresa.getTempMinEntrega()));
        edt_TempoMaximo.setText(String.valueOf(empresa.getTempMaxEntrega()));
        edt_Categoria.setText(empresa.getCategoria());

        configSalvar(false);

    }

    private void configSalvar (Boolean progress){
        if (progress){
            progressBar.setVisibility(View.VISIBLE);
            ib_Salvar.setVisibility(View.GONE);
        }else {
            progressBar.setVisibility(View.GONE);
            ib_Salvar.setVisibility(View.VISIBLE);
        }
    }

    public void selecionaLogo (View view){
        verificaPermissaoGaleria();

    }

    private void validaDadosEmpresa (){

        String nome = edt_Nome.getText().toString().trim();
        String telefone = edt_Telefone.getUnMasked();

        double taxaEntrega = (double) edt_TaxaEntrega.getRawValue()/100;
        double pedidoMinimo = (double) edt_PedidoMinimo.getRawValue()/100;

        int tempoMinimo = 0;
        if(!edt_TempoMinimo.getText().toString().isEmpty())  tempoMinimo = Integer.parseInt(edt_TempoMinimo.getText().toString());

        int tempoMaximo = 0;
        if(!edt_TempoMaximo.getText().toString().isEmpty())  tempoMaximo = Integer.parseInt(edt_TempoMaximo.getText().toString());

        String categoria = edt_Categoria.getText().toString().trim();

        if(!nome.isEmpty()){
            if(edt_Telefone.isDone()){
                if (tempoMinimo > 0){
                    if (tempoMaximo > 0){
                        if(!categoria.isEmpty()){

                            ocultarTeclado();

                            configSalvar(true);

                            empresa.setNome(nome);
                            empresa.setTelefone(telefone);
                            empresa.setTaxaEntrega(taxaEntrega);
                            empresa.setPedidoMinimo(pedidoMinimo);
                            empresa.setTempMinEntrega(tempoMinimo);
                            empresa.setTempMaxEntrega(tempoMaximo);
                            empresa.setCategoria(categoria);

                            if(caminhoLogo!=null){
                                salvarImagemFireBase();

                            }else{
                                empresa.salvar();

                                configSalvar(false);
                                Toast.makeText(this, "Dados salvos com sucesso!", Toast.LENGTH_SHORT).show();
                            }

                        }else{
                            edt_Categoria.requestFocus();
                            edt_Categoria.setError("Informe a categoria da empresa.");
                        }

                    }else{
                        edt_TempoMaximo.requestFocus();
                        edt_TempoMaximo.setError("Adicione o tempo máximo de entrega.");
                    }
                }else{
                    edt_TempoMinimo.requestFocus();
                    edt_TempoMinimo.setError("Adicione o tempo mínimo de entrega.");
                }

            }else{
                edt_Telefone.requestFocus();
                edt_Telefone.setError("Informe o telefone para seu cadastro.");
            }

        }else{
            edt_Nome.requestFocus();
            edt_Nome.setError("Informe um nome para seu cadastro.");
        }
    }

    private void iniciaComponetes(){
        TextView text_ToolBar = findViewById(R.id.txt_Toobar);
        text_ToolBar.setText("Dados da empresa");

        img_logo = findViewById(R.id.img_Logo);
        edt_Nome = findViewById(R.id.edt_Nome);
        edt_Telefone = findViewById(R.id.edt_Telefone);

        edt_TaxaEntrega = findViewById(R.id.edt_TaxaEntrega);
        edt_TaxaEntrega.setLocale(new Locale("PT", "br"));
        edt_PedidoMinimo = findViewById(R.id.edt_PedidoMinimo);
        edt_PedidoMinimo.setLocale(new Locale("PT", "br"));

        edt_TempoMinimo = findViewById(R.id.edt_TempoMinimo);
        edt_TempoMaximo = findViewById(R.id.edt_TempoMaximo);
        edt_Categoria = findViewById(R.id.edt_Categoria);
        progressBar = findViewById(R.id.progressBar);
        ib_Salvar = findViewById(R.id.ib_Salvar);
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

    public void salvarImagemFireBase (){

        StorageReference storageReference = FirebaseHelper.getStorageReference()
                .child("imagens")
                .child("perfil")
                .child(empresa.getId() + ".JPEG");

        UploadTask uploadTask = storageReference.putFile(Uri.parse(caminhoLogo));
        uploadTask.addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnCompleteListener(task -> {

            empresa.setUrlLogo(task.getResult().toString());
            empresa.salvar();

            configSalvar(false);
            Toast.makeText(this, "Dados salvos com sucesso!", Toast.LENGTH_SHORT).show();

        })).addOnFailureListener(e -> {

            configSalvar(false);
            erroSalvarDados(e.getMessage());
        });

    }

    private void erroSalvarDados(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Atenção");
        builder.setMessage(msg);
        builder.setPositiveButton("OK", ((dialog, which) -> {
            dialog.dismiss();
        }));

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void ocultarTeclado(){
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                edt_Nome.getWindowToken(), 0
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == REQUEST_GALERIA){
                Bitmap bitmap;

                Uri imagemSelecionada = data.getData();
                caminhoLogo = data.getData().toString();

                if(Build.VERSION.SDK_INT < 28){
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagemSelecionada);
                        img_logo.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else {
                    ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), imagemSelecionada);
                    try {
                        bitmap = ImageDecoder.decodeBitmap(source);
                        img_logo.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


            }
        }
    }

}