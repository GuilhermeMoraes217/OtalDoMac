package br.labex.hambre.fragment.empresa;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import br.labex.hambre.R;
import br.labex.hambre.activity.autenticacao.CriarContaActivity;
import br.labex.hambre.activity.autenticacao.LoginActivity;
import br.labex.hambre.activity.empresa.EmpresaAddMaisActivity;
import br.labex.hambre.activity.empresa.EmpresaCategoriaActivity;
import br.labex.hambre.activity.empresa.EmpresaConfigActivity;
import br.labex.hambre.activity.empresa.EmpresaEnderecoActivity;
import br.labex.hambre.activity.empresa.EmpresaEntregasActivity;
import br.labex.hambre.activity.empresa.EmpresaRecebimentoActivity;
import br.labex.hambre.activity.usuario.UsuarioEnderecoActivity;
import br.labex.hambre.activity.usuario.UsuarioFavoritoActivity;
import br.labex.hambre.activity.usuario.UsuarioHomeActivity;
import br.labex.hambre.activity.usuario.UsuarioPerfilActivity;
import br.labex.hambre.helper.FirebaseHelper;


public class EmpresaConfigFragment extends Fragment {

    private ImageView img_Logo;
    private TextView text_Empresa;
    private LinearLayout menu_Empresa;
    private LinearLayout menu_Categorias;
    private LinearLayout menu_Recebimentos;
    private LinearLayout menu_Add_Mais;
    private LinearLayout menu_Endereco;
    private LinearLayout menu_Entregas;
    private LinearLayout menu_Deslogar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_empresa_config, container, false);

        iniciaComponentes(view);
        confidCliques();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        configAcesso();

    }

    private void configAcesso(){

        Picasso.get().load(FirebaseHelper.getAuth().getCurrentUser().getPhotoUrl()).into(img_Logo); // MÉTODO QUE TRÁS A LOGO DO FIREBASE
        text_Empresa.setText(FirebaseHelper.getAuth().getCurrentUser().getDisplayName());

    }

    private void iniciaComponentes (View view){

        img_Logo = view.findViewById(R.id.img_Logo);
        text_Empresa = view.findViewById(R.id.text_Empresa);
        menu_Empresa = view.findViewById(R.id.menu_Empresa);
        menu_Categorias = view.findViewById(R.id.menu_Categorias);
        menu_Recebimentos = view.findViewById(R.id.menu_Recebimentos);
        menu_Add_Mais = view.findViewById(R.id.menu_Add_Mais);
        menu_Endereco = view.findViewById(R.id.menu_Endereco);
        menu_Entregas = view.findViewById(R.id.menu_Entregas);
        menu_Deslogar = view.findViewById(R.id.menu_Deslogar);

    }

    private void confidCliques (){
        menu_Empresa.setOnClickListener(view -> startActivity(new Intent(requireActivity(), EmpresaConfigActivity.class)));
        menu_Categorias.setOnClickListener(view -> startActivity(new Intent(requireActivity(), EmpresaCategoriaActivity.class)));
        menu_Recebimentos.setOnClickListener(view -> startActivity(new Intent(requireActivity(), EmpresaRecebimentoActivity.class)));
        menu_Add_Mais.setOnClickListener(view -> startActivity(new Intent(requireActivity(), EmpresaAddMaisActivity.class)));
        menu_Endereco.setOnClickListener(view -> startActivity(new Intent(requireActivity(), EmpresaEnderecoActivity.class)));
        menu_Entregas.setOnClickListener(view -> startActivity(new Intent(requireActivity(), EmpresaEntregasActivity.class)));

        menu_Deslogar.setOnClickListener(view -> deslogar());
    }

    private void deslogar (){
        FirebaseHelper.getAuth().signOut(); //DESLOGANDO DO FIREBASE "EMPRESA"
        requireActivity().finish();
        startActivity(new Intent(requireActivity(), UsuarioHomeActivity.class));
    }

}