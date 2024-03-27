package com.example.firechat.splash;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.firechat.CoreActivity;
import com.example.firechat.home.Home;
import com.example.firechat.login.LoginActivity;
import com.example.firechat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public class SplashActivity extends CoreActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String email = getSharedPreferences("local_data",MODE_PRIVATE).getString("email","{}");
                String password = getSharedPreferences("local_data", MODE_PRIVATE).getString("password","{}");
                mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            startActivity(new Intent(getApplicationContext(), Home.class));
                        } else {
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        }
                    }
                });
            }
        },3000);
    }
}