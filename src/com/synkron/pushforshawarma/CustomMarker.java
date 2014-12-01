package com.synkron.pushforshawarma;

public class CustomMarker {
    
	private String mLabel;
    private String mIcon;
    private Double mLatitude;
    private Double mLongitude;
	private String mDescription;
	
	public CustomMarker(String label, String icon, Double latitude, Double longitude)
    {
        this.mLabel = label;
        this.mLatitude = latitude;
        this.mLongitude = longitude;
        this.mIcon = icon;
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

}
