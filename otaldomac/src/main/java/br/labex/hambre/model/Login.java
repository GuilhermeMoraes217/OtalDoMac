package br.labex.hambre.model;

import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

import br.labex.hambre.helper.FirebaseHelper;

public class Login implements Serializable {

    private String id;
    private String tipo;
    private Boolean acesso;

    public Login() {
    }

    public Login(String id, String tipo, Boolean acesso) {
        this.id = id;
        this.tipo = tipo;
        this.acesso = acesso;
    }

    public void salvar (){
        DatabaseReference loginref = FirebaseHelper.getDatabaseReference()
                .child("login")
                .child(getId());
        loginref.setValue(this);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Boolean getAcesso() {
        return acesso;
    }

    public void setAcesso(Boolean acesso) {
        this.acesso = acesso;
    }
}
