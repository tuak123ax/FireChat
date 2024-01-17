package com.example.firechat.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firechat.chat.Chat;
import com.example.firechat.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    Context context;
    ArrayList<User> listUsers;
    public UserAdapter(Context context, ArrayList<User> listUsers) {
        this.context=context;
        this.listUsers=listUsers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.user_row,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user=listUsers.get(position);
        holder.name.setText(user.getName());
        holder.status.setText(user.getStatus());
        Picasso.get().load(user.getImage()).into(holder.avatar);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, Chat.class);
                Bundle receiverData = new Bundle();
                receiverData.putSerializable("receiverUser", user);
                intent.putExtras(receiverData);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return listUsers.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        TextView status;
        CircleImageView avatar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.user);
            status=itemView.findViewById(R.id.status);
            avatar=itemView.findViewById(R.id.avtImg);
        }
    }
}
