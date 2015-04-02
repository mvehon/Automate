package com.automate.app;

import java.io.Serializable;

public class Shop implements Serializable {
	String name, address, email;

	public Shop(String nm, String adr) {
		name = nm;
		address = adr;
		email = "mvehon@asu.edu";
	}
	
	public Shop(String nm, String adr, String em) {
		name = nm;
		address = adr;
		email = em;
	}
	
	public String getEmail(){
		return email;
	}
	
	@Override
    public String toString() {
		return name + "\n" + address;
		}
}
