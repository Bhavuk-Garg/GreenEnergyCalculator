package com.example.pracprac;

import android.content.Intent;
import android.media.tv.TvContract;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class SignUp extends AppCompatActivity {

    private FirebaseAuth mAuth;
    EditText email;
    EditText pass;
    String emailString,passString;
    ProgressBar progressBar;
    boolean doubleBackToExitPressedOnce = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(getApplicationContext());
        setContentView(R.layout.activity_sign_up);

        email = findViewById(R.id.emaileditText);
        progressBar=findViewById(R.id.progressbar);

        findViewById(R.id.signInTextView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(SignUp.this,SignIn.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        pass = findViewById(R.id.passwordEditText);
        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.signUpButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser()
    {
        emailString=email.getText().toString().trim();
        passString=pass.getText().toString().trim();
        if(emailString.isEmpty())
        {
            email.setError("enter email");
            email.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailString).matches())
        {
            email.setError("enter valid Email");
            email.requestFocus();
            return;
        }
        if(passString.isEmpty())
        {
            pass.setError("enter pass");
            pass.requestFocus();
            return;
        }
        if(passString.length()<6) {
            pass.setError("Min Length required is 6 ");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(emailString,passString).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Email Registered Successful ", Toast.LENGTH_SHORT).show();
                    final Handler handler = new Handler();

                } else {
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        email.setError("Email Exists Already");
                        email.requestFocus();
                    } else
                    {
                        Toast.makeText(SignUp.this, task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }

        }, 2000);
    }



}
