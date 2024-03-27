package com.example.firechat.chat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firechat.CoreActivity;
import com.example.firechat.R;
import com.example.firechat.constants.Constant;
import com.example.firechat.home.Home;
import com.example.firechat.home.User;
import com.example.firechat.login.LoginActivity;
import com.example.firechat.notification.ApiService;
import com.example.firechat.notification.Client;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Chat extends CoreActivity {
    public static User receiverUser;
    TextView name;
    EditText sendmess;
    ImageButton sendBut;
    CircleImageView oppAvatar;
    public static User sendUser;
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
    RelativeLayout layoutChat;
    RelativeLayout seperateLine;
    RelativeLayout seperateLine2;
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Bundle receiverData = getIntent().getExtras();
        if(receiverData != null) {
            receiverUser = (User) receiverData.getSerializable("receiverUser");
        }
        mapping();
        Picasso.get().load(receiverUser.getImage()).into(oppAvatar);
        name.setText(receiverUser.getName());
        senderRoom= mAuth.getUid()+receiverUser.getUid();
        receiverRoom=receiverUser.getUid()+mAuth.getUid();
        messageAdapter=new MessageAdapter(getApplicationContext(),listMessage);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(false);
        messagePanel.setLayoutManager(linearLayoutManager);
        messagePanel.setAdapter(messageAdapter);
        DatabaseReference ref=database.getReference().child("user").child(mAuth.getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sendUser = new User();
                sendUser.setName(snapshot.child("name").getValue().toString());
                sendUser.setImage(snapshot.child("image").getValue().toString());
                sendUser.setToken(snapshot.child("token").getValue().toString());
                sendUser.setUid(snapshot.child("uid").getValue().toString());
                sendUser.setEmail(snapshot.child("email").getValue().toString());
                sendUser.setStatus(snapshot.child("status").getValue().toString());
                rName=receiverUser.getName();
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
                    showToast(getApplicationContext(), "Hãy nhập tin nhắn!");
                    return;
                }
                sendmess.setText("");
                Date date=new Date();
                Message mess=new Message(message,mAuth.getUid(),date.getTime(),"text");
                DatabaseReference reference=database.getReference();
                reference.child("chats").child(senderRoom).child("messages").push().setValue(mess)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                database.getReference().child("chats")
                                        .child(receiverRoom).child("messages").push().setValue(mess)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                sendMessageToServer(createMessageForServer(message));
                                                messagePanel.scrollToPosition(listMessage.size() - 1);
                                            }
                                        });
                            }
                        });
            }
        });
        DatabaseReference chatRef=database.getReference().child("chats").child(senderRoom)
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
                messagePanel.scrollToPosition(listMessage.size() - 1);
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
        if(Home.isDarkMode) {
            messagePanel.setBackgroundColor(Color.BLACK);
            layoutChat.setBackgroundColor(getColor(R.color.green_background));
            seperateLine.setBackgroundColor(getColor(R.color.white));
            seperateLine2.setBackgroundColor(getColor(R.color.white));
        }
    }
    public void mapping()
    {
        sendPic=findViewById(R.id.btnSendHinh);
        bck=findViewById(R.id.backButton);
        oppAvatar=findViewById(R.id.circle);
        name=findViewById(R.id.NameView);
        sendmess=findViewById(R.id.edittextNhaptinnhan);
        sendBut=findViewById(R.id.sendButton);
        messagePanel=findViewById(R.id.messagePanel);
        listMessage=new ArrayList<>();
        layoutChat=findViewById(R.id.layoutChat);
        seperateLine = findViewById(R.id.seperateLine);
        seperateLine2 = findViewById(R.id.seperateLine2);
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "PNG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".png",         /* suffix */
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
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.firechat.chat",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityIfNeeded(takePictureIntent, CAMERA_CODE);
            }

        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==CAMERA_CODE&&resultCode==RESULT_OK)
        {
            //Scan new image added
            MediaScannerConnection.scanFile(getApplicationContext(), new String[]{new File(currentPhotoPath).getPath()}, new String[]{"image/png"}, null);
            File f = new File(currentPhotoPath);
            Uri contentUri = Uri.fromFile(f);
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, contentUri);
            getApplicationContext().sendBroadcast(intent);
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
            Date now=new Date();
            String filename=simpleDateFormat.format(now);
            StorageReference sto=storage.getReference("upload").child(Home.currentUser.getUid()+filename);
            sto.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    sto.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Date date=new Date();
                            Message mess=new Message(uri.toString(),mAuth.getUid(),date.getTime(),"image");
                            DatabaseReference reference=LoginActivity.database.getReference();
                            reference.child("chats").child(senderRoom).child("messages").push().setValue(mess)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            database.getReference().child("chats")
                                                    .child(receiverRoom).child("messages").push().setValue(mess)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            sendMessageToServer(createMessageForServer("Sent a picture!"));
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
//                    showToast(getApplicationContext(), "Fail!");
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
                StorageReference sto=storage.getReference("upload").child(Home.currentUser.getUid()+filename);
                sto.putFile(image).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            sto.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Date date=new Date();
                                    Message mess=new Message(uri.toString(),mAuth.getUid(),date.getTime(),"image");
                                    DatabaseReference reference=database.getReference();
                                    reference.child("chats").child(senderRoom).child("messages").push().setValue(mess)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    database.getReference().child("chats")
                                                            .child(receiverRoom).child("messages").push().setValue(mess)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    sendMessageToServer(createMessageForServer("Sent a picture!"));
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

    private String createMessageForServer(String message){
        JSONObject body = new JSONObject();
        try{
            JSONArray tokens = new JSONArray();
            tokens.put(receiverUser.getToken());
            JSONObject data = new JSONObject();
            data.put(Constant.KEY_FCM_TOKEN, sendUser.getToken());
            data.put(Constant.KEY_MESSAGE, message);
            data.put(Constant.KEY_USER_ID, sendUser.getUid());
            data.put(Constant.KEY_NAME, sendUser.getName());
            data.put(Constant.KEY_AVATAR, sendUser.getImage());
            data.put(Constant.KEY_EMAIL, sendUser.getEmail());

            body.put(Constant.REMOTE_MSG_DATA, data);
            body.put(Constant.REMOTE_MSG_REGISTRATION_IDS, tokens);
        }catch(Exception e){
            e.printStackTrace();
        }
        return body.toString();
    }
    private void sendMessageToServer(String message){
        Client.getClient(Constant.FCM_URL).create(ApiService.class).sendMessage(Constant.getRemoteMsgHeaders(), message)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call,@NonNull Response<String> response) {
                        if(response.isSuccessful()){
                            try{
                                if(response.body() != null) {
                                    JSONObject responseJson = new JSONObject(response.body());
                                    JSONArray results = responseJson.getJSONArray("results");
                                    if(responseJson.getInt("failure") == 1){
                                        JSONObject error = (JSONObject) results.get(0);
//                                        showToast(getApplicationContext(), "error: "+error.getString("error"));
                                    }
                                }
                            } catch(JSONException e){
                                e.printStackTrace();
                            }
                        } else {
//                            showToast(getApplicationContext(), "Error: "+ response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call,@NonNull Throwable t) {
//                        showToast(getApplicationContext(), t.getMessage());
                    }
                });
    }
    public static void showToast(Context context,String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}