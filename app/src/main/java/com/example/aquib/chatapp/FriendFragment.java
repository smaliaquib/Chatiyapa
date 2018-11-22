package com.example.aquib.chatapp;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendFragment extends Fragment {

    RecyclerView mFriendList;
    DatabaseReference databaseReference;
    DatabaseReference mUserdatabaseRef;
    FirebaseAuth mAuth;
    String currentUser;
    View mainView;
    CircleImageView circleImageView;


    public FriendFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mainView = inflater.inflate(R.layout.fragment_friend, container, false);
        circleImageView = mainView.findViewById(R.id.circleImageView101);

        mFriendList = mainView.findViewById(R.id.friend_list);
        mAuth = FirebaseAuth.getInstance();

        currentUser = mAuth.getCurrentUser().getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Friends").child(currentUser);
        databaseReference.keepSynced(true);

        mUserdatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mUserdatabaseRef.keepSynced(true);


        mFriendList.setHasFixedSize(true);
        mFriendList.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inflate the layout for this fragment
        return mainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Friends,FriendViewholder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Friends, FriendViewholder>(
                Friends.class,
                R.layout.users_single_layout,
                FriendViewholder.class,
                databaseReference
        ) {
            @Override
            protected void populateViewHolder(final FriendViewholder viewHolder, Friends model, int position) {

                viewHolder.setDate(model.getDate());

                final String listId = getRef(position).getKey();

                Log.d("populateViewHolder: ",listId+model.getDate());

                mUserdatabaseRef.child(listId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String name = dataSnapshot.child("user").getValue().toString();


                        String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                        viewHolder.setName(name);
                        viewHolder.setThumb(thumb_image,getContext());

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                String[] alert = new String[]{"Open Profile","Send Message"};

                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                                builder.setTitle("Select the option");

                                builder.setItems(alert, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        switch (which){

                                            case 0:

                                                Intent intent = new Intent(getContext(),ProfileActivity.class);
                                                intent.putExtra("user_id",listId);
                                                startActivity(intent);
                                                  break;

                                            case 1:
                                                Intent intent1 = new Intent(getContext(),ChatActivity.class);
                                                intent1.putExtra("user_id",listId);
                                                intent1.putExtra("user_name",name);
                                                startActivity(intent1);
                                                break;

                                                default:
                                                    break;
                                        }

                                    }
                                });

                                builder.show();
                            }
                        });


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        };

        mFriendList.setAdapter(firebaseRecyclerAdapter);
}


   public static class FriendViewholder extends RecyclerView.ViewHolder{

        View mView;

       public FriendViewholder(View itemView){
           super(itemView);

           mView = itemView;

       }

       public void setDate(String date){

           TextView textView = mView.findViewById(R.id.textView4);
           textView.setText(date);

       }

       public void setName(String name){

           TextView textView = mView.findViewById(R.id.textView101);
           textView.setText(name);

       }


         public void setThumb(String thmb, Context ctx){

             CircleImageView circleImageView = mView.findViewById(R.id.circleImageView101);
             Picasso.with(ctx).load(thmb).placeholder(R.drawable.download).into(circleImageView);

        }

   }
}

