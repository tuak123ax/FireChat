package com.example.firechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class DangKy extends AppCompatActivity {

    EditText mail,pass,confirm;
    Button quaylai,dangky;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_ky);

        anhXa();
        quaylai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        dangky.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=mail.getText().toString();
                String password=pass.getText().toString();
                String confirmPass=confirm.getText().toString();
                if(email.equals("")||password.equals("")||confirmPass.equals(""))
                {
                    Toast.makeText(DangKy.this,"Hãy nhập đầy đủ thông tin!",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(!password.equals(confirmPass))
                    {
                        Toast.makeText(DangKy.this,"Password không giống nhau!",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        if(password.length()<6)
                        {
                            Toast.makeText(DangKy.this,"Password quá ngắn!",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            LoginActivity.mAuth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(DangKy.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                Intent intent=new Intent(DangKy.this,Information.class);
                                                intent.putExtra("mail",email);
                                                intent.putExtra("password",password);
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(DangKy.this,"Tài khoản đã tồn tại!",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }
                }
            }
        });
    }
    public void anhXa()
    {
        mail=(EditText)findViewById(R.id.editTextTextEmailAddressDK);
        pass=(EditText)findViewById(R.id.editTextTextPasswordDK);
        confirm=(EditText)findViewById(R.id.editTextConfirm);
        quaylai=(Button)findViewById(R.id.buttonQuaylai);
        dangky=(Button)findViewById(R.id.butDangky);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}