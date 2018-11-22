package com.example.aquib.chatapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class setProfile extends AppCompatActivity {

    LocationManager LocManage;
    LocationListener LocListen;
    List<Address> list;
    String Deg;
    String Descp;
    String Li[]={"Weather","Location"};

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==1){

            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {

                if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {

                    LocManage.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, LocListen);

                }
            }
        }

    }

    public class weather extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... strings) {

            String res="";
            URL url;
            HttpURLConnection httpURLConnection;

            try{

                url = new URL(strings[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();

                InputStream in = httpURLConnection.getInputStream();

                InputStreamReader in1 = new InputStreamReader(in);

                int data = in1.read();

                while (data!=-1){

                    char c = (char)data;
                    res +=c;
                    data = in1.read();

                }

                return res;

            }catch(Exception e){

                e.printStackTrace();

            }


            return null;


        }

        @Override
        protected void onPostExecute(String Result) {
            super.onPostExecute(Result);


            try {

                JSONObject jsonObject = new JSONObject( Result);
                String weather = jsonObject.getString("weather");
                String main = jsonObject.getString("main");

                Log.d("onPostExecute: ",weather);
                Log.d("onPostExecute: ",main);
                Log.d("onResultExecute: ",Result);

                JSONArray jsonArray = new JSONArray(weather);
               // JSONArray jsonArray1 = new JSONArray(main);

                for(int i=0;i<jsonArray.length();i++){

                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    Descp=jsonObject1.getString("description");
                    Log.d("onPostExecute:",jsonObject1.getString("description"));
                }

                JSONObject jsonObject1 = new JSONObject(main);
                Deg=jsonObject1.getString("temp");

                Log.d("onPostExecute: ",jsonObject1.getString("temp"));


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    class CustomAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return Li.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            String ar[]={"Current Location","Weather"};

            convertView = getLayoutInflater().inflate(R.layout.customlayout,null);

            TextView textView = findViewById(R.id.textView7);
            TextView textView1 = findViewById(R.id.textView18);

            textView.setText(Li[position]);

            Log.d("getView: ", ar[position]+String.valueOf(position)+ar.length);

            return convertView;
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_profile);



        ListView listView = findViewById(R.id.listVie);
        CustomAdapter customAdapter = new CustomAdapter();
        listView.setAdapter(customAdapter);

        weather we = new weather();

        we.execute("https://openweathermap.org/data/2.5/weather?q=jamshedpur&appid=b6907d289e10d714a6e88b30761fae22");

        LocManage = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        LocListen = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                try{

                    list = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);

                    Log.i("onLocationChanged: ",list.get(0).toString());

                    if(list.get(0).getAddressLine(1)!=null){

                        Log.d("onLocationChanged: ",list.get(0).getAddressLine(1));

                    }

                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if(Build.VERSION.SDK_INT<23){

            LocManage.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,LocListen);

        }else {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            } else {

                LocManage.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, LocListen);

            }

        }
    }
}
