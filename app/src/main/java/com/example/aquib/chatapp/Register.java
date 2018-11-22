package com.example.aquib.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class Register extends AppCompatActivity {

    TextInputLayout dispName;
    TextInputLayout dispEmail;
    TextInputLayout dispPass;
    Button btnRegister;
    Button buttonmem;

    //Firebase Auth
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private Toolbar mtoolbar;

    //Progress
    private ProgressDialog mprogress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mprogress = new ProgressDialog(this);

        buttonmem = findViewById(R.id.button6);

        //toolbar set
        mtoolbar = findViewById(R.id.mainpage_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //Registration Field
        dispName =  findViewById(R.id.DispName);
        dispEmail = findViewById(R.id.DispEmail);
        dispPass = findViewById(R.id.DispPass);
        btnRegister = findViewById(R.id.DispRegister);

        buttonmem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(Register.this,LoginActivity.class);
                startActivity(in);
            }
        });

    }

    public void createAC(View view){

        String getName = dispName.getEditText().getText().toString();
        String getEmail = dispEmail.getEditText().getText().toString();
        String getPass = dispPass.getEditText().getText().toString();

        if(!TextUtils.isEmpty(getEmail)&& !TextUtils.isEmpty(getName) && !TextUtils.isEmpty(getPass)) {

            mprogress.setTitle("Registering User");
            mprogress.setMessage("Please wait while we create your account!!");
            mprogress.setCanceledOnTouchOutside(false);
            mprogress.show();

            register_user(getName, getEmail, getPass);

        }else{

            Toast.makeText(this,"Error",Toast.LENGTH_LONG).show();
        }
    }

    private void register_user(final String Name, String Email, String Pass) {

        mAuth.createUserWithEmailAndPassword(Email,Pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    FirebaseUser currentuser = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = currentuser.getUid();
                    String tokenId = FirebaseInstanceId.getInstance().getToken();

                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                    HashMap<String,String>hashMap = new HashMap<>();
                    hashMap.put("device_token",tokenId);
                    hashMap.put("user",Name);
                    hashMap.put("status","Hi there, I'm using Chat app");
                    hashMap.put("image","default");
                    hashMap.put("thumb_image","default");

                    mDatabase.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){

                                mprogress.dismiss();
                                Intent in = new Intent(Register.this,MainActivity.class);
          //                    in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(in);
                                finish();

                            }

                        }
                    });

                }else {
                    mprogress.hide();
                    Toast.makeText(Register.this,"Error",Toast.LENGTH_LONG).show();
                }

            }
        });


    }

}
