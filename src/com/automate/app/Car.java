package com.automate.app;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.text.format.Time;

public class Car implements Serializable {
	public String make, model = "";
	int year, mileage, yeardate, currentyear;
	double estimatedmileage; // The mileage to add per day
	List<Maintenance> mnlist;

	public Car() {

	}

	public Car(String ma, String mod, int yr, int mil, double emil, int yrdt,
			int cyr) {
		make = ma;
		model = mod;
		year = yr;
		mileage = mil;
		estimatedmileage = emil;
		yeardate = yrdt;
		currentyear = cyr;
		createMaintenanceList();
	}

	private void createMaintenanceList() {
		mnlist = new ArrayList<Maintenance>();
		mnlist.add(new Maintenance(mileage, "Oil change", true, 3000));
		mnlist.add(new Maintenance(30000,
				"Replace air filter and power steering fluid"));
		mnlist.add(new Maintenance(40000, "Replace serpentine belt"));
		mnlist.add(new Maintenance(60000, "Replace timing belt"));
	}

	public String getMake() {
		return make;
	}

	public String getModel() {
		return model;
	}

	public int getYear() {
		return year;
	}

	public int getMileage() {
		return mileage;
	}

	public double getEstimatedmileage() {
		return estimatedmileage;
	}

	public void setMake(String a) {
		make = a;
	}

	public void setModel(String a) {
		model = a;
	}

	public double estCurMileage() {
		double t = estimatedmileage * daysSinceLastReport();
		return t + mileage;
	}

	public int daysSinceLastReport() {
		int x = 0;
		Time dt = new Time();
		dt.setToNow();
		if (dt.yearDay >= yeardate) {
			x = dt.yearDay - yeardate;
		} else {
			x = 365 - yeardate;
			x += dt.yearDay;
		}
		return x;
	}

	public Boolean activeMaintenance() {
		for (int i = 0; i < mnlist.size(); i++) {
			if (estCurMileage() > mnlist.get(i).miles
					&& !mnlist.get(i).cleared) {
				return true;
			}
		}
		return false;
	}
	
	public int selectedMaintenance(){
		for (int i = 0; i < mnlist.size(); i++) {
			if (estCurMileage() > mnlist.get(i).miles
					&& !mnlist.get(i).cleared) {
				return i;
			}
		}
		return 0;
	}
}
