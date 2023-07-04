package com.AmmarSherwan.i200409_i200689;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignIn extends AppCompatActivity {

    EditText editEmail;
    EditText editPassword;
    Button signInBtn;
    TextView signupHyperLink;
    FirebaseAuth mAuth;
    Button guest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        editEmail = findViewById(R.id.txt_login_email);
        editPassword = findViewById(R.id.txt_login_password);
        signInBtn = findViewById(R.id.signin_btn);
        signupHyperLink = findViewById(R.id.signup_hyperlink);
        mAuth = FirebaseAuth.getInstance();
        guest = findViewById(R.id.guest_btn);

        guest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editEmail.setText("guest@guest.com");
                editPassword.setText("guest123456");
                login();
                editPassword.setText("");
                editEmail.setText("");
            }
        });

        signupHyperLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignIn.this, SignUp.class));
            }
        });

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

    }

    private void login() {
        String email_txt = editEmail.getText().toString();
        String password = editPassword.getText().toString();

        if (TextUtils.isEmpty(email_txt)) {
            editEmail.setError("Email cannot be empty");
            editEmail.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            editPassword.setError("Password cannot be empty");
            editPassword.requestFocus();
        } else {
            mAuth.signInWithEmailAndPassword(editEmail.getText().toString(), editPassword.getText().toString())
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Intent intent = new Intent(SignIn.this, Profile.class);
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignIn.this, "Sign in Failure", Toast.LENGTH_LONG).show();
                    }
                });

        }
    }

}