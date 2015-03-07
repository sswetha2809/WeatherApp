package com.example.weatherapplication;
/*
 * author: swetha
 */
/**
 * The Class Report holds all values returned from the openweather API.
 */
public class Report {

	/** The description. */
	String description;
	
	/** The min temp. */
	Double minTemp;
	
	/** The max temp. */
	Double maxTemp;
	
	/** The humidity. */
	double humidity;
	
	/** The wind speed. */
	double windSpeed;
	
	/** The clouds. */
	double clouds;
	
	/** The icon id. */
	String iconId;
	
	/** The temp. */
	Double temp;
	
	/** The month. */
	String month;
	
	/** The day. */
	int day;
	
	/** The city name. */
	String cityName;
	
	/** The latitude. */
	double latitude;
	
	/** The longitude. */
	double longitude;

	/**
	 * Gets the latitude.
	 *
	 * @return the latitude
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * Sets the latitude.
	 *
	 * @param latitude the new latitude
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	/**
	 * Gets the longitude.
	 *
	 * @return the longitude
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * Sets the longitude.
	 *
	 * @param longitude the new longitude
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	/**
	 * Gets the city name.
	 *
	 * @return the city name
	 */
	public String getCityName() {
		return cityName;
	}

	/**
	 * Sets the city name.
	 *
	 * @param cityName the new city name
	 */
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	/**
	 * Gets the month.
	 *
	 * @return the month
	 */
	public String getMonth() {
		return month;
	}

	/**
	 * Sets the month.
	 *
	 * @param month the new month
	 */
	public void setMonth(String month) {
		this.month = month;
	}

	/**
	 * Gets the day.
	 *
	 * @return the day
	 */
	public int getDay() {
		return day;
	}

	/**
	 * Sets the day.
	 *
	 * @param day the new day
	 */
	public void setDay(int day) {
		this.day = day;
	}

	/**
	 * Gets the temp.
	 *
	 * @return the temp
	 */
	public Double getTemp() {
		return temp;
	}

	/**
	 * Sets the temp.
	 *
	 * @param temp the new temp
	 */
	public void setTemp(Double temp) {
		this.temp = temp;
	}

	/**
	 * Gets the icon id.
	 *
	 * @return the icon id
	 */
	public String getIconId() {
		return iconId;
	}

	/**
	 * Sets the icon id.
	 *
	 * @param iconId the new icon id
	 */
	public void setIconId(String iconId) {
		this.iconId = iconId;
	}

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description.
	 *
	 * @param description the new description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets the min temp.
	 *
	 * @return the min temp
	 */
	public Double getMinTemp() {
		return minTemp;
	}

	/**
	 * Sets the min temp.
	 *
	 * @param minTemp the new min temp
	 */
	public void setMinTemp(Double minTemp) {
		this.minTemp = minTemp;
	}

	/**
	 * Gets the max temp.
	 *
	 * @return the max temp
	 */
	public Double getMaxTemp() {
		return maxTemp;
	}

	/**
	 * Sets the max temp.
	 *
	 * @param maxTemp the new max temp
	 */
	public void setMaxTemp(Double maxTemp) {
		this.maxTemp = maxTemp;
	}

	/**
	 * Gets the humidity.
	 *
	 * @return the humidity
	 */
	public double getHumidity() {
		return humidity;
	}

	/**
	 * Sets the humidity.
	 *
	 * @param humidity the new humidity
	 */
	public void setHumidity(double humidity) {
		this.humidity = humidity;
	}

	/**
	 * Gets the wind speed.
	 *
	 * @return the wind speed
	 */
	public double getWindSpeed() {
		return windSpeed;
	}

	/**
	 * Sets the wind speed.
	 *
	 * @param windSpeed the new wind speed
	 */
	public void setWindSpeed(double windSpeed) {
		this.windSpeed = windSpeed;
	}

	/**
	 * Gets the clouds.
	 *
	 * @return the clouds
	 */
	public double getClouds() {
		return clouds;
	}

	/**
	 * Sets the clouds.
	 *
	 * @param clouds the new clouds
	 */
	public void setClouds(double clouds) {
		this.clouds = clouds;
	}

}
