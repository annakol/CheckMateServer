package model;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import model.GooglePlace.Type;

public class Place {
	private String id;
	private String icon;
	private String name;
	private String vicinity;
	private Double latitude;
	private Double longitude;
	private String address;
	private String phoneNumber;
	private float rating;
	private String url;
	private List<String> types = new ArrayList<String>();

	public List<String> getTypes() {
		return types;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVicinity() {
		return vicinity;
	}

	public void setVicinity(String vicinity) {
		this.vicinity = vicinity;
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

	public float getRating() {
		return rating;
	}

	public void setRating(float rating) {
		this.rating = rating;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setTypes(List<String> types) {
		this.types = types;
	}

	public static Place jsonToPontoReferencia(JsonObject pontoReferencia) {
		try {
			Place result = new Place();
			JsonObject geometry = (JsonObject) pontoReferencia.get("geometry");
			JsonObject location = (JsonObject) geometry.get("location");
			result.setLatitude(location.get("lat").getAsDouble());
			result.setLongitude(location.get("lng").getAsDouble());
			result.setIcon(pontoReferencia.get("icon").getAsString());
			result.setName(pontoReferencia.get("name").getAsString());
			JsonElement vicinityJson = pontoReferencia.get("vicinity");
			if (vicinityJson != null) {
				result.setVicinity(vicinityJson.getAsString());
			}
			result.setId(pontoReferencia.get("id").getAsString());
			// result.setAddress(pontoReferencia.get("formatted_address").getAsString());
			// result.setPhoneNumber(pontoReferencia.get("formatted_phone_number").getAsString());
			JsonElement rating = pontoReferencia.get("rating");
			if (rating != null) {
				result.setRating(rating.getAsFloat());
			}
			// result.setUrl(pontoReferencia.get("url").getAsString());

			JsonArray typesArray = pontoReferencia.getAsJsonArray("types");

			for (int j = 0; j < typesArray.size(); j++) {
				result.getTypes().add(typesArray.get(j).getAsString());
			}
			return result;
		} catch (JsonParseException ex) {
			Logger.getLogger(Place.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	@Override
	public String toString() {
		return "Place{" + "id=" + id + ", icon=" + icon + ", name=" + name + ", latitude=" + latitude + ", longitude="
				+ longitude + ", address=" + address + ", phone=" + phoneNumber + ", rating=" + rating + ", url=" + url
				+ "}";
	}

}