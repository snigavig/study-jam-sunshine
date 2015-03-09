package com.goodcodeforfun.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.goodcodeforfun.sunshine.data.WeatherContract;


public class DetailActivity extends ActionBarActivity {

    private static String forecastString;
    public static final String LOG_TAG = "sunshine.DetailActivity";


    @Override
    protected void onPause() {
        super.onPause();
        Log.i(LOG_TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.i(LOG_TAG, "onStop");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(LOG_TAG, "onStart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "onDestroy");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(LOG_TAG, "onCreate");
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }
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

        return super.onOptionsItemSelected(item);
    }

    /**
     * A detail fragment containing a simple view.
     */
    public static class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

        private static final int LOADER_ID = 1;

        public static ShareActionProvider mShareActionProvider;

        private static final String[] FORECAST_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
        };
        private static final int COL_WEATHER_ID = 0;
        private static final int COL_WEATHER_DATE = 1;
        private static final int COL_WEATHER_DESC = 2;
        private static final int COL_WEATHER_MAX_TEMP = 3;
        private static final int COL_WEATHER_MIN_TEMP = 4;

        public TextView detailTextView;
        public DetailFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            // Inflate the menu; this adds items to the action bar if it is present.
            inflater.inflate(R.menu.menu_detail, menu);
            MenuItem item = menu.findItem(R.id.action_share);

            mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
            if (forecastString != null) {
                mShareActionProvider.setShareIntent(getShareIntent());
            }
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            getLoaderManager().initLoader(LOADER_ID, null, this);
            super.onActivityCreated(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            detailTextView = (TextView) rootView.findViewById(R.id.detailViewText);

            return rootView;
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Intent intent = getActivity().getIntent();
            if (intent == null) {
                return null;
            }
            return new CursorLoader(
                    getActivity(),
                    intent.getData(),
                    FORECAST_COLUMNS,
                    null,
                    null,
                    null
            );
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

            if (!data.moveToFirst()) { return; }
            String dateString = Utility.formatDate(
                    data.getLong(COL_WEATHER_DATE));
            String weatherDescription =
                    data.getString(COL_WEATHER_DESC);
            boolean isMetric = Utility.isMetric(getActivity());
            String high = Utility.formatTemperature(
                    data.getDouble(COL_WEATHER_MAX_TEMP), isMetric);
            String low = Utility.formatTemperature(
                    data.getDouble(COL_WEATHER_MIN_TEMP), isMetric);
            forecastString = String.format("%s - %s - %s/%s", dateString, weatherDescription, high, low);

            detailTextView.setText(forecastString);
            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(getShareIntent());
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }

        private Intent getShareIntent(){
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            String shareString = forecastString + " #SunshineApp";
            intent.putExtra(Intent.EXTRA_TEXT, shareString);
            //noinspection deprecation
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            return intent;
        }

    }
}
