package com.example.firechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class Chat extends AppCompatActivity {
    public static String oppName,oppUid,oppImage;
    TextView name;
    EditText sendmess;
    ImageButton sendBut;
    CircleImageView oppAvatar;
    public static String senderName;
    public static String rName;
    String senderRoom,receiverRoom;
    RecyclerView messagePanel;
    ArrayList<Message> listMessage;
    MessageAdapter messageAdapter;
    ImageButton bck;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        oppName=getIntent().getStringExtra("Name").toString();
        oppUid=getIntent().getStringExtra("uid").toString();
        oppImage=getIntent().getStringExtra("image").toString();
        anhXa();
        Picasso.get().load(oppImage).into(oppAvatar);
        name.setText(oppName);
        senderRoom=LoginActivity.mAuth.getUid()+oppUid;
        receiverRoom=oppUid+LoginActivity.mAuth.getUid();
        messageAdapter=new MessageAdapter(Chat.this,listMessage);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        messagePanel.setLayoutManager(linearLayoutManager);
        messagePanel.setAdapter(messageAdapter);
        DatabaseReference ref=LoginActivity.database.getReference().child("user").child(LoginActivity.mAuth.getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                senderName=snapshot.child("name").getValue().toString();
                rName=oppName;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        sendBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message=sendmess.getText().toString();
                if(message.equals("")) {
                    Toast.makeText(Chat.this, "Hãy nhập tin nhắn!", Toast.LENGTH_SHORT).show();
                    return;
                }
                sendmess.setText("");
                Date date=new Date();
                Message mess=new Message(message,LoginActivity.mAuth.getUid(),date.getTime());
                DatabaseReference reference=LoginActivity.database.getReference();
                reference.child("chats").child(senderRoom).child("messages").push().setValue(mess)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                LoginActivity.database.getReference().child("chats")
                                        .child(receiverRoom).child("messages").push().setValue(mess)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                            }
                                        });
                            }
                        });
            }
        });
        DatabaseReference chatRef=LoginActivity.database.getReference().child("chats").child(senderRoom)
                .child("messages");
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listMessage.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    Message message=dataSnapshot.getValue(Message.class);
                    listMessage.add(message);
                }
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        bck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    public void anhXa()
    {
        bck=(ImageButton)findViewById(R.id.backButton);
        oppAvatar=(CircleImageView)findViewById(R.id.circle);
        name=(TextView)findViewById(R.id.NameView);
        sendmess=(EditText)findViewById(R.id.edittextNhaptinnhan);
        sendBut=(ImageButton) findViewById(R.id.sendButton);
        messagePanel=(RecyclerView)findViewById(R.id.messagePanel);
        listMessage=new ArrayList<>();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}