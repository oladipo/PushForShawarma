package com.synkron.pushforshawarma;

public class Menu {
	private String Code;
	private String OutletCode;
	private String Name;
	private String ImageURL;
	private String Price;
	private String Description;
	
	public Menu(String code, String outletCode, String name, String price, String description){
		this.Code = code;
		this.OutletCode = outletCode;
		this.Name = name;
		this.Price = price;
		this.Description = description;
	}
	
	public String getCode() {
		return Code;
	}
	public void setCode(String code) {
		Code = code;
	}
	/**
	 * @return the outletCode
	 */
	public String getOutletCode() {
		return OutletCode;
	}
	/**
	 * @param outletCode the outletCode to set
	 */
	public void setOutletCode(String outletCode) {
		OutletCode = outletCode;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return Name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		Name = name;
	}
	/**
	 * @return the imageURL
	 */
	public String getImageURL() {
		return ImageURL;
	}
	/**
	 * @param imageURL the imageURL to set
	 */
	public void setImageURL(String imageURL) {
		ImageURL = imageURL;
	}
	/**
	 * @return the price
	 */
	public String getPrice() {
		return Price;
	}
	/**
	 * @param price the price to set
	 */
	public void setPrice(String price) {
		Price = price;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return Description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		Description = description;
	}
}
