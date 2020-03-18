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

import static android.content.ContentValues.TAG;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link IndiaStatsWidgetConfigureActivity IndiaStatsWidgetConfigureActivity}
 */
public class IndiaStatsWidget extends AppWidgetProvider {

    public static String countryCases, countryCured, countryDeaths;
    public static RemoteViews temp;

    static final int MAX_T = 10;

    //Thread Pool
    static ExecutorService pool = Executors.newFixedThreadPool(MAX_T);

    static Thread thw = new Thread(new Runnable() {
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
            JsonObject cdata = rootobj.getAsJsonObject("countryData"); //grabbing country data
            Log.d(TAG, "run: In WidgetClass Thread");

            countryCases = cdata.get("total").toString();
            countryCured = cdata.get("cured_dischargedTotal").toString();
            countryDeaths = cdata.get("deathsTotal").toString();

            Log.d(TAG, "run: In WidgetClass Thread"+countryCases);

            //setFields(temp);




        }
    });


    static void setFields(RemoteViews v){
        try{
            if(v!=null) {
                v.setTextViewText(R.id.casesvalw, "1000");
                v.setTextViewText(R.id.cured_dischargedvalw, countryCured);
                v.setTextViewText(R.id.deathstotalvalw, countryDeaths);
            }}
        catch (Exception e){

            Log.d(TAG,"App Crashed In setFields!");
            e.printStackTrace();

        }
    }




    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {


        CharSequence widgetText = IndiaStatsWidgetConfigureActivity.loadTitlePref(context, appWidgetId);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.india_stats_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);

        // Instruct the widget manager to update the widget
        try {
            temp = views;
            //pool.execute(thw);
            views.setTextViewText(R.id.casesvalw, "1000");
            //views.setTextViewText(R.id.cured_dischargedvalw, countryCured);
            //views.setTextViewText(R.id.deathstotalvalw, countryDeaths);
            //Toast.makeText(this, "Updated State Data For " +stateName, Toast.LENGTH_SHORT).show();
        }catch (Exception e) {
            Log.d(TAG,"App Crashed In updateAppWidget!");
            e.printStackTrace();

        }






        appWidgetManager.updateAppWidget(appWidgetId, views);

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
        for (int appWidgetId : appWidgetIds) {
            IndiaStatsWidgetConfigureActivity.deleteTitlePref(context, appWidgetId);
            Toast.makeText(context, "Add widget to stay informed !", Toast.LENGTH_SHORT).show();
        }
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

