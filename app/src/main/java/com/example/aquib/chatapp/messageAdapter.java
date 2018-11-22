package com.example.aquib.chatapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class messageAdapter extends RecyclerView.Adapter<messageAdapter.messageViewHolder> {

    List<messages> list ;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    String from;

    public messageAdapter(List<messages> list){

        this.list = list;

    }

    @Override
    public messageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


          databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");


            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_single_layout,parent,false);

            return new messageViewHolder(v);




    }

    @Override
    public void onBindViewHolder(@NonNull final messageViewHolder holder, int position) {

        firebaseAuth = FirebaseAuth.getInstance();

        final messages c = list.get(position);
        //String type = c.getType();
        from = c.getFrom();
        final long lg = c.getTime();

        databaseReference.child(from).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name=dataSnapshot.child("user").getValue().toString();
                String image=dataSnapshot.child("thumb_image").getValue().toString();
                holder.textView1.setText(name);
                holder.textView.setText(c.getMessage());
                GetTimeAgo getTimeAgo = new GetTimeAgo();
                holder.textView2.setText(String.valueOf(getTimeAgo.getTimeAgo(lg)));
                Picasso.with(holder.circleImageView.getContext()).load(image).placeholder(R.drawable.download).into(holder.circleImageView);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//        if(type.equals("text")){
//
//            holder.textView.setText(c.getMessage());
//            holder.imageView.setVisibility(View.INVISIBLE);
//
//        }else{
//
//            holder.imageView.setVisibility(View.VISIBLE);
//            holder.textView.setVisibility(View.INVISIBLE);
//            Picasso.with(holder.circleImageView.getContext()).load(c.getMessage()).placeholder(R.drawable.download).into(holder.imageView);
//        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class messageViewHolder extends RecyclerView.ViewHolder{

        TextView textView;
        CircleImageView circleImageView;
        TextView textView1;
        ImageView imageView;
        TextView textView2;

        public messageViewHolder(View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.textView09);
            textView1 = itemView.findViewById(R.id.textView9);
            circleImageView = itemView.findViewById(R.id.circleImageView2);
            imageView = itemView.findViewById(R.id.imageView4);
            textView2 = itemView.findViewById(R.id.textView14);

        }
    }

}

