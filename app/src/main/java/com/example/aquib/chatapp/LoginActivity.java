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
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {

    private ImageButton imageButton;
    private Toolbar mtoolbar;

    private FirebaseAuth mAuth;

    DatabaseReference databaseReferenceCurrent;
    private TextInputLayout logEmail;
    private TextInputLayout logPass;
    private ProgressDialog mprogress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        imageButton = findViewById(R.id.imageButton6);
        mAuth = FirebaseAuth.getInstance();

        databaseReferenceCurrent = FirebaseDatabase.getInstance().getReference().child("Users");

        mprogress = new ProgressDialog(this);

        mtoolbar = findViewById(R.id.mainpage1_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        logEmail = findViewById(R.id.logEmail);
        logPass = findViewById(R.id.logPass);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(LoginActivity.this,Register.class);
                startActivity(in);

            }
        });

    }

    public void log(View view){

        String lg_Email = logEmail.getEditText().getText().toString();
        String lg_Pass = logPass.getEditText().getText().toString();

        if(!TextUtils.isEmpty(lg_Email) && !TextUtils.isEmpty(lg_Pass)) {

            mprogress.setTitle("Logging In");
            mprogress.setMessage("Please wait while we check your credentials.");
            mprogress.setCanceledOnTouchOutside(false);
            mprogress.show();

            logInUser(lg_Email, lg_Pass);

        }else {

            Toast.makeText(this,"Error",Toast.LENGTH_LONG).show();

        }
    }

    private void logInUser(String lg_email, String lg_pass) {

        mAuth.signInWithEmailAndPassword(lg_email,lg_pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    String userId = mAuth.getCurrentUser().getUid();
                    String tokenId = FirebaseInstanceId.getInstance().getToken();

                    databaseReferenceCurrent.child(userId).child("device_token").setValue(tokenId).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            //Sign in success
                            mprogress.dismiss();
                            Intent in = new Intent(LoginActivity.this,MainActivity.class);
//                    in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(in);
                            finish();

                        }
                    });
                }else {
                    //Sign in error
                    mprogress.hide();
                    Toast.makeText(LoginActivity.this,"Cannot Sign in Please check the form",Toast.LENGTH_LONG).show();

                }

            }
        });
    }

}
