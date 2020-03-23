package me.shaikharfan.coronastatsindia;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    public String countryCases, countryCured, countryDeaths, intTotal, localTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context context = getApplicationContext();
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if(isConnected==true) {
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

            text1.setText("COVID-19 India Stats");
            localTotalw.setText("Indians Affected");
            intTotalw.setText("NRI's/Tourists Affected");
            totalIndia.setText("Total Affected In India");
            curedTotal.setText("Total Cured In India");
            deathTotal.setText("Total Deaths In India");

            th1.start();
            while (th1.isAlive()) {
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
            else{
            Toast.makeText(context, "Internet Not Available, Please Try Again Later", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Internet Not Available");
            finish();
            }
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
                Intent intent = new Intent(this, UsefulResources.class);
                startActivity(intent);
                return true;
            case R.id.item2:
                Intent intent1 = new Intent(this, About.class);
                startActivity(intent1);
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

    //Thread1- Method used from https://github.com/google/gson
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
            JsonObject rootobj = root.getAsJsonObject();                    //May be an array, may be an object.
            JsonObject data = rootobj.getAsJsonObject("data");              //grabbing Json data
            JsonObject cdata = data.getAsJsonObject("summary");             //grabbing country summary
            Log.d(TAG,"Data: "+data);
            Log.d(TAG,"CountryData: "+cdata);
            setData(cdata);
        }
    });

    //Setting data into global variables by using JsonObject
    public void setData(JsonObject cdata){
        countryCases = cdata.get("total").toString();
        countryCured = cdata.get("discharged").toString();
        countryDeaths = cdata.get("deaths").toString();
        intTotal = cdata.get("confirmedCasesForeign").toString();
        localTotal = cdata.get("confirmedCasesIndian").toString();
    }}



