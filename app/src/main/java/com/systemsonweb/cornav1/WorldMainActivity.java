package com.systemsonweb.cornav1;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

public class WorldMainActivity extends AppCompatActivity {

    private final static String CORONA_URI = "https://api.covid19api.com/";

    private final static String CORONA_URI_SUMMARY ="https://api.covid19api.com/summary";

    HashMap<String, String> myMap = new HashMap<String, String>();

    // For Some reason Data became Unreachable
    //private final static String UNITED_STATES ="https://api.covid19api.com/live/country/united-states/status/confirmed";

    private TextView  mNewConfirmed;
    private TextView  mTotalConfirmed;
    private TextView  mNewDeaths;
    private TextView  mTotalDeaths;
    private TextView  mNewRecovered;
    private TextView  mTotalRecovered;
    //private SlidrInterface slidr;

    private Button button;



    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.world_activity_main);


        mNewConfirmed = findViewById(R.id.NewConfirmed);
        mTotalConfirmed=findViewById(R.id.TotalConfirmed);
        mNewDeaths= findViewById(R.id.NewDeaths);
        mTotalDeaths= findViewById(R.id.TotalDeaths);
        mNewRecovered= findViewById(R.id.NewRecovered);
        mTotalRecovered= findViewById(R.id.TotalRecovered);


        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url(CORONA_URI_SUMMARY)
                .method("GET", null)
                .build();
        Log.d("", "Did Request ");

        client.newCall(request).enqueue(new Callback(){
            @Override
            public void onFailure(Call call, IOException e){
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException{
                if(response.isSuccessful()){
                    final String myResponse = response.body().string();

                    String str2 = myResponse;

                    str2 = str2.replace(" ", "");
                    str2 = str2.replace("\n", "");


                    str2 = str2.replace("(", "");
                    str2 = str2.replace(")", "");
                    str2 = str2.replace("[", "");
                    str2 = str2.replace("]", "");
                    str2 = str2.replace("{", "");
                    str2 = str2.replace("}", "");
                    str2 = str2.replace("NewConfirmed", "");



                    str2 = str2.replace("\"", "");
                    str2 = str2.substring(0, 6) + str2.substring(6 + 1);
                    Log.d("str2",str2);

                    String[] arrOfStr = str2.split(",");
                    for (String a : arrOfStr) {
                        Log.d("Mark","============================================================");
                        //a.replaceAll("\\s+", "");
                        Log.d("bBb","Start="+a);
                        String[] putinHash= new String [2];
                        putinHash= a.split(":");
                        if(!putinHash[0].equals("Countries")) {
                            try {
                                myMap.put(putinHash[0], putinHash[1]);
                            } catch (Exception e) {
                                myMap.put(putinHash[0], "");
                            }
                        }else{
                            break;
                        }


                    }

                    Log.d("Final Map",Arrays.asList(myMap).toString());
                    WorldMainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //mNewConfirmed.setText(myResponse);
                            mNewConfirmed.append(myMap.get("Global"));
                            mTotalConfirmed.append(myMap.get("TotalConfirmed"));
                            mNewDeaths.append(myMap.get("NewDeaths"));
                            mTotalDeaths.append(myMap.get("TotalDeaths"));
                            mNewRecovered.append(myMap.get("NewRecovered"));
                            mTotalRecovered.append(myMap.get("TotalRecovered"));

                        }
                    });

                }
            }

        });

        //slidr = Slidr.attach(this);

        button =findViewById(R.id.buttonOne);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCountry();
            }
        });

    }


    public void openCountry(){
        Intent intent = new Intent(this , Country.class);
        startActivity(intent);

    }







}
