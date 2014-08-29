package app.sunshine.juanjo.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import app.sunshine.juanjo.R;
import app.sunshine.juanjo.sync.SunshineSyncAdapter;
import app.sunshine.juanjo.views.fragments.DetailFragment;
import app.sunshine.juanjo.views.fragments.ForecastFragment;

public class MainActivity extends ActionBarActivity implements ForecastFragment.Callback {

	private boolean mTwoPane = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (findViewById(R.id.weather_detail_container) != null) {

			mTwoPane = true;

			if (savedInstanceState == null) {
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.weather_detail_container, new DetailFragment()).commit();
			}
		}

		ForecastFragment forecastFragment = ((ForecastFragment) getSupportFragmentManager()
				.findFragmentById(R.id.fragment_forecast));
		forecastFragment.setUseTodayLayout(!mTwoPane);

		SunshineSyncAdapter.initializeSyncAdapter(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onItemSelected(String date) {
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle args = new Bundle();
			args.putString(DetailActivity.DATE_KEY, date);

			DetailFragment fragment = new DetailFragment();
			fragment.setArguments(args);

			getSupportFragmentManager().beginTransaction()
					.replace(R.id.weather_detail_container, fragment).commit();

		} else {
			Intent intent = new Intent(this, DetailActivity.class).putExtra(
					DetailActivity.DATE_KEY, date);
			startActivity(intent);
		}
	}
}
