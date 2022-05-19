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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChangeInfo extends AppCompatActivity {

    CircleImageView avatar;
    EditText name,status;
    Button back,thaydoi;
    int GET_IMAGE=100;
    Uri img_uri;
    String img_uri_str;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_info);
        anhXa();
        Picasso.get().load(Home.userAvatar).into(avatar);
        name.setText(Home.name);
        status.setText(Home.status);
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityIfNeeded(intent,GET_IMAGE);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        thaydoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ten=name.getText().toString();
                String sta=status.getText().toString();
                if(ten.equals("")||sta.equals(""))
                {
                    Toast.makeText(ChangeInfo.this,"Hãy nhập đủ thông tin!",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    DatabaseReference ref=LoginActivity.database.getReference().child("user").child(LoginActivity.mAuth.getUid());
                    StorageReference sto=LoginActivity.storage.getReference().child("upload")
                            .child(LoginActivity.mAuth.getUid());
                    if(img_uri!=null)
                    {
                        sto.putFile(img_uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if(task.isSuccessful())
                                {
                                    sto.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            img_uri_str=uri.toString();
                                            User user=new User(Home.uid,
                                                    Home.email,
                                                    sta,ten,img_uri_str);
                                            ref.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful())
                                                    {
                                                        Toast.makeText(ChangeInfo.this,"Thay đổi thông tin thành công!",Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(ChangeInfo.this,Home.class));
                                                    }
                                                    else
                                                    {
                                                        Toast.makeText(ChangeInfo.this,"Đã có lỗi xảy ra! Xin hãy thử lại!",Toast.LENGTH_SHORT).show();
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
                        img_uri_str=Home.userAvatar.toString();
                        User user=new User(Home.uid,
                                Home.email,
                                sta,ten,
                                img_uri_str);
                        Toast.makeText(ChangeInfo.this,"Thay đổi thông tin thành công!",Toast.LENGTH_SHORT).show();
                        ref.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    Toast.makeText(ChangeInfo.this,"Thay đổi thông tin thành công!",Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(ChangeInfo.this,Home.class));
                                }
                                else
                                {
                                    Toast.makeText(ChangeInfo.this,"Đã có lỗi xảy ra! Xin hãy thử lại!",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            }
        });
    }
    public void anhXa()
    {
        avatar=(CircleImageView)findViewById(R.id.circleCN);
        name=(EditText)findViewById(R.id.edtsuaTen);
        status=(EditText)findViewById(R.id.edtSuastatus);
        back=(Button)findViewById(R.id.ql);
        thaydoi=(Button)findViewById(R.id.update);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GET_IMAGE&&data!=null)
        {
            img_uri=data.getData();
            avatar.setImageURI(img_uri);
        }
    }
}