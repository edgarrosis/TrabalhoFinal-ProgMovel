package com.example.trabalhofinal_progmovel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trabalhofinal_progmovel.databinding.ActivityMainBinding;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        binding.btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realizarLogin();
            }
        });

        binding.btnCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Cadastro.class);
                startActivity(intent);
            }
        });
    }

    private void realizarLogin(){
        String email = binding.edtEmailLogin.getText().toString();
        String senha = binding.edtSenhaLogin.getText().toString();

        if (!email.isEmpty()) {
            if (!senha.isEmpty()) {
                loginFirebase(email,senha);
            } else {
                Toast.makeText(this, "Preencha o campo senha",
                        Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this, "Preencha o email do usuário",
                    Toast.LENGTH_SHORT).show();
        }

    }

    private void loginFirebase(String email, String senha){
        mAuth.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        startActivity(new Intent(MainActivity.this,TelaJogo.class));
                        Toast.makeText(this,"Logado com sucesso!",Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(this,"Erro no Login!",Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
