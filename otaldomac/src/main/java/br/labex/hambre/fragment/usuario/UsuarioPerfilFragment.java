package br.labex.hambre.fragment.usuario;

import android.content.Intent;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import br.labex.hambre.R;
import br.labex.hambre.activity.autenticacao.CriarContaActivity;
import br.labex.hambre.activity.autenticacao.LoginActivity;
import br.labex.hambre.activity.usuario.UsuarioEnderecoActivity;
import br.labex.hambre.activity.usuario.UsuarioFavoritoActivity;
import br.labex.hambre.activity.usuario.UsuarioPerfilActivity;
import br.labex.hambre.helper.FirebaseHelper;

public class UsuarioPerfilFragment extends Fragment {
    private TextView text_Usuario;
    private ConstraintLayout l_Logado;
    private ConstraintLayout l_deslogado;
    private LinearLayout menu_Perfil;
    private LinearLayout menu_Favoritos;
    private LinearLayout menu_Enderecos;
    private LinearLayout menu_Deslogar;
    private Button btn_Entrar;
    private Button btn_Cadastrar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_usuario_perfil, container, false);

        iniciaComponentes(view);

        confidCliques();

        return view;

    }

    @Override
    public void onStart() {
        super.onStart();

        verificaAcesso();

    }

    private void verificaAcesso (){
        if(FirebaseHelper.getAutenticado()){
            l_deslogado.setVisibility(View.GONE);
            l_Logado.setVisibility(View.VISIBLE);
            menu_Deslogar.setVisibility(View.VISIBLE);
            text_Usuario.setText(FirebaseHelper.getAuth().getCurrentUser().getDisplayName()); // MÉTODO QUE TRÁS O NOME DO USUÁRIO

        }else{
            l_deslogado.setVisibility(View.VISIBLE);
            l_Logado.setVisibility(View.GONE);
            menu_Deslogar.setVisibility(View.GONE);

        }
    }

    private void iniciaComponentes (View view){

        text_Usuario = view.findViewById(R.id.text_Usuario);
        l_Logado = view.findViewById(R.id.l_Logado);
        l_deslogado = view.findViewById(R.id.l_deslogado);
        menu_Perfil = view.findViewById(R.id.menu_Perfil);
        menu_Favoritos = view.findViewById(R.id.menu_Favoritos);
        menu_Enderecos = view.findViewById(R.id.menu_Enderecos);
        menu_Deslogar = view.findViewById(R.id.menu_Deslogar);
        btn_Entrar = view.findViewById(R.id.btn_Entrar);
        btn_Cadastrar = view.findViewById(R.id.btn_Cadastrar);

    }

    private void confidCliques (){
        btn_Entrar.setOnClickListener(v -> startActivity(new Intent(requireActivity(), LoginActivity.class)));
        btn_Cadastrar.setOnClickListener(v -> startActivity(new Intent(requireActivity(), CriarContaActivity.class)));
        menu_Deslogar.setOnClickListener(view -> deslogar());

        menu_Perfil.setOnClickListener(view -> verificaAutenticacao(UsuarioPerfilActivity.class));
        menu_Favoritos.setOnClickListener(view -> verificaAutenticacao(UsuarioFavoritoActivity.class));
        menu_Enderecos.setOnClickListener(view -> verificaAutenticacao(UsuarioEnderecoActivity.class));
    }

    private void deslogar (){
        FirebaseHelper.getAuth().signOut(); //DESLOGANDO DO FIREBASE "EMPRESA"
        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.menu_home);
    }

    private void verificaAutenticacao(Class<?> clazz){
        if(FirebaseHelper.getAutenticado()){
            startActivity(new Intent(requireActivity(), clazz));
        }else {
            startActivity(new Intent(requireActivity(), LoginActivity.class));
        }
    }}