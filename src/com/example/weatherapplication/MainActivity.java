/*
 * author: swetha
 */
package com.example.weatherapplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

// TODO: Auto-generated Javadoc
/**
 * The Class MainActivity.
 */
@SuppressLint("NewApi")
public class MainActivity extends ActionBarActivity implements
		OnItemClickListener {

	/** The report. */
	static Report report = new Report();

	/** The prediction. */
	TextView prediction = null;

	/** The temp. */
	TextView temp = null;

	/** The humidity. */
	TextView humidity = null;

	/** The cloud percent. */
	TextView cloudPercent = null;

	/** The wind speed. */
	TextView windSpeed = null;

	/** The city. */
	TextView city = null;

	/** The state_country. */
	TextView state_country = null;

	/** The date. */
	TextView date = null;

	/** The auto comp view. */
	AutoCompleteTextView autoCompView = null;

	/** The description icon. */
	ImageView descriptionIcon = null;

	/** The location icon. */
	ImageView locationIcon = null;

	/** The clear icon. */
	ImageView clearIcon = null;

	/** The min maxtemp. */
	TextView minMaxtemp = null;

	/** The forecast report. */
	List<Report> forecastReport = new ArrayList<Report>();

	/** The nearby report. */
	List<Report> nearbyReport = new ArrayList<Report>();

	/** The icon map. */
	HashMap<String, Integer> iconMap = new HashMap<String, Integer>();

	/** The bg map. */
	HashMap<String, Integer> bgMap = new HashMap<String, Integer>();

	/** The cal. */
	Calendar cal = Calendar.getInstance();

	/** The main layout. */
	LinearLayout mainLayout = null;

	/** The temp details. */
	LinearLayout tempDetails = null;

	/** The forecast layout. */
	LinearLayout forecastLayout = null;

	/** The forecast progress. */
	RelativeLayout forecastProgress = null;

	/** The nearby layout. */
	LinearLayout nearbyLayout = null;

	/** The temp progress. */
	ProgressDialog tempProgress = null;

	/** The title bar. */
	LinearLayout titleBar = null;

	/** The location manager. */
	LocationManager locationManager = null;

	/** The weather complete. */
	boolean weatherComplete = false;

	/** The forecast complete. */
	boolean forecastComplete = false;

	/** The nearby complete. */
	boolean nearbyComplete = false;

	/** The str array. */
	String[] strArray;

	/** The main context. */
	public Context mainContext = this;

	/** The activity. */
	public MainActivity activity = this;

	/** The temperature icon. */
	TextView temperatureIcon = null;

	/** The prediction icon. */
	ImageView predictionIcon = null;

	/**
	 * Initializes all the variables with the necessary values.
	 */
	private void initializeAll() {
		predictionIcon = (ImageView) findViewById(R.id.predictionIcon);
		autoCompView = (AutoCompleteTextView) findViewById(R.id.autocomplete);
		autoCompView.setAdapter(new PlacesAutoCompleteAdapter(this,
				R.layout.list_item));
		autoCompView.setOnItemClickListener(this);
		forecastLayout = (LinearLayout) findViewById(R.id.forecastView);
		nearbyLayout = (LinearLayout) findViewById(R.id.nearbyView);
		mainLayout = (LinearLayout) findViewById(R.id.mainLayout);
		prediction = (TextView) findViewById(R.id.prediction);
		temp = (TextView) findViewById(R.id.temp);
		humidity = (TextView) findViewById(R.id.humidity);
		cloudPercent = (TextView) findViewById(R.id.clouds);
		windSpeed = (TextView) findViewById(R.id.wind);
		minMaxtemp = (TextView) findViewById(R.id.minMaxTemp);
		city = (TextView) findViewById(R.id.city);
		state_country = (TextView) findViewById(R.id.state_country);
		temperatureIcon = (TextView) findViewById(R.id.temperatureIcon);
		temperatureIcon.setContentDescription("Celcius");
		tempDetails = (LinearLayout) findViewById(R.id.tempDetails);
		date = (TextView) findViewById(R.id.time);
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		locationIcon = (ImageView) findViewById(R.id.locationIcon);
		clearIcon = (ImageView) findViewById(R.id.clearIcon);

		tempProgress = new ProgressDialog(mainContext);
		titleBar = (LinearLayout) findViewById(R.id.titlebar);
		titleBar.setVisibility(View.GONE);
		tempDetails.setVisibility(View.GONE);
		forecastLayout.setVisibility(View.GONE);
		nearbyLayout.setVisibility(View.GONE);
		initializeMaps();

	}

	/**
	 * Initialize the icon - image map and background image map. used for
	 * loading the icons and the backgrounds for various screens
	 */
	private void initializeMaps() {
		iconMap.put("01d", R.drawable.clearsky_day_icon);
		iconMap.put("01n", R.drawable.clearsky_night_icon);
		iconMap.put("02d", R.drawable.fewclouds_day_icon);
		iconMap.put("02n", R.drawable.clearsky_night_icon);
		iconMap.put("03d", R.drawable.scatteredclouds_day_icon);
		iconMap.put("03n", R.drawable.scatteredclouds_night_icon);
		iconMap.put("04d", R.drawable.brokenclouds_day_icon);
		iconMap.put("04n", R.drawable.brokenclouds_night_icon);
		iconMap.put("09d", R.drawable.showerrain_icon);
		iconMap.put("09n", R.drawable.showerrain_icon);
		iconMap.put("10d", R.drawable.rain_icon);
		iconMap.put("10n", R.drawable.rain_icon);
		iconMap.put("11d", R.drawable.thunderstorm_icon);
		iconMap.put("11n", R.drawable.thunderstorm_icon);
		iconMap.put("13d", R.drawable.snow_icon);
		iconMap.put("13n", R.drawable.snow_icon);
		iconMap.put("50d", R.drawable.mist_icon);
		iconMap.put("50n", R.drawable.thunderstorm_icon);

		bgMap.put("01d", R.drawable.clearsky);
		bgMap.put("01n", R.drawable.clearsky);
		bgMap.put("02d", R.drawable.cloudy);
		bgMap.put("02n", R.drawable.cloudy);
		bgMap.put("03d", R.drawable.cloudy);
		bgMap.put("03n", R.drawable.cloudy);
		bgMap.put("04d", R.drawable.cloudy);
		bgMap.put("04n", R.drawable.cloudy);
		bgMap.put("09d", R.drawable.rainyday);
		bgMap.put("09n", R.drawable.rainyday);
		bgMap.put("10d", R.drawable.rainyday);
		bgMap.put("10n", R.drawable.rainyday);
		bgMap.put("11d", R.drawable.snowymorning);
		bgMap.put("11n", R.drawable.snowymorning);
		bgMap.put("13d", R.drawable.snowymorning);
		bgMap.put("13n", R.drawable.snowymorning);
		bgMap.put("50d", R.drawable.snowymorning);
		bgMap.put("50n", R.drawable.snowymorning);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v7.app.ActionBarActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		// Remove notification bar
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.weather_main);
		initializeAll();

		// Showing spinner bar till the content is loaded

		tempProgress.setMessage(Constants.SPINNER_MESSAGE);
		tempProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		tempProgress.setIndeterminate(true);
		temperatureIcon.setText(Constants.DEGREE + "C");

		// listener for clear icon which clears the autocomplete text area

		clearIcon.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				autoCompView.setText(Constants.EMPTY_STRING);

			}
		});

		// listener for location icon which fetches the current location
		// temperature

		locationIcon.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				getAddress();
				tempDetails.setVisibility(View.GONE);
				forecastLayout.setVisibility(View.GONE);
				nearbyLayout.setVisibility(View.GONE);

			}
		});

		// listener for temperature icon that converts the temp between
		// Fahreheit and Celcius
		temperatureIcon.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				int minTemp = 0, maxTemp = 0, temp1 = 0;

				if (temperatureIcon.getContentDescription().equals(
						Constants.CELCIUS)) {

					temperatureIcon.setContentDescription(Constants.FAHRENHEIT);
					minTemp = (int) (report.getMinTemp() - 273.15) * (9 / 5)
							+ 32;
					maxTemp = (int) (report.getMaxTemp() - 273.15) * (9 / 5)
							+ 32;
					temp1 = (int) (report.getTemp() - 273.15) * (9 / 5) + 32;
					temperatureIcon.setText(Constants.DEGREE + "F");

				} else {

					temperatureIcon.setContentDescription(Constants.CELCIUS);
					minTemp = (int) (report.getMinTemp() - 273.15);
					maxTemp = (int) (report.getMaxTemp() - 273.15);
					temp1 = (int) (report.getTemp() - 273.15);
					temperatureIcon.setText(Constants.DEGREE + "C");

				}
				minMaxtemp.setText(minTemp + Constants.DEGREE + "     "
						+ maxTemp + Constants.DEGREE);
				temp.setText(String.valueOf(temp1));

			}
		});
		mainLayout.setVisibility(View.VISIBLE);
		getAddress();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Method that handles the autocomplete text area with the suggestions of
	 * the city names
	 * 
	 * @param input
	 *            the input
	 * @return the array list
	 */
	private ArrayList<String> autocomplete(String input) {
		ArrayList<String> resultList = null;

		HttpURLConnection conn = null;
		StringBuilder jsonResults = new StringBuilder();
		try {
			URL url = new URL(Constants.GEO_CODE_URL + input);
			conn = (HttpURLConnection) url.openConnection();
			InputStreamReader in = new InputStreamReader(conn.getInputStream());

			// Load the results into a StringBuilder
			int read;
			char[] buff = new char[1024];
			while ((read = in.read(buff)) != -1) {
				jsonResults.append(buff, 0, read);
			}
		} catch (MalformedURLException e) {
			Log.e(Constants.LOG_TAG, "Error processing Places API URL", e);
			return resultList;
		} catch (IOException e) {
			Log.e(Constants.LOG_TAG, "Error connecting to Places API", e);
			return resultList;
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		try {

			JSONArray predsJsonArray = new JSONArray(jsonResults.toString());
			resultList = new ArrayList<String>(predsJsonArray.length());
			for (int i = 0; i < predsJsonArray.length(); i++) {
				if (predsJsonArray.get(i).toString().equalsIgnoreCase("%s"))
					break;
				resultList.add(predsJsonArray.get(i).toString());
			}
		} catch (JSONException e) {
			Log.e(Constants.LOG_TAG, "Cannot process JSON results", e);
		}

		return resultList;
	}

	/**
	 * Adapter Class for PlacesAutoComplete
	 */
	private class PlacesAutoCompleteAdapter extends ArrayAdapter<String>
			implements Filterable {

		/** The result list. */
		private ArrayList<String> resultList;

		/**
		 * Instantiates a new places auto complete adapter.
		 * 
		 * @param context
		 *            the context
		 * @param textViewResourceId
		 *            the text view resource id
		 */
		public PlacesAutoCompleteAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.ArrayAdapter#getCount()
		 */
		@Override
		public int getCount() {
			if (resultList != null)
				return resultList.size();
			else
				return 0;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.ArrayAdapter#getItem(int)
		 */
		@Override
		public String getItem(int index) {
			return resultList.get(index);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.ArrayAdapter#getFilter()
		 */
		@Override
		public Filter getFilter() {
			Filter filter = new Filter() {
				@Override
				protected FilterResults performFiltering(CharSequence constraint) {
					FilterResults filterResults = new FilterResults();
					if (constraint != null) {
						String str = constraint.toString();
						str.replace(" ", Constants.EMPTY_STRING);
						resultList = autocomplete(constraint.toString());
						filterResults.values = resultList;
						filterResults.count = resultList.size();
					}
					return filterResults;
				}

				@Override
				protected void publishResults(CharSequence constraint,
						FilterResults results) {
					if (results != null && results.count > 0) {
						notifyDataSetChanged();
					} else {
						notifyDataSetInvalidated();
					}
				}
			};
			return filter;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget
	 * .AdapterView, android.view.View, int, long) Listener to select from the
	 * list of cities
	 */
	@Override
	public void onItemClick(AdapterView<?> adapterView, View view,
			int position, long ID) {
		tempDetails.setVisibility(View.GONE);
		forecastLayout.setVisibility(View.GONE);
		nearbyLayout.setVisibility(View.GONE);
		String str = (String) adapterView.getItemAtPosition(position);
		strArray = str.split(Constants.COMMA_SPLITTER);
		str = str.replace(" ", Constants.EMPTY_STRING);

		// Invoke the Async task to display the weather info of the currently
		// selected city

		WeatherTask task = new WeatherTask();
		task.setCity(strArray[0]);
		task.setState(strArray[1]);
		task.setFirstSearch(false);
		task.execute();

		// Invoke the Async task to display the forecast info of the currently
		// selected city

		ForecastTask task1 = new ForecastTask();
		task1.setCity(strArray[0]);
		task1.setState(strArray[1]);
		task1.setIsFirstSearch(false);
		task1.execute();

	}

	/**
	 * To get the current location on application load
	 * 
	 * @return the address
	 */
	public void getAddress() {

		// Check whether GPS and internet are available

		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			mainLayout.setVisibility(View.GONE);
			buildAlertMessageNoGps();
		} else if (!isConn()) {
			mainLayout.setVisibility(View.GONE);
			buildAlertMessageNoInternet();
		} else {

			tempProgress.show();
			Location location = locationManager
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (location == null) {
				LocationListener locationListener = new MyLocationListener();
				locationManager.requestLocationUpdates(
						LocationManager.GPS_PROVIDER, 0, 0, locationListener);
			} else {
				updateWeatherInfo(location);
			}
		}

	}

	/**
	 * The Class WeatherNearbyTask handles the API call and displays the weather
	 * info in the near by locations to the current place / selected place.
	 */
	private class WeatherNearbyTask extends AsyncTask<Void, String, String> {

		/** The latitude. */
		private double latitude;

		/** The longitude. */
		private double longitude;

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
		 * @param latitude
		 *            the new latitude
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
		 * @param longitude
		 *            the new longitude
		 */
		public void setLongitude(double longitude) {
			this.longitude = longitude;
		}

		/**
		 * Sets the nearby view.
		 */
		private void setNearbyView() {

			// report of the 4 nearby places

			final Report report1 = nearbyReport.get(1);
			final Report report2 = nearbyReport.get(2);
			final Report report3 = nearbyReport.get(3);
			final Report report4 = nearbyReport.get(4);

			LinearLayout nearby1 = (LinearLayout) findViewById(R.id.nearby1);
			LinearLayout nearby2 = (LinearLayout) findViewById(R.id.nearby2);
			LinearLayout nearby3 = (LinearLayout) findViewById(R.id.nearby3);
			LinearLayout nearby4 = (LinearLayout) findViewById(R.id.nearby4);

			// listener for the first nearby location

			nearby1.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					WeatherTask task = new WeatherTask();
					task.setLatitude(report1.getLatitude());
					task.setLongitude(report1.getLongitude());
					task.setFirstSearch(true);
					task.execute();

					ForecastTask task1 = new ForecastTask();
					task1.setLatitude(report1.getLatitude());
					task1.setLongitude(report1.getLongitude());
					task1.setIsFirstSearch(true);
					task1.execute();

					WeatherNearbyTask task2 = new WeatherNearbyTask();
					task2.setLatitude(report1.getLatitude());
					task2.setLongitude(report1.getLongitude());
					task2.execute();

					updateCity(report1.getCityName());

				}
			});

			// listener for the second nearby location

			nearby2.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					WeatherTask task = new WeatherTask();
					task.setLatitude(report2.getLatitude());
					task.setLongitude(report2.getLongitude());
					task.setFirstSearch(true);
					task.execute();

					ForecastTask task1 = new ForecastTask();
					task1.setLatitude(report2.getLatitude());
					task1.setLongitude(report2.getLongitude());
					task1.setIsFirstSearch(true);
					task1.execute();

					WeatherNearbyTask task2 = new WeatherNearbyTask();
					task2.setLatitude(report2.getLatitude());
					task2.setLongitude(report2.getLongitude());
					task2.execute();

					updateCity(report2.getCityName());

				}
			});

			// listener for the third nearby location

			nearby3.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					WeatherTask task = new WeatherTask();
					task.setLatitude(report3.getLatitude());
					task.setLongitude(report3.getLongitude());
					task.setFirstSearch(true);
					task.execute();

					ForecastTask task1 = new ForecastTask();
					task1.setLatitude(report3.getLatitude());
					task1.setLongitude(report3.getLongitude());
					task1.setIsFirstSearch(true);
					task1.execute();

					WeatherNearbyTask task2 = new WeatherNearbyTask();
					task2.setLatitude(report3.getLatitude());
					task2.setLongitude(report3.getLongitude());
					task2.execute();

					updateCity(report3.getCityName());

				}
			});

			// listener for the fourth nearby location

			nearby4.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					WeatherTask task = new WeatherTask();
					task.setLatitude(report4.getLatitude());
					task.setLongitude(report4.getLongitude());
					task.setFirstSearch(true);
					task.execute();

					ForecastTask task1 = new ForecastTask();
					task1.setLatitude(report4.getLatitude());
					task1.setLongitude(report4.getLongitude());
					task1.setIsFirstSearch(true);
					task1.execute();

					WeatherNearbyTask task2 = new WeatherNearbyTask();
					task2.setLatitude(report4.getLatitude());
					task2.setLongitude(report4.getLongitude());
					task2.execute();

					updateCity(report4.getCityName());

				}
			});

			TextView city1 = (TextView) findViewById(R.id.city1);
			TextView city2 = (TextView) findViewById(R.id.city2);
			TextView city3 = (TextView) findViewById(R.id.city3);
			TextView city4 = (TextView) findViewById(R.id.city4);

			TextView distance1 = (TextView) findViewById(R.id.distance1);
			TextView distance2 = (TextView) findViewById(R.id.distance2);
			TextView distance3 = (TextView) findViewById(R.id.distance3);
			TextView distance4 = (TextView) findViewById(R.id.distance4);

			ImageView icon1 = (ImageView) findViewById(R.id.icon1);
			ImageView icon2 = (ImageView) findViewById(R.id.icon2);
			ImageView icon3 = (ImageView) findViewById(R.id.icon3);
			ImageView icon4 = (ImageView) findViewById(R.id.icon4);

			TextView temp1 = (TextView) findViewById(R.id.temp1);
			TextView temp2 = (TextView) findViewById(R.id.temp2);
			TextView temp3 = (TextView) findViewById(R.id.temp3);
			TextView temp4 = (TextView) findViewById(R.id.temp4);

			TextView humid1 = (TextView) findViewById(R.id.hum1);
			TextView humid2 = (TextView) findViewById(R.id.hum2);
			TextView humid3 = (TextView) findViewById(R.id.hum3);
			TextView humid4 = (TextView) findViewById(R.id.hum4);

			TextView wind1 = (TextView) findViewById(R.id.wind1);
			TextView wind2 = (TextView) findViewById(R.id.wind2);
			TextView wind3 = (TextView) findViewById(R.id.wind3);
			TextView wind4 = (TextView) findViewById(R.id.wind4);

			// trimming the city name if it is more than 9 characters

			if (report1.getCityName().length() > 9) {
				city1.setText(report1.getCityName().subSequence(0, 10));

			} else
				city1.setText(report1.getCityName());

			if (report2.getCityName().length() > 9) {
				city2.setText(report2.getCityName().subSequence(0, 10));

			} else
				city2.setText(report2.getCityName());

			if (report3.getCityName().length() > 9) {
				city3.setText(report3.getCityName().subSequence(0, 10));

			} else
				city3.setText(report3.getCityName());

			if (report4.getCityName().length() > 9) {
				city4.setText(report4.getCityName().subSequence(0, 10));

			} else
				city4.setText(report4.getCityName());

			// calculate the distance of the nearby place with respect to the
			// current location

			double distance = calculateDistance(report.getLongitude(),
					report.getLatitude(), report1.getLongitude(),
					report1.getLatitude())
					* Constants.MILES_CONVERSION;

			distance1.setText(String.valueOf(distance).substring(0, 3)
					+ Constants.MILES);

			distance = calculateDistance(report.getLongitude(),
					report.getLatitude(), report2.getLongitude(),
					report2.getLatitude())
					* Constants.MILES_CONVERSION;

			distance2.setText(String.valueOf(distance).substring(0, 3)
					+ Constants.MILES);

			distance = calculateDistance(report.getLongitude(),
					report.getLatitude(), report3.getLongitude(),
					report3.getLatitude())
					* Constants.MILES_CONVERSION;

			distance3.setText(String.valueOf(distance).substring(0, 3)
					+ Constants.MILES);

			distance = calculateDistance(report.getLongitude(),
					report.getLatitude(), report4.getLongitude(),
					report4.getLatitude())
					* Constants.MILES_CONVERSION;

			distance4.setText(String.valueOf(distance).substring(0, 3)
					+ Constants.MILES);

			// setting weather icon for each place

			icon1.setBackgroundResource(iconMap.get(report1.getIconId()));
			icon2.setBackgroundResource(iconMap.get(report2.getIconId()));
			icon3.setBackgroundResource(iconMap.get(report3.getIconId()));
			icon4.setBackgroundResource(iconMap.get(report4.getIconId()));

			// setting the temperature details for each nearby location

			int temp12 = 0;
			if (temperatureIcon.getContentDescription().equals(
					Constants.CELCIUS)) {
				temp12 = (int) (report1.getTemp() - Constants.KELVIN_CONVERSION);

			} else {
				temp12 = (int) (report1.getTemp() - Constants.KELVIN_CONVERSION)
						* (9 / 5) + 32;
			}

			temp1.setText(String.valueOf(temp12));

			if (temperatureIcon.getContentDescription().equals(
					Constants.FAHRENHEIT)) {
				temp12 = (int) (report2.getTemp() - Constants.KELVIN_CONVERSION);

			} else {
				temp12 = (int) (report2.getTemp() - Constants.KELVIN_CONVERSION)
						* (9 / 5) + 32;
			}

			temp2.setText(String.valueOf(temp12));

			if (temperatureIcon.getContentDescription().equals(
					Constants.CELCIUS)) {
				temp12 = (int) (report3.getTemp() - Constants.KELVIN_CONVERSION);

			} else {
				temp12 = (int) (report3.getTemp() - Constants.KELVIN_CONVERSION)
						* (9 / 5) + 32;
			}

			temp3.setText(String.valueOf(temp12));

			if (temperatureIcon.getContentDescription().equals(
					Constants.CELCIUS)) {
				temp12 = (int) (report4.getTemp() - Constants.KELVIN_CONVERSION);

			} else {
				temp12 = (int) (report4.getTemp() - Constants.KELVIN_CONVERSION)
						* (9 / 5) + 32;
			}

			temp4.setText(String.valueOf(temp12));

			// setting humidity and wind details extracted from the report

			humid1.setText("Humid: " + report1.getHumidity()
					+ Constants.PERCENT);
			humid2.setText("Humid: " + report2.getHumidity()
					+ Constants.PERCENT);
			humid3.setText("Humid: " + report3.getHumidity()
					+ Constants.PERCENT);
			humid4.setText("Humid: " + report4.getHumidity()
					+ Constants.PERCENT);

			wind1.setText("Wind: " + report1.getWindSpeed()
					+ Constants.MILES_PER_SECOND);
			wind2.setText("Wind: " + report2.getWindSpeed()
					+ Constants.MILES_PER_SECOND);
			wind3.setText("Wind: " + report3.getWindSpeed()
					+ Constants.MILES_PER_SECOND);
			wind4.setText("Wind: " + report4.getWindSpeed()
					+ Constants.MILES_PER_SECOND);

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected String doInBackground(Void... params) {
			HttpURLConnection con = null;
			InputStream is = null;
			try {

				con = (HttpURLConnection) (new URL(Constants.LAT_LONG_URL
						+ "lat=" + getLatitude() + "&lon=" + getLongitude()
						+ Constants.CONT_URL)).openConnection();

				con.setRequestMethod("GET");
				con.setDoInput(true);
				con.setDoOutput(true);
				con.connect();

				// Let's read the response
				StringBuffer buffer = new StringBuffer();
				is = con.getInputStream();
				BufferedReader br = new BufferedReader(
						new InputStreamReader(is));
				String line = null;
				while ((line = br.readLine()) != null)
					buffer.append(line + "\r\n");
				is.close();
				con.disconnect();
				String jsonObject = buffer.toString();
				JsonParser parser = new JsonParser();
				JsonElement root = parser.parse(jsonObject).getAsJsonObject()
						.get(Constants.LIST).getAsJsonArray();

				nearbyReport.clear();
				// extracting info from the JSON
				for (int i = 0; i < 5; i++) {
					Report rep = new Report();

					JsonElement currentObject = root.getAsJsonArray().get(i);

					JsonElement coord = currentObject.getAsJsonObject().get(
							Constants.COORD);

					rep.setLatitude(coord.getAsJsonObject().get(Constants.LAT)
							.getAsDouble());
					rep.setLongitude(coord.getAsJsonObject()
							.get(Constants.LONG).getAsDouble());

					JsonArray weather = currentObject.getAsJsonObject()
							.get(Constants.WEATHER).getAsJsonArray();

					rep.setCityName(currentObject.getAsJsonObject()
							.get(Constants.NAME).getAsString());
					rep.setIconId(weather.get(0).getAsJsonObject()
							.get(Constants.ICON).getAsString());

					JsonElement main = currentObject.getAsJsonObject().get(
							Constants.MAIN);

					rep.setTemp(main.getAsJsonObject().get(Constants.TEMP)
							.getAsDouble());
					rep.setHumidity(main.getAsJsonObject()
							.get(Constants.HUMIDITY).getAsDouble());
					JsonElement wind = currentObject.getAsJsonObject().get(
							Constants.WIND);

					rep.setWindSpeed(wind.getAsJsonObject()
							.get(Constants.SPEED).getAsDouble());
					JsonElement clouds = currentObject.getAsJsonObject().get(
							Constants.CLOUDS);

					rep.setClouds(clouds.getAsJsonObject().get(Constants.ALL)
							.getAsDouble());

					nearbyReport.add(i, rep);

				}

				activity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						if (nearbyReport.size() > 0)
							setNearbyView();
						else
							Toast.makeText(mainContext,
									"Nearby Weather info unavailable",
									Toast.LENGTH_LONG).show();
					}
				});

			} catch (Throwable t) {
				activity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(mainContext,
								Constants.TOAST_NEARBY_UNAVAILABLE,
								Toast.LENGTH_LONG).show();
					}
				});

				t.printStackTrace();
			} finally {
				try {
					is.close();
				} catch (Throwable t) {
				}
				try {
					con.disconnect();
				} catch (Throwable t) {
				}
			}
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			nearbyComplete = true;
			activity.runOnUiThread(new Runnable() {

				@Override
				public void run() {

					nearbyLayout.setVisibility(View.VISIBLE);
				}
			});

		}

	}

	/**
	 * The Class ForecastTask is to call the openweather API and update the
	 * forecast info for the currently selected city.
	 */
	private class ForecastTask extends AsyncTask<Void, String, String> {

		/** The city. */
		private String city;

		/** The state. */
		private String state;

		/** The is first search. */
		private boolean isFirstSearch;

		/** The latitude. */
		private double latitude;

		/** The longitude. */
		private double longitude;

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
		 * @param latitude
		 *            the new latitude
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
		 * @param longitude
		 *            the new longitude
		 */
		public void setLongitude(double longitude) {
			this.longitude = longitude;
		}

		/**
		 * Gets the city.
		 * 
		 * @return the city
		 */
		public String getCity() {
			return city;
		}

		/**
		 * Sets the city.
		 * 
		 * @param city
		 *            the new city
		 */
		public void setCity(String city) {
			this.city = city;
		}

		/**
		 * Gets the state.
		 * 
		 * @return the state
		 */
		public String getState() {
			return state;
		}

		/**
		 * Sets the state.
		 * 
		 * @param state
		 *            the new state
		 */
		public void setState(String state) {
			this.state = state;
		}

		/**
		 * Sets the if is the first search.
		 * 
		 * @param isFirstSearch
		 *            the new checks if is first search
		 */
		public void setIsFirstSearch(boolean isFirstSearch) {
			this.isFirstSearch = isFirstSearch;
		}

		/**
		 * Sets the forecast view with the forecast for next 10 days.
		 */
		private void setForecastView() {
			LinearLayout hsv = (LinearLayout) findViewById(R.id.forecastView);
			hsv.removeAllViews();

			for (int i = 0; i < 10; i++) {
				Report currentReport = forecastReport.get(i);
				LinearLayout parentLayout = new LinearLayout(mainContext);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						0, LayoutParams.MATCH_PARENT, 1);
				parentLayout.setOrientation(LinearLayout.VERTICAL);
				parentLayout.setBackgroundResource(R.drawable.forecastmargin);
				params.setMargins(0, 10, 10, 10);
				parentLayout.setId(i);
				parentLayout.setWeightSum(7);

				// Sets the forecast month and day in MMM, DD format

				TextView day = new TextView(mainContext);
				day.setText(currentReport.getMonth() + " "
						+ currentReport.getDay());
				LinearLayout.LayoutParams dayParams = new LinearLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, 0, 2);
				dayParams.setMargins(0, 10, 0, 0);
				day.setTextSize(14);
				day.setGravity(Gravity.CENTER);
				day.setLayoutParams(dayParams);

				parentLayout.addView(day);

				// Sets the weather icon for that particular day

				ImageView icon = new ImageView(mainContext);
				LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, 0, 3);

				iconParams.setMargins(0, 0, 0, 0);

				iconParams.gravity = Gravity.CENTER;
				icon.setBackgroundResource(iconMap.get(currentReport
						.getIconId()));
				icon.setLayoutParams(iconParams);

				parentLayout.addView(icon);

				// Sets the temerature prediction

				TextView temp = new TextView(mainContext);
				int minTemp = 0, maxTemp = 0;
				if (temperatureIcon.getContentDescription().equals(
						Constants.CELCIUS)) {
					minTemp = (int) (currentReport.getMinTemp() - Constants.KELVIN_CONVERSION);
					maxTemp = (int) (currentReport.getMaxTemp() - Constants.KELVIN_CONVERSION);
				} else {
					minTemp = (int) (currentReport.getMinTemp() - Constants.KELVIN_CONVERSION)
							* (9 / 5) + 32;
					maxTemp = (int) (currentReport.getMaxTemp() - Constants.KELVIN_CONVERSION)
							* (9 / 5) + 32;

				}
				temp.setGravity(Gravity.CENTER);
				temp.setText(maxTemp + Constants.DEGREE + "        " + minTemp
						+ Constants.DEGREE);
				LinearLayout.LayoutParams tempTextParams = new LinearLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, 0, 2);
				temp.setTextSize(14);
				tempTextParams.setMargins(0, 0, 0, 10);
				temp.setLayoutParams(tempTextParams);

				parentLayout.addView(temp);

				hsv.addView(parentLayout);

			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected String doInBackground(Void... arg0) {
			HttpURLConnection con = null;
			InputStream is = null;
			try {

				if (isFirstSearch) {
					con = (HttpURLConnection) (new URL(
							Constants.LAT_LONG_URL_FORECAST + "lat="
									+ getLatitude() + "&lon=" + getLongitude()
									+ Constants.CONT_URL_FOREACST))
							.openConnection();

				} else {
					con = (HttpURLConnection) (new URL(Constants.BASE_URL
							+ getCity() + Constants.COMMA_SPLITTER + getState()
							+ Constants.CONT_URL_FOREACST)).openConnection();
				}
				con.setRequestMethod("GET");
				con.setDoInput(true);
				con.setDoOutput(true);
				con.connect();

				// Let's read the response
				StringBuffer buffer = new StringBuffer();
				is = con.getInputStream();
				BufferedReader br = new BufferedReader(
						new InputStreamReader(is));
				String line = null;
				while ((line = br.readLine()) != null)
					buffer.append(line + "\r\n");
				is.close();
				con.disconnect();
				String jsonObject = buffer.toString();
				JsonParser parser = new JsonParser();
				JsonElement root = parser.parse(jsonObject).getAsJsonObject()
						.get(Constants.LIST).getAsJsonArray();
				int cnt = parser.parse(jsonObject).getAsJsonObject()
						.get(Constants.CNT).getAsInt();
				forecastReport.clear();

				// extracting info from the JSON
				for (int i = 0; i < cnt; i++) {
					JsonElement currentObject = root.getAsJsonArray().get(i);
					JsonArray weather = currentObject.getAsJsonObject()
							.get(Constants.WEATHER).getAsJsonArray();
					Report rep = new Report();

					rep.setDescription(weather.get(0).getAsJsonObject()
							.get(Constants.DESCRIPTION).getAsString());
					rep.setIconId(weather.get(0).getAsJsonObject()
							.get(Constants.ICON).getAsString());

					JsonElement main = currentObject.getAsJsonObject().get(
							Constants.TEMP);

					rep.setMinTemp(main.getAsJsonObject().get(Constants.MIN)
							.getAsDouble());
					rep.setMaxTemp(main.getAsJsonObject().get(Constants.MAX)
							.getAsDouble());
					rep.setTemp(main.getAsJsonObject().get(Constants.DAY)
							.getAsDouble());
					rep.setHumidity(currentObject.getAsJsonObject()
							.get(Constants.HUMIDITY).getAsDouble());

					rep.setWindSpeed(currentObject.getAsJsonObject()
							.get(Constants.SPEED).getAsDouble());
					rep.setClouds(currentObject.getAsJsonObject()
							.get(Constants.CLOUDS).getAsDouble());

					Date newDate = DateUtil.addDays(cal.getTime(), i + 1);
					rep.setDay(Integer.parseInt(new SimpleDateFormat("dd")
							.format(newDate)));
					rep.setMonth(new SimpleDateFormat("MMM").format(newDate));

					forecastReport.add(i, rep);

				}

				activity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						setForecastView();
					}
				});
			} catch (Throwable t) {
				activity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(mainContext,
								Constants.TOAST_FORECAST_UNAVAILABLE,
								Toast.LENGTH_LONG).show();
					}
				});
				t.printStackTrace();
			} finally {
				try {
					is.close();
				} catch (Throwable t) {
				}
				try {
					con.disconnect();
				} catch (Throwable t) {
				}
			}
			return null;

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			activity.runOnUiThread(new Runnable() {

				@Override
				public void run() {

					forecastLayout.setVisibility(View.VISIBLE);
				}
			});

		}

	}

	/**
	 * The Class WeatherTask is to call openweather API and display the weather
	 * information.
	 */
	private class WeatherTask extends AsyncTask<Void, String, String> {

		/** The city. */
		private String city;

		/** The state. */
		private String state;

		/** The latitude. */
		private double latitude;

		/** The longitude. */
		private double longitude;

		/** The is first search. */
		private boolean isFirstSearch;

		/**
		 * Sets the first search.
		 * 
		 * @param isFirstSearch
		 *            the new first search
		 */
		public void setFirstSearch(boolean isFirstSearch) {
			this.isFirstSearch = isFirstSearch;
		}

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
		 * @param latitude
		 *            the new latitude
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
		 * @param longitude
		 *            the new longitude
		 */
		public void setLongitude(double longitude) {
			this.longitude = longitude;
		}

		/**
		 * Gets the city.
		 * 
		 * @return the city
		 */
		public String getCity() {
			return city;
		}

		/**
		 * Sets the city.
		 * 
		 * @param city
		 *            the new city
		 */
		public void setCity(String city) {
			this.city = city;
		}

		/**
		 * Gets the state.
		 * 
		 * @return the state
		 */
		public String getState() {
			return state;
		}

		/**
		 * Sets the state.
		 * 
		 * @param state
		 *            the new state
		 */
		public void setState(String state) {
			this.state = state;
		}

		/**
		 * Sets the weather view for the currently selected city.
		 */
		public void setWeatherView() {
			// sets the city and state details
			if (!isFirstSearch) {
				activity.city.setText(strArray[0]);
				state_country.setText(strArray[1] + Constants.COMMA_SPLITTER
						+ strArray[2]);
			}
			// sets the weather info
			prediction.setText(report.getDescription());
			predictionIcon
					.setBackgroundResource(iconMap.get(report.getIconId()));
			date.setText(report.getMonth() + " " + report.getDay());
			mainLayout.setBackgroundResource(bgMap.get(report.getIconId()));

			// sets the temperature details - current, minimum and maximum
			int minTemp = 0, maxTemp = 0, temp1 = 0;
			if (temperatureIcon.getContentDescription().equals(
					Constants.CELCIUS)) {
				minTemp = (int) (report.getMinTemp() - Constants.KELVIN_CONVERSION);
				maxTemp = (int) (report.getMaxTemp() - Constants.KELVIN_CONVERSION);
				temp1 = (int) (report.getTemp() - Constants.KELVIN_CONVERSION);
			} else {
				minTemp = (int) (report.getMinTemp() - Constants.KELVIN_CONVERSION)
						* (9 / 5) + 32;
				maxTemp = (int) (report.getMaxTemp() - Constants.KELVIN_CONVERSION)
						* (9 / 5) + 32;
				temp1 = (int) (report.getTemp() - Constants.KELVIN_CONVERSION)
						* (9 / 5) + 32;
			}
			minMaxtemp.setText(minTemp + Constants.DEGREE + "    " + maxTemp
					+ Constants.DEGREE);
			temp.setText(String.valueOf(temp1));

			// sets the humidity, clouds and wind speed
			humidity.setText(report.getHumidity() + Constants.PERCENT);
			cloudPercent.setText(report.getClouds() + Constants.PERCENT);
			windSpeed.setText(report.getWindSpeed()
					+ Constants.MILES_PER_SECOND);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected String doInBackground(Void... arg0) {
			HttpURLConnection con = null;
			InputStream is = null;

			try {
				if (isFirstSearch) {
					con = (HttpURLConnection) (new URL(Constants.LAT_LONG_URL
							+ "lat=" + getLatitude() + "&lon=" + getLongitude()))
							.openConnection();

				} else {
					con = (HttpURLConnection) (new URL(
							Constants.BASE_URL_WEATHER + getCity()
									+ Constants.COMMA_SPLITTER + getState()))
							.openConnection();
				}
				con.setRequestMethod("GET");
				con.setDoInput(true);
				con.setDoOutput(true);
				con.connect();

				// Let's read the response
				StringBuffer buffer = new StringBuffer();
				is = con.getInputStream();
				BufferedReader br = new BufferedReader(
						new InputStreamReader(is));
				String line = null;
				while ((line = br.readLine()) != null)
					buffer.append(line + "\r\n");
				is.close();
				con.disconnect();
				String jsonObject = buffer.toString();
				JsonParser parser = new JsonParser();
				JsonElement root = null;

				// extract the JSON info

				if (isFirstSearch) {
					root = parser.parse(jsonObject).getAsJsonObject()
							.get(Constants.LIST).getAsJsonArray().get(0);

				} else {
					root = parser.parse(jsonObject);
					JsonElement coord = root.getAsJsonObject().get(
							Constants.COORD);
					latitude = coord.getAsJsonObject().get(Constants.LAT)
							.getAsDouble();
					longitude = coord.getAsJsonObject().get(Constants.LONG)
							.getAsDouble();
					WeatherNearbyTask task = new WeatherNearbyTask();
					task.setLatitude(latitude);
					task.setLongitude(longitude);

					task.execute();

				}
				report.setLatitude(latitude);
				report.setLongitude(longitude);
				JsonArray weather = root.getAsJsonObject()
						.get(Constants.WEATHER).getAsJsonArray();

				report.setDescription(weather.get(0).getAsJsonObject()
						.get(Constants.DESCRIPTION).getAsString());
				report.setIconId(weather.get(0).getAsJsonObject()
						.get(Constants.ICON).getAsString());

				JsonElement main = root.getAsJsonObject().get(Constants.MAIN);

				report.setMinTemp(main.getAsJsonObject()
						.get(Constants.TEMP_MIN).getAsDouble());
				report.setMaxTemp(main.getAsJsonObject()
						.get(Constants.TEMP_MAX).getAsDouble());
				report.setTemp(main.getAsJsonObject().get(Constants.TEMP)
						.getAsDouble());
				report.setHumidity(main.getAsJsonObject()
						.get(Constants.HUMIDITY).getAsDouble());
				JsonElement wind = root.getAsJsonObject().get(Constants.WIND);

				report.setWindSpeed(wind.getAsJsonObject().get(Constants.SPEED)
						.getAsDouble());
				JsonElement clouds = root.getAsJsonObject().get(
						Constants.CLOUDS);

				report.setClouds(clouds.getAsJsonObject().get(Constants.ALL)
						.getAsDouble());

				report.setMonth(new SimpleDateFormat("MMM").format(cal
						.getTime()));
				report.setDay(Integer.parseInt(new SimpleDateFormat("dd")
						.format(cal.getTime())));
				activity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						setWeatherView();
					}
				});

				return buffer.toString();
			} catch (Exception e) {
				activity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(mainContext,
								Constants.TOAST_WEATHER_UNAVAILABLE,
								Toast.LENGTH_LONG).show();
						// tempProgress.setVisibility(View.GONE);
					}
				});

				e.printStackTrace();
			} finally {
				try {
					is.close();
				} catch (Throwable t) {
				}
				try {
					con.disconnect();
				} catch (Throwable t) {
				}
			}

			return null;

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			weatherComplete = true;
			activity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					titleBar.setVisibility(View.VISIBLE);

					tempDetails.setVisibility(View.VISIBLE);
					tempProgress.dismiss();
				}
			});

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			tempProgress.show();

		}

	}

	/**
	 * Calculates distance between 2 given locations
	 * 
	 * @param fromLong
	 *            the from long
	 * @param fromLat
	 *            the from lat
	 * @param toLong
	 *            the to long
	 * @param toLat
	 *            the to lat
	 * @return the double
	 */
	private double calculateDistance(double fromLong, double fromLat,
			double toLong, double toLat) {
		double d2r = Math.PI / 180;
		double dLong = (toLong - fromLong) * d2r;
		double dLat = (toLat - fromLat) * d2r;
		double a = Math.pow(Math.sin(dLat / 2.0), 2) + Math.cos(fromLat * d2r)
				* Math.cos(toLat * d2r) * Math.pow(Math.sin(dLong / 2.0), 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double d = 6367000 * c;
		return Math.round(d);
	}

	/**
	 * Builds the alert message to show when the GPS is turned off.
	 */
	private void buildAlertMessageNoGps() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(Constants.GPS_OFF_MSG)
				.setCancelable(false)
				.setPositiveButton(Constants.YES,
						new DialogInterface.OnClickListener() {
							public void onClick(final DialogInterface dialog,
									final int id) {
								startActivity(new Intent(
										android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
								activity.finish();
							}
						})
				.setNegativeButton(Constants.NO,
						new DialogInterface.OnClickListener() {
							public void onClick(final DialogInterface dialog,
									final int id) {
								dialog.cancel();
								activity.finish();
							}
						});
		final AlertDialog alert = builder.create();
		alert.show();
	}

	/**
	 * Builds the alert message and shows when the internet connection is not
	 * available.
	 */
	private void buildAlertMessageNoInternet() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(Constants.INTERNET_OFF_MSG)
				.setCancelable(false)
				.setPositiveButton(Constants.OK,
						new DialogInterface.OnClickListener() {
							public void onClick(final DialogInterface dialog,
									final int id) {
								activity.finish();
							}
						});
		final AlertDialog alert = builder.create();
		alert.show();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
	}

	/**
	 * Checks if internet connection is available.
	 * 
	 * @return true, if is conn
	 */
	public boolean isConn() {
		ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity.getActiveNetworkInfo() != null) {
			if (connectivity.getActiveNetworkInfo().isConnected())
				return true;
		}
		return false;
	}

	/**
	 * The listener interface for receiving myLocation events. The class that is
	 * interested in processing a myLocation event implements this interface,
	 * and the object created with that class is registered with a component
	 * using the component's <code>addMyLocationListener<code> method. When
	 * the myLocation event occurs, that object's appropriate
	 * method is invoked.
	 * 
	 * @see MyLocationEvent
	 */
	private class MyLocationListener implements LocationListener {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.location.LocationListener#onLocationChanged(android.location
		 * .Location)
		 */
		@Override
		public void onLocationChanged(Location location) {

			updateWeatherInfo(location);

			locationManager.removeUpdates(this);

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.location.LocationListener#onProviderDisabled(java.lang.String
		 * )
		 */
		@Override
		public void onProviderDisabled(String provider) {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.location.LocationListener#onProviderEnabled(java.lang.String)
		 */
		@Override
		public void onProviderEnabled(String provider) {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.location.LocationListener#onStatusChanged(java.lang.String,
		 * int, android.os.Bundle)
		 */
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}

	/**
	 * Updates the city details.
	 * 
	 * @param city1
	 *            the city1
	 */
	private void updateCity(String city1) {
		city.setText(city1);
		autoCompView.setText("");
	}

	/**
	 * Update weather info on the display.
	 * 
	 * @param location
	 *            the location
	 */
	private void updateWeatherInfo(Location location) {
		Geocoder gcd = new Geocoder(MainActivity.this, Locale.getDefault());

		List<Address> addresses;
		autoCompView.setText(Constants.EMPTY_STRING);
		try {
			addresses = gcd.getFromLocation(location.getLatitude(),
					location.getLongitude(), 1);
			if (addresses.size() > 0) {
				Address current = addresses.get(0);
				city.setText(current.getLocality());
				state_country.setText(current.getAdminArea()
						+ Constants.COMMA_SPLITTER + current.getCountryCode());
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		WeatherTask task = new WeatherTask();
		task.setLatitude(location.getLatitude());
		task.setLongitude(location.getLongitude());
		task.setFirstSearch(true);
		task.execute();

		ForecastTask task1 = new ForecastTask();
		task1.setLatitude(location.getLatitude());
		task1.setLongitude(location.getLongitude());
		task1.setIsFirstSearch(true);
		task1.execute();

		WeatherNearbyTask task2 = new WeatherNearbyTask();
		task2.setLatitude(location.getLatitude());
		task2.setLongitude(location.getLongitude());
		task2.execute();
	}

}
