package me.shaikharfan.coronastatsindia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    public String countryCases, countryCured, countryDeaths, intTotal, localTotal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



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

        while (th1.isAlive()){
            locTotal.setText(localTotal);
            IntTotVal.setText(intTotal);
            totInd.setText(countryCases);
            cureTotal.setText(countryCured);
            deadTotal.setText(countryDeaths);
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
                Toast.makeText(this, "item3", Toast.LENGTH_SHORT).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

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

}
