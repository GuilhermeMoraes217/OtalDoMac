package br.labex.hambre.fragment.usuario;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.labex.hambre.DAO.EmpresaDAO;
import br.labex.hambre.DAO.ItemPedidoDAO;
import br.labex.hambre.R;
import br.labex.hambre.activity.empresa.EmpresaCardapioActivity;
import br.labex.hambre.activity.usuario.CarrinhoActivity;
import br.labex.hambre.adapter.EmpresasAdapter;
import br.labex.hambre.helper.FirebaseHelper;
import br.labex.hambre.helper.GetMask;
import br.labex.hambre.model.Empresa;

public class UsuarioHomeFragment extends Fragment implements EmpresasAdapter.OnClickListener {

    private EmpresasAdapter empresasAdapter;
    private List<Empresa> empresaList = new ArrayList<>();

    private RecyclerView rv_empresas;
    private ProgressBar progressBar;
    private TextView text_info;

    private TextView textQtdItemSacola;
    private TextView textVerSacola;
    private TextView textTotalCarrinho;
    private ConstraintLayout ll_sacola;

    private ItemPedidoDAO itemPedidoDAO;
    private EmpresaDAO empresaDAO;
    //git 1


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_usuario_home, container, false);

        itemPedidoDAO = new ItemPedidoDAO(getContext());
        empresaDAO = new EmpresaDAO (getContext());

        iniciaComponentes(view);

        configCliques();

        configRv();
        ;

        recuperaEmpresas();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        configSacola();
    }

    private void configCliques() {
        ll_sacola.setOnClickListener(view -> startActivity(new Intent(getActivity(), CarrinhoActivity.class)));
    }

    private void configRv() {
        rv_empresas.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv_empresas.setHasFixedSize(true);
        empresasAdapter = new EmpresasAdapter(empresaList, this, requireContext());
        rv_empresas.setAdapter(empresasAdapter);
    }

    private void recuperaEmpresas() {
        DatabaseReference empresaRef = FirebaseHelper.getDatabaseReference()
                .child("empresas");
        empresaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    empresaList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Empresa empresa = ds.getValue(Empresa.class);
                        empresaList.add(empresa);
                    }
                    text_info.setText("");
                } else {
                    text_info.setText("Nenhuma empresa cadastrada.");
                }

                progressBar.setVisibility(View.GONE);
                Collections.reverse(empresaList);
                empresasAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void configSacola() {
        if (!itemPedidoDAO.getList().isEmpty()) {
            double totalPedido = itemPedidoDAO.getTotal() + empresaDAO.getEmpresa().getTaxaEntrega();

            ll_sacola.setVisibility(View.VISIBLE);
            textQtdItemSacola.setText(String.valueOf(itemPedidoDAO.getList().size()));
            textTotalCarrinho.setText(getString(R.string.text_Valor, GetMask.getValor(totalPedido)));
        } else {
            ll_sacola.setVisibility(View.GONE);
            textQtdItemSacola.setText(" ");
            textTotalCarrinho.setText(" ");
        }
    }

    private void iniciaComponentes(View view) {
        rv_empresas = view.findViewById(R.id.rv_empresas);
        progressBar = view.findViewById(R.id.progressBar);
        text_info = view.findViewById(R.id.text_info);

        textQtdItemSacola = view.findViewById(R.id.textQtdItemSacola);
        textVerSacola = view.findViewById(R.id.textVerSacola);
        textTotalCarrinho = view.findViewById(R.id.textTotalCarrinho);
        ll_sacola = view.findViewById(R.id.ll_sacola);
    }

    @Override
    public void OnClick(Empresa empresa) {
        Intent intent = new Intent(requireActivity(), EmpresaCardapioActivity.class);
        intent.putExtra("empresaSelecionada", empresa);
        startActivity(intent);
    }
}