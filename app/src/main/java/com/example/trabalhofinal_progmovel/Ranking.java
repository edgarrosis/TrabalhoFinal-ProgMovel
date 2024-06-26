package com.example.trabalhofinal_progmovel;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.trabalhofinal_progmovel.databinding.ActivityRankingBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Ranking extends AppCompatActivity {
    private ActivityRankingBinding binding;
    private String uid;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private ListView listViewRank;
    private Intent edtIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRankingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseApp.initializeApp(getApplicationContext());
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();

        listViewRank = binding.listRankings;

        if (user != null) {
            uid = user.getUid();
        } else {
            binding.txtUsuarioRank.setVisibility(View.GONE);
            binding.imgBirdRed.setVisibility((ImageView.GONE));
            binding.txtUsuarioPont.setVisibility(View.GONE);
        }

        db.collection("users").document(uid).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String nome = documentSnapshot.getString("nome");
                            Long recordeAtual = documentSnapshot.getLong("recorde");
                            if (nome != null) {
                                binding.txtUsuarioRank.setText(nome);
                            } else {
                                binding.txtUsuarioRank.setText("Nome não encontrado");
                            }
                            if (recordeAtual != null) {
                                binding.txtUsuarioPont.setText("Pontualção Atual: " + recordeAtual.toString());
                            } else {
                                binding.txtUsuarioPont.setText("Recorde: 0");
                            }
                        } else {
                            binding.txtUsuarioRank.setText("Nome não encontrado");
                            binding.txtUsuarioRank.setText("0");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        binding.txtUsuarioRank.setText("Erro ao obter nome");
                    }
                });
        binding.btnBackRank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //inicializando a lista de rankings.
        preencherUsuarios();
    }

    private void preencherUsuarios() {
        // Referência para a coleção de usuários
        db.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Map<String, Object>> users = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            Map<String, Object> user = document.getData();
                            if (user != null) {
                                user.put("id", document.getId()); // Adiciona o ID do documento ao mapa do usuário
                                users.add(user);
                            }
                        }

                        // Ordena os usuários pelo recorde do maior para o menor
                        Collections.sort(users, (u1, u2) -> {
                            Long recorde1 = (Long) u1.get("recorde");
                            Long recorde2 = (Long) u2.get("recorde");

                            if (recorde1 == null) {
                                recorde1 = 0L;
                            }
                            if (recorde2 == null) {
                                recorde2 = 0L;
                            }

                            return recorde2.compareTo(recorde1);
                        });

                        // Cria uma lista de strings para exibir no ListView
                        List<String> displayList = new ArrayList<>();
                        for (Map<String, Object> usuario : users) {
                            String nome = (String) usuario.get("nome");
                            Long recorde = (Long) usuario.get("recorde");
                            if (recorde == null) {
                                recorde = 0L;
                            }
                            displayList.add(nome + " - Recorde: " + recorde);
                        }

                        // Configura o adapter com a lista de exibição
                        runOnUiThread(() -> {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(Ranking.this,
                                    android.R.layout.simple_list_item_1, displayList);
                            listViewRank.setAdapter(adapter);
                        });
                    } else {
                        // Trate o caso de falha na recuperação dos documentos
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });
    }
}