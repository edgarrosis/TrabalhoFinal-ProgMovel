package com.example.trabalhofinal_progmovel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trabalhofinal_progmovel.databinding.ActivityMainBinding;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


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
        binding.btnRanking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Ranking.class);
                startActivity(intent);
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

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Usuário está logado, redirecionar para a tela de seleção ou tela do jogo
            Intent intent = new Intent(MainActivity.this, telaSelecao.class);
            startActivity(intent);
            finish(); // Finaliza a MainActivity para que o usuário não possa voltar para ela
        }
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
                        startActivity(new Intent(MainActivity.this,telaSelecao.class));
                        Toast.makeText(this,"Logado com sucesso!",Toast.LENGTH_SHORT).show();
                        finish();
                    }else {
                        Toast.makeText(this,"Erro no Login!",Toast.LENGTH_SHORT).show();
                    }
                });
        
    }
}
