package br.labex.hambre.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.labex.hambre.R;
import br.labex.hambre.model.CategoriaCardapio;

public class CardapioAdapter extends RecyclerView.Adapter<CardapioAdapter.MyViewHolder> {

    private final List<CategoriaCardapio> categoriaList;
    private final Activity activity;

    public CardapioAdapter(List<CategoriaCardapio> categoriaList, Activity activity) {
        this.categoriaList = categoriaList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemVIew = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_cardapio, parent, false);
        return new MyViewHolder(itemVIew);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        CategoriaCardapio categoria = categoriaList.get(position);

        holder.textCategoriaNome.setText(categoria.getNome());

        holder.rvProdutos.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        holder.rvProdutos.setHasFixedSize(true);
        ProdutoCardapioAdapter produtoCardapioAdapter = new ProdutoCardapioAdapter(categoria.getProdutoList(), activity);
        holder.rvProdutos.setAdapter(produtoCardapioAdapter);

        produtoCardapioAdapter.notifyDataSetChanged();

    }

    @Override
    public int getItemCount() {
        return categoriaList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView textCategoriaNome;
        RecyclerView rvProdutos;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textCategoriaNome = itemView.findViewById(R.id.textCategoriaNome);
            rvProdutos = itemView.findViewById(R.id.rvProdutos);
        }
    }
}
