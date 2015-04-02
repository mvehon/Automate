package com.automate.app;

import java.io.Serializable;

public class Maintenance implements Serializable{
	int miles, interval;
	String desc;
	Boolean cleared;
	Boolean repeatable;
	public Maintenance(int mil, String de) {
		miles = mil;
		desc = de;
		cleared = false;
		repeatable = false;
	}
	
	public Maintenance(int mil, String de, Boolean cyc, int interv) {
		miles = mil+interv;
		desc = de;
		cleared = false;
		repeatable = cyc;
		interval = interv;
	}
	
	public void setCleared(Boolean b){
		cleared = b;
	}
	public void setMiles(int x){
		miles = x;
	}
}
