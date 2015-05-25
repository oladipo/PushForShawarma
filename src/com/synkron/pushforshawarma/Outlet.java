package com.synkron.pushforshawarma;

public class Outlet {
	public String Name;
	public String Icon;
	public String Latitude;
	public String Longitude;
	
	public Outlet(String Name, String Icon, String Latitude, String Longitude){
		this.Name = Name;
		this.Icon = Icon;
		this.Latitude = Latitude;
		this.Longitude = Longitude;
	}
	
	public String getName(){
		return this.Name;
	}
	
	public String getIcon(){
		return this.Icon;
	}
	
	public String getLatitude(){
		return this.Latitude;
	}
	
	public String getLongitude(){
		return this.Longitude;
	}
}
