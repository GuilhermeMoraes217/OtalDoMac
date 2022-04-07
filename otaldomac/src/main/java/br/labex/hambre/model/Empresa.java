package br.labex.hambre.model;

import android.net.Uri;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.io.Serializable;

import br.labex.hambre.helper.FirebaseHelper;

public class Empresa implements Serializable {

    private String id;
    private String nome;
    private String email;
    private String senha;
    private String telefone;
    private String urlLogo;
    private Boolean acesso;
    private Double pedidoMinimo;
    private Double taxaEntrega;
    private int tempMinEntrega;
    private int tempMaxEntrega;
    private String categoria;
    private Long dataCadastro;

    private String publicKey;
    private String accessToken;

    public Empresa() {
    }


    public void salvar(){
        DatabaseReference empresaRef = FirebaseHelper.getDatabaseReference()
                .child("empresas")
                .child(getId());
        empresaRef.setValue(this);

        FirebaseUser user = FirebaseHelper.getAuth().getCurrentUser();
        UserProfileChangeRequest perfil;
        if(getUrlLogo() == null){
            perfil = new UserProfileChangeRequest.Builder()
                    .setDisplayName(getNome())
                    .build();
        }else {
            perfil = new UserProfileChangeRequest.Builder()
                    .setDisplayName(getNome())
                    .setPhotoUri(Uri.parse(getUrlLogo()))
                    .build();
        }

        if(user != null) user.updateProfile(perfil);
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    @Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getUrlLogo() {
        return urlLogo;
    }

    public void setUrlLogo(String urlLogo) {
        this.urlLogo = urlLogo;
    }

    public Boolean getAcesso() {
        return acesso;
    }

    public void setAcesso(Boolean acesso) {
        this.acesso = acesso;
    }

    public Double getPedidoMinimo() {
        return pedidoMinimo;
    }

    public void setPedidoMinimo(Double pedidoMinimo) {
        this.pedidoMinimo = pedidoMinimo;
    }

    public Double getTaxaEntrega() {
        return taxaEntrega;
    }

    public void setTaxaEntrega(Double taxaEntrega) {
        this.taxaEntrega = taxaEntrega;
    }

    public int getTempMinEntrega() {
        return tempMinEntrega;
    }

    public void setTempMinEntrega(int tempMinEntrega) {
        this.tempMinEntrega = tempMinEntrega;
    }

    public int getTempMaxEntrega() {
        return tempMaxEntrega;
    }

    public void setTempMaxEntrega(int tempMaxEntrega) {
        this.tempMaxEntrega = tempMaxEntrega;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Long getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Long dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
