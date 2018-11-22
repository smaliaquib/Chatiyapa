package com.example.aquib.chatapp;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

public class StatusActivity extends AppCompatActivity {

    Toolbar mtoolbar;
    TextInputLayout textInputLayout;

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        String value = getIntent().getStringExtra("hey");

        progressDialog = new ProgressDialog(this);

        mtoolbar = findViewById(R.id.mainpage2_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Update Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textInputLayout = findViewById(R.id.updateStatus);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = firebaseUser.getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        textInputLayout.getEditText().setText(value);

    }

    public void updateStatus(View view){

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                progressDialog.setTitle("Updating Status");
                progressDialog.setMessage("Please wait we update the status");
                progressDialog.setCanceledOnTouchOutside(true);
                progressDialog.show();

                String status;
                status = textInputLayout.getEditText().getText().toString();

                databaseReference.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){

                            progressDialog.hide();

                        }else{

                            progressDialog.dismiss();

                        }

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}
