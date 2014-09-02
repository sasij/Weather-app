package app.sunshine.juanjo.views.fragments;

/**
 * Created by juanjo on 05/08/14.
 */

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.Date;

import app.sunshine.juanjo.R;
import app.sunshine.juanjo.sync.SunshineSyncAdapter;
import app.sunshine.juanjo.util.Utility;
import app.sunshine.juanjo.util.WeatherContract;
import app.sunshine.juanjo.util.WeatherContract.LocationEntry;
import app.sunshine.juanjo.util.WeatherContract.WeatherEntry;
import app.sunshine.juanjo.views.adapter.ForecastAdapter;

/**
 * Encapsulates fetching the forecast and displaying it as a
 * {@link android.widget.ListView} layout.
 */
public class ForecastFragment extends Fragment implements LoaderCallbacks<Cursor> {

	private ForecastAdapter mForecastAdapter;
	private int mPosition;
	private ListView listView;
	private boolean mUseTodayLayout;

	private static final String SELECTED_KEY = "selected_position";

	private static final int FORECAST_LOADER = 0;
	private String mLocation;

	private static final String[] FORECAST_COLUMNS = {
			// In this case the id needs to be fully qualified with a table
			// name, since
			// the content provider joins the location & weather tables in the
			// background
			// (both have an _id column)
			// On the one hand, that's annoying. On the other, you can search
			// the weather table
			// using the location set by the user, which is only in the Location
			// table.
			// So the convenience is worth it.
			WeatherEntry.TABLE_NAME + "." + WeatherEntry._ID, WeatherEntry.COLUMN_DATETEXT,
			WeatherEntry.COLUMN_SHORT_DESC, WeatherEntry.COLUMN_MAX_TEMP,
			WeatherEntry.COLUMN_MIN_TEMP, LocationEntry.COLUMN_LOCATION_SETTING,
			WeatherEntry.COLUMN_WEATHER_ID, LocationEntry.COLUMN_LATITUDE,
			LocationEntry.COLUMN_LONGITUDE };

	// These indices are tied to FORECAST_COLUMNS. If FORECAST_COLUMNS changes,
	// these
	// must change.
	public static final int COL_WEATHER_ID = 0;
	public static final int COL_WEATHER_DATE = 1;
	public static final int COL_WEATHER_DESC = 2;
	public static final int COL_WEATHER_MAX_TEMP = 3;
	public static final int COL_WEATHER_MIN_TEMP = 4;
	public static final int COL_LOCATION_SETTING = 5;
	public static final int COL_WEATHER_CONDITION_ID = 6;
	public static final int COL_COORD_LAT = 7;
	public static final int COL_COORD_LONG = 8;

	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of item
	 * selections.
	 */
	public interface Callback {
		/**
		 * DetailFragmentCallback for when an item has been selected.
		 */
		public void onItemSelected(String date);
	}

	public ForecastFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Add this line in order for this fragment to handle menu events.
		setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.forecastfragment, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		// if (id == R.id.action_refresh) {
		// updateWeather();
		// return true;
		// }
		if (id == R.id.action_map) {
			openPreferredLocationInMap();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		// The ArrayAdapter will take data from a source and
		// use it to populate the ListView it's attached to.
		mForecastAdapter = new ForecastAdapter(getActivity(), null, 0);

		View rootView = inflater.inflate(R.layout.fragment_main, container, false);

		// Get a reference to the ListView, and attach this adapter to it.
		listView = (ListView) rootView.findViewById(R.id.listview_forecast);
		listView.setAdapter(mForecastAdapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
				Cursor cursor = mForecastAdapter.getCursor();
				if (cursor != null && cursor.moveToPosition(position)) {
					((Callback) getActivity()).onItemSelected(cursor.getString(COL_WEATHER_DATE));
				}
				mPosition = position;
			}
		});

		// If there's instance state, mine it for useful information.
		// The end-goal here is that the user never knows that turning their
		// device sideways
		// does crazy lifecycle related things. It should feel like some stuff
		// stretched out,
		// or magically appeared to take advantage of room, but data or place in
		// the app was never
		// actually *lost*.
		if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
			// The listview probably hasn't even been populated yet. Actually
			// perform the
			// swapout in onLoadFinished.
			mPosition = savedInstanceState.getInt(SELECTED_KEY);
		}

		mForecastAdapter.setUseTodayLayout(mUseTodayLayout);

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		getLoaderManager().initLoader(FORECAST_LOADER, null, this);
		super.onActivityCreated(savedInstanceState);
	}

	private void updateWeather() {

		// String location = Utility.getPreferredLocation(getActivity());
		//
		// Intent intent = new Intent(getActivity(),
		// SunshineService.AlarmReceiver.class);
		// intent.putExtra(SunshineService.LOCATION_QUERY_EXTRA, location);
		//
		// // Wrap in a pending intent which only fires once.
		// PendingIntent pi = PendingIntent.getBroadcast(getActivity(), 0,
		// intent,
		// PendingIntent.FLAG_ONE_SHOT);// getBroadcast(context, 0, i, 0);
		//
		// AlarmManager am = (AlarmManager)
		// getActivity().getSystemService(Context.ALARM_SERVICE);
		//
		// // Set the AlarmManager to wake up the system.
		// am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 2000,
		// pi);

		SunshineSyncAdapter.syncImmediately(getActivity());
	}

	private void openPreferredLocationInMap() {
		// Using the URI scheme for showing a location found on a map. This
		// super-handy
		// intent can is detailed in the "Common Intents" page of Android's
		// developer site:
		// http://developer.android.com/guide/components/intents-common.html#Maps
		if (null != mForecastAdapter) {
			Cursor c = mForecastAdapter.getCursor();
			if (null != c) {
				c.moveToPosition(0);
				String posLat = c.getString(COL_COORD_LAT);
				String posLong = c.getString(COL_COORD_LONG);
				Uri geoLocation = Uri.parse("geo:" + posLat + "," + posLong);

				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(geoLocation);

				if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
					startActivity(intent);
				} else {
					System.out.println("Couldn't call " + geoLocation.toString()
							+ ", no receiving apps installed!");
				}
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mLocation != null && !mLocation.equals(Utility.getPreferredLocation(getActivity()))) {
			getLoaderManager().restartLoader(FORECAST_LOADER, null, this);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mPosition != ListView.INVALID_POSITION) {
			outState.putInt(SELECTED_KEY, mPosition);
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// This is called when a new Loader needs to be created. This
		// fragment only uses one loader, so we don't care about checking the
		// id.

		// To only show current and future dates, get the String representation
		// for today,
		// and filter the query to return weather only for dates after or
		// including today.
		// Only return data after today.
		String startDate = WeatherContract.getDbDateString(new Date());

		// Sort order: Ascending, by date.
		String sortOrder = WeatherEntry.COLUMN_DATETEXT + " ASC";

		mLocation = Utility.getPreferredLocation(getActivity());
		Uri weatherForLocationUri = WeatherEntry.buildWeatherLocationWithStartDate(mLocation,
				startDate);

		// Now create and return a CursorLoader that will take care of
		// creating a Cursor for the data being displayed.
		return new CursorLoader(getActivity(), weatherForLocationUri, FORECAST_COLUMNS, null, null,
				sortOrder);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		mForecastAdapter.swapCursor(data);
		if (mPosition != ListView.INVALID_POSITION) {
			// If we don't need to restart the loader, and there's a desired
			// position to restore
			// to, do so now.
			listView.smoothScrollToPosition(mPosition);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mForecastAdapter.swapCursor(null);
	}

	public void setUseTodayLayout(boolean useTodayLayout) {
		mUseTodayLayout = useTodayLayout;
		if (mForecastAdapter != null) {
			mForecastAdapter.setUseTodayLayout(mUseTodayLayout);
		}
	}
}
