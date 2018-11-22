package com.example.aquib.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingActivity extends AppCompatActivity {

    DatabaseReference databaseReference;
    TextView mName;
    TextView mStatus;
    FirebaseUser currentuser;
    CircleImageView mDisplayImage;


    ProgressDialog mProgresbar;
    int GALLERY_PICK = 1;

    String status;

    // Storage Firebase
    private StorageReference mImageStorage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mImageStorage = FirebaseStorage.getInstance().getReference();

        mName = findViewById(R.id.displayName);
        mStatus = findViewById(R.id.displayStatus);
        mDisplayImage = findViewById(R.id.circleImageView5);

        currentuser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = currentuser.getUid();


            databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
            databaseReference.keepSynced(true);

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String user = dataSnapshot.child("user").getValue().toString();
                    status = dataSnapshot.child("status").getValue().toString();
                    final String image = dataSnapshot.child("image").getValue().toString();
                    String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                    mName.setText(user);
                    mStatus.setText(status);

                    if (!image.equals("default")) {

                        //   Picasso.with(SettingActivity.this).load(image).placeholder(R.drawable.download).into(mDisplayImage);

                        Picasso.with(SettingActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.download).into(mDisplayImage, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {

                                Picasso.with(SettingActivity.this).load(image).placeholder(R.drawable.download).into(mDisplayImage);

                            }
                        });


                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        public void changeStatus (View view){

            Intent in = new Intent(SettingActivity.this, StatusActivity.class);
            in.putExtra("hey", status);

            startActivity(in);

        }

        public void changeImg (View view){

            Intent in = new Intent();
            in.setType("image/");
            in.setAction(Intent.ACTION_GET_CONTENT);

            startActivityForResult(Intent.createChooser(in, "Select Image"), GALLERY_PICK);

        /*
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(SettingActivity.this);
        */

        }

        @Override
        protected void onActivityResult ( int requestCode, int resultCode, Intent data){
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {

                Uri imageUri = data.getData();

                CropImage.activity(imageUri)
                        .setAspectRatio(1, 1)
                        .start(this);
            }

            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

                CropImage.ActivityResult result = CropImage.getActivityResult(data);

                if (resultCode == RESULT_OK) {

                    mProgresbar = new ProgressDialog(SettingActivity.this);
                    mProgresbar.setTitle("Uploading Image...");
                    mProgresbar.setMessage("Please wait while we Upload the image");
                    mProgresbar.setCanceledOnTouchOutside(true);
                    mProgresbar.show();

                    String uid = currentuser.getUid();

                    Uri resultUri = result.getUri();

                    File thumb_filepath = new File(resultUri.getPath());

                    Bitmap thumb_bitmap = new Compressor(this).setMaxHeight(200).setMaxWidth(200).setQuality(75).compressToBitmap(thumb_filepath);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    final byte[] thumb_byte = baos.toByteArray();

                    StorageReference filepath = mImageStorage.child("profile_images").child(uid + ".jpg");
                    final StorageReference thumbfilepath = mImageStorage.child("profile_images").child("thumbs").child(uid + ".jpg");

                    filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            if (task.isSuccessful()) {

                                final String download_url = task.getResult().getDownloadUrl().toString();

                                UploadTask uploadTask = thumbfilepath.putBytes(thumb_byte);
                                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {

                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {

                                        final String thumb_url = thumb_task.getResult().getDownloadUrl().toString();

                                        if (thumb_task.isSuccessful()) {

                                            Map updatehash = new HashMap();
                                            updatehash.put("image", download_url);
                                            updatehash.put("thumb_image", thumb_url);

                                            databaseReference.updateChildren(updatehash).addOnCompleteListener(new OnCompleteListener() {
                                                @Override
                                                public void onComplete(@NonNull Task task) {

                                                    if(task.isSuccessful()){

                                                        mProgresbar.dismiss();
                                                        Toast.makeText(SettingActivity.this, "Success in Uploading Thumbnail", Toast.LENGTH_LONG).show();

                                                    }

                                                }
                                            });

                                        } else {

                                            Toast.makeText(SettingActivity.this, "Error in Uploading Thumbnail...", Toast.LENGTH_LONG).show();
                                            mProgresbar.dismiss();

                                        }

                                    }
                                });


                            } else {

                                Toast.makeText(SettingActivity.this, "Error in Uploading...", Toast.LENGTH_LONG).show();
                                mProgresbar.dismiss();

                            }

                        }
                    });


                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                    Exception error = result.getError();

                }
            }

        }

    /*
    public static String random(){
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(10);
        char tempChar;
        for(int i=0;i<randomLength;i++){
            tempChar = (char) (generator.nextInt(96)+32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }

    */

}
