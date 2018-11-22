package com.example.aquib.chatapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    Toolbar mToolbar;
    DatabaseReference databaseReference;
    DatabaseReference databaseRef;
    FirebaseAuth firebaseAuth;
    ImageButton sendBtn;
    ImageButton addBtn;
    EditText editText;
    FirebaseUser currentUser;

//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        FirebaseAuth firebaseAuth1 =FirebaseAuth.getInstance();
//
//        DatabaseReference data = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseAuth1.getCurrentUser().getUid());
//        data.child("online").setValue(true);
//
//    }

    RecyclerView recyclerView;
    List<messages> msg = new ArrayList<>();
    CircleImageView circleImageView1;
    messageAdapter messageAdapter;
    ImageButton imageButton;
    StorageReference mImageStorage;
    int GALLERY_PICK=1;

    TextView textView;
    String current_id;
    String UserChatId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mImageStorage = FirebaseStorage.getInstance().getReference();
        imageButton = findViewById(R.id.imageButton);
        circleImageView1 = findViewById(R.id.circleImage4);
        sendBtn = findViewById(R.id.imageButton2);
        addBtn = findViewById(R.id.imageButton);
        editText = findViewById(R.id.editText);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseAuth.getCurrentUser().getUid());

        databaseRef.child("online").setValue(true);

        Intent in = getIntent();
        mToolbar = findViewById(R.id.mainpage1_toolbar);
        setSupportActionBar(mToolbar);
        textView = findViewById(R.id.textView09);


        //ActionBar actionBar = getSupportActionBar();
//        getSupportActionBar().setTitle(in.getStringExtra("user_name"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayShowCustomEnabled(true); // To add customView

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.chat_custom_bar,null);

        getSupportActionBar().setCustomView(view);

        current_id = firebaseAuth.getCurrentUser().getUid();
        UserChatId = in.getStringExtra("user_id");

        TextView dispTextView = findViewById(R.id.textView8);
        final TextView lastseenTextView = findViewById(R.id.textView11);

        dispTextView.setText(in.getStringExtra("user_name"));


        recyclerView = findViewById(R.id.mChatview);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        messageAdapter = new messageAdapter(msg);

        recyclerView.setAdapter(messageAdapter);
        loadMessage();

        databaseReference.child("Users").child(UserChatId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String bool = dataSnapshot.child("online").getValue().toString();
                String image = dataSnapshot.child("thumb_image").getValue().toString();

                if(bool.equals("true")){

                    lastseenTextView.setText("Online");

                }else {

                    long lg = Long.parseLong(bool);

                    GetTimeAgo getTimeAgo = new GetTimeAgo();

                    lastseenTextView.setText(String.valueOf(getTimeAgo.getTimeAgo(lg)));

                }

                Picasso.with(ChatActivity.this).load(image).placeholder(R.drawable.download).into(circleImageView1);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        databaseReference.child("Chat").child(current_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(!dataSnapshot.hasChild(UserChatId)){

                    Log.d("onDataChange:",UserChatId);

                    Map chatAddmMap = new HashMap();
                    chatAddmMap.put("seen",false);
                    chatAddmMap.put("timestamp",ServerValue.TIMESTAMP);

                    Map chatUserMap = new HashMap();
                    chatUserMap.put("Chat/"+current_id+"/"+UserChatId,chatAddmMap);
                    chatUserMap.put("Chat/"+UserChatId+"/"+current_id,chatAddmMap);

                    databaseReference.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {

                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                            Log.d("onComplete: ", String.valueOf(databaseReference));

                        }
                    });

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(v);
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);

                Toast.makeText(getApplicationContext(),"Sorry App is in Alpha Stage!! this will not Work.",Toast.LENGTH_LONG).show();

            }
        });

    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK){
//
//            Uri imageUri = data.getData();
//
//            final String currentUser1 = "messages/"+current_id+"/"+UserChatId;
//            final String chatRef1 = "messages/"+UserChatId+"/"+current_id;
//
//            final DatabaseReference data1 = databaseReference.child("messages").child(current_id).child(UserChatId).push();
//
//            final String push_id1 = data1.getKey();
//
//            StorageReference filepath = mImageStorage.child("message_images").child( push_id1 + ".jpg");
//
//            filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//
//                    if(task.isSuccessful()){
//
//                        String download_url = task.getResult().getDownloadUrl().toString();
//
//                        Map msgMap = new HashMap();
//                        msgMap.put("from",current_id);
//                        msgMap.put("message",download_url);
//                        msgMap.put("seen",false);
//                        msgMap.put("type","image");
//                        msgMap.put("time",ServerValue.TIMESTAMP);
//
//                        Map messageUser = new HashMap();
//                        messageUser.put(currentUser1+"/"+push_id1,msgMap);
//                        messageUser.put(chatRef1+"/"+push_id1,msgMap);
//
//
//                        databaseReference.updateChildren(messageUser, new DatabaseReference.CompletionListener() {
//                            @Override
//                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//
//                                if(databaseError != null){
//
//                                    Log.d("CHAT_LOG", databaseError.getMessage().toString());
//
//                                }
//
//                            }
//                        });
//
//
//                    }
//
//                }
//            });
//
//        }
//
//
//    }

    public void loadMessage(){


        DatabaseReference Reference =FirebaseDatabase.getInstance().getReference().child("messages").child(current_id).child(UserChatId);

        Reference.addChildEventListener(new ChildEventListener() {
                @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                messages m = dataSnapshot.getValue(messages.class);

                msg.add(m);
                messageAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(msg.size()-1);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void sendMessage(View view){

        String currentUser = "messages/"+current_id+"/"+UserChatId;
        String chatRef = "messages/"+UserChatId+"/"+current_id;

        final String message = editText.getText().toString();

        //push create different id so that sender and receiver multi msg
        DatabaseReference data = databaseReference.child("messages").child(current_id).child(UserChatId).push();

        if(!TextUtils.isEmpty(message)){

            String push_id = data.getKey();

            Map msgMap = new HashMap();
            msgMap.put("from",current_id);
            msgMap.put("message",message);
            msgMap.put("seen",false);
            msgMap.put("type","text");
            msgMap.put("time",ServerValue.TIMESTAMP);

            Map messageUser = new HashMap();
            messageUser.put(currentUser+"/"+push_id,msgMap);
            messageUser.put(chatRef+"/"+push_id,msgMap);

            databaseReference.child("Chat").child(current_id).child(UserChatId).child("seen").setValue(true);
            databaseReference.child("Chat").child(UserChatId).child(current_id).child("timestamp").setValue(ServerValue.TIMESTAMP);

            databaseReference.child("Chat").child(UserChatId).child(current_id).child("seen").setValue(false);
            databaseReference.child("Chat").child(current_id).child(UserChatId).child("timestamp").setValue(ServerValue.TIMESTAMP);


            databaseReference.updateChildren(messageUser, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                    if(databaseError!=null){
                        Toast.makeText(getApplicationContext(), message,Toast.LENGTH_LONG).show();
                    }

                }
            });

        }
        editText.setText("");


    }

//    public void seenMessage(){
//
//
//        DatabaseReference data = databaseReference.child("messages").child(current_id).child(UserChatId).push();
//
//        String push_id = data.getKey();
//
//        Log.d(TAG, "seenMessage: ");
//
//    }


}
