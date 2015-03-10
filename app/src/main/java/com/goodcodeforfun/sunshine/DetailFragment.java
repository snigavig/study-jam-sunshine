package com.goodcodeforfun.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.goodcodeforfun.sunshine.data.WeatherContract;

/**
 * A detail fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 1;

    private static String forecastString;
    public static ShareActionProvider mShareActionProvider;

    private static final String[] FORECAST_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE
    };
    private static final int COL_WEATHER_ID = 0;
    private static final int COL_WEATHER_DATE = 1;
    private static final int COL_WEATHER_DESC = 2;
    private static final int COL_WEATHER_MAX_TEMP = 3;
    private static final int COL_WEATHER_MIN_TEMP = 4;
    private static final int COL_WEATHER_HUMIDITY = 5;
    private static final int COL_WEATHER_WIND_SPEED = 6;
    private static final int COL_WEATHER_DEGREES = 7;
    private static final int COL_WEATHER_PRESSURE = 8;

    public ImageView iconView;
    public TextView dateView;
    public TextView descView;
    public TextView highView;
    public TextView lowView;
    public TextView dateFullView;
    public TextView humidityView;
    public TextView windView;
    public TextView pressureView;
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
        iconView = (ImageView) rootView.findViewById(R.id.list_item_icon);
        dateView = (TextView) rootView.findViewById(R.id.list_item_date_textview);
        descView = (TextView) rootView.findViewById(R.id.list_item_forecast_textview);
        highView = (TextView) rootView.findViewById(R.id.list_item_high_textview);
        lowView = (TextView) rootView.findViewById(R.id.list_item_low_textview);
        dateFullView = (TextView) rootView.findViewById(R.id.list_item_date_full_textview);
        humidityView = (TextView) rootView.findViewById(R.id.list_item_humidity_textview);
        windView = (TextView) rootView.findViewById(R.id.list_item_wind_textview);
        pressureView = (TextView) rootView.findViewById(R.id.list_item_pressure_textview);
        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Intent intent = getActivity().getIntent();
        if (intent == null || intent.getData() == null) {
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
        String dateFullString = Utility.getFriendlyDayString(getActivity(),
                data.getLong(COL_WEATHER_DATE));
        String weatherDescription =
                data.getString(COL_WEATHER_DESC);
        boolean isMetric = Utility.isMetric(getActivity());
        String high = Utility.formatTemperature(
                getActivity(), data.getDouble(COL_WEATHER_MAX_TEMP), isMetric);
        String low = Utility.formatTemperature(
                getActivity(), data.getDouble(COL_WEATHER_MIN_TEMP), isMetric);
        String humidity = String.format(
                getActivity().getResources().getString(R.string.format_humidity), data.getDouble(COL_WEATHER_HUMIDITY));
        String wind = Utility.getFormattedWind(
                getActivity(), data.getFloat(COL_WEATHER_WIND_SPEED), data.getFloat(COL_WEATHER_DEGREES));
        String pressure = String.format(
                getActivity().getResources().getString(R.string.format_pressure), data.getDouble(COL_WEATHER_HUMIDITY));

        forecastString = String.format("%s - %s - %s/%s", dateString, weatherDescription, high, low);

        int weatherId = data.getInt(COL_WEATHER_ID);
        iconView.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));
        dateView.setText(dateString);
        descView.setText(weatherDescription);
        highView.setText(high);
        lowView.setText(low);
        dateFullView.setText(dateFullString);
        humidityView.setText(humidity);
        windView.setText(wind);
        pressureView.setText(pressure);

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