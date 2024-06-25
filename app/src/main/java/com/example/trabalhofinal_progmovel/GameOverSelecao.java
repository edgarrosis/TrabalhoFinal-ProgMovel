package com.example.trabalhofinal_progmovel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.trabalhofinal_progmovel.databinding.ActivityGameOverSelecaoBinding;

public class GameOverSelecao extends AppCompatActivity {
    private ActivityGameOverSelecaoBinding binding;
    private TextView txtNewRecorde;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGameOverSelecaoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        txtNewRecorde = findViewById(R.id.txtNewRecorde);
        boolean novoRecorde = getIntent().getBooleanExtra("novoRecorde", false);
        int novoRecordeNumero = getIntent().getIntExtra("novoRecordeNumero", 0);

        if (novoRecorde) {
            txtNewRecorde.setText("Novo recorde: " + novoRecordeNumero);
        } else {
            txtNewRecorde.setText("");
        }

        binding.btnReinicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameOverSelecao.this, TelaJogo.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    binding.btnSair.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(GameOverSelecao.this, telaSelecao.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    });

    }
}