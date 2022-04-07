package br.labex.hambre.activity.autenticacao;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import br.labex.hambre.R;
import br.labex.hambre.adapter.ViewPagerAdapter;
import br.labex.hambre.fragment.empresa.EmpresaFragment;
import br.labex.hambre.fragment.usuario.UsuarioFragment;

public class CriarContaActivity extends AppCompatActivity {

    private TabLayout tab_Layout;
    private ViewPager view_Pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(br.labex.hambre.R.layout.activity_criar_conta);

        iniciaComponets();
        configTabsLayout();
        configCliques();
    }


    private void configCliques(){
        findViewById(R.id.ib_Voltar).setOnClickListener(view ->finish());
    }

    private void configTabsLayout(){
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new UsuarioFragment(), "Usuário");
        viewPagerAdapter.addFragment(new EmpresaFragment(), "Empresa");

        view_Pager.setAdapter(viewPagerAdapter);
        tab_Layout.setElevation(0);
        tab_Layout.setupWithViewPager(view_Pager);
    }

    private void iniciaComponets(){
        TextView text_Toobar = findViewById(R.id.txt_Toobar);
        text_Toobar.setText("Cadastre-se");

        view_Pager = findViewById(R.id.view_Pager);
        tab_Layout = findViewById(R.id.tab_Layout);

    }
}