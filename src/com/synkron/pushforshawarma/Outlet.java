package com.synkron.pushforshawarma;

public class Outlet {
	private String Code;
	private String Name;
	private String Icon;
	private String Latitude;
	private String Longitude;
	private String Address;
	private String Phone;
	private String Email;
	
	public Outlet(String Name, String Icon, String Latitude, String Longitude){
		this.Name = Name;
		this.Icon = Icon;
		this.Latitude = Latitude;
		this.Longitude = Longitude;
	}
	
	public Outlet(String Code, String Name, String Icon, String Latitude, String Longitude, String Address,
			String Phone, String Email){
		this.Code = Code;
		this.Name = Name;
		this.Icon = Icon;
		this.Latitude = Latitude;
		this.Longitude = Longitude;
		this.Address = Address;
		this.Phone = Phone;
		this.Email = Email;
	}

	public String getCode(){
		return this.Code;
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
	
	public String getAddress(){
		return this.Address;
	}
	
	public String getPhone(){
		return this.Phone;
	}
	
	public String getEmail(){
		return this.Email;
	}
}
