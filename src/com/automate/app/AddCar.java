package com.automate.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

/**
 * Created by Matthew on 3/1/2015.
 */
public class AddCar extends Activity {
	Spinner make_spinner, model_spinner, year_spinner, timeframe_spinner;
	EditText mileage_text, estmileage_text;
	SpinnerAdapter mSpinnerAdapter;
	Context context;
	Time now;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addcarlayout);
		context = this;

		now = new Time();
		now.setToNow();

		make_spinner = (Spinner) findViewById(R.id.make_spinner);
		model_spinner = (Spinner) findViewById(R.id.model_spinner);
		year_spinner = (Spinner) findViewById(R.id.year_spinner);
		timeframe_spinner = (Spinner) findViewById(R.id.timeframe_spinner);
		mileage_text = (EditText) findViewById(R.id.mileage_text);
		estmileage_text = (EditText) findViewById(R.id.estmileage_text);

		make_spinner.setAdapter(ArrayAdapter.createFromResource(context,
				R.array.make_array,
				android.R.layout.simple_spinner_dropdown_item)); // set the
																	// adapter
		model_spinner.setAdapter(ArrayAdapter.createFromResource(context,
				R.array.placeholder_model_array,
				android.R.layout.simple_spinner_dropdown_item));
		year_spinner.setAdapter(ArrayAdapter.createFromResource(context,
				R.array.years_array,
				android.R.layout.simple_spinner_dropdown_item));
		timeframe_spinner.setAdapter(ArrayAdapter.createFromResource(context,
				R.array.timeframe_array,
				android.R.layout.simple_spinner_dropdown_item));

		make_spinner
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						switch (position) {
						case 0:
							model_spinner.setAdapter(ArrayAdapter
									.createFromResource(
											context,
											R.array.chevrolet_model_array,
											android.R.layout.simple_spinner_dropdown_item));
							break;
						case 1:
							model_spinner.setAdapter(ArrayAdapter
									.createFromResource(
											context,
											R.array.chrysler_model_array,
											android.R.layout.simple_spinner_dropdown_item));
							break;
						case 2:
							mSpinnerAdapter = ArrayAdapter
									.createFromResource(
											context,
											R.array.dodge_model_array,
											android.R.layout.simple_spinner_dropdown_item);
							model_spinner.setAdapter(mSpinnerAdapter);
							break;
						case 3:
							mSpinnerAdapter = ArrayAdapter
									.createFromResource(
											context,
											R.array.ford_model_array,
											android.R.layout.simple_spinner_dropdown_item);
							model_spinner.setAdapter(mSpinnerAdapter);
							break;
						case 4:
							mSpinnerAdapter = ArrayAdapter
									.createFromResource(
											context,
											R.array.hyundai_model_array,
											android.R.layout.simple_spinner_dropdown_item);
							model_spinner.setAdapter(mSpinnerAdapter);
							break;
						case 5:
							mSpinnerAdapter = ArrayAdapter
									.createFromResource(
											context,
											R.array.kia_model_array,
											android.R.layout.simple_spinner_dropdown_item);
							model_spinner.setAdapter(mSpinnerAdapter);
							break;
						case 6:
							mSpinnerAdapter = ArrayAdapter
									.createFromResource(
											context,
											R.array.toyota_model_array,
											android.R.layout.simple_spinner_dropdown_item);
							model_spinner.setAdapter(mSpinnerAdapter);
							break;
						case 7:
							mSpinnerAdapter = ArrayAdapter
									.createFromResource(
											context,
											R.array.volvo_model_array,
											android.R.layout.simple_spinner_dropdown_item);
							model_spinner.setAdapter(mSpinnerAdapter);
							break;
						case 8:
							mSpinnerAdapter = ArrayAdapter
									.createFromResource(
											context,
											R.array.volkswagen_model_array,
											android.R.layout.simple_spinner_dropdown_item);
							model_spinner.setAdapter(mSpinnerAdapter);
							break;
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {

					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.addcar, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		// noinspection SimplifiableIfStatement
		if (id == R.id.cancel) {
			startActivity(new Intent(AddCar.this, MainActivity.class));
			AddCar.this.finish();
		}
		if (id == R.id.clear_data) {
			List<Car> car_list = new ArrayList<Car>();
			try {
				car_list = (List<Car>) InternalStorage.readObject(
						getBaseContext(), "car_list");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			car_list.clear();
			try {
				InternalStorage.writeObject(getBaseContext(), "car_list",
						car_list);
			} catch (IOException e) {
				Log.e("ERR", e.getMessage());
			}
			AddCar.this.finish();
			return true;
		}
		if (id == R.id.submit) {
			if (fieldsCompleted()) {
				Double em = Double.parseDouble(estmileage_text.getText()
						.toString());
				if (timeframe_spinner.getSelectedItemPosition() == 1) {
					em = em / 7;
				} else if (timeframe_spinner.getSelectedItemPosition() == 2) {
					em = em / 30;
				} else if (timeframe_spinner.getSelectedItemPosition() == 3) {
					em = em / 365;
				}
				Car newcar = new Car(make_spinner.getSelectedItem().toString(),
						model_spinner.getSelectedItem().toString(),
						Integer.parseInt(year_spinner.getSelectedItem()
								.toString()), Integer.parseInt(mileage_text
								.getText().toString()), em, now.yearDay,
						now.year);

				List<Car> car_list = new ArrayList<Car>();
				try {
					car_list = (List<Car>) InternalStorage.readObject(
							getBaseContext(), "car_list");
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				car_list.add(newcar);

				try {
					InternalStorage.writeObject(getBaseContext(), "car_list",
							car_list);
				} catch (IOException e) {
					Log.e("ERR", e.getMessage());
				}
				Toast.makeText(getBaseContext(), "Your car has been added",
						Toast.LENGTH_LONG).show();
				startActivity(new Intent(AddCar.this, MainActivity.class));
				AddCar.this.finish();

			}
		}
		return super.onOptionsItemSelected(item);

	}
	
	public Boolean fieldsCompleted(){
		if(estmileage_text.getText().length()==0){
			Toast.makeText(getBaseContext(), "Please enter all fields", Toast.LENGTH_LONG).show();
			return false;
		}
		if(mileage_text.getText().length()==0){
			Toast.makeText(getBaseContext(), "Please enter all fields", Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}
}
