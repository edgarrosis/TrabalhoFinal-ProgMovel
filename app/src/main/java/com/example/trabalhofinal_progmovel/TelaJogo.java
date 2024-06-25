package com.example.trabalhofinal_progmovel;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trabalhofinal_progmovel.databinding.ActivityTelaJogoBinding;

public class TelaJogo extends AppCompatActivity {
    private ActivityTelaJogoBinding binding;
    private Jogabilidade jogabilidade;
    private ImageButton btnPause;
    private boolean isPaused = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTelaJogoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FrameLayout gameLayout = findViewById(R.id.gameLayout);
        jogabilidade = new Jogabilidade(this, 300); // Inicializa com a posição inicial do pássaro
        gameLayout.addView(jogabilidade); // Adiciona o SurfaceView ao layout
        btnPause = findViewById(R.id.btnPause);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (jogabilidade != null) {
            jogabilidade.pause();
        }
        isPaused = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (jogabilidade != null && isPaused) {
            jogabilidade.resume();
        }
        isPaused = false;
    }
}
