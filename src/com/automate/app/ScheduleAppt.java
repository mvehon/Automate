package com.automate.app;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TimePicker;
import android.widget.Toast;

public class ScheduleAppt extends Activity {
	EditText fn, ln;
	Spinner loc;
	Button date, time;
	Time appt_time;
	List<Shop> shop_list;
	List<Car> car_list = new ArrayList<Car>();
	int index=0;
	private TCPClient mTcpClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scheduleform);
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        
		try {
			car_list = (List<Car>) InternalStorage.readObject(getBaseContext(),
					"car_list");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		new connectTask().execute("");
		
		index = (int) getIntent().getExtras().get("index");


		fn = (EditText) findViewById(R.id.firstname);
		ln = (EditText) findViewById(R.id.lastname);

		loc = (Spinner) findViewById(R.id.loc_spinner);
		
		shop_list = new ArrayList<Shop>();
		shop_list.add(new Shop("Random Dealership", "213 Mill Ave. Tempe, AZ"));
		shop_list.add(new Shop("Repairs and Stuff Etc", "321 E Rural Rd. Tempe, AZ"));
		shop_list.add(new Shop("So and So Repair", "123 Elm St. Tempe, AZ"));
        ArrayAdapter posAdapter = new ArrayAdapter(this, R.layout.multiline_spinner_dropdown_item, shop_list);
        
        loc.setAdapter(posAdapter);

		date = (Button) findViewById(R.id.date);
		time = (Button) findViewById(R.id.time);

		appt_time = new Time();
		appt_time.setToNow();

		date.setText(setDateFormat(appt_time));
		date.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(date.getId());
			}
		});

		String result = getHour(appt_time.hour);
		time.setText(result);
		time.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(time.getId());
			}
		});

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case R.id.date:
			return new DatePickerDialog(this, datePickerListener,
					appt_time.year, appt_time.month, appt_time.monthDay);
		case R.id.time:
			return new TimePickerDialog(this, timePickerListener,
					appt_time.hour, appt_time.minute, false);
		}
		return null;
	}

	public String setDateFormat(Time dt) {
		return Integer.toString(dt.month + 1) + "/"
				+ Integer.toString(dt.monthDay) + "/"
				+ Integer.toString(dt.year - 2000);
	}

	public String getHour(int hours) {
		String hr;
		if (hours > 24) {
			hours -= 24;
			hr = Integer.toString(hours) + ":00 AM ";
		} else if (hours > 12) {
			hours -= 12;
			hr = Integer.toString(hours) + ":00 PM ";
		} else {
			hr = Integer.toString(hours) + ":00 AM ";
		}

		if (hours == 0) {
			hr = "12:00 AM";
		} else if (hours == 12) {
			hr = "12:00 PM";
		}
		return hr;
	}
	
	public String getHour(int hours, int minutes) {
		String hr;
		if (hours > 24) {
			hours -= 24;
			hr = Integer.toString(hours) + ":" + getMinutes(minutes) + " PM ";
		} else if (hours > 12) {
			hours -= 12;
			hr = Integer.toString(hours) + ":" + getMinutes(minutes) + " PM ";
		} else {
			hr = Integer.toString(hours) + ":" + getMinutes(minutes) + " AM ";
		}

		if (hours == 0) {
			hr = "12:00 AM";
		}else if(hours==12){
			hr = "12:00 PM";
		}
		return hr;
	}
	
	public String getMinutes(int minutes) {
		if (minutes < 10) {
			return "0" + Integer.toString(minutes);
		}
		return Integer.toString(minutes);
	}
	
	private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

		// when dialog box is closed, below method will be called.
		public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {
			int year = selectedYear;
			int month = selectedMonth;
			int day = selectedDay;
			
			int hour=appt_time.hour;

				appt_time.set(day, month, year);
				appt_time.normalize(true);
				appt_time.hour=hour;
				date.setText(setDateFormat(appt_time));
		}
	};
	
	
	private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			// TODO Auto-generated method stub
			int hour = hourOfDay;
			int min = minute;

			appt_time.hour = hour;
			appt_time.minute = minute;

			String result = getHour(appt_time.hour, appt_time.minute);
			time.setText(result);
		}
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.appt, menu);
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
			startActivity(new Intent(ScheduleAppt.this, MainActivity.class));
			ScheduleAppt.this.finish();
		}
		if (id == R.id.submit) {
			if(fieldsCompleted()){
				
				//This is the server area
				 String message = "Hey Kyle is the server receiving this?"; 
				 if (mTcpClient != null) {
	                    mTcpClient.sendMessage(message);
	                }
				 
				 //End server area
				 
			//This is where all of the email data is sent.
			String combined = "Name: " + fn.getText().toString() + " "
					+ ln.getText().toString() + "\n\n";
			combined += "Car: " + Integer.toString(car_list.get(index).year)
					+ " " + car_list.get(index).make + " "
					+ car_list.get(index).model + "\n\n";
			combined += "Appointment time: " + date.getText().toString()
					+ " at " + time.getText().toString() + "\n\n";
			combined += "Maintenance type: "
					+ car_list.get(index).mnlist.get(car_list.get(
							index).selectedMaintenance()).desc;
			
			Mail m = new Mail("automateappschedule@gmail.com", "autoMate15");
			String[] toArr = { "poyopoyo91@gmail.com" };
			m.setTo(toArr);
			m.setBody(combined);

			try {
				if (m.send()) {
					if(car_list.get(index).mnlist.get(car_list.get(index).selectedMaintenance()).repeatable){
						car_list.get(index).mnlist.get(car_list.get(index).selectedMaintenance()).miles = 
								car_list.get(index).mnlist.get(car_list.get(index).selectedMaintenance()).miles + 
								car_list.get(index).mnlist.get(car_list.get(index).selectedMaintenance()).interval;
					}
					else{
						car_list.get(index).mnlist.get(car_list.get(index).selectedMaintenance()).setCleared(true);
						}
					

					try {
						InternalStorage.writeObject(getBaseContext(), "car_list", car_list);
					} catch (IOException e) {
						Log.e("ERR", e.getMessage());
					}
				
					showToast("Appointment scheduled successfully.");
					startActivity(new Intent(ScheduleAppt.this, MainActivity.class));
					ScheduleAppt.this.finish();
				} else {
					showToast("Appointment request could not be sent");
				}
			} catch (Exception e) {
				// Toast.makeText(MailApp.this,
				// "There was a problem sending the email.",
				// Toast.LENGTH_LONG).show();
				Log.e("MailApp", "Could not send appointment request", e);
			}
		}
		}

		return super.onOptionsItemSelected(item);
	}

	void showToast(String s){
		Toast.makeText(this, s, Toast.LENGTH_LONG).show();
	}
	
	
	public class connectTask extends AsyncTask<String,String,TCPClient> {

        @Override
        protected TCPClient doInBackground(String... message) {

            //we create a TCPClient object and
            mTcpClient = new TCPClient(new TCPClient.OnMessageReceived() {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(String message) {
                    //this method calls the onProgressUpdate
                	
                    //publishProgress(message);
                }
            });
            mTcpClient.run();

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            //in the arrayList we add the messaged received from server
            // notify the adapter that the data set has changed. This means that new message received
            // from server was added to the list
        }
    }
	
	public Boolean fieldsCompleted() {
		if (fn.getText().length() == 0) {
			showToast("Please enter a first name");
			return false;
		}
		if (ln.getText().length() == 0) {
			showToast("Please enter a last name");
			return false;
		}
		return true;
	}
	
}