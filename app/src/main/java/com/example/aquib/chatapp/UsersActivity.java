package com.example.aquib.chatapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView mUserList;
    private DatabaseReference mUserdatabase;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        mUserdatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = mUserdatabase.child(firebaseAuth.getCurrentUser().getUid());

        mToolbar = findViewById(R.id.mainpage3_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("All User");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUserList = findViewById(R.id.recyclerUserList);
        mUserList.setHasFixedSize(true);
        mUserList.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();


        final FirebaseRecyclerAdapter<Users,UserViewHolder> recyclerAdapter = new FirebaseRecyclerAdapter<Users, UserViewHolder>(
                Users.class,
                R.layout.users_single_layout,
                UserViewHolder.class,
                mUserdatabase
        ) {
            @Override
            protected void populateViewHolder(UserViewHolder viewHolder, Users model, int position) {


                FirebaseAuth firebaseAuth1 = FirebaseAuth.getInstance();
                final String uid = getRef(position).getKey();

                   if(!firebaseAuth1.getCurrentUser().getUid().equals(uid)) {



                   }
                       viewHolder.setName(model.getUser());
                       viewHolder.setStatus(model.getStatus());
                       viewHolder.setImage(model.getThumb_image(), getApplicationContext());


                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(UsersActivity.this,ProfileActivity.class);
                        intent.putExtra("user_id",uid);
                        startActivity(intent);
                    }
                });
            }
        };

        mUserList.setAdapter(recyclerAdapter);

    }

    public static class UserViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public UserViewHolder(View itemView) {
            super(itemView);

            mView = itemView;


        }
        public void setName(String name){

            TextView mUserName = mView.findViewById(R.id.textView101);
            mUserName.setText(name);

        }
        public void setStatus(String status){

            TextView mUserStatus = mView.findViewById(R.id.textView4);
            mUserStatus.setText(status);

        }
        public void setImage(String image, Context ctx){

            CircleImageView circleImageView = mView.findViewById(R.id.circleImageView101);
            Picasso.with(ctx).load(image).placeholder(R.drawable.download).into(circleImageView);
        }

    }
}
