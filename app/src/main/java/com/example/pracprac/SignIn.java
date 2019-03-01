package com.example.pracprac;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignIn extends AppCompatActivity  {

    public FirebaseAuth mAuth;
    ProgressBar progressBar;
    EditText email;
    EditText pass;
    String emailString,passString;
    boolean doubleBackToExitPressedOnce = false;

    //Sign In
    RelativeLayout rellay1, rellay2;
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            rellay1.setVisibility(View.VISIBLE);
            rellay2.setVisibility(View.VISIBLE);
        }
    };
    //Sign In

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_sign_in);

        //Sign In
        rellay1 = (RelativeLayout) findViewById(R.id.rellay1);
        rellay2 = (RelativeLayout) findViewById(R.id.rellay2);

        handler.postDelayed(runnable, 1400);
        //Sign In

        progressBar=findViewById(R.id.progressbar);
        TextView signUpView=findViewById(R.id.signUpTextView);
        signUpView.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent;
                intent = new Intent(SignIn.this, SignUp.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        email = findViewById(R.id.emaileditText);
        pass = findViewById(R.id.passwordEditText);
        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.signInButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignIn();
            }
        });



    }

    public void SignIn()
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
            pass.setError("enter password");
            pass.requestFocus();
            return;
        }
        if(passString.length()<6) {
            pass.setError("Min Length required is 6 ");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(emailString, passString)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {

                            Log.i("activity_sign_in", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent =new Intent(SignIn.this,Admin.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignIn.this, task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();

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

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user=mAuth.getCurrentUser();
        if(user!=null)
        {
            finish();
            if(user.getDisplayName()!=null)
            {
                startActivity(new Intent(SignIn.this,ChooseEnergy.class));
            }
            else
                startActivity(new Intent(SignIn.this,Admin.class));
        }
    }
}