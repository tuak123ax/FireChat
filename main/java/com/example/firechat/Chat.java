package com.example.firechat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class Chat extends AppCompatActivity {
    public static String oppName,oppUid,oppImage;
    Switch darkSwitch;
    TextView name;
    EditText sendmess;
    ImageButton sendBut;
    CircleImageView oppAvatar;
    public static String senderName;
    public static String rName;
    String senderRoom,receiverRoom;
    public static RecyclerView messagePanel;
    ArrayList<Message> listMessage;
    MessageAdapter messageAdapter;
    ImageButton bck;
    ImageButton sendPic;
    int CAMERA_CODE=500;
    int GALLERY_CODE=501;
    String currentPhotoPath;
    TextView labelDark;
    RelativeLayout layoutChat;
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
                Message mess=new Message(message,LoginActivity.mAuth.getUid(),date.getTime(),"text");
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
        sendPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog=new AlertDialog.Builder(Chat.this);
                dialog.setTitle("Choose");
                CharSequence[] option={"Camera","Bộ sưu tập"};
                dialog.setItems(option, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(option[i].equals("Camera"))
                        {
                            dispatchTakePictureIntent();
                        }
                        else
                        {
                            Intent pickPic=new Intent(Intent.ACTION_PICK);
                            pickPic.setType("image/*");
                            pickPic.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityIfNeeded(pickPic,GALLERY_CODE);
                        }
                    }
                });
                dialog.show();
            }
        });
        darkSwitch.setChecked(false);
        darkSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b==true)
                {
                    labelDark.setTextColor(Color.GREEN);
                    messagePanel.setBackgroundColor(Color.BLACK);
                    layoutChat.setBackgroundColor(Color.BLACK);
                    bck.setBackgroundColor(Color.GREEN);
                }
                else
                {
                    labelDark.setTextColor(Color.BLACK);
                    messagePanel.setBackgroundColor(Color.WHITE);
                    layoutChat.setBackgroundColor(Color.WHITE);
                    bck.setBackgroundColor(Color.WHITE);
                }
            }
        });
    }
    public void anhXa()
    {
        sendPic=(ImageButton)findViewById(R.id.btnSendHinh);
        bck=(ImageButton)findViewById(R.id.backButton);
        oppAvatar=(CircleImageView)findViewById(R.id.circle);
        name=(TextView)findViewById(R.id.NameView);
        sendmess=(EditText)findViewById(R.id.edittextNhaptinnhan);
        sendBut=(ImageButton) findViewById(R.id.sendButton);
        messagePanel=(RecyclerView)findViewById(R.id.messagePanel);
        listMessage=new ArrayList<>();
        darkSwitch=(Switch)findViewById(R.id.darkswitchChat);
        labelDark=(TextView)findViewById(R.id.labelDark);
        layoutChat=(RelativeLayout)findViewById(R.id.layoutChat);
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityIfNeeded(takePictureIntent, CAMERA_CODE);
            }
        }
    }
    private void sendNotification()
    {

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==CAMERA_CODE&&resultCode==RESULT_OK&&data!=null)
        {
            File f=new File(currentPhotoPath);
            Intent mediaScan=new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri=Uri.fromFile(f);
            mediaScan.setData(contentUri);
            this.sendBroadcast(mediaScan);
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
            Date now=new Date();
            String filename=simpleDateFormat.format(now);
            StorageReference sto=LoginActivity.storage.getReference("upload").child(Home.uid+filename);
            sto.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    sto.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Date date=new Date();
                            Message mess=new Message(uri.toString(),LoginActivity.mAuth.getUid(),date.getTime(),"image");
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
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Chat.this,"Fail!",Toast.LENGTH_SHORT).show();
                }
            });

        }
        if(requestCode==GALLERY_CODE&&resultCode==RESULT_OK&&data!=null)
        {
            Uri image=data.getData();
            if(image!=null)
            {
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
                Date now=new Date();
                String filename=simpleDateFormat.format(now);
                StorageReference sto=LoginActivity.storage.getReference("upload").child(Home.uid+filename);
                sto.putFile(image).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            sto.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Date date=new Date();
                                    Message mess=new Message(uri.toString(),LoginActivity.mAuth.getUid(),date.getTime(),"image");
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
                        }
                    }
                });
            }
        }
    }
}