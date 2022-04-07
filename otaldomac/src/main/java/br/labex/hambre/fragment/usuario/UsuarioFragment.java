package br.labex.hambre.fragment.usuario;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import br.labex.hambre.R;
import br.labex.hambre.activity.usuario.UsuarioFinalizaCadastroActivity;
import br.labex.hambre.helper.FirebaseHelper;
import br.labex.hambre.model.Login;
import br.labex.hambre.model.Usuario;

public class UsuarioFragment extends Fragment {

    private EditText edt_email;
    private EditText edt_senha;
    private ProgressBar progressBar;
    private Button btn_criar_conta;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_usuario, container, false);

        iniciaComponentes(view);

        configCliques();

        return view;
    }

    private void configCliques(){
        btn_criar_conta.setOnClickListener(v -> validaDados());
    }

    private void validaDados(){
        String email = edt_email.getText().toString();
        String senha = edt_senha.getText().toString();

        if(!email.isEmpty()){
            if(!senha.isEmpty()){

                ocultarTeclado();

                progressBar.setVisibility(View.VISIBLE);

                Usuario usuario = new Usuario();
                usuario.setEmail(email);
                usuario.setSenha(senha);

                criarConta(usuario);

            }else {
                edt_senha.requestFocus();
                edt_senha.setError("Informe sua senha.");
            }
        }else {
            edt_email.requestFocus();
            edt_email.setError("Informe seu e-mail.");
        }

    }

    private void criarConta(Usuario usuario){
        FirebaseHelper.getAuth().createUserWithEmailAndPassword(
                usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(task -> {
            if(task.isSuccessful()){

                String id = task.getResult().getUser().getUid();

                usuario.setId(id);
                usuario.salvar();

                Login login = new Login(id, "U", false);
                login.salvar();

                requireActivity().finish(); // FECHANDO A TELA DE LOGIN ----------------------------

                Intent intent = new Intent(requireActivity(), UsuarioFinalizaCadastroActivity.class);
                intent.putExtra("login", login);
                intent.putExtra("usuario", usuario);

                startActivity(intent);

            }else {
                progressBar.setVisibility(View.GONE);
                erroAutenticacao(FirebaseHelper.validaErros(task.getException().getMessage()));
            }
        });

    }

    private void erroAutenticacao(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Atenção");
        builder.setMessage(msg);
        builder.setPositiveButton("OK", ((dialog, which) -> {
            dialog.dismiss();
        }));

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void iniciaComponentes(View view){
        edt_email = view.findViewById(R.id.edt_Email);
        edt_senha = view.findViewById(R.id.edt_Senha);
        progressBar = view.findViewById(R.id.progressBar);
        btn_criar_conta = view.findViewById(R.id.btn_CriarConta);
    }

    private void ocultarTeclado(){
        ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                btn_criar_conta.getWindowToken(), 0
        );
    }

}