package com.example.firechat.login;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.firechat.CoreActivity;
import com.example.firechat.home.Home;
import com.example.firechat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public class LoginActivity extends CoreActivity {

    EditText mail,pass;
    Button dangnhap,dangky;
    public static String status="I'm using this application!";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        anhXa();
        dangky.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
        dangnhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=mail.getText().toString();
                String password=pass.getText().toString();
                if(email.equals("")||password.equals(""))
                {
                    Toast.makeText(LoginActivity.this,"Please fill all information!",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(LoginActivity.this,"Login successfully!",Toast.LENGTH_SHORT).show();
                                        SharedPreferences sharedPreferences = getSharedPreferences("local_data", MODE_PRIVATE);
                                        sharedPreferences.edit().putString("email", email).apply();
                                        sharedPreferences.edit().putString("password", password).apply();
                                        startActivity(new Intent(LoginActivity.this, Home.class));
                                    } else {
                                        Toast.makeText(LoginActivity.this,"Your account does not exist or your password is wrong or other errors!",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }
    public void anhXa()
    {
        mail=findViewById(R.id.editTextTextEmailAddress);
        pass=findViewById(R.id.editTextTextPassword);
        dangnhap=findViewById(R.id.buttonDangNhap);
        dangky=findViewById(R.id.buttonDangky);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}