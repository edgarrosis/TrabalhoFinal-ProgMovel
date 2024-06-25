package com.example.trabalhofinal_progmovel;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.trabalhofinal_progmovel.databinding.ActivityCadastroBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Cadastro extends AppCompatActivity {
    private ActivityCadastroBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private static final String TAG = "Cadastro";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCadastroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Inicialize o Firebase
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();

        binding.btnConfCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validarUsuario(view);
            }
        });

        binding.btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void validarUsuario(View view) {
        String nome = binding.edtNome.getText().toString();
        String email = binding.edtEmail.getText().toString();
        String senha = binding.edtSenha.getText().toString();
        String confSenha = binding.edtConfirmarSenha.getText().toString();

        if (!email.isEmpty()) {
            if (!nome.isEmpty()) {
                if (!senha.isEmpty()) {
                    if (!confSenha.isEmpty()) {
                        criarConta(nome, email, senha, confSenha);
                    } else {
                        Toast.makeText(this, "Preencha o campo de confirmar senha", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Preencha a senha do usuário", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Preencha o nome do usuário", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Preencha o email do usuário", Toast.LENGTH_SHORT).show();
        }
    }

    private void criarConta(String nome, String email, String senha, String confirmarSenha) {
        if (Objects.equals(senha, confirmarSenha)) {
            mAuth.createUserWithEmailAndPassword(email, senha)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                String uid = user.getUid();
                                Log.d(TAG, "UID do usuário: " + uid);
                                criarUsuario(uid, nome, email);
                            }
                            finish();
                            startActivity(new Intent(this, MainActivity.class));
                            Toast.makeText(this, "Cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Email já cadastrado", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "As Senhas digitadas são diferentes", Toast.LENGTH_SHORT).show();
        }
    }

    private void criarUsuario(String uid, String nome, String email) {
        // Criação do usuário com nome e email
        Map<String, Object> user = new HashMap<>();
        user.put("nome", nome);
        user.put("email", email);

        // Adição de um novo documento com o UID do usuário como ID
        db.collection("users")
                .document(uid)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written with UID: " + uid);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document with UID: " + uid, e);
                    }
                });
    }
}
