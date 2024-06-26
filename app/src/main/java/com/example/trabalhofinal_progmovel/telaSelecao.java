package com.example.trabalhofinal_progmovel;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.trabalhofinal_progmovel.databinding.ActivityTelaSelecaoBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class telaSelecao extends AppCompatActivity {
    private ActivityTelaSelecaoBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String uid;
    private ImageView imgSelectedBird;
    private int currentColorIndex = 0; // Índice da cor atual
    private List<Integer> birdColors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTelaSelecaoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseApp.initializeApp(getApplicationContext());
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        imgSelectedBird = findViewById(R.id.imageView);

        birdColors = new ArrayList<>();
        birdColors.add(R.drawable.vermelho); // Adicione as cores que deseja disponibilizar
        birdColors.add(R.drawable.azul);
        birdColors.add(R.drawable.amarelo);
        birdColors.add(R.drawable.emo);
        birdColors.add(R.drawable.robo);

        imgSelectedBird.setImageResource(birdColors.get(currentColorIndex));

        if (user != null) {
            uid = user.getUid();
        } else {
            // Se o usuário não estiver autenticado, redirecione para a tela de login
            startActivity(new Intent(telaSelecao.this, MainActivity.class));
            finish();
            return;
        }

        db.collection("users").document(uid).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String nome = documentSnapshot.getString("nome");
                            Long recordeAtual = documentSnapshot.getLong("recorde");
                            if (nome != null) {
                                binding.txtNomeUser.setText(nome);
                            } else {
                                binding.txtNomeUser.setText("Nome não encontrado");
                            }
                            if (recordeAtual != null) {
                                binding.txtRecordeUsuario.setText("Recorde atual: " + recordeAtual.toString());
                            } else {
                                binding.txtRecordeUsuario.setText("Recorde: 0");
                            }
                        } else {
                            binding.txtNomeUser.setText("Nome não encontrado");
                            binding.txtNomeUser.setText("0");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        binding.txtNomeUser.setText("Erro ao obter nome");
                    }
                });
        binding.btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(telaSelecao.this,TelaJogo.class));
            }
        });
        binding.btnRankingSelecao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(telaSelecao.this,Ranking.class));
            }
        });
        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectBird(v);
            }
        });
        binding.btnLogoutApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(telaSelecao.this)
                        .setMessage("Deseja deslogar do jogo?")
                        .setCancelable(false)
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mAuth.signOut();
                                Intent intent = new Intent(telaSelecao.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                                telaSelecao.super.onBackPressed();
                            }
                        })
                        .setNegativeButton("Não", null)
                        .show();
            }
        });
        }
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Deseja sair do jogo?")
                .setCancelable(false)
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        telaSelecao.super.onBackPressed();
                    }
                })
                .setNegativeButton("Não", null)
                .show();

    }

    public void selectBird(View view) {
        // Atualiza para a próxima cor disponível
        currentColorIndex = (currentColorIndex + 1) % birdColors.size();
        imgSelectedBird.setImageResource(birdColors.get(currentColorIndex));

        // Armazena a seleção do pássaro no SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("BirdPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("selectedBird", birdColors.get(currentColorIndex));
        editor.apply();
    }
}