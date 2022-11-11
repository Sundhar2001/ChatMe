package com.example.chatme.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatme.ChatDetailsActivity;
import com.example.chatme.Models.Users;
import com.example.chatme.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    ArrayList<Users> list;
    Context context;

    //constructor
    public UsersAdapter(ArrayList<Users> list, Context context) {
        this.list = list;
        this.context = context;
    }

    //3 extends from UsersAdapter
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(context).inflate(R.layout.sample_show_user,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Users users=list.get(position);
        Picasso.get().load(users.getProfilePick()).placeholder(R.drawable.avathar).into(holder.image);
        holder.userName.setText(users.getUserName());

        //TO SET LAST MESSAGE
        FirebaseDatabase.getInstance().getReference().child("chats")
                        .child(FirebaseAuth.getInstance().getUid()+users.getUserId())
                                .orderByChild("timestamp").limitToFirst(1)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.hasChildren()){
                                    for (DataSnapshot snapshot1:snapshot.getChildren()){
                                        holder.lastMessage.setText(snapshot1.child("message").getValue().toString());


                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, ChatDetailsActivity.class);
                intent.putExtra("userId",users.getUserId());
                intent.putExtra("profilePick",users.getProfilePick());
                intent.putExtra("userName",users.getUserName());
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }



    //1
    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView userName,lastMessage;

        //2 extends fom ViewHolder
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image=itemView.findViewById(R.id.profileImage);
            userName=itemView.findViewById(R.id.userNameList);
            lastMessage=itemView.findViewById(R.id.lastMessage);
        }
    }
}
