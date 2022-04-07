package br.labex.hambre.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import br.labex.hambre.R;
import br.labex.hambre.helper.GetMask;
import br.labex.hambre.model.Produto;

public class ProdutoAdapterEmpresa extends RecyclerView.Adapter <ProdutoAdapterEmpresa.MyViewHolder> {

    private List<Produto> produtoList;
    private Context context;
    private OnClickListener onClickListener;

    public ProdutoAdapterEmpresa(List<Produto> produtoList, Context context, OnClickListener onClickListener) {
        this.produtoList = produtoList;
        this.context = context;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.produto_item, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Produto produto = produtoList.get(position);

        Picasso.get().load(produto.getUrlImagem()).into(holder.img_ProdutoEmpresa);
        holder.text_Nome.setText(produto.getNome());
        holder.text_Descricao.setText(produto.getDescricao());
        holder.text_Valor.setText(context.getString(R.string.text_Valor, GetMask.getValor(produto.getValor())));

        if (produto.getValorAntigo() > 0){
            holder.text_ValorAntigo.setText(context.getString(R.string.text_Valor, GetMask.getValor(produto.getValorAntigo())));
        }else {
            holder.text_ValorAntigo.setText("");

        }

        holder.itemView.setOnClickListener(view -> onClickListener.OnClick(produto));
    }

    @Override
    public int getItemCount() {
        return produtoList.size();
    }

    public interface OnClickListener{
        void OnClick(Produto produto);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView img_ProdutoEmpresa;
        TextView text_Nome;
        TextView text_Descricao;
        TextView text_Valor;
        TextView text_ValorAntigo;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            img_ProdutoEmpresa = itemView.findViewById(R.id.img_ProdutoEmpresa);
            text_Nome = itemView.findViewById(R.id.text_Nome);
            text_Descricao = itemView.findViewById(R.id.text_Descricao);
            text_Valor = itemView.findViewById(R.id.text_Valor);
            text_ValorAntigo = itemView.findViewById(R.id.text_ValorAntigo);
        }
    }
}
