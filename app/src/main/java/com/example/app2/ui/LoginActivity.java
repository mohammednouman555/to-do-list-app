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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ✅ AUTO LOGIN
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        setContentView(R.layout.activity_login);

        email = findViewById(R.id.emailInput);
        password = findViewById(R.id.passwordInput);

        loginBtn = findViewById(R.id.loginBtn);
        signupBtn = findViewById(R.id.signupBtn);

        auth = FirebaseAuth.getInstance();

        // 🔐 LOGIN
        loginBtn.setOnClickListener(v -> {

            String e = email.getText().toString().trim();
            String p = password.getText().toString().trim();

            auth.signInWithEmailAndPassword(e, p)
                    .addOnSuccessListener(result -> {

                        Toast.makeText(this,
                                "Login Successful",
                                Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(this, MainActivity.class));
                        finish();

                    })
                    .addOnFailureListener(err -> {

                        Toast.makeText(this,
                                err.getMessage(),
                                Toast.LENGTH_LONG).show();
                    });
        });

        // 🆕 SIGNUP
        signupBtn.setOnClickListener(v -> {

            String e = email.getText().toString().trim();
            String p = password.getText().toString().trim();

            auth.createUserWithEmailAndPassword(e, p)
                    .addOnSuccessListener(result -> {

                        Toast.makeText(this,
                                "Account Created",
                                Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(this, MainActivity.class));
                        finish();

                    })
                    .addOnFailureListener(err -> {

                        Toast.makeText(this,
                                err.getMessage(),
                                Toast.LENGTH_LONG).show();
                    });
        });
    }
}