package com.example.trabalhofinal_progmovel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trabalhofinal_progmovel.databinding.ActivityCadastroBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class Cadastro extends AppCompatActivity {
    private ActivityCadastroBinding binding;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCadastroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
                    if(!confSenha.isEmpty()) {
                        criarConta(email, senha, confSenha);
                    } else{
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

    private void criarConta(String email, String senha, String confirmarSenha){
        if (Objects.equals(senha, confirmarSenha)) {
            mAuth.createUserWithEmailAndPassword(email, senha)
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            finish();
                            startActivity(new Intent(this,MainActivity.class));
                            Toast.makeText(this, "Cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(this,"Erro ao cadastrar!",Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "As Senhas digitadas são diferentes", Toast.LENGTH_SHORT).show();
        }
    }
}