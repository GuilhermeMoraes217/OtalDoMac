package br.labex.hambre.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

import br.labex.hambre.R;
import br.labex.hambre.model.Endereco;

public class SelecionaEnderecoAdapter extends RecyclerView.Adapter<SelecionaEnderecoAdapter.MyViewHolder> {

    private final List<Endereco> enderecoList;
    private final OnClickListener onClickListener;

    private int lastSelectedPosition = -1;

    public SelecionaEnderecoAdapter(List<Endereco> enderecoList, OnClickListener onClickListener) {
        this.enderecoList = enderecoList;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.endereco_select, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) { // MODIFICADO @SuppressLint

        Endereco endereco = enderecoList.get(position);

        holder.text_logradouro.setText(endereco.getLogradouro());

        StringBuilder enderecoCompleto = new StringBuilder()
                .append(endereco.getReferencia())
                .append(", ")
                .append(endereco.getBairro())
                .append(", ")
                .append(endereco.getMunicipio());

        holder.text_endereco.setText(enderecoCompleto);

        if(lastSelectedPosition == position){
            holder.radioButton.setChecked(true);
        }

        holder.radioButton.setOnClickListener(v -> {
            lastSelectedPosition = position;
            notifyDataSetChanged();
            onClickListener.OnClick(endereco);
        });
    }

    @Override
    public int getItemCount() {
        return enderecoList.size();
    }

    public interface OnClickListener {
        void OnClick(Endereco endereco);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        RadioButton radioButton;
        TextView text_logradouro, text_endereco;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            radioButton = itemView.findViewById(R.id.radioButton);
            text_logradouro = itemView.findViewById(R.id.text_logradouro);
            text_endereco = itemView.findViewById(R.id.text_endereco);
        }
    }
}
