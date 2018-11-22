package com.example.aquib.chatapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    Toolbar mToolbar;
    DatabaseReference databaseReference;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    CircleImageView circleImageView;

    private ViewPager viewPager;
    private SectionPagerAdapter mSectionPagerAdaper;
    private TabLayout mTablayout;
    FirebaseUser currentUser;
    TextView textView;
    String image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigationView = findViewById(R.id.navView);
        View header = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);
        textView = header.findViewById(R.id.textView13);

        circleImageView = header.findViewById(R.id.circle_image01);

        drawerLayout = findViewById(R.id.drawLay);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);

        DrawerArrowDrawable arrow = actionBarDrawerToggle.getDrawerArrowDrawable();
        arrow.setColor(getResources().getColor(R.color.colorWhite));

        //toolbar
        mToolbar = findViewById(R.id.mainpage_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("ChatiyApa");
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser()!=null) {

            databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    textView.setText(dataSnapshot.child("user").getValue().toString());


                    image = dataSnapshot.child("thumb_image").getValue().toString();

                    if(!image.equals("default")){

                        Log.d("onDataChange: ","Hlo");

                        Picasso.with(MainActivity.this).load(image).placeholder(R.drawable.download).into(circleImageView);

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
        //tabs
        viewPager = findViewById(R.id.main_viewpager);
        mSectionPagerAdaper = new SectionPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(mSectionPagerAdaper);

        mTablayout = findViewById(R.id.main_tab);
        mTablayout.setupWithViewPager(viewPager);



    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();


        if(currentUser == null) {

            sendToStart();

        }else{

            databaseReference.child("online").setValue(true);

        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(currentUser!=null) {

            databaseReference.child("online").setValue(ServerValue.TIMESTAMP);

        }

    }

    private void sendToStart() {

        Intent in = new Intent(MainActivity.this,StartActivity.class);
        startActivity(in);
        finish();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(actionBarDrawerToggle.onOptionsItemSelected(item)){

            return true;
        }


        return  true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {



        switch (item.getItemId()){

            case R.id.allUser :
                Intent in = new Intent(MainActivity.this,UsersActivity.class);
                startActivity(in);
                break;

            case R.id.setting_stats:
                Intent in1 = new Intent(MainActivity.this,SettingActivity.class);
                startActivity(in1);
                break;

            case R.id.setPro:
                Intent intent = new Intent(MainActivity.this,setProfile.class);
                startActivity(intent);
                break;

            case R.id.main_logout:
                databaseReference.child("online").setValue(ServerValue.TIMESTAMP);
                FirebaseAuth.getInstance().signOut();
                sendToStart();
                break;
        }

        return true;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}