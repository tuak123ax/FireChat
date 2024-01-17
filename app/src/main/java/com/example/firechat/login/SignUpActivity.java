package com.example.firechat.login;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.firechat.CoreActivity;
import com.example.firechat.info.Information;
import com.example.firechat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public class SignUpActivity extends CoreActivity {

    EditText mail,pass,confirm;
    Button quaylai,dangky;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

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
                    Toast.makeText(SignUpActivity.this,"Please fill all information!",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(!password.equals(confirmPass))
                    {
                        Toast.makeText(SignUpActivity.this,"Passwords are not the same!",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        if(password.length()<6)
                        {
                            Toast.makeText(SignUpActivity.this,"Password is too short!",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            mAuth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                Intent intent=new Intent(SignUpActivity.this, Information.class);
                                                intent.putExtra("mail",email);
                                                intent.putExtra("password",password);
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(SignUpActivity.this,"This account existed!",Toast.LENGTH_SHORT).show();
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
        mail=findViewById(R.id.editTextTextEmailAddressDK);
        pass=findViewById(R.id.editTextTextPasswordDK);
        confirm=findViewById(R.id.editTextConfirm);
        quaylai=findViewById(R.id.buttonQuaylai);
        dangky=findViewById(R.id.butDangky);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}