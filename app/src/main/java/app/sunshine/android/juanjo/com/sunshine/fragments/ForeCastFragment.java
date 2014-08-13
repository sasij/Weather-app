package app.sunshine.android.juanjo.com.sunshine.fragments;

/**
 * Created by juanjo on 05/08/14.
 */

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.net.URL;
import java.util.ArrayList;

import app.sunshine.android.juanjo.com.sunshine.R;
import app.sunshine.android.juanjo.com.sunshine.Util.WeatherJsonParser;
import app.sunshine.android.juanjo.com.sunshine.network.APIHTTP;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForeCastFragment extends Fragment {

	private ListView listView;
	private ArrayAdapter<String> adapter;

	private ArrayList<String> entryList = new ArrayList<String>();

	private String URL_BASE = "http://api.openweathermap.org/data/2.5/forecast/daily?";
	private String QUERY_PARAM = "q";
	private String FORMAT_PARAM = "mode";
	private String FORMAT = "json";
	private String UNITS_PARAM = "units";
	private String UNITS = "metric";
	private String DAYS_PARAM = "cnt";
	private int numDays = 7;

	private String response;

	public ForeCastFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main, container, false);

		adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_forecast,
				R.id.list_item_forecast_textview, entryList);

		listView = (ListView) rootView.findViewById(R.id.listview_forecast);
		listView.setAdapter(adapter);

		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		new FetchWeatherTask().execute("14007");

		// new Thread(new Runnable() {
		// @Override
		// public void run() {
		// try {
		// Log.d("WEATHER", "Preparing");
		// response = new HTTP().run(url);
		// Log.d("WEATHER", response);
		// Log.d("WEATHER", "Finish!");
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// }
		// }).start();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.forecastfragment, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();
		if (id == R.id.action_refresh) {
			Log.d("=>", "menu");
			new FetchWeatherTask().execute("14007");
		}

		return super.onOptionsItemSelected(item);
	}

	private String getUrl(String postalCode) {
		URL url = null;

		Uri builtUri = Uri.parse(URL_BASE).buildUpon()
				.appendQueryParameter(QUERY_PARAM, postalCode)
				.appendQueryParameter(FORMAT_PARAM, FORMAT)
				.appendQueryParameter(UNITS_PARAM, UNITS)
				.appendQueryParameter(DAYS_PARAM, Integer.toString(numDays)).build();

		Log.v("WEATHER", "Built URI:" + builtUri.toString());

		return builtUri.toString();

	}

	public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

		@Override
		protected String[] doInBackground(String... params) {
			try {

				String url = getUrl(params[0]);
				Log.d("=>", url);

				response = new APIHTTP().run(url);
				Log.d("=>", response);

				String[] days = new WeatherJsonParser().getWeatherDataFromJson(response, numDays);

				return days;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(String[] strings) {
			super.onPostExecute(strings);

			if (strings != null) {
				adapter.clear();
				if (Build.VERSION.SDK_INT >= 11) {
					adapter.addAll(strings);
				} else {
					for (String day : strings) {
						adapter.add(day);
					}
				}
			}
			// We have not call to NotifyDatasetChanged() because add() and
			// addAll() already call it
			adapter.notifyDataSetChanged();
		}
	}

}
