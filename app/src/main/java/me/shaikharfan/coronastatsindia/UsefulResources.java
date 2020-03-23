package me.shaikharfan.coronastatsindia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class UsefulResources extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_useful_resources);
    }


    public void onTapGoCoronaGo(View view) {
        Intent viewIntent =
                new Intent("android.intent.action.VIEW",
                        Uri.parse("http://www.gocoronago.org"));
        startActivity(viewIntent);
    }


    public void onTapTracker(View view) {
        Intent viewIntent =
                new Intent("android.intent.action.VIEW",
                        Uri.parse(" https://www.coronatracker.in"));
        startActivity(viewIntent);
    }

    public void onTapHowBadIsCorona(View view) {
        Intent viewIntent =
                new Intent("android.intent.action.VIEW",
                        Uri.parse("https://howbadiscorona.com/"));
        startActivity(viewIntent);
    }
}
