package services;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import model.Location;
import model.Place;

public class PlaceDetailsService {

	private String placeId;
	private static final String TYPE_DETAILS = "/details";
	
	public PlaceDetailsService(String PlaceId){
		this.placeId = PlaceId;
	}
	
	public JsonObject getDetails(){
		JsonObject object = new JsonParser().parse(getPlacesJson()).getAsJsonObject();
		JsonObject result = object.get("result").getAsJsonObject();
		
		return result;
	}

	//TODO: change json object
	//TODO: unite with PlacesDetails
	private String getPlacesJson() {
		HttpURLConnection conn = null;
		StringBuilder jsonResults = new StringBuilder();
		try {
			URL url = makeUrl();
			conn = (HttpURLConnection) url.openConnection();
			InputStreamReader in = new InputStreamReader(conn.getInputStream(), GoogleServicesCons.UTF8);

			int read;
			char[] buff = new char[1024];
			while ((read = in.read(buff)) != -1) {
				jsonResults.append(buff, 0, read);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		
		return jsonResults.toString();

	}
	
    private URL makeUrl() throws MalformedURLException {
		StringBuilder sb = new StringBuilder(GoogleServicesCons.PLACES_API_BASE);
		sb.append(TYPE_DETAILS);
		sb.append(GoogleServicesCons.OUT_JSON);
		sb.append("?key=" + GoogleServicesCons.API_SERVER_KEY);
		sb.append("&language=" + GoogleServicesCons.HEBREW_LANG);
		sb.append("&placeid=" + placeId);

		URL url = new URL(sb.toString());
		return url;
	}

}
