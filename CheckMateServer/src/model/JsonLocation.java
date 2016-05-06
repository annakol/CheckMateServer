package model;

import com.google.gson.JsonObject;

public class JsonLocation implements Comparable<JsonLocation> {

	public JsonObject location;
	public Integer weight;
	
	public JsonLocation(JsonObject loc,int weight) {
		this.location = loc;
		this.weight = weight;
	}
	

	@Override
	public int compareTo(JsonLocation other) {
		return this.weight.compareTo(other.weight);
	}
	
	
	
}