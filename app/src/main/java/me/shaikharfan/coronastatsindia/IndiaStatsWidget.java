package me.shaikharfan.coronastatsindia;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.concurrent.atomic.AtomicBoolean;

import static android.content.ContentValues.TAG;


/**
 * Implementation of App Widget functionality.
 */
public class IndiaStatsWidget extends AppWidgetProvider {

    public static String countryCases, countryCured, countryDeaths;





     static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,int appWidgetId)
     {
         //Thread-Method used from https://github.com/google/gson

          Thread thw = new Thread(new Runnable() {
             @Override
             public void run() {
                 //GITHUB - https://github.com/amodm/api-covid19-in
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
                 JsonObject data = rootobj.getAsJsonObject("data"); //grabbing Json Data
                 JsonObject cdata = data.getAsJsonObject("summary");//grabbing country data
                 Log.d(TAG, "run: In WidgetClass Thread");
                 //Setting data in global variables
                 countryCases = cdata.get("total").toString();
                 countryCured = cdata.get("discharged").toString();
                 countryDeaths = cdata.get("deaths").toString();
                 Log.d(TAG, "run: In WidgetClass Thread"+countryCases);
                 Log.d(TAG, "run: In WidgetClass Thread"+countryCured);
                 Log.d(TAG, "run: In WidgetClass Thread"+countryDeaths);
             }
         });

         // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.india_stats_widget);
        views.setTextViewText(R.id.appwidget_text,"COVID19 Stats India");
        int i=3;
         try {
             thw.setPriority(10);
             thw.start();
             while(thw.isAlive() && i!=0){
                 try {
                     Log.d(TAG, "run: In update "+countryCases);
                     Log.d(TAG, "run: In update "+countryCured);
                     Log.d(TAG, "run: In update "+countryDeaths);
                     if(countryCases!=null) {
                         views.setTextViewText(R.id.casesvalw, countryCases);
                         i--;
                     }
                     if(countryCured!=null) {
                         views.setTextViewText(R.id.cured_dischargedvalw, countryCured);
                         i--;
                     }
                     if(countryDeaths!=null) {
                         views.setTextViewText(R.id.deathstotalvalw, countryDeaths);
                         i--;
                     }
                 }catch (Exception e){
                     Log.d(TAG,"Crashed while setting fields");
                     e.printStackTrace();
                 }}
             appWidgetManager.updateAppWidget(appWidgetId, views);
         }catch (Exception e) {
             Log.d(TAG,"App Crashed In updateAppWidget!");
             e.printStackTrace();
         }

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
            Toast.makeText(context, "Add widget to stay informed !", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
     }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}



