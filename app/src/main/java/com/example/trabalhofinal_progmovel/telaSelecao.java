package com.example.trabalhofinal_progmovel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.trabalhofinal_progmovel.databinding.ActivityTelaSelecaoBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class telaSelecao extends AppCompatActivity {
    private ActivityTelaSelecaoBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTelaSelecaoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseApp.initializeApp(getApplicationContext());
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();

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
        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(telaSelecao.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}