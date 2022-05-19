package com.example.firechat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.zip.Inflater;

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
                Intent intent=new Intent(context,Chat.class);
                intent.putExtra("Name",user.getName());
                intent.putExtra("uid",user.getUid());
                intent.putExtra("image",user.getImage());
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
