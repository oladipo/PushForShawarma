package com.synkron.pushforshawarma;

public class CustomMarker {
    
	private String mLabel;
    private String mIcon;
    private Double mLatitude;
    private Double mLongitude;
	private String mDescription;
	private String mAddress;
	private String mPhone;
	private String mEmail;
	
	public CustomMarker(String label, String icon, Double latitude, Double longitude)
    {
        this.mLabel = label;
        this.mLatitude = latitude;
        this.mLongitude = longitude;
        this.mIcon = icon;
    }

	public CustomMarker(String label, String icon, Double latitude, Double longitude, String description)
    {
        this.mLabel = label;
        this.mLatitude = latitude;
        this.mLongitude = longitude;
        this.mIcon = icon;
        this.mDescription = description;
    }
	public CustomMarker(String label, String icon, Double latitude, Double longitude, String address,
			String phone, String email)
    {
        this.mLabel = label;
        this.mLatitude = latitude;
        this.mLongitude = longitude;
        this.mIcon = icon;
        this.mAddress = address;
        this.mPhone = phone;
        this.mEmail = email;
    }
	
    public String getmLabel()
    {
        return mLabel;
    }

    public void setmDescription(String mDesc){
    	this.mDescription = mDesc;
    }
    
    public String getDescription(){
    	return mDescription;
    }
    public void setmLabel(String mLabel)
    {
        this.mLabel = mLabel;
    }

    public String getmIcon()
    {
        return mIcon;
    }

    public void setmIcon(String icon)
    {
        this.mIcon = icon;
    }

    public Double getmLatitude()
    {
        return mLatitude;
    }

    public void setmLatitude(Double mLatitude)
    {
        this.mLatitude = mLatitude;
    }

    public Double getmLongitude()
    {
        return mLongitude;
    }

    public void setmLongitude(Double mLongitude)
    {
        this.mLongitude = mLongitude;
    }

	public String getmEmail() {
		return mEmail;
	}

	public void setmEmail(String mEmail) {
		this.mEmail = mEmail;
	}

	public String getmAddress() {
		return mAddress;
	}

	public void setmAddress(String mAddress) {
		this.mAddress = mAddress;
	}

	public String getmPhone() {
		return mPhone;
	}

	public void setmPhone(String mPhone) {
		this.mPhone = mPhone;
	}

}
