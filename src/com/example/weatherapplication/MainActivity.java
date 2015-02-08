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

@SuppressLint("NewApi")
public class MainActivity extends ActionBarActivity implements
OnItemClickListener {
	static Report report = new Report();
	TextView prediction = null;
	TextView temp = null;
	TextView humidity = null;
	TextView cloudPercent = null;
	TextView windSpeed = null;
	TextView city = null;
	TextView state_country = null;
	TextView date = null;
	AutoCompleteTextView autoCompView = null;
	ImageView descriptionIcon = null;
	ImageView locationIcon = null;
	ImageView clearIcon = null;
	TextView minMaxtemp = null;
	List<Report> forecastReport = new ArrayList<Report>();
	List<Report> nearbyReport = new ArrayList<Report>();
	HashMap<String, Integer> iconMap = new HashMap<String, Integer>();
	HashMap<String, Integer> bgMap = new HashMap<String, Integer>();

	Calendar cal = Calendar.getInstance();
	LinearLayout mainLayout = null;
	LinearLayout tempDetails = null;
	LinearLayout forecastLayout = null;
	RelativeLayout forecastProgress = null;
	LinearLayout nearbyLayout = null;
	ProgressDialog tempProgress = null;
	LinearLayout titleBar = null;

	private static final String GEO_CODE_URL = "http://gd.geobytes.com/AutoCompleteCity?callback=&q=";
	private static final String LOG_TAG = "WeatherApp";

	LocationManager locationManager = null;
	boolean weatherComplete = false;
	boolean forecastComplete = false;
	boolean nearbyComplete = false;
	String[] strArray;

	/** The main context. */
	public Context mainContext = this;

	/** The activity. */
	public MainActivity activity = this;

	TextView temperatureIcon = null;
	ImageView predictionIcon = null;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		// Remove notification bar
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.weather_main);
		initializeAll();

		tempProgress.setMessage("Loading Weather Info ");
		tempProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		tempProgress.setIndeterminate(true);
		temperatureIcon.setText("\u00b0C");

		clearIcon.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				autoCompView.setText("");

			}
		});

		locationIcon.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				getAddress();
				tempDetails.setVisibility(View.GONE);
				forecastLayout.setVisibility(View.GONE);
				nearbyLayout.setVisibility(View.GONE);

			}
		});
		temperatureIcon.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				int minTemp = 0, maxTemp = 0, temp1 = 0;

				if (temperatureIcon.getContentDescription().equals("Celcius")) {

					temperatureIcon.setContentDescription("Fahrenheit");
					minTemp = (int) (report.getMinTemp() - 273.15) * (9 / 5)
							+ 32;
					maxTemp = (int) (report.getMaxTemp() - 273.15) * (9 / 5)
							+ 32;
					temp1 = (int) (report.getTemp() - 273.15) * (9 / 5) + 32;
					temperatureIcon.setText("\u00b0F");


				} else {

					temperatureIcon.setContentDescription("Celcius");
					minTemp = (int) (report.getMinTemp() - 273.15);
					maxTemp = (int) (report.getMaxTemp() - 273.15);
					temp1 = (int) (report.getTemp() - 273.15);
					temperatureIcon.setText("\u00b0C");



				}
				minMaxtemp.setText(minTemp + "\u00b0     " + maxTemp + "\u00b0");
				temp.setText(String.valueOf(temp1));


			}
		});
		mainLayout.setVisibility(View.VISIBLE);
		getAddress();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

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

	private ArrayList<String> autocomplete(String input) {
		ArrayList<String> resultList = null;

		HttpURLConnection conn = null;
		StringBuilder jsonResults = new StringBuilder();
		try {
			URL url = new URL(GEO_CODE_URL + input);
			conn = (HttpURLConnection) url.openConnection();
			InputStreamReader in = new InputStreamReader(conn.getInputStream());

			// Load the results into a StringBuilder
			int read;
			char[] buff = new char[1024];
			while ((read = in.read(buff)) != -1) {
				jsonResults.append(buff, 0, read);
			}
		} catch (MalformedURLException e) {
			Log.e(LOG_TAG, "Error processing Places API URL", e);
			return resultList;
		} catch (IOException e) {
			Log.e(LOG_TAG, "Error connecting to Places API", e);
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
			Log.e(LOG_TAG, "Cannot process JSON results", e);
		}

		return resultList;
	}

	private class PlacesAutoCompleteAdapter extends ArrayAdapter<String>
	implements Filterable {
		private ArrayList<String> resultList;

		public PlacesAutoCompleteAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);

		}

		@Override
		public int getCount() {
			if (resultList != null)
				return resultList.size();
			else
				return 0;
		}

		@Override
		public String getItem(int index) {
			return resultList.get(index);
		}

		@Override
		public Filter getFilter() {
			Filter filter = new Filter() {
				@Override
				protected FilterResults performFiltering(CharSequence constraint) {
					FilterResults filterResults = new FilterResults();
					if (constraint != null) {
						String str = constraint.toString();
						str.replace(" ", "");
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

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view,
			int position, long ID) {
		tempDetails.setVisibility(View.GONE);
		forecastLayout.setVisibility(View.GONE);
		nearbyLayout.setVisibility(View.GONE);
		String str = (String) adapterView.getItemAtPosition(position);
		strArray = str.split(",");
		str = str.replace(" ", "");

		WeatherTask task = new WeatherTask();
		task.setCity(strArray[0]);
		task.setState(strArray[1]);
		task.setFirstSearch(false);
		task.execute();

		ForecastTask task1 = new ForecastTask();
		task1.setCity(strArray[0]);
		task1.setState(strArray[1]);
		task1.setIsFirstSearch(false);
		task1.execute();
		

	}

	public void getAddress() {

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

	private class WeatherNearbyTask extends AsyncTask<Void, String, String> {
		private String LAT_LONG_URL = "http://api.openweathermap.org/data/2.5/find?";
		private String CONT_URL = "&cnt=5&mode=json";
		private double latitude;
		private double longitude;

		public double getLatitude() {
			return latitude;
		}

		public void setLatitude(double latitude) {
			this.latitude = latitude;
		}

		public double getLongitude() {
			return longitude;
		}

		public void setLongitude(double longitude) {
			this.longitude = longitude;
		}

		private void setNearbyView() {

			final Report report1 = nearbyReport.get(1);
			final Report report2 = nearbyReport.get(2);
			final Report report3 = nearbyReport.get(3);
			final Report report4 = nearbyReport.get(4);

			LinearLayout nearby1 = (LinearLayout) findViewById(R.id.nearby1);
			LinearLayout nearby2 = (LinearLayout) findViewById(R.id.nearby2);
			LinearLayout nearby3 = (LinearLayout) findViewById(R.id.nearby3);
			LinearLayout nearby4 = (LinearLayout) findViewById(R.id.nearby4);

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

			double distance = calculateDistance(report.getLongitude(),
					report.getLatitude(), report1.getLongitude(),
					report1.getLatitude()) * 0.000621371;

			distance1.setText(String.valueOf(distance).substring(0, 3) + "mi");

			distance = calculateDistance(report.getLongitude(),
					report.getLatitude(), report2.getLongitude(),
					report2.getLatitude()) * 0.000621371;

			distance2.setText(String.valueOf(distance).substring(0, 3) + "mi");

			distance = calculateDistance(report.getLongitude(),
					report.getLatitude(), report3.getLongitude(),
					report3.getLatitude()) * 0.000621371;

			distance3.setText(String.valueOf(distance).substring(0, 3) + "mi");

			distance = calculateDistance(report.getLongitude(),
					report.getLatitude(), report4.getLongitude(),
					report4.getLatitude()) * 0.000621371;

			distance4.setText(String.valueOf(distance).substring(0, 3) + "mi");

			icon1.setBackgroundResource(iconMap.get(report1.getIconId()));
			icon2.setBackgroundResource(iconMap.get(report2.getIconId()));
			icon3.setBackgroundResource(iconMap.get(report3.getIconId()));
			icon4.setBackgroundResource(iconMap.get(report4.getIconId()));

			int temp12 = 0;
			if (temperatureIcon.getContentDescription().equals("Celcius")) {
				temp12 = (int) (report1.getTemp() - 273.15);

			} else {
				temp12 = (int) (report1.getTemp() - 273.15) * (9 / 5) + 32;
			}

			temp1.setText(String.valueOf(temp12));

			if (temperatureIcon.getContentDescription().equals("Celcius")) {
				temp12 = (int) (report2.getTemp() - 273.15);

			} else {
				temp12 = (int) (report2.getTemp() - 273.15) * (9 / 5) + 32;
			}

			temp2.setText(String.valueOf(temp12));

			if (temperatureIcon.getContentDescription().equals("Celcius")) {
				temp12 = (int) (report3.getTemp() - 273.15);

			} else {
				temp12 = (int) (report3.getTemp() - 273.15) * (9 / 5) + 32;
			}

			temp3.setText(String.valueOf(temp12));

			if (temperatureIcon.getContentDescription().equals("Celcius")) {
				temp12 = (int) (report4.getTemp() - 273.15);

			} else {
				temp12 = (int) (report4.getTemp() - 273.15) * (9 / 5) + 32;
			}

			temp4.setText(String.valueOf(temp12));

			humid1.setText("Humid: " + report1.getHumidity() + "%");
			humid2.setText("Humid: " + report2.getHumidity() + "%");
			humid3.setText("Humid: " + report3.getHumidity() + "%");
			humid4.setText("Humid: " + report4.getHumidity() + "%");

			wind1.setText("Wind: " + report1.getWindSpeed() + "mps");
			wind2.setText("Wind: " + report2.getWindSpeed() + "mps");
			wind3.setText("Wind: " + report3.getWindSpeed() + "mps");
			wind4.setText("Wind: " + report4.getWindSpeed() + "mps");

		}

		@Override
		protected String doInBackground(Void... params) {
			HttpURLConnection con = null;
			InputStream is = null;
			try {

				con = (HttpURLConnection) (new URL(LAT_LONG_URL + "lat="
						+ getLatitude() + "&lon=" + getLongitude() + CONT_URL))
						.openConnection();

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
						.get("list").getAsJsonArray();

				nearbyReport.clear();
				for (int i = 0; i < 5; i++) {
					Report rep = new Report();

					JsonElement currentObject = root.getAsJsonArray().get(i);

					JsonElement coord = currentObject.getAsJsonObject().get(
							"coord");

					rep.setLatitude(coord.getAsJsonObject().get("lat")
							.getAsDouble());
					rep.setLongitude(coord.getAsJsonObject().get("lon")
							.getAsDouble());

					JsonArray weather = currentObject.getAsJsonObject()
							.get("weather").getAsJsonArray();

					rep.setCityName(currentObject.getAsJsonObject().get("name")
							.getAsString());
					rep.setIconId(weather.get(0).getAsJsonObject().get("icon")
							.getAsString());

					JsonElement main = currentObject.getAsJsonObject().get(
							"main");

					rep.setTemp(main.getAsJsonObject().get("temp")
							.getAsDouble());
					rep.setHumidity(main.getAsJsonObject().get("humidity")
							.getAsDouble());
					JsonElement wind = currentObject.getAsJsonObject().get(
							"wind");

					rep.setWindSpeed(wind.getAsJsonObject().get("speed")
							.getAsDouble());
					JsonElement clouds = currentObject.getAsJsonObject().get(
							"clouds");

					rep.setClouds(clouds.getAsJsonObject().get("all")
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
								"Nearby Weather info unavailable",
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

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
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

	private class ForecastTask extends AsyncTask<Void, String, String> {
		private String city;
		private String state;
		private boolean isFirstSearch;
		private double latitude;
		private double longitude;
		private String BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?q=";
		private String LAT_LONG_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
		private String CONT_URL = "&cnt=10&mode=json";

		public double getLatitude() {
			return latitude;
		}

		public void setLatitude(double latitude) {
			this.latitude = latitude;
		}

		public double getLongitude() {
			return longitude;
		}

		public void setLongitude(double longitude) {
			this.longitude = longitude;
		}

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		public String getState() {
			return state;
		}

		public void setState(String state) {
			this.state = state;
		}

		

		public void setIsFirstSearch(boolean isFirstSearch) {
			this.isFirstSearch = isFirstSearch;
		}

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

				ImageView icon = new ImageView(mainContext);
				LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, 0, 3);

				iconParams.setMargins(0, 0, 0, 0);

				iconParams.gravity = Gravity.CENTER;
				icon.setBackgroundResource(iconMap.get(currentReport
						.getIconId()));
				icon.setLayoutParams(iconParams);

				parentLayout.addView(icon);

				TextView temp = new TextView(mainContext);
				int minTemp = 0, maxTemp = 0;
				if (temperatureIcon.getContentDescription().equals("Celcius")) {
					minTemp = (int) (currentReport.getMinTemp() - 273.15);
					maxTemp = (int) (currentReport.getMaxTemp() - 273.15);
				} else {
					minTemp = (int) (currentReport.getMinTemp() - 273.15)
							* (9 / 5) + 32;
					maxTemp = (int) (currentReport.getMaxTemp() - 273.15)
							* (9 / 5) + 32;

				}
				temp.setGravity(Gravity.CENTER);
				temp.setText(maxTemp + "\u00b0        " + minTemp + "\u00b0");
				LinearLayout.LayoutParams tempTextParams = new LinearLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, 0, 2);
				temp.setTextSize(14);
				tempTextParams.setMargins(0, 0, 0, 10);
				temp.setLayoutParams(tempTextParams);

				parentLayout.addView(temp);

				hsv.addView(parentLayout);

			}
		}

		@Override
		protected String doInBackground(Void... arg0) {
			HttpURLConnection con = null;
			InputStream is = null;
			try {

				if (isFirstSearch) {
					con = (HttpURLConnection) (new URL(LAT_LONG_URL + "lat="
							+ getLatitude() + "&lon=" + getLongitude()
							+ CONT_URL)).openConnection();

				} else {
					con = (HttpURLConnection) (new URL(BASE_URL + getCity()
							+ "," + getState() + CONT_URL)).openConnection();
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
						.get("list").getAsJsonArray();
				int cnt = parser.parse(jsonObject).getAsJsonObject().get("cnt")
						.getAsInt();
				forecastReport.clear();
				for (int i = 0; i < cnt; i++) {
					JsonElement currentObject = root.getAsJsonArray().get(i);
					JsonArray weather = currentObject.getAsJsonObject()
							.get("weather").getAsJsonArray();
					Report rep = new Report();

					rep.setDescription(weather.get(0).getAsJsonObject()
							.get("description").getAsString());
					rep.setIconId(weather.get(0).getAsJsonObject().get("icon")
							.getAsString());

					JsonElement main = currentObject.getAsJsonObject().get(
							"temp");

					rep.setMinTemp(main.getAsJsonObject().get("min")
							.getAsDouble());
					rep.setMaxTemp(main.getAsJsonObject().get("max")
							.getAsDouble());
					rep.setTemp(main.getAsJsonObject().get("day").getAsDouble());
					rep.setHumidity(currentObject.getAsJsonObject()
							.get("humidity").getAsDouble());

					rep.setWindSpeed(currentObject.getAsJsonObject()
							.get("speed").getAsDouble());
					rep.setClouds(currentObject.getAsJsonObject().get("clouds")
							.getAsDouble());

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
								"Forecast Weather info unavailable",
								Toast.LENGTH_LONG).show();
						// forecastProgress.setVisibility(View.GONE);
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

	private class WeatherTask extends AsyncTask<Void, String, String> {
		private String BASE_URL = "http://api.openweathermap.org/data/2.5/weather?q=";
		private String LAT_LONG_URL = "http://api.openweathermap.org/data/2.5/find?";
		private String city;
		private String state;
		private double latitude;
		private double longitude;
		private boolean isFirstSearch;

		

		public void setFirstSearch(boolean isFirstSearch) {
			this.isFirstSearch = isFirstSearch;
		}

		public double getLatitude() {
			return latitude;
		}

		public void setLatitude(double latitude) {
			this.latitude = latitude;
		}

		public double getLongitude() {
			return longitude;
		}

		public void setLongitude(double longitude) {
			this.longitude = longitude;
		}

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		public String getState() {
			return state;
		}

		public void setState(String state) {
			this.state = state;
		}

		public void setWeatherView() {
			if(!isFirstSearch)
			{
			activity.city.setText(strArray[0]);
			state_country.setText(strArray[1] + "," + strArray[2]);
			}
			prediction.setText(report.getDescription());
			predictionIcon
			.setBackgroundResource(iconMap.get(report.getIconId()));
			date.setText(report.getMonth() + " " + report.getDay());
			mainLayout.setBackgroundResource(bgMap.get(report.getIconId()));

			int minTemp = 0, maxTemp = 0, temp1 = 0;
			if (temperatureIcon.getContentDescription().equals("Celcius")) {
				minTemp = (int) (report.getMinTemp() - 273.15);
				maxTemp = (int) (report.getMaxTemp() - 273.15);
				temp1 = (int) (report.getTemp() - 273.15);
			} else {
				minTemp = (int) (report.getMinTemp() - 273.15) * (9 / 5) + 32;
				maxTemp = (int) (report.getMaxTemp() - 273.15) * (9 / 5) + 32;
				temp1 = (int) (report.getTemp() - 273.15) * (9 / 5) + 32;
			}
			minMaxtemp.setText(minTemp + "\u00b0     " + maxTemp + "\u00b0");
			temp.setText(String.valueOf(temp1));

			humidity.setText(report.getHumidity() + "%");
			cloudPercent.setText(report.getClouds() + "%");
			windSpeed.setText(report.getWindSpeed() + "mps");
		}

		@Override
		protected String doInBackground(Void... arg0) {
			HttpURLConnection con = null;
			InputStream is = null;

			try {
				if (isFirstSearch) {
					con = (HttpURLConnection) (new URL(LAT_LONG_URL + "lat="
							+ getLatitude() + "&lon=" + getLongitude()))
							.openConnection();

				} else {
					con = (HttpURLConnection) (new URL(BASE_URL + getCity()
							+ "," + getState())).openConnection();
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

				if (isFirstSearch) {
					root = parser.parse(jsonObject).getAsJsonObject()
							.get("list").getAsJsonArray().get(0);

				} else {
					root = parser.parse(jsonObject);
					JsonElement coord = root.getAsJsonObject().get("coord");
					latitude = coord.getAsJsonObject().get("lat").getAsDouble();
					longitude = coord.getAsJsonObject().get("lon")
							.getAsDouble();
					WeatherNearbyTask task = new WeatherNearbyTask();
					task.setLatitude(latitude);
					task.setLongitude(longitude);

					task.execute();

				}
				report.setLatitude(latitude);
				report.setLongitude(longitude);
				JsonArray weather = root.getAsJsonObject().get("weather")
						.getAsJsonArray();

				report.setDescription(weather.get(0).getAsJsonObject()
						.get("description").getAsString());
				report.setIconId(weather.get(0).getAsJsonObject().get("icon")
						.getAsString());

				JsonElement main = root.getAsJsonObject().get("main");

				report.setMinTemp(main.getAsJsonObject().get("temp_min")
						.getAsDouble());
				report.setMaxTemp(main.getAsJsonObject().get("temp_max")
						.getAsDouble());
				report.setTemp(main.getAsJsonObject().get("temp").getAsDouble());
				report.setHumidity(main.getAsJsonObject().get("humidity")
						.getAsDouble());
				JsonElement wind = root.getAsJsonObject().get("wind");

				report.setWindSpeed(wind.getAsJsonObject().get("speed")
						.getAsDouble());
				JsonElement clouds = root.getAsJsonObject().get("clouds");

				report.setClouds(clouds.getAsJsonObject().get("all")
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
								"Weather details unavailable",
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

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			weatherComplete = true;
			activity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// tempProgress.setVisibility(View.GONE);
					titleBar.setVisibility(View.VISIBLE);
					
					tempDetails.setVisibility(View.VISIBLE);
					tempProgress.dismiss();
				}
			});

		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

			tempProgress.show();

		}

	}

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

	private void buildAlertMessageNoGps() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(
				"Your GPS seems to be disabled, do you want to enable it?")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
					public void onClick(
							final DialogInterface dialog,
							 final int id) {
						startActivity(new Intent(
								android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
						activity.finish();
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog,
							 final int id) {
						dialog.cancel();
						activity.finish();
					}
				});
		final AlertDialog alert = builder.create();
		alert.show();
	}

	private void buildAlertMessageNoInternet() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Please check you network connection")
		.setCancelable(false)
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(
					 final DialogInterface dialog,
					final int id) {
				activity.finish();
			}
		});
		final AlertDialog alert = builder.create();
		alert.show();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	public boolean isConn() {
		ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity.getActiveNetworkInfo() != null) {
			if (connectivity.getActiveNetworkInfo().isConnected())
				return true;
		}
		return false;
	}

	private class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {

			updateWeatherInfo(location);

			locationManager.removeUpdates(this);

		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}

	private void updateCity(String city1) {
		city.setText(city1);
		autoCompView.setText("");
	}

	private void updateWeatherInfo(Location location) {
		Geocoder gcd = new Geocoder(MainActivity.this, Locale.getDefault());

		List<Address> addresses;
		autoCompView.setText("");
		try {
			addresses = gcd.getFromLocation(location.getLatitude(),
					location.getLongitude(), 1);
			if (addresses.size() > 0) {
				Address current = addresses.get(0);
				city.setText(current.getLocality());
				state_country.setText(current.getAdminArea() + ","
						+ current.getCountryCode());
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
