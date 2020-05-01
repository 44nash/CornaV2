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

//import com.r0adkll.slidr.Slidr;
//import com.r0adkll.slidr.model.SlidrInterface;

import com.hbb20.CountryCodePicker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Country extends AppCompatActivity {


    private final static String DEAFULT_INDIA ="https://api.covid19api.com/live/country/india"; //================================
            //"https://api.covid19api.com/live/country/india/status/confirmed";

    //Used to get country with Slug info
    String GET_COUNTRIES = "https://api.covid19api.com/countries";

    //Will append from hashMap
    //String apiStr = "https://api.covid19api.com/live/country/";       //================================


    //  https://api.covid19api.com/total/country/south-africa
    String apiStr = "https://api.covid19api.com/total/country/";

    String countrySlug;

    //String COUNTRY_OF_CHOICE= apiStr+countrySlug+"/status/confirmed"; //=================================
    String COUNTRY_OF_CHOICE= apiStr+countrySlug;

    private TextView mCountry;
    private TextView mCountryCode;
    private TextView mConfirmed;
    private TextView mDeaths;
    private TextView mRecovered;
    private TextView mDate;
    private TextView mActive;

    protected CountryCodePicker mccp;



    //private SlidrInterface slidr;
    private Button button;
    HashMap<String, String> myMap = new HashMap<String, String>();
    //HashMap<String, String> findSlugMap = new HashMap<String, String>();


    HashMap<String, String> temperarySlugHash = new HashMap<String, String>();
    //HashMap<String, String> countryToSlugHash = new HashMap<String, String>();

    //Map<String, String> temperarySlugHash = new HashMap<String, String>();

    Map<String, String> countryCodeHash = new HashMap<String, String>();

    List<String> allMatches = new ArrayList<String>();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.country_display);



        mCountry = findViewById(R.id.Country);
        mCountryCode = findViewById(R.id.CountryCode);
        mConfirmed= findViewById(R.id.Confirmed);
        mDeaths = findViewById(R.id.Deaths);
        mRecovered = findViewById(R.id.Recovered);
        mDate = findViewById(R.id.Date);
        mActive = findViewById(R.id.Active);

        mccp =findViewById(R.id.ccp);
        createsIdToSlugHash(); //creates CountryToSlugHash()
        Log.d("defaultCC",mccp.getDefaultCountryNameCode());

        String openContry = mccp.getSelectedCountryNameCode();

        mccp.setDefaultCountryUsingNameCode("IN");
        setCountryInfoWithThisURL(DEAFULT_INDIA);

        // Country Listenner  ========================================================================
        mccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener(){
            @Override
            public void onCountrySelected() {
                setCountryInfoWithThisURL(makeCountriesURL(mccp.getSelectedCountryNameCode()));
                //setCountryInfoWithThisURL(SOUTH_AFRICA);
                Log.d("myMap", ""+Arrays.asList(myMap));
                Log.d("deltaConfirm","Confirmed: "+myMap.get("Confirmed"));



//                mCountry.setText("Country: "+mccp.getSelectedCountryName());
//                mCountryCode.setText("CountryCode: "+mccp.getSelectedCountryNameCode());
//                mConfirmed.setText("Confirmed: "+myMap.get("Confirmed"));
//                mDeaths.setText(myMap.get("Deaths"));
//                mRecovered.setText(myMap.get("Recovered"));
//                mActive.setText("Active Cases: "+ myMap.get("Active"));
//                mDate.setText(myMap.get("Date"));

            }
        });

/**
 *          The file said this was the right way to a changeListener. It does not work
 *          the code above it the right way to implement this
 *
 *         ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
 *             @Override
 *             public void onCountrySelected(Country selectedCountry) {
 *                 Toast.makeText(getContext(), "Updated " + selectedCountry.getName(), Toast.LENGTH_SHORT).show();
 *             }
 *         });
 */



        // To Main Activity    ========================================================================
        button =findViewById(R.id.buttonTwo);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onStop();
                openMainActivity();

            }
        });










    }





    public void openMainActivity(){
        Intent intent = new Intent(this , WorldMainActivity.class);
        startActivity(intent);

    }

    public String isUnknown(String str){
        if(str ==null){
            return "Unknow Info";
        }else{
            return str;
        }
    }


    public String makeCountriesURL(String countryCode){
        Log.d("CountryName","++@@@@@@"+mccp.getSelectedCountryName());  //fine
        countryCode = countryCode.replace(" ", "");             //fine
        //but first get rid of spaces in the mccp.getSelectedCountryName()
        //  countrySlug =coutryHash.get(mccp.getSelectedCountryName())
        //Log.d("findSlug","8888888888888888888 "+countryCodeHash.get(countryCode));        //fine
        Log.d("idtoslag", ""+Arrays.asList(countryCodeHash));
        countrySlug = countryCodeHash.get(countryCode);

        //String CountryURL= apiStr+countrySlug+"/status/confirmed";                /================================
        String CountryURL= apiStr+countrySlug; //fine
        Log.d("api","55555555555555555"+CountryURL);
        return CountryURL;
    }



    public void setCountryInfoWithThisURL(String CountryUrl){
        // Orginal (gets country information and displays) ========================================================================
        myMap.clear();
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url(CountryUrl)                                                 //this is what I want to set;
                .method("GET", null)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException, IllegalArgumentException{
                if (response.isSuccessful()) {
                    final String myResponse = response.body().string();
                    //String s = myRespone.replaceAll("]", "");
                    String str2 = myResponse;

                    str2 = str2.replace(" ", "");
                    str2 = str2.replace("\n", "");


                    str2 = str2.replace("(", "");
                    str2 = str2.replace(")", "");
                    str2 = str2.replace("[", "");
                    str2 = str2.replace("]", "");
                    str2 = str2.replace("{", "");
                    str2 = str2.replace("}", "");


                    str2 = str2.replace("\"", "");
                    Log.d("str2",str2);


                    String[] arrOfStr = str2.split(",");
                    for (String a : arrOfStr) {
                        Log.d("Mark1","============================================================");
                        //a.replaceAll("\\s+", "");
                        Log.d("aAa",a);
                        String[] putinHash= new String [2];
                        putinHash= a.split(":");
                        try {
                            myMap.put(putinHash[0], putinHash[1]);
                        }catch(Exception e){
                            myMap.put(putinHash[0], "");
                        }

                    }


                    Log.d("myMap in func", ""+Arrays.asList(myMap));
                    Country.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //mTextViewResult.setText(myRespone);
                            //mTextViewResult.setText(myMap.get("CountryCode"));
                            //mTextViewResult.setText(myMap.get("Country"));

                            //mCountry.setText("Country: "+myMap.get("Country"));
                            mCountry.setText("Country: "+mccp.getSelectedCountryName());

                            //mCountryCode .setText("CountryCode: "+myMap.get("CountryCode"));
                            mCountryCode.setText("CountryCode: "+mccp.getSelectedCountryNameCode());

                            mConfirmed.setText("Confirmed Cases: "+isUnknown(myMap.get("Confirmed")));
                            mDeaths.setText("Deaths Cases: "+isUnknown(myMap.get("Deaths")));
                            mRecovered.setText("Recovered Cases: "+ isUnknown(myMap.get("Recovered")));
                            mActive.setText("Active Cases: "+ isUnknown(myMap.get("Active")));
                            mDate.setText("Date: "+isUnknown(myMap.get("Date")));

                            //    [{CityCode=, Status=confirmed, Country=Switzerland, Lon=8.23, City=,
                             //       CountryCode=CH, Province=, Lat=46.82, Cases=29164, Date=2020-04-28T00}]

                        }
                    });

                }
            }

        });

        Log.d("myMap in func", ""+Arrays.asList(myMap));

    }




// few countries are fixed up //Later On will have to fix Province Issue !!!!!!!!!!!!!!!
    public void createsIdToSlugHash(){
        //New make the url from Country slug complete ========================================================================
        OkHttpClient client2 = new OkHttpClient().newBuilder()
                .build();
        Request request2 = new Request.Builder()
                .url(GET_COUNTRIES)
                .method("GET", null)
                .build();

        client2.newCall(request2).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException, IllegalArgumentException{
                if (response.isSuccessful()) {
                    final String myResponse = response.body().string();
                    String str2 = myResponse;

                    str2 = str2.replace("\n", "");

                    str2 = str2.replace("(", "");
                    str2 = str2.replace(")", "");
                    str2 = str2.replace("[", "");
                    str2 = str2.replace("]", "");
                    str2 = str2.replace("\"", "");

                    Matcher matcher = Pattern.compile("\\{(.*?)\\}").matcher(str2);

                    while (matcher.find()) {
                        allMatches.add(matcher.group());
                    }

                    // Iterator<String> crunchifyIterator = crunchifyList.iterator();
                    Iterator<String> iter = allMatches.iterator();

                    while (iter.hasNext()) {
                        System.out.println(iter.next());
                    }
                    System.out.println("+++++++++++++++++++++++++++++");

                    // Above is Good

                    List<String> slugToIdList = new ArrayList<String>();

                    for (String i : allMatches) {
                        int index = allMatches.indexOf(i);
                        System.out.println("********************");
                        System.out.println("i val: " + i);
                        String temp = i.replaceFirst(".*(?=Slug:)", "");
                        allMatches.set(index, temp);
                        System.out.println("i val Changed: " + temp);
                    }

                    for (String i : allMatches) {

                        System.out.println("i final: " + i);
                    }

                    System.out.println("oooooooooooooooooooooooooooooooooooo");

                    for (String i : allMatches) {
                        // str2 = str2.replace("[\\[\\](){}]", "");
                        str2 = i;
                        str2 = str2.replace(" ", "");

                        str2 = str2.replace("{", "");
                        str2 = str2.replace("}", "");

                        // System.out.println(str2);

                        System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");

                        String[] arrOfStr = str2.split(",");
                        for (String a : arrOfStr) {
                            System.out.println("============================================================");
                            a.replaceAll("\\s+", "");
                            System.out.println(a);
                            String[] putinHash = new String[2];
                            putinHash = a.split(":");
                            try {
                                temperarySlugHash.put(putinHash[0], putinHash[1]);
                            } catch (Exception e) {
                                temperarySlugHash.put(putinHash[0], "");
                            }

                            if (temperarySlugHash.size() ==2) {
                                countryCodeHash.put(temperarySlugHash.get("ISO2"), temperarySlugHash.get("Slug"));
                                temperarySlugHash.clear();
                            }

                        }
                    }

                }
            }

        });
    }


}
