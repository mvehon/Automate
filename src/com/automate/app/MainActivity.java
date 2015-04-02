package com.automate.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements ActionBar.TabListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v13.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	SharedPreferences prefs;
	static Context context;
	static List<Car> car_list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		context = this;
		prefs = this.getSharedPreferences("com.automate.app",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		car_list = new ArrayList<Car>();
		try {
			car_list = (List<Car>) InternalStorage.readObject(getBaseContext(),
					"car_list");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (car_list.size() == 0) {
			startActivity(new Intent(MainActivity.this, AddCar.class));
			MainActivity.this.finish();
		}

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
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
		if (id == R.id.clear_data) {
			prefs.edit().clear().commit();

			car_list.clear();
			try {
				InternalStorage.writeObject(getBaseContext(), "car_list",
						car_list);
			} catch (IOException e) {
				Log.e("ERR", e.getMessage());
			}
			MainActivity.this.finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class
			// below).
			return PlaceholderFragment.newInstance(position + 1);
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return car_list.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			return Integer.toString(car_list.get(position).year) + " "
					+ car_list.get(position).make + " "
					+ car_list.get(position).model;
		}
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
			View v = null;
			Bundle args = getArguments();
			final int curView = args.getInt(ARG_SECTION_NUMBER);
			Log.d("curView", Integer.toString(curView));
			if (curView > 0) {
				v = inflater.inflate(R.layout.fragment_main, container, false);
				TextView make = (TextView) v.findViewById(R.id.make);
				TextView model = (TextView) v.findViewById(R.id.model);
				TextView miles = (TextView) v.findViewById(R.id.miles);
				ImageButton addcar = (ImageButton) v
						.findViewById(R.id.addcar_btn);
				final ImageButton error = (ImageButton) v
						.findViewById(R.id.error_btn);

				addcar.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						addCarActive();
					}
				});

				make.setText(car_list.get(curView - 1).make);
				model.setText(car_list.get(curView - 1).model);
				int estmiles = (int) car_list.get(curView - 1).estCurMileage();
				miles.setText(Integer.toString(estmiles));
				if (!car_list.get(curView - 1).activeMaintenance()) {
					error.setVisibility(View.GONE);
				}
				error.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub

						AlertDialog.Builder builder = new AlertDialog.Builder(
								context);
						builder.setMessage(
								"You have overdue scheduled maintenance for your car.\nSuggested maintenance is:\n\n"
										+ car_list.get(curView - 1).mnlist
												.get(car_list.get(curView - 1)
														.selectedMaintenance()).desc)
								.setPositiveButton("Schedule Appointment",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int id) {
												startActivity(new Intent(
														context,
														ScheduleAppt.class));
												Intent intent = new Intent(context, ScheduleAppt.class);
												intent.putExtra("index", curView-1);
												startActivity(intent);
												((Activity) MainActivity.context)
														.finish();
											}
										})
								.setNegativeButton("Cancel",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int id) {
												// FIRE ZE MISSILES!
											}
										})
								.setNeutralButton("Clear",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int id) {
												error.setVisibility(View.GONE);
												car_list.get(curView - 1).mnlist
														.get(car_list
																.get(curView - 1)
																.selectedMaintenance())
														.setCleared(true);
												;

											}
										});
						;
						builder.create();
						builder.show();

					}
				});
			}
			return v;
		}
	}

	public static void addCarActive() {
		context.startActivity(new Intent(context, AddCar.class));
		((Activity) MainActivity.context).finish();
	}

	public static void scheduleAppt() {
		context.startActivity(new Intent(context, ScheduleAppt.class));
		((Activity) MainActivity.context).finish();
	}

	@Override
	protected void onPause() {
		super.onPause();
		try {
			InternalStorage.writeObject(getBaseContext(), "car_list", car_list);
		} catch (IOException e) {
			Log.e("ERR", e.getMessage());
		}
	}

}
