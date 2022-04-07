package br.labex.hambre.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import br.labex.hambre.R;
import br.labex.hambre.helper.GetMask;
import br.labex.hambre.model.Produto;

public class AddMaisAdapter extends RecyclerView.Adapter <AddMaisAdapter.MyViewHolder> {

    private List<Produto> produtoList;
    private List<String> addMaisList;
    private Context context;
    private OnClickListener onClickListener;

    public AddMaisAdapter(List<Produto> produtoList, List<String> addMaisList, Context context, OnClickListener onClickListener) {
        this.produtoList = produtoList;
        this.addMaisList = addMaisList;
        this.context = context;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_mais_item, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Produto produto = produtoList.get(position);

        Picasso.get().load(produto.getUrlImagem()).into(holder.img_ProdutoEmpresa);
        holder.text_Nome.setText(produto.getNome());
        holder.text_Valor.setText(context.getString(R.string.text_Valor, GetMask.getValor(produto.getValor())));
        holder.cb_Status.setChecked(addMaisList.contains(produto.getId()));

        holder.cb_Status.setOnCheckedChangeListener((compoundButton, b) -> {
            onClickListener.OnClick(produto.getId(), b);

        });
    }

    @Override
    public int getItemCount() {
        return produtoList.size();
    }

    public interface OnClickListener{
        void OnClick(String idProduto, Boolean status);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView img_ProdutoEmpresa;
        TextView text_Nome;
        TextView text_Valor;
        CheckBox cb_Status;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            img_ProdutoEmpresa = itemView.findViewById(R.id.img_ProdutoEmpresa);
            text_Nome = itemView.findViewById(R.id.text_Nome);
            text_Valor = itemView.findViewById(R.id.text_Valor);
            cb_Status = itemView.findViewById(R.id.cb_Status);
        }
    }
}
