package br.labex.hambre.activity.usuario;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.labex.hambre.DAO.EmpresaDAO;
import br.labex.hambre.DAO.EntregaDAO;
import br.labex.hambre.DAO.ItemPedidoDAO;
import br.labex.hambre.R;
import br.labex.hambre.activity.autenticacao.LoginActivity;
import br.labex.hambre.adapter.CarrinhoAdapter;
import br.labex.hambre.adapter.ProdutoCarrinhoAdapter;
import br.labex.hambre.helper.FirebaseHelper;
import br.labex.hambre.helper.GetMask;
import br.labex.hambre.model.Endereco;
import br.labex.hambre.model.ItemPedido;
import br.labex.hambre.model.Pagamento;
import br.labex.hambre.model.Produto;

public class CarrinhoActivity extends AppCompatActivity implements CarrinhoAdapter.OnClickListener, ProdutoCarrinhoAdapter.OnClickListener {

    private final int REQUEST_LOGIN = 100;
    private final int REQUEST_ENDERECO = 200;
    private final int REQUEST_PAGAMENTO = 300;

    private final List<Produto> produtoList = new ArrayList<>();
    private List<ItemPedido> itemPedidoList = new ArrayList<>();

    private CarrinhoAdapter carrinhoAdapter;
    private ProdutoCarrinhoAdapter produtoCarrinhoAdapter;

    private RecyclerView rv_produtos;
    private RecyclerView rv_add_mais;
    private LinearLayout ll_add_mais;
    private LinearLayout ll_btn_add_mais;

    private Endereco endereco;
    private LinearLayout ll_endereco;
    private TextView text_logradouro;
    private TextView text_referencia;

    private String pagamento;
    private TextView text_forma_pagamento;

    private TextView text_subtotal;
    private TextView text_taxa_entrega;
    private TextView text_total;
    private Button btn_add_mais;

    private ItemPedidoDAO itemPedidoDAO;
    private EmpresaDAO empresaDAO;
    private EntregaDAO entregaDAO;

    private Button btn_continuar;
    private Button text_escolher_pagamento;
    private BottomSheetDialog bottomSheetDialog;
    private int quantidade = 0;
    private TextView text_Nome_Produto;
    private TextView textQtdProduto;
    private TextView textAtualizar;
    private TextView textTotalProdutoDialog;
    private ImageButton ibMenos;
    private ImageButton ibAddMais;
    private Produto produto;
    private ItemPedido itemPedido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrinho);

        itemPedidoDAO = new ItemPedidoDAO(getBaseContext());
        itemPedidoList = itemPedidoDAO.getList();
        empresaDAO = new EmpresaDAO(getBaseContext());
        entregaDAO = new EntregaDAO(getBaseContext());

        iniciaComponentes();

        configCliques();

        configRv();

        recuperaIdsItensAddMais();

        recuperaEnderecos();

        configSaldoCarrinho();

        configPagamento();

    }

    private void showBottomSheet() {
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_item_carrinho, null);
        bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomShettDialog);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

        text_Nome_Produto = view.findViewById(R.id.text_Nome_Produto);
        textQtdProduto = view.findViewById(R.id.textQtdProduto);
        textAtualizar = view.findViewById(R.id.textAtualizar);
        textTotalProdutoDialog = view.findViewById(R.id.textTotalProdutoDialog);

        ibMenos = view.findViewById(R.id.ibMenos);
        ibAddMais = view.findViewById(R.id.ibAddMais);

        produto = new Produto();
        produto.setIdLocal(itemPedido.getId());
        produto.setNome(itemPedido.getItem());
        produto.setId(itemPedido.getIdItem());
        produto.setValor(itemPedido.getValor());
        produto.setUrlImagem(itemPedido.getUrlImagem());

        ibAddMais.setOnClickListener(view1 -> addQtdItem());
        ibMenos.setOnClickListener(view1 -> delQtdItem());

        textQtdProduto.setText(String.valueOf(itemPedido.getQuantidade()));
        text_Nome_Produto.setText(produto.getNome());
        textTotalProdutoDialog.setText(getString(R.string.text_Valor, GetMask.getValor(produto.getValor() * itemPedido.getQuantidade())));
        quantidade = itemPedido.getQuantidade();


    }

    private void configValoresDialog() {
        textQtdProduto.setText(String.valueOf(quantidade));
        textTotalProdutoDialog.setText(getString(R.string.text_Valor, GetMask.getValor(produto.getValor() * quantidade)));

    }

    private void addQtdItem() {
        quantidade++;
        if (quantidade == 1) {
            ibMenos.setImageResource(R.drawable.ic_remove_red);
            textTotalProdutoDialog.setVisibility(View.VISIBLE);
            textAtualizar.setText("Atualizar");
        }
        textAtualizar.setOnClickListener(view -> {
            atualizarItem();
        });

        configValoresDialog();
    }

    private void delQtdItem() {
        if (quantidade > 0) {
            quantidade--;
            if (quantidade == 0) { // remover do carrinho
                ibMenos.setImageResource(R.drawable.ic_remove);
                textTotalProdutoDialog.setVisibility(View.GONE);
                textAtualizar.setText("Remover");
                textAtualizar.setGravity(Gravity.CENTER);

                textAtualizar.setOnClickListener(view -> {

                    itemPedidoDAO.remover(itemPedido.getId());
                    itemPedidoList.remove(itemPedido);

                    addMaisList();
                    configSaldoCarrinho();
                    configBtnAddMais();

                    carrinhoAdapter.notifyDataSetChanged();
                    bottomSheetDialog.dismiss();
                });
            } else {
                textAtualizar.setOnClickListener(view -> {
                    atualizarItem();
                });
            }
        }
        configValoresDialog();
    }

    private void configBtnAddMais() {
        if (itemPedidoList.isEmpty()) {
            ll_btn_add_mais.setVisibility(View.GONE);
        } else {
            ll_btn_add_mais.setVisibility(View.VISIBLE);
        }
    }

    private void atualizarItem() {
        itemPedido.setQuantidade(quantidade);
        itemPedidoDAO.atualizar(itemPedido);
        carrinhoAdapter.notifyDataSetChanged();

        configSaldoCarrinho();
        bottomSheetDialog.dismiss();
    }

    private void configSaldoCarrinho() {
        double subTotal = 0;
        double taxaEntrega = 0;
        double total = 0;

        if (!itemPedidoDAO.getList().isEmpty()) {
            subTotal = itemPedidoDAO.getTotal();
            taxaEntrega = empresaDAO.getEmpresa().getTaxaEntrega();
            total = subTotal + taxaEntrega;
        }
        text_subtotal.setText(getString(R.string.text_Valor, GetMask.getValor(subTotal)));
        text_taxa_entrega.setText(getString(R.string.text_Valor, GetMask.getValor(taxaEntrega)));
        text_total.setText(getString(R.string.text_Valor, GetMask.getValor(total)));
    }

    private void recuperaEnderecos() {
        if (FirebaseHelper.getAutenticado() && entregaDAO.getEndereco() == null) {
            DatabaseReference enderecoRef = FirebaseHelper.getDatabaseReference()
                    .child("enderecos")
                    .child(FirebaseHelper.getIdFirebase());
            enderecoRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            endereco = ds.getValue(Endereco.class);
                        }

                        entregaDAO.salvarEndereco(endereco);

                        configEndereco();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            configEndereco();
        }
    }

    private void configEndereco() {
        endereco = entregaDAO.getEndereco();
        if (endereco != null) {
            text_logradouro.setText(endereco.getLogradouro());
            text_referencia.setText(endereco.getReferencia());
            ll_endereco.setVisibility(View.VISIBLE);
        }

        configStatus();
    }

    private void configPagamento() {
        pagamento = entregaDAO.getEntrega().getFormaPagamento();
        if (pagamento != null && !pagamento.isEmpty()) {
            text_forma_pagamento.setText(pagamento);
            configStatus();
        }
    }

    private void configStatus() {
        if (endereco == null) {
            btn_continuar.setText("Selecione o endereço");
        } else {
            if (pagamento == null) {
                btn_continuar.setText("Selecione a forma de pagamento");
            } else {
                btn_continuar.setText("Continuar");
                text_escolher_pagamento.setText("Trocar");
            }
        }
    }

    private void configLayoutAddMais() {
        if (produtoList.isEmpty()) {
            ll_add_mais.setVisibility(View.GONE);
        } else {
            ll_add_mais.setVisibility(View.VISIBLE);
        }
    }

    private void addMaisList(){
        boolean contem = false;
        if (produtoList.size() == 0){
            produtoList.add(produto);
        }else{
            for (Produto prod : produtoList){
                if (prod.getId().equals(produto.getId())){
                    contem = true;
                    break;
                }
            }

            if (!contem){
                produtoList.add(produto);
            }
        }
        configLayoutAddMais();

        produtoCarrinhoAdapter.notifyDataSetChanged();

    }

    private void configCliques() {
        btn_add_mais.setOnClickListener(view -> {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        });
        findViewById(R.id.ib_Voltar).setOnClickListener(v -> finish());
        findViewById(R.id.ib_limpar).setOnClickListener(v -> {
            itemPedidoDAO.limparCarrinho();
            finish();
        });

        btn_continuar.setOnClickListener(v -> continuar());

        ll_endereco.setOnClickListener(v -> {
            Intent intent = new Intent(this, UsuarioSelecionaEnderecoActivity.class);
            startActivityForResult(intent, REQUEST_ENDERECO);
        });

        text_escolher_pagamento.setOnClickListener(v -> {
            Intent intent = new Intent(this, UsuarioSelecionaPagamentoActivity.class);
            startActivityForResult(intent, REQUEST_PAGAMENTO);
        });

    }

    private void continuar() {
        if (FirebaseHelper.getAutenticado()) {
            if (endereco == null) {
                Intent intent = new Intent(this, UsuarioSelecionaEnderecoActivity.class);
                startActivityForResult(intent, REQUEST_ENDERECO);
            } else {
                if (pagamento == null) {
                    Intent intent = new Intent(this, UsuarioSelecionaPagamentoActivity.class);
                    startActivityForResult(intent, REQUEST_PAGAMENTO);
                }else {
                    startActivity(new Intent(this, UsuarioResumoPedidoActivity.class));
                }
            }
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, REQUEST_LOGIN);
        }
    }

    private void recuperaIdsItensAddMais() {
        DatabaseReference addMaisRef = FirebaseHelper.getDatabaseReference()
                .child("addMais")
                .child(empresaDAO.getEmpresa().getId());
        addMaisRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    List<String> idsItensList = new ArrayList<>();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String idProduto = ds.getValue(String.class);
                        idsItensList.add(idProduto);
                    }

                    recuperaProdutos(idsItensList);

                } else {
                    configLayoutAddMais();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void recuperaProdutos(List<String> idsItensList) {
        DatabaseReference produtosRef = FirebaseHelper.getDatabaseReference()
                .child("produtos")
                .child(empresaDAO.getEmpresa().getId());
        produtosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Produto produto = ds.getValue(Produto.class);
                    if (idsItensList.contains(produto.getId())) produtoList.add(produto);
                }

                Collections.reverse(produtoList);
                produtoCarrinhoAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void configRv() {
        rv_produtos.setLayoutManager(new LinearLayoutManager(this));
        rv_produtos.setHasFixedSize(true);
        carrinhoAdapter = new CarrinhoAdapter(itemPedidoList, getBaseContext(), this);
        rv_produtos.setAdapter(carrinhoAdapter);

        rv_add_mais.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rv_add_mais.setHasFixedSize(true);
        produtoCarrinhoAdapter = new ProdutoCarrinhoAdapter(produtoList, getBaseContext(), this);
        rv_add_mais.setAdapter(produtoCarrinhoAdapter);
    }

    private void iniciaComponentes() {
        TextView text_toolbar = findViewById(R.id.txt_Toobar);
        text_toolbar.setText("Sacola");

        rv_produtos = findViewById(R.id.rv_produtos);
        rv_add_mais = findViewById(R.id.rv_add_mais);
        ll_add_mais = findViewById(R.id.ll_add_mais);
        ll_btn_add_mais = findViewById(R.id.ll_btn_add_mais);
        btn_continuar = findViewById(R.id.btn_continuar);
        text_forma_pagamento = findViewById(R.id.text_forma_pagamento);
        text_escolher_pagamento = findViewById(R.id.text_escolher_pagamento);

        ll_endereco = findViewById(R.id.ll_endereco);
        text_logradouro = findViewById(R.id.text_logradouro);
        text_referencia = findViewById(R.id.text_referencia);

        text_subtotal = findViewById(R.id.text_subtotal);
        text_taxa_entrega = findViewById(R.id.text_taxa_entrega);
        text_total = findViewById(R.id.text_total);
        btn_add_mais = findViewById(R.id.btn_add_mais);
    }


    @Override
    public void OnClick(Produto produto) { // Peça mais
        ItemPedido itemPedido = new ItemPedido();
        itemPedido.setQuantidade(1);
        itemPedido.setItem(produto.getNome());
        itemPedido.setIdItem(produto.getId());
        itemPedido.setValor(produto.getValor());
        itemPedido.setUrlImagem(produto.getUrlImagem());


        itemPedido.setId(itemPedidoDAO.salvar(itemPedido)); // RECUPERANDO O ID DO SQLite ATRAVES DE UM ID LONG, que faz com que ele possa ser removido do banco de dados local

        itemPedidoList.add(itemPedido);

        carrinhoAdapter.notifyDataSetChanged(); // PRODUTOS QUE O CLIENTE ADD NO CARRINHO

        produtoList.remove(produto);
        produtoCarrinhoAdapter.notifyDataSetChanged(); // PRODUTOS DO ADD MAIS

        configSaldoCarrinho();

        configLayoutAddMais();
    }

    @Override
    public void OnClick(ItemPedido itemPedido) { // RV PRINCIPAL
        this.itemPedido = itemPedido;
        showBottomSheet();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_LOGIN) {
                Intent intent = new Intent(this, UsuarioSelecionaEnderecoActivity.class);
                startActivityForResult(intent, REQUEST_ENDERECO);

            } else if (requestCode == REQUEST_ENDERECO) {
                endereco = (Endereco) data.getSerializableExtra("enderecoSelecionado");
                if (entregaDAO.getEndereco() == null) {
                    entregaDAO.salvarEndereco(endereco);
                } else {
                    entregaDAO.atualizarEndereco(endereco);
                }
                configEndereco();
            } else if (requestCode == REQUEST_PAGAMENTO) {
                Pagamento formaPagamento = (Pagamento) data.getSerializableExtra("pagamentoSelecionado");
                pagamento = formaPagamento.getDescricao();
                entregaDAO.salvarPagamento(pagamento);
                configPagamento();

            }
        }
    }
}