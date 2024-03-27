package com.example.firechat.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firechat.R;
import com.example.firechat.home.Home;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter {
    int send=1;
    int receive=2;
    int sendPic=3;
    int receivePic=4;
    Context context;
    ArrayList<Message> listMessages;

    public MessageAdapter() {
    }

    public MessageAdapter(Context context, ArrayList<Message> listMessages) {
        this.context = context;
        this.listMessages = listMessages;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==send)
        {
            View view= LayoutInflater.from(context).inflate(R.layout.sender_layout,parent,false);
            return new SenderViewHolder(view);
        }
        else
        {
            if(viewType==receive)
            {
            View view= LayoutInflater.from(context).inflate(R.layout.receiver_layout,parent,false);
            return  new ReceiverViewHolder(view);
            }
            else
            {
                if(viewType==sendPic)
                {
                    View view= LayoutInflater.from(context).inflate(R.layout.sender_picture,parent,false);
                    return new SenderPicViewHolder(view);
                }
                else
                {
                    View view= LayoutInflater.from(context).inflate(R.layout.receiver_picture,parent,false);
                    return  new ReceiverPicViewHolder(view);
                }
            }
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message=listMessages.get(position);
        if(holder.getClass()==SenderViewHolder.class)
        {
            SenderViewHolder senderViewHolder= (SenderViewHolder) holder;
            senderViewHolder.content.setText(message.getMessage());
            Picasso.get().load(Chat.sendUser.getImage()).into(senderViewHolder.avatar);
        }
        else
        {
            if(holder.getClass()==ReceiverViewHolder.class)
            {
            ReceiverViewHolder receiverViewHolder= (ReceiverViewHolder) holder;
            receiverViewHolder.content.setText(message.getMessage());
            Picasso.get().load(Chat.receiverUser.getImage()).into(receiverViewHolder.avatar);
            }
            else
            {
                if(holder.getClass()==SenderPicViewHolder.class)
                {
                    SenderPicViewHolder senderPicViewHolder= (SenderPicViewHolder) holder;
                    Picasso.get().load(message.message).into(senderPicViewHolder.content);
                    Picasso.get().load(Chat.sendUser.getImage()).into(senderPicViewHolder.avatar);
                }
                else
                {
                    ReceiverPicViewHolder receiverPicViewHolder= (ReceiverPicViewHolder) holder;
                    Picasso.get().load(message.message).into(receiverPicViewHolder.content);
                    Picasso.get().load(Chat.receiverUser.getImage()).into(receiverPicViewHolder.avatar);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return listMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message=listMessages.get(position);
        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(message.senderId)
        &&message.type.equals("text"))
        {
            return send;
        }
        else
        {
            if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(message.senderId)
                    &&message.type.equals("image"))
            {
                return sendPic;
            }
            else
            {
                if(!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(message.senderId)
                        &&message.type.equals("text"))
                {
                    return receive;
                }
                return receivePic;
            }
        }
    }

    class SenderViewHolder extends RecyclerView.ViewHolder {
        CircleImageView avatar;
        TextView content;
        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar=itemView.findViewById(R.id.sender);
            content=itemView.findViewById(R.id.sendText);
        }
    }
    class ReceiverViewHolder extends RecyclerView.ViewHolder {
        CircleImageView avatar;
        TextView content;
        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar=itemView.findViewById(R.id.receiver);
            content=itemView.findViewById(R.id.receiverText);
        }
    }
    class SenderPicViewHolder extends RecyclerView.ViewHolder {
        CircleImageView avatar;
        ImageView content;
        public SenderPicViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar=itemView.findViewById(R.id.sender2);
            content=itemView.findViewById(R.id.sendPic);
        }
    }
    class ReceiverPicViewHolder extends RecyclerView.ViewHolder {
        CircleImageView avatar;
        ImageView content;
        public ReceiverPicViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar=itemView.findViewById(R.id.receiver2);
            content=itemView.findViewById(R.id.receivePic);
        }
    }
}
