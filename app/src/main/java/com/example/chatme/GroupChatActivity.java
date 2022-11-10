package com.example.chatme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.chatme.Fragment.ChatAdapter;
import com.example.chatme.Models.Message;
import com.example.chatme.databinding.ActivityGroupChatBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class GroupChatActivity extends AppCompatActivity {

    ActivityGroupChatBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityGroupChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(GroupChatActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        final FirebaseDatabase database=FirebaseDatabase.getInstance();
        final ArrayList<Message> messageModel=new ArrayList<>();

        final String senderId= FirebaseAuth.getInstance().getUid();
        binding.Username.setText("Group Chat");

        final ChatAdapter adapter=new ChatAdapter(messageModel,this);
        binding.chatRecyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        binding.chatRecyclerView.setLayoutManager(layoutManager);


        database.getReference().child("Group Chat")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                messageModel.clear();
                                for (DataSnapshot dataSnapshot :snapshot.getChildren()){
                                    Message model=dataSnapshot.getValue(Message.class);
                                    messageModel.add(model);

                                }
                                adapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String message=binding.enterMessage.getText().toString();
                final Message model=new Message(senderId,message);
                model.setTimestamp(new Date().getTime());

                binding.enterMessage.setText("");
                database.getReference().child("Group Chat")
                        .push()
                        .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(GroupChatActivity.this, "Message send.", Toast.LENGTH_SHORT).show();
                                
                            }
                        });
            }
        });
    }
}