package com.benbrittain.qs_gps_logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.Toast;

public class MainActivity extends Activity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;
	public static Intent GPSService; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));

		LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
		boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);
		int score=0;
		score++;
		System.out.println(score);

		if (enabled) {
			Log.i("info", "wat");
			GPSService = new Intent(this, GPSpoll.class);
			this.startService(GPSService);
		} else {
			Toast.makeText(this, "This App will not work without Location Services enabled.", Toast.LENGTH_LONG)
			.show();
		}
	}

	public void sendData(View view) {
		this.stopService(GPSService);
		String FILENAME = "gpsdata";

		ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			BufferedReader in;
			try {
				JSONObject pointBlob = new JSONObject();
				JSONArray pointList = new JSONArray();
				String filestr = getFilesDir().getPath() + "/" + FILENAME;
				File file = new File(filestr);
				in = new BufferedReader(new FileReader(filestr));
				String strLine;
				while ((strLine = in.readLine()) != null)   {
					String[] vals = strLine.split(",");
					JSONObject point = new JSONObject();
					point.put("timestamp", vals[0]);
					point.put("latitude", vals[2]);
					point.put("longitude", vals[1]);
					point.put("altitude", vals[3]);
					point.put("accuracy", vals[4]);
					point.put("speed", vals[5]);
					point.put("bearing", vals[6]);
					pointList.put(point);
				}
				in.close();
				pointBlob.put("locations", pointList);

				SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
				String endpoint = sharedPref.getString("pref_endpoint","");
				AsyncPost asyncPost = new AsyncPost(pointBlob);
				asyncPost.execute(endpoint);
				// Sync because YOLO
				if (asyncPost.get()) {
					Toast.makeText(this, "Successful Post", Toast.LENGTH_SHORT).show();
					file.renameTo(new File(filestr + ".bk"));
				} else {
					Toast.makeText(this, "Upload Failed - Check Endpoint", Toast.LENGTH_SHORT).show();
				}
			} catch (IOException | JSONException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				Toast.makeText(this, "failure sending data", Toast.LENGTH_SHORT).show();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		} else {
			Toast.makeText(this, "Connect to a network", Toast.LENGTH_SHORT).show();
		}
		this.startService(GPSService);
	}


	@Override
	public void onNavigationDrawerItemSelected(int position) {
		Fragment frag;
		switch (position + 1){
		case 1:
			frag = new ControlsFragment();
			break;
		case 2:
			frag = new SettingsFragment();
			break;
		case 3:
			frag = new AboutFragment();
			break;
		default:
			frag = PlaceholderFragment.newInstance(position + 1);
		}
		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager
		.beginTransaction()
		.replace(R.id.container, frag).commit();
	}

	public void onSectionAttached(int number) {
		switch (number) {
		case 1:
			mTitle = getString(R.string.title_section1);
			break;
		case 2:
			mTitle = getString(R.string.title_section2);
			break;
		case 3:
			mTitle = getString(R.string.title_section3);
			break;
		}
	}

	public void restoreActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.main, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		//		int id = item.getItemId();
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((MainActivity) activity).onSectionAttached(getArguments().getInt(
					ARG_SECTION_NUMBER));
		}
	}
	public static class ControlsFragment extends Fragment {
		private static final String ARG_SECTION_NUMBER = "section_number";


		public ControlsFragment() {
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, 1);
			this.setArguments(args);
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_controls, container,
					false);
			return rootView;
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((MainActivity) activity).onSectionAttached(getArguments().getInt(
					ARG_SECTION_NUMBER));
		}
	}


	public static class SettingsFragment extends PreferenceFragment {
		private static final String ARG_SECTION_NUMBER = "section_number";

		public SettingsFragment() {

			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, 2);
			this.setArguments(args);
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.layout.fragment_settings);
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((MainActivity) activity).onSectionAttached(getArguments().getInt(
					ARG_SECTION_NUMBER));
		}
	}

	public static class AboutFragment extends Fragment {
		private static final String ARG_SECTION_NUMBER = "section_number";

		public AboutFragment() {
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, 3);
			this.setArguments(args);
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_about, container,
					false);
			return rootView;
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((MainActivity) activity).onSectionAttached(getArguments().getInt(
					ARG_SECTION_NUMBER));
		}
	}

}
