package br.labex.hambre.activity.autenticacao;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import br.labex.hambre.R;

public class RecuperaContaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recupera_conta);
        inciaComponentes();
        configCliques();
    }

    private void configCliques(){
        findViewById(R.id.ib_Voltar).setOnClickListener(view -> finish());
    }

    private void inciaComponentes (){
        TextView text_Toobar = findViewById(R.id.txt_Toobar);
        text_Toobar.setText("Recuperar conta");
    }
}