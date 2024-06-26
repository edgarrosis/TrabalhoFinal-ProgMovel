package com.example.trabalhofinal_progmovel;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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
            AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.anim.color_animation);
            set.setTarget(txtNewRecorde);
            set.start();
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