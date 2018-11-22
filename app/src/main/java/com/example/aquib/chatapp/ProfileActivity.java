package com.example.aquib.chatapp;

import android.app.ProgressDialog;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class ProfileActivity extends AppCompatActivity {

    DatabaseReference onData;
    FirebaseAuth auth;
    DatabaseReference databaseReq;
    DatabaseReference databaseFriend;
    DatabaseReference databaseReference;
    DatabaseReference databaseNotification;
    DatabaseReference mRootref;
    ProgressDialog progressDialog;
    FirebaseUser currentUser;
    String mCurrentState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        auth = FirebaseAuth.getInstance();
        onData = FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getCurrentUser().getUid());

        final String User_Id = getIntent().getStringExtra("user_id");

        mCurrentState = "not_friends";
        final Button Decbutton = findViewById(R.id.button5);
        final Button Reqbutton = findViewById(R.id.button4);
        final TextView user_name = findViewById(R.id.textView5);
        final TextView status_name = findViewById(R.id.textView6);
        final ImageView profile_image = findViewById(R.id.imageView2);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        progressDialog = new ProgressDialog(ProfileActivity.this);

        Log.d("onCreate: ",User_Id);
            databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(User_Id);
            databaseReference.keepSynced(true);
            databaseFriend = FirebaseDatabase.getInstance().getReference().child("Friends");
            databaseReq = FirebaseDatabase.getInstance().getReference().child("Friend_Req");
            databaseNotification = FirebaseDatabase.getInstance().getReference().child("Notification");
            mRootref = FirebaseDatabase.getInstance().getReference();
            progressDialog.setTitle("Loading User Data");
            progressDialog.setMessage("Please wait while we load the data");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String user = dataSnapshot.child("user").getValue().toString();
                    String status = dataSnapshot.child("status").getValue().toString();
                    String image = dataSnapshot.child("image").getValue().toString();

                    user_name.setText(user);
                    status_name.setText(status);

                    Picasso.with(ProfileActivity.this).load(image).into(profile_image);

                    // -------------------- Friend List / Request Feature--------------------

                    databaseReq.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot.hasChild(User_Id)) {

                                String req_type = dataSnapshot.child(User_Id).child("request_type").getValue().toString();

                                if (req_type.equals("received")) {

                                    Reqbutton.setText("Accept Friend Request");
                                    mCurrentState = "req_received";
                                    Decbutton.setVisibility(View.VISIBLE);

                                } else if (req_type.equals("sent")) {

                                    mCurrentState = "req_sent";
                                    Reqbutton.setText("Cancel Friend Request");
                                    Decbutton.setVisibility(View.INVISIBLE);

                                }

                                progressDialog.dismiss();


                            } else {

                                databaseFriend.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        if (dataSnapshot.hasChild(User_Id)) {

                                            Reqbutton.setText("Unfriend this Person");
                                            mCurrentState = "friends";
                                            Decbutton.setVisibility(View.INVISIBLE);

                                        }

                                        progressDialog.dismiss();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }

                            progressDialog.dismiss();

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            // -------------------- Friend Request--------------------

            Reqbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Reqbutton.setEnabled(false);

                    // -------------------- Not Friend State --------------------

                    if (mCurrentState.equals("not_friends")) {

                        DatabaseReference newNotifref = mRootref.child("Notification").child(User_Id).push();
                        String newNotifId = newNotifref.getKey();

                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("from", currentUser.getUid());
                        hashMap.put("type", "request");


                        Map requestMap = new HashMap();
                        requestMap.put("Friend_Req/" + currentUser.getUid() + "/" + User_Id + "/" + "request_type", "sent");
                        requestMap.put("Friend_Req/" + User_Id + "/" + currentUser.getUid() + "/" + "request_type", "received");
                        requestMap.put("Notification/" + User_Id + "/" + newNotifId, hashMap);

                        mRootref.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                if (databaseError == null) {

                                    Reqbutton.setEnabled(true);
                                    Reqbutton.setText("Cancel Friend Request");
                                    Reqbutton.setBackground(getResources().getDrawable(R.color.colorBtn));
                                    mCurrentState = "req_sent";
                                    Decbutton.setVisibility(View.INVISIBLE);


                                } else {

                                    Toast.makeText(ProfileActivity.this, "There was some error in sending request", Toast.LENGTH_SHORT).show();

                                }
                                Reqbutton.setEnabled(true);
                            }
                        });
                    }

                    // -------------------- Cancel Friend Request--------------------

                    if (mCurrentState.equals("req_sent")) {

                        Map map = new HashMap();
                        map.put("Friend_Req/" + currentUser.getUid() + "/" + User_Id, null);
                        map.put("Friend_Req/" + User_Id + "/" + currentUser.getUid(), null);

                        mRootref.updateChildren(map, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                if (databaseError == null) {
                                    Reqbutton.setEnabled(true);
                                    Reqbutton.setText("Send Friend Request");
                                    mCurrentState = "not_friends";
                                    Decbutton.setVisibility(View.INVISIBLE);

                                } else {
                                    Toast.makeText(ProfileActivity.this, "Error in doing it", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
//
//                      databaseReq.child(currentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
//                          @Override
//                          public void onSuccess(Void aVoid) {
//
//                              databaseReq.child(User_Id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
//                                  @Override
//                                  public void onSuccess(Void aVoid) {
//
//                                      Reqbutton.setEnabled(true);
//                                      Reqbutton.setText("Send Friend Request");
//                                      mCurrentState="not_friends";
//                                      Decbutton.setVisibility(View.INVISIBLE);
//
//                                  }
//                              });
//                          }
//                      });
                    }

                    // -------------------- Req Received State --------------------

                    if (mCurrentState.equals("req_received")) {

                        final String currentDate = DateFormat.getDateInstance().format(new Date());

                        Map map = new HashMap();
                        map.put("Friends/" + currentUser.getUid() + "/" + User_Id + "/date", currentDate);
                        map.put("Friends/" + User_Id + "/" + currentUser.getUid() + "/date", currentDate);
                        map.put("Friend_Req/" + currentUser.getUid() + "/" + User_Id, null);
                        map.put("Friend_Req/" + User_Id + "/" + currentUser.getUid(), null);

                        mRootref.updateChildren(map, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                if (databaseError == null) {

                                    Reqbutton.setText("Unfriend this Person");
                                    Reqbutton.setEnabled(true);
                                    mCurrentState = "friends";
                                    Decbutton.setVisibility(View.INVISIBLE);


                                } else {

                                    Toast.makeText(ProfileActivity.this, "Error in Receiving", Toast.LENGTH_SHORT).show();

                                }

                            }
                        });

//
//                    databaseFriend.child(currentUser.getUid()).child(User_Id).setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//
//                            databaseFriend.child(User_Id).child(currentUser.getUid()).setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void aVoid) {
//
//                                    databaseReq.child(currentUser.getUid()).child(User_Id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
//                                        @Override
//                                        public void onSuccess(Void aVoid) {
//
//                                            databaseReq.child(User_Id).child(currentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                @Override
//                                                public void onSuccess(Void aVoid) {
//
//                                                    Reqbutton.setText("Unfriend this Person");
//                                                    Reqbutton.setEnabled(true);
//                                                    mCurrentState = "friends";
//                                                    Decbutton.setVisibility(View.INVISIBLE);
//
//
//                                                }
//                                            });
//                                        }
//                                    });
//                                }
//                            });
//                        }
//                    });
                    }

                    //----------------- UnFriend ----------------------

                    if (mCurrentState.equals("friends")) {

                        Map map = new HashMap();

                        map.put("Friends/" + currentUser.getUid() + "/" + User_Id, null);
                        map.put("Friends/" + User_Id + "/" + currentUser.getUid(), null);

                        mRootref.updateChildren(map, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                if (databaseError == null) {

                                    mCurrentState = "not_friends";
                                    Reqbutton.setEnabled(true);
                                    Reqbutton.setText("Send Friend Request");
                                    Decbutton.setVisibility(View.INVISIBLE);


                                } else {

                                    Toast.makeText(ProfileActivity.this, databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();

                                }

                            }
                        });

                    }

                }
            });

            Decbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Map map = new HashMap();
                    map.put("Friend_Req/" + currentUser.getUid() + "/" + User_Id, null);
                    map.put("Friend_Req/" + User_Id + "/" + currentUser.getUid(), null);

                    mRootref.updateChildren(map, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if (databaseError == null) {

                                Decbutton.setVisibility(View.INVISIBLE);
                                Reqbutton.setText("Send Friend Request");
                                mCurrentState = "not_friends";

                            } else {

                                Toast.makeText(ProfileActivity.this, "Error in the dec btn", Toast.LENGTH_SHORT).show();

                            }

                        }
                    });

                }
            });
    }
}


//
//databaseReq.child(currentUser.getUid()).child(User_Id).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
//@Override
//public void onComplete(@NonNull Task<Void> task) {
//
//        if(task.isSuccessful()){
//
//        databaseReq.child(User_Id).child(currentUser.getUid()).child("request_type").setValue("received")
//        .addOnSuccessListener(new OnSuccessListener<Void>() {
//@Override
//public void onSuccess(Void aVoid) {
//
//        HashMap<String,String> hashMap = new HashMap<>();
//        hashMap.put("from",currentUser.getUid());
//        hashMap.put("type","request");
//
//        databaseNotification.child(User_Id).push().setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
//@Override
//public void onSuccess(Void aVoid) {
//
//        Reqbutton.setEnabled(true);
//        Reqbutton.setText("Cancel Friend Request");
//        Reqbutton.setBackground(getResources().getDrawable(R.color.colorBtn));
//        mCurrentState="req_sent";
//        Decbutton.setVisibility(View.INVISIBLE);
//
//        }
//        });
//
//        //   Toast.makeText(ProfileActivity.this,"Request sent Successfully",Toast.LENGTH_SHORT);
//
//        }
//        });
//        }else {
//
//        Toast.makeText(ProfileActivity.this,"Failed to send the Request",Toast.LENGTH_SHORT).show();
//
//
//        }
//
//        }
//        });

//



//    Map map = new HashMap();
//
//                    map.put("Friend_Req/"+currentUser.getUid()+"/"+User_Id,null);
//                            map.put("Friend_Req/"+User_Id+"/"+currentUser.getUid(),null);
//
//                            mRootref.updateChildren(map, new DatabaseReference.CompletionListener() {
//@Override
//public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//
//        if(databaseError==null){
//
//        Reqbutton.setText(" Friend Request");
//        mCurrentState="not_friends";
//        Decbutton.setVisibility(View.INVISIBLE);
//
//        }else{
//        Toast.makeText(ProfileActivity.this,"Error",Toast.LENGTH_SHORT).show();
//        }
//        }
//        });


//
//if(mCurrentState.equals("req_sent")){
//
//        databaseReq.child(currentUser.getUid()).child(User_Id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
//@Override
//public void onSuccess(Void aVoid) {
//
//        databaseReq.child(User_Id).child(currentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
//@Override
//public void onSuccess(Void aVoid) {
//
//        Reqbutton.setEnabled(true);
//        Reqbutton.setText("Send Friend Request");
//        mCurrentState="not_friends";
//        Decbutton.setVisibility(View.INVISIBLE);
//
//        }
//        });
//
//
//        }
//        });
//
//        }