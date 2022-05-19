package com.example.firechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class LoginActivity extends AppCompatActivity {

    EditText mail,pass;
    Button dangnhap,dangky;
    public static FirebaseAuth mAuth;
    public static FirebaseDatabase database;
    public static FirebaseStorage storage;
    public static String status="I'm using this application!";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        anhXa();
        dangky.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,DangKy.class);
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
                    Toast.makeText(LoginActivity.this,"Hãy nhập đầy đủ thông tin!",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(LoginActivity.this,"Đăng nhập thành công!",Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(LoginActivity.this,Home.class));
                                    } else {
                                        Toast.makeText(LoginActivity.this,"Tài khoản không tồn tại hoặc mật khẩu không đúng!",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }
    public void anhXa()
    {
        mail=(EditText)findViewById(R.id.editTextTextEmailAddress);
        pass=(EditText)findViewById(R.id.editTextTextPassword);
        dangnhap=(Button)findViewById(R.id.buttonDangNhap);
        dangky=(Button)findViewById(R.id.buttonDangky);
        mAuth = FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        storage=FirebaseStorage.getInstance();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}