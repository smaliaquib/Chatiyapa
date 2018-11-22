package com.example.aquib.chatapp;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.VideoView;

public class StartActivity extends AppCompatActivity {

    private VideoView mVedio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        mVedio = findViewById(R.id.videoView);

          mVedio.setVideoURI(Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.videoplayback));
          mVedio.start();

          mVedio.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
              @Override
              public void onPrepared(MediaPlayer mp) {
                  mp.setLooping(true);
              }
          });

    }

    @Override
    protected void onStart() {
        super.onStart();

        mVedio.start();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mVedio.start();
    }

    public void createAc(View view){

        Intent in = new Intent(StartActivity.this,Register.class);
        startActivity(in);

    }

    public void logIn(View view){

        Intent in = new Intent(StartActivity.this, LoginActivity.class);
        startActivity(in);

    }

}
