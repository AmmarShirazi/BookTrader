package com.AmmarSherwan.i200409_i200689;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignUp extends AppCompatActivity {

    Button signUpBtn;
    EditText username;
    EditText editEmail;
    EditText editPassword;
    TextView goSignIn;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        signUpBtn = findViewById(R.id.signup_btn);
        username = findViewById(R.id.txt_signup_username);
        editEmail = findViewById(R.id.txt_signup_email);
        editPassword = findViewById(R.id.txt_signup_password);
        goSignIn = findViewById(R.id.signup_hyperlink);
        mAuth = FirebaseAuth.getInstance();

        goSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUp.this, SignIn.class));
            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUser();
            }
        });

    }

    private void createUser() {
        String email_txt = editEmail.getText().toString();
        String password = editPassword.getText().toString();

        if (TextUtils.isEmpty(email_txt)) {
            editEmail.setError("Email cannot be empty");
            editEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            editPassword.setError("Password cannot be empty");
            editPassword.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(username.getText().toString())) {
            username.setError("Username cannot be empty");
            username.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email_txt, password)
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {

                        Toast.makeText(SignUp.this, "Sign up Success", Toast.LENGTH_LONG).show();

                        FirebaseUser user = mAuth.getCurrentUser();
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(username.getText().toString())
                                .build();
                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("MSG", "User profile updated.");
                                        }
                                    }
                                });


                        Intent intent = new Intent(SignUp.this, SignIn.class);
                        mAuth.signOut();
                        startActivity(intent);
                    } else {
                        Toast.makeText(SignUp.this, "Registration Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }


                }
            });

    }
}
