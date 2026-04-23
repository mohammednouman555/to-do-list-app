package com.example.app2.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app2.R;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    Button loginBtn, signupBtn;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn);
        signupBtn = findViewById(R.id.signupBtn);

        auth = FirebaseAuth.getInstance();

        loginBtn.setOnClickListener(v -> login());
        signupBtn.setOnClickListener(v -> signup());
    }

    void login() {
        auth.signInWithEmailAndPassword(
                email.getText().toString(),
                password.getText().toString()
        ).addOnSuccessListener(a -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }).addOnFailureListener(e ->
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    void signup() {
        auth.createUserWithEmailAndPassword(
                email.getText().toString(),
                password.getText().toString()
        ).addOnSuccessListener(a ->
                Toast.makeText(this, "Account created", Toast.LENGTH_SHORT).show()
        ).addOnFailureListener(e ->
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}