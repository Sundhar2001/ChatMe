package com.example.chatme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.chatme.Fragment.ChatAdapter;
import com.example.chatme.Models.Message;
import com.example.chatme.databinding.ActivityChatDetailsBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class ChatDetailsActivity extends AppCompatActivity {

    ActivityChatDetailsBinding binding;
    FirebaseAuth mAuth;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding=ActivityChatDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        database=FirebaseDatabase.getInstance();
        mAuth=FirebaseAuth.getInstance();

        final String senderId=mAuth.getUid();
        String receiverId=getIntent().getStringExtra("userId");
        String userName=getIntent().getStringExtra("userName");
        String profilePick=getIntent().getStringExtra("profilePick");

        binding.Username.setText(userName);
        Picasso.get().load(profilePick).placeholder(R.drawable.avathar1).into(binding.profileImage);

        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ChatDetailsActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        final ArrayList<Message> messages=new ArrayList<>();
        final ChatAdapter chatAdapter=new ChatAdapter(messages,this,receiverId);

        binding.chatRecyclerView.setAdapter(chatAdapter);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        binding.chatRecyclerView.setLayoutManager(layoutManager);

        final String senderRoom = senderId + receiverId;
        final  String receiverRoom = receiverId + senderId;

        database.getReference().child("chats")
                        .child(senderRoom).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messages.clear();
                        for (DataSnapshot snapshot1 : snapshot.getChildren()){
                            Message messageModels=snapshot1.getValue(Message.class);
                            messageModels.setMessageId(snapshot1.getKey());
                            messages.add(messageModels);
                        }
                        chatAdapter.notifyItemChanged(messages.indexOf(snapshot.getKey()));

//                        chatAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        binding.imageViewMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popupMenu=new PopupMenu(ChatDetailsActivity.this,binding.imageViewMenu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){

                            case R.id.settings:
                                Intent intent2=new Intent(ChatDetailsActivity.this,SettingsActivity.class);
                                startActivity(intent2);
                                break;

                            case R.id.group:
                                Intent intent3=new Intent(ChatDetailsActivity.this,GroupChatActivity.class);
                                startActivity(intent3);
                                break;

                        }
                        return false;
                    }
                });

                popupMenu.inflate(R.menu.menu);
                popupMenu.show();
                
            }
        });

        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message=binding.enterMessage.getText().toString();
                final Message messageModel=new Message(senderId,message);
                messageModel.setTimestamp(new Date().getTime());


                if (binding.enterMessage.getText().toString().equals("")){


                }else {

                    //STORE MESSAGE TO DATABASE
                    database.getReference().child("chats")
                            .child(senderRoom)
                            .push()
                            .setValue(messageModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                    database.getReference().child("chats")
                                            .child(receiverRoom)
                                            .push()
                                            .setValue(messageModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {

                                                }
                                            });
                                }
                            });
                    binding.enterMessage.setText("");
                }
            }
        });

    }

}