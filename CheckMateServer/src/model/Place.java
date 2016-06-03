package model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.restfb.Connection;
import com.restfb.FacebookClient;
import com.restfb.Parameter;

import algo.RatingManager;
import services.FacebookPlace;
import services.LoggedInFacebookClient;
import services.PlaceDetailsService;

public class Place {
	private String place_id;
	private String icon;
	private String name;
	private Location location = new Location();
	private float googleRating;
	private List<String> types = new ArrayList<String>();
	private double distance;

	private String address;
	private String phoneNumber;
	private String website;
	private String[] openHoursText;
	private JsonArray openHours;
	private String photo;

	// local
	private Double rate;

	// facebook
	private int checkins;

	public List<String> getTypes() {
		return types;
	}

	public String getId() {
		return place_id;
	}

	public void setId(String id) {
		this.place_id = id;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public Double getLatitude() {
		return Double.parseDouble(location.lat);
	}

	public void setLatitude(Double latitude) {
		this.location.lat = latitude.toString();
	}

	public Double getLongitude() {
		return Double.parseDouble(location.lng);
	}

	public void setLongitude(Double longitude) {
		this.location.lng = longitude.toString();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getUrl() {
		return website;
	}

	public void setUrl(String url) {
		this.website = url;
	}

	public void setTypes(List<String> types) {
		this.types = types;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public Double getRate() {
		return rate;
	}

	public void setRate(Double rate) {
		this.rate = rate;
	}

	public int getCheckins() {
		return checkins;
	}

	public void setCheckins(int checkins) {
		this.checkins = checkins;
	}

	public float getGoogleRating() {
		return googleRating;
	}

	public void setGoogleRating(float googlRating) {
		this.googleRating = googlRating;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String[] getOpenHoursText() {
		return openHoursText;
	}

	public void setOpenHoursText(String[] openHoursText) {
		this.openHoursText = openHoursText;
	}

	public JsonArray getOpenHours() {
		return openHours;
	}

	public void setOpenHours(JsonArray openHours) {
		this.openHours = openHours;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public Place() {
	}

	public void fetchFullData() {
		PlaceDetailsService pds = new PlaceDetailsService(this.place_id);
		JsonObject details = pds.getDetails();

		this.setAddress(details.get("formatted_address").getAsString());
		JsonElement formatted_phone_number = details.get("formatted_phone_number");
		this.setPhoneNumber(formatted_phone_number != null ? formatted_phone_number.getAsString() : "");
		JsonElement website = details.get("website");
		this.setUrl(website != null ? website.getAsString() : "");

		JsonElement opening_hours = details.get("opening_hours");
		if (opening_hours != null) {
			JsonObject openingHours = opening_hours.getAsJsonObject();

			this.setOpenHours(openingHours.get("periods").getAsJsonArray());

			JsonArray tempWeekDay = openingHours.get("weekday_text").getAsJsonArray();
			int length = tempWeekDay.size();
			String[] h = new String[length];

			if (length > 0) {
				for (int i = 0; i < length; i++) {
					h[i] = tempWeekDay.get(i).getAsString();
				}
			}
			this.setOpenHoursText(h);
		}
		
		JsonElement photos = details.get("photos");
		if(photos != null){
		JsonArray tempPhotos = photos.getAsJsonArray();

		this.setPhoto(tempPhotos.get(0).getAsJsonObject().get("photo_reference").getAsString());
		}
	}

	public Place(JsonObject placeJson) {
		try {
			JsonObject geometry = (JsonObject) placeJson.get("geometry");
			JsonObject location = (JsonObject) geometry.get("location");
			this.setLatitude(location.get("lat").getAsDouble());
			this.setLongitude(location.get("lng").getAsDouble());
			this.setIcon(placeJson.get("icon").getAsString());
			this.setName(placeJson.get("name").getAsString());

			this.setId(placeJson.get("place_id").getAsString());
			JsonElement rating = placeJson.get("rating");
			if (rating != null) {
				this.setGoogleRating(rating.getAsFloat());
			}

			JsonArray typesArray = placeJson.getAsJsonArray("types");

			for (int j = 0; j < typesArray.size(); j++) {
				this.getTypes().add(typesArray.get(j).getAsString());
			}

			this.fetchFacebookData();

			this.rate = RatingManager.getInstance().getRate(this);

		} catch (JsonParseException ex) {
			Logger.getLogger(Place.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	// TODO: check if the place is open at the selected time
	public boolean isOpen(Calendar dateAndTime) {
		if(this.openHours != null || !this.openHours.isJsonNull() || 
				!this.openHours.isJsonPrimitive()){
			int day = dateAndTime.get(Calendar.DAY_OF_WEEK);
			JsonObject dayOpenHours = this.openHours.get(day).getAsJsonObject();
			
			 dayOpenHours.get("open");
			
			
		}
		return true;
	}

	public void fetchFacebookData() {
		FacebookClient facebookClient = new LoggedInFacebookClient();
		Connection<FacebookPlace> placeSearch = facebookClient.fetchConnection("search", FacebookPlace.class,
				Parameter.with("q", this.getName()), Parameter.with("type", "place"),
				Parameter.with("fields", "id,likes,name,checkins,location,category,category_list"));
		// TODO: remove chars like ', ", /, \ and such....
		int size = placeSearch.getData().size();
		int checkins = 0;
		if (size <= 0) {
			System.out.println("there is no place named " + this.getName() + " in facebook");

		} else if (size <= 2) {
			for (FacebookPlace place : placeSearch.getData()) {
				// System.out.println(place.getCheckins());
				if (place.getCheckins() != null) {
					checkins += place.getCheckins().intValue();
				}
			}
		} else {
			for (int i = 0; i < 3; i++) {
				// TODO: check locations?
				// System.out.println(placeSearch.getData().get(i).getCheckins());
				if (placeSearch.getData().get(i).getCheckins() != null) {
					checkins += placeSearch.getData().get(i).getCheckins().intValue();
				}
			}
		}

		this.setCheckins(checkins);

	}

	@Override
	public String toString() {
		return "Place{" + this.name + ":" + this.rate + " - distance=" + this.distance + " ,id=" + this.place_id + "}";
	}

}