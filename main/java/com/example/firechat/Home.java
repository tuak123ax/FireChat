package com.example.firechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Home extends AppCompatActivity {

    CircleImageView avatar;
    RecyclerView recyclerView;
    UserAdapter adapter;
    ArrayList<User> listUsers;
    ImageButton outButton;
    public static String userAvatar;
    public static String name, status,email,uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        anhXa();
        DatabaseReference ref=LoginActivity.database.getReference().child("user").child(LoginActivity.mAuth.getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userAvatar=snapshot.child("image").getValue().toString();
                Picasso.get().load(userAvatar).into(avatar);
                name=snapshot.child("name").getValue().toString();
                status=snapshot.child("status").getValue().toString();
                email=snapshot.child("email").getValue().toString();
                uid=snapshot.child("uid").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        DatabaseReference databaseReference=LoginActivity.database.getReference().child("user");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listUsers.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    User user=dataSnapshot.getValue(User.class);
                    listUsers.add(user);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        outButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog=new AlertDialog.Builder(Home.this);
                alertDialog.setTitle("Đăng xuất");
                alertDialog.setMessage("Bạn có chắc chắn muốn đăng xuất không?");
                alertDialog.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                alertDialog.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(Home.this,LoginActivity.class));
                    }
                });
                alertDialog.show();
            }
        });
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Home.this,ChangeInfo.class));
            }
        });
    }
    public void anhXa()
    {
        avatar=(CircleImageView)findViewById(R.id.title);
        recyclerView=(RecyclerView)findViewById(R.id.recyclerview);
        listUsers=new ArrayList<>();
        adapter=new UserAdapter(Home.this,listUsers);
        outButton=(ImageButton)findViewById(R.id.signout);
    }
}