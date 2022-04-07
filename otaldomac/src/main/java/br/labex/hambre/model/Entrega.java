package br.labex.hambre.model;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

import br.labex.hambre.helper.FirebaseHelper;

public class Entrega {

    private Boolean status = false;
    private String descricao;
    private Double taxa;

    public Entrega() {
    }

    public static void salvar (List<Entrega> entregaList){
        DatabaseReference entregaRef = FirebaseHelper.getDatabaseReference()
                .child("entregas")
                .child(FirebaseHelper.getIdFirebase());
        entregaRef.setValue(entregaList);
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Double getTaxa() {
        return taxa;
    }

    public void setTaxa(Double taxa) {
        this.taxa = taxa;
    }
}
