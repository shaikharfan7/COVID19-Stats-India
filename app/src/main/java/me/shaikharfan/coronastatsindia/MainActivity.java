package me.shaikharfan.coronastatsindia;

import androidx.annotation.MainThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    public String countryCases, countryCured, countryDeaths, intTotal, localTotal;

    public String stateName,stateName1,stateCases,stateCured,stateDeaths,stateHelpline;

    Spinner stateTxt;
    TextView statename,casevalue,curevalue,deathvalue,hlpValue;

    //ThreadPool Max
    static final int MAX_T = 10;


    //Thread Pool
    ExecutorService pool = Executors.newFixedThreadPool(MAX_T);

    //
    View x;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //UPDATEINPUT
        stateTxt = findViewById(R.id.textState);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.states_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateTxt.setAdapter(adapter);


        //BUTTON
        Button updatebtn = findViewById(R.id.update);

        //State Card
        CardView card = findViewById(R.id.card);
        statename = findViewById(R.id.updatedStateName);
        TextView case1 = findViewById(R.id.cases);
        TextView cured = findViewById(R.id.cured_discharged);
        TextView death = findViewById(R.id.deathState);
        TextView helpline = findViewById(R.id.helpline); //on tap open phoneapp

        case1.setText("Cases");
        cured.setText("Cured");
        death.setText("Deaths");
        helpline.setText("Helpline");

         casevalue = findViewById(R.id.casesval);
         curevalue = findViewById(R.id.cured_dischargedval);
         deathvalue = findViewById(R.id.deathStateVal);
         hlpValue = findViewById(R.id.stateHelplineval);

        //Country Card
        CardView countryCard = findViewById(R.id.card2);
        TextView text1 = findViewById(R.id.countryName);
        TextView localTotalw = findViewById(R.id.localtotal);
        TextView intTotalw = findViewById(R.id.intTotal);
        TextView totalIndia = findViewById(R.id.total);
        TextView curedTotal = findViewById(R.id.curedtotal);
        TextView deathTotal = findViewById(R.id.deathstotal);

        TextView locTotal = findViewById(R.id.localtotalval);
        TextView IntTotVal = findViewById(R.id.intertotal);
        TextView totInd = findViewById(R.id.totalval);
        TextView cureTotal = findViewById(R.id.curedtotalval);
        TextView deadTotal = findViewById(R.id.deathstotalval);

        text1.setText      ("COVID-19 India Stats");
        localTotalw.setText("Indians Affected");
        intTotalw.setText  ("NRI's/Tourists Affected");
        totalIndia.setText ("Total Affected In India");
        curedTotal.setText ("Total Cured In India");
        deathTotal.setText ("Total Deaths In India");



        th1.start();
        th2.start();
        while (th1.isAlive()){
        locTotal.setText(localTotal);
        IntTotVal.setText(intTotal);
        totInd.setText(countryCases);
        cureTotal.setText(countryCured);
        deadTotal.setText(countryDeaths);
        }


        while(th2.isAlive()){
            try{
                    statename.setText(stateName1);
                    casevalue.setText(stateCases);
                    curevalue.setText(stateCured);
                    deathvalue.setText(stateDeaths);
                    hlpValue.setText(stateHelpline);
                }
            catch (Exception e){

                Log.d(TAG,"App Crashed In setFields!");
                e.printStackTrace();

            }
        }


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent viewIntent =
                        new Intent("android.intent.action.VIEW",
                                Uri.parse("https://twitter.com/COVID19_INDIA"));
                startActivity(viewIntent);
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items,menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.item1:
                Toast.makeText(this, "item1", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.item2:
                Toast.makeText(this, "item2", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.item3:
                finish();
                startActivity(getIntent());
                Toast.makeText(this, "Refreshing", Toast.LENGTH_SHORT).show();
                return true;

                default:
                    return super.onOptionsItemSelected(item);
        }

    }

    //Thread1
    Thread th1 = new Thread(new Runnable() {
        @Override
        public void run() {
            String sURL = "https://api.rootnet.in/covid19-in/stats/latest"; //DATA SOURCE URL

            // Connect to the URL using java's native library

            URL url = null;
            try {
                url = new URL(sURL);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            URLConnection request = null;
            try {
                request = url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                request.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Convert to a JSON object to print data
            JsonParser jp = new JsonParser(); //from gson
            JsonElement root = null; //Convert the input stream to a json element
            try {
                root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            JsonObject rootobj = root.getAsJsonObject(); //May be an array, may be an object.
            JsonObject data = rootobj.getAsJsonObject("data"); //grabbing country data
            JsonObject cdata = data.getAsJsonObject("summary");
            Log.d(TAG,"Data: "+data);
            Log.d(TAG,"CountryData: "+cdata);
            setData(cdata);


        }
    });

    public void setData(JsonObject cdata){
        Character tempRep= '"';
        Character empty = Character.MIN_VALUE;
        countryCases = cdata.get("total").toString();
        countryCases = countryCases.replace(tempRep,empty);
        Log.d(TAG,"A : " +countryCases);
        countryCured = cdata.get("discharged").toString();
        countryDeaths = cdata.get("deaths").toString();
        intTotal = cdata.get("confirmedCasesForeign").toString();
        localTotal = cdata.get("confirmedCasesIndian").toString();
    }




    //Thread2 for state data default
    Thread th2 = new Thread(new Runnable() {
        @Override
        public void run() {
            String sURL = "https://exec.clay.run/kunksed/mohfw-covid"; //DATA SOURCE URL
            // Connect to the URL using java's native library
            URL url = null;
            try {
                url = new URL(sURL);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            URLConnection request = null;
            try {
                request = url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                request.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Convert to a JSON object to print data
            JsonParser jp = new JsonParser(); //from gson
            JsonElement root = null; //Convert the input stream to a json element
            try {
                root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            JsonObject rootobj = root.getAsJsonObject(); //May be an array, may be an object.
            JsonObject sdata = rootobj.getAsJsonObject("stateData");
            String goadata = sdata.get("Goa").toString();
            Log.d(TAG, "onDone: " + goadata);
            Log.d(TAG,"Alive In thread 2 ");
            defaultState(sdata);
            String defaultState="Goa";
            setStateData(sdata,defaultState);

        }
        });


    private void defaultState(JsonObject data) {
        String state = "Karnataka";
        JsonObject statedata = (JsonObject) data.get(state);
        String stateDeaths = statedata.get("deaths").toString();
        Log.d(TAG,"State Deaths : " +stateDeaths);

    }

    public void setStateData(JsonObject data,String stateName){
        Character tempRep= '"';
        Character empty = Character.MIN_VALUE;
        JsonObject statedata = (JsonObject) data.get(stateName);
        //String stateDeaths = statedata.get("deaths").toString();
        stateName1 = stateName;
        stateCases = statedata.get("cases").toString().replace(tempRep,empty);
        stateCured = statedata.get("cured_discharged").toString().replace(tempRep,empty);
        stateDeaths = statedata.get("deaths").toString().replace(tempRep,empty);
        stateHelpline = statedata.get("helpline").toString().replace(tempRep,empty);
    }

    //Thread3 for updating State data
    Thread th3 = new Thread(new Runnable() {
        @Override

        public void run() {
            String sURL = "https://exec.clay.run/kunksed/mohfw-covid"; //DATA SOURCE UR
            // Connect to the URL using java's native library
                    URL url = null;
                    try {
                        url = new URL(sURL);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    URLConnection request = null;
                    try {
                        request = url.openConnection();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        request.connect();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // Convert to a JSON object to print data
                    JsonParser jp = new JsonParser(); //from gson
                    JsonElement root = null; //Convert the input stream to a json element
                    try {
                        root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    JsonObject rootobj = root.getAsJsonObject(); //May be an array, may be an object.
                    JsonObject data = rootobj.getAsJsonObject("stateData");
                    String stateValue=stateName;
                    Log.d(TAG,"Alive In thread 3 with "+stateValue);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    setStateData(data,stateValue);

                    setFields(x);
                    /*
                    if(x!=null) {
                    statename.setText(stateName1);
                    casevalue.setText(stateCases);
                    curevalue.setText(stateCured);
                    deathvalue.setText(stateDeaths);
                    hlpValue.setText(stateHelpline);
                    }*/
                }
            });



    //State
    public void updateState(View v) {
        try {
            x = v;
            stateName = stateTxt.getSelectedItem().toString();
            pool.execute(th3);
            //setFields(x);
            Toast.makeText(this, "Updating State Data For " + stateName, Toast.LENGTH_LONG).show();
            //Log.d(TAG,"ENTERED SNAME: " +stateName);
            //Toast.makeText(this, "Updated State Data For " +stateName, Toast.LENGTH_SHORT).show();
               }catch (Exception e) {
            Log.d(TAG,"App Crashed In updateState!");
            e.printStackTrace();

    }
    }


    public void setFields(View x){

        try{
        if(x!=null) {
        statename.setText(stateName1);
        casevalue.setText(stateCases);
        curevalue.setText(stateCured);
        deathvalue.setText(stateDeaths);
        hlpValue.setText(stateHelpline);
        }}
        catch (Exception e){

            Log.d(TAG,"App Crashed In setFields!");
            e.printStackTrace();

        }
    }


}


