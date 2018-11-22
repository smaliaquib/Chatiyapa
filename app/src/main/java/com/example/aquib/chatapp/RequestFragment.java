package com.example.aquib.chatapp;


import android.content.Context;
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

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestFragment extends Fragment {

    RecyclerView mFriendReq;
    DatabaseReference databaseReference;
    DatabaseReference databaseReference1;
    FirebaseAuth mAuth;
    String currentUser;
    View mainView;
    CircleImageView circleImageView;

    public RequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

            // Inflate the layout for this fragment

            mainView = inflater.inflate(R.layout.fragment_request, container, false);
            circleImageView = mainView.findViewById(R.id.circleImageView101);

            mAuth = FirebaseAuth.getInstance();
            mFriendReq = mainView.findViewById(R.id.request_list);

            currentUser = mAuth.getCurrentUser().getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference().child("Friend_Req").child(currentUser);
            databaseReference.keepSynced(true);

        Log.d("onCreateView: ",databaseReference.toString());

            databaseReference1 = FirebaseDatabase.getInstance().getReference().child("Users");
            databaseReference1.keepSynced(true);

             mFriendReq.setHasFixedSize(true);
             mFriendReq.setLayoutManager(new LinearLayoutManager(getContext()));

       return mainView;
   }

    @Override
    public void onStart() {
        super.onStart();

          FirebaseRecyclerAdapter<Friend_Req,Friend_ReqViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Friend_Req, Friend_ReqViewHolder>(
                  Friend_Req.class,
                  R.layout.users_single_layout,
                  Friend_ReqViewHolder.class,
                  databaseReference
          ) {
              @Override
              protected void populateViewHolder(final Friend_ReqViewHolder viewHolder, Friend_Req model, int position) {

                  final String listId = getRef(position).getKey();


                  databaseReference1.child(listId).addValueEventListener(new ValueEventListener() {
                      @Override
                      public void onDataChange(DataSnapshot dataSnapshot) {

                          final String user = dataSnapshot.child("user").getValue().toString();
                          String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();
                          String status = dataSnapshot.child("status").getValue().toString();

                          viewHolder.setImage(thumb_image,getContext());
                          viewHolder.setStatus(status);
                          viewHolder.setUser(user);

                          viewHolder.view.setOnClickListener(new View.OnClickListener() {
                              @Override
                              public void onClick(View v) {

                                  Intent in = new Intent(getContext(),ProfileActivity.class);
                                  in.putExtra("user_id",listId);
                                  in.putExtra("user_name",user);
                                  startActivity(in);

                              }
                          });

                      }

                      @Override
                      public void onCancelled(DatabaseError databaseError) {

                      }
                  });

              }
          };


          Log.d(TAG, "onStart: ");
          mFriendReq.setAdapter(firebaseRecyclerAdapter);

    }

    public static class Friend_ReqViewHolder extends RecyclerView.ViewHolder{

        View view;


        public Friend_ReqViewHolder(View itemView) {
            super(itemView);

            view = itemView;

        }

        public void setImage(String image, Context ctx){

            CircleImageView circleImageView = view.findViewById(R.id.circleImageView101);
            Picasso.with(ctx).load(image).placeholder(R.drawable.download).into(circleImageView);

        }

        public void setStatus(String tx){

            TextView txt = view.findViewById(R.id.textView4);
            txt.setText(tx);


        }

        public void setUser(String User){

            TextView txt = view.findViewById(R.id.textView101);
            txt.setText(User);

        }
    }

}
