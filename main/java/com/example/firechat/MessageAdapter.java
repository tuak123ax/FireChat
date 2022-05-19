package com.example.firechat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter {
    int send=1;
    int receive=2;
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
            View view= LayoutInflater.from(context).inflate(R.layout.receiver_layout,parent,false);
            return  new ReceiverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message=listMessages.get(position);
        if(holder.getClass()==SenderViewHolder.class)
        {
            SenderViewHolder senderViewHolder= (SenderViewHolder) holder;
            senderViewHolder.content.setText(message.getMessage());
            Picasso.get().load(Home.userAvatar).into(senderViewHolder.avatar);
        }
        else
        {
            ReceiverViewHolder receiverViewHolder= (ReceiverViewHolder) holder;
            receiverViewHolder.content.setText(message.getMessage());
            Picasso.get().load(Chat.oppImage).into(receiverViewHolder.avatar);
        }
    }

    @Override
    public int getItemCount() {
        return listMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message=listMessages.get(position);
        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(message.senderId))
        {
            return send;
        }
        return receive;
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
}
