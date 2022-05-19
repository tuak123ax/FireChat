package com.example.firechat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class Information extends AppCompatActivity {

    EditText Ten;
    Button next;
    CircleImageView avatar;
    int GET_IMAGE=100;
    Uri image_Uri;
    String imageUri_str;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        anhXa();
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=getIntent().getStringExtra("mail");
                DatabaseReference ref=LoginActivity.database.getReference().child("user")
                        .child(LoginActivity.mAuth.getUid());
                StorageReference sto=LoginActivity.storage.getReference().child("upload")
                        .child(LoginActivity.mAuth.getUid());
                if(image_Uri!=null)
                {
                    sto.putFile(image_Uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful())
                            {
                                sto.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        imageUri_str=uri.toString();
                                        User user=new User(LoginActivity.mAuth.getUid(),
                                                email,
                                                LoginActivity.status,Ten.getText().toString(),
                                                imageUri_str);
                                        ref.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful())
                                                {
                                                    Toast.makeText(Information.this,"Tạo tài khoản thành công!",Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(Information.this,Home.class));
                                                }
                                                else
                                                {
                                                    Toast.makeText(Information.this,"Đã có lỗi xảy ra! Xin hãy thử lại!",Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    });
                }
                else
                {
                    imageUri_str="https://firebasestorage.googleapis.com/v0/b/firechat-aa433.appspot.com/o/unknownavatar.png?alt=media&token=9a49ff27-e5fa-4813-97d4-47bd15281550";
                    User user=new User(LoginActivity.mAuth.getUid(),
                            email,
                            LoginActivity.status,Ten.getText().toString(),
                            imageUri_str);
                    ref.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(Information.this,"Tạo tài khoản thành công!",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Information.this,Home.class));
                            }
                            else
                            {
                                Toast.makeText(Information.this,"Đã có lỗi xảy ra! Xin hãy thử lại!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityIfNeeded(intent,GET_IMAGE);
            }
        });
    }
    public void anhXa()
    {
        Ten=(EditText)findViewById(R.id.edtTen);
        avatar=(CircleImageView) findViewById(R.id.btn_hinhanh);
        next=(Button)findViewById(R.id.buttonNext);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GET_IMAGE&&data!=null)
        {
            image_Uri=data.getData();
            avatar.setImageURI(image_Uri);
        }
    }
}