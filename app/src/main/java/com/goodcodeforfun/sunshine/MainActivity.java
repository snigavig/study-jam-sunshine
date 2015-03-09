package com.goodcodeforfun.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import static android.widget.Toast.*;


public class MainActivity extends ActionBarActivity {

    public SharedPreferences prefs;
    private final String FORECASTFRAGMENT_TAG = "FFTAG";
    private String mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mLocation = Utility.getPreferredLocation(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ForecastFragment(), FORECASTFRAGMENT_TAG)
                    .commit();
        }

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String location = Utility.getPreferredLocation( this );
        if (location != null && !location.equals(mLocation)) {
            ForecastFragment ff = (ForecastFragment)getSupportFragmentManager().findFragmentByTag(FORECASTFRAGMENT_TAG);
            if ( null != ff ) {
                ff.onLocationChanged();
            }
            mLocation = location;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent startSettingsActivityIntent = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivityIntent);
            return true;
        }

        if (id == R.id.action_show_map) {
            showMap();
        }

        return super.onOptionsItemSelected(item);
    }

    public void showMap() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme("geo")
                .appendPath("0.0")
                .appendQueryParameter("q", prefs.getString(
                        getResources().getString(R.string.pref_key_location),
                        getResources().getString(R.string.pref_default_location)));

        intent.setData(uriBuilder.build());
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            makeText(this, "Sorry, no application to show a map", LENGTH_SHORT).show();
        }
    }
}
