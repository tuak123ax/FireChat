package com.example.firechat.home;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firechat.CoreActivity;
import com.example.firechat.R;
import com.example.firechat.constants.Constant;
import com.example.firechat.info.ChangeInfo;
import com.example.firechat.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class Home extends CoreActivity {
    CircleImageView avatar;
    RecyclerView recyclerView;
    UserAdapter adapter;
    ArrayList<User> listUsers;
    ImageButton outButton;
    RelativeLayout loadingView;
    TextView username;
    public static User currentUser;
    public static Switch darkmode;
    public static boolean isDarkMode = false;
    RelativeLayout toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mapping();
        if(mAuth != null) {
            DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("user").child(Objects.requireNonNull(mAuth.getUid()));
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String name = snapshot.child("name").getValue().toString();
                    currentUser.setName(name);
                    username.setText(name);
                    currentUser.setImage(snapshot.child("image").getValue().toString());
                    Picasso.get().load(currentUser.getImage()).into(avatar);
                    currentUser.setStatus(snapshot.child("status").getValue().toString());
                    currentUser.setEmail(snapshot.child("email").getValue().toString());
                    currentUser.setUid(snapshot.child("uid").getValue().toString());
                    currentUser.setToken(snapshot.child("token").getValue().toString());
                    String currentToken = getSharedPreferences("local_data",MODE_PRIVATE).getString(Constant.KEY_FCM_TOKEN, "");
                    if(!currentToken.isEmpty() && !currentToken.equals(currentUser.getToken())){
                        updateToken(currentToken);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference databaseReference=database.getReference().child("user");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listUsers.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    User user=dataSnapshot.getValue(User.class);
                    if(!user.getUid().equals(currentUser.getUid())){
                        listUsers.add(user);
                    }
                }
                adapter.notifyDataSetChanged();
                loadingView.setVisibility(View.GONE);
                toolbar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        outButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLogoutDialog();
            }
        });
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Home.this, ChangeInfo.class));
            }
        });
        darkmode.setChecked(false);
        darkmode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    isDarkMode = true;
                    recyclerView.setBackgroundColor(Color.BLACK);
                    toolbar.setBackgroundColor(getColor(R.color.green_background));
                }
                else{
                    isDarkMode = false;
                    recyclerView.setBackgroundColor(Color.WHITE);
                    toolbar.setBackgroundColor(getColor(R.color.red_background));
                }
            }
        });
        askNotificationPermission();
    }

    private void showLogoutDialog() {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(Home.this);
        alertDialog.setTitle("Logout");
        alertDialog.setMessage("Do you really want to logout?");
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferences sharedPreferences = getSharedPreferences("local_data", MODE_PRIVATE);
                sharedPreferences.edit().putString("email", "{}").apply();
                sharedPreferences.edit().putString("password", "{}").apply();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(Home.this,LoginActivity.class));
            }
        });
        alertDialog.show();
    }
    private void updateToken(String token) {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("user").child(Objects.requireNonNull(mAuth.getUid()));
        Map<String,Object> map=new HashMap<>();
        map.put("token",token);
        ref.updateChildren(map);
    }

    public void mapping()
    {
        darkmode=findViewById(R.id.darkswitch);
        avatar=findViewById(R.id.title);
        recyclerView=findViewById(R.id.recyclerview);
        outButton=findViewById(R.id.signout);
        toolbar=findViewById(R.id.toolbar);
        username=findViewById(R.id.username);
        loadingView=findViewById(R.id.loadingView);
        listUsers=new ArrayList<>();
        adapter=new UserAdapter(getApplicationContext(),listUsers);
        currentUser = new User();
    }

    private void askNotificationPermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if (ContextCompat.checkSelfPermission(
                    getApplicationContext(), Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // You can use the API that requires the permission.
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
        }
    // Register the permissions callback, which handles the user's response to the
// system permissions dialog. Save the return value, an instance of
// ActivityResultLauncher, as an instance variable.
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if(isGranted) {
                } else {
                    showExplainDialog();
                }
            });

    private void showExplainDialog() {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(Home.this);
        alertDialog.setTitle("Explain");
        alertDialog.setMessage("Because you didn't grant permission, our app cannot send notifications.");
        alertDialog.setPositiveButton("I understand", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        showLogoutDialog();
    }
}