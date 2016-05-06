package model;

import java.util.ArrayList;
import java.util.List;

public class GooglePlace {
	
	public enum Type {
		amusement_park  ,
		aquarium        ,
		art_gallery     ,
		bakery          ,
		bar             ,
		beauty_salon    ,
		book_store      ,
		bowling_alley   ,
		cafe            ,
		campground      ,
		casino          ,
		gym             ,
		library         ,
		movie_rental    ,
		movie_theater   ,
		museum          ,
		night_club      ,
		park            ,
		restaurant      ,
		shopping_mall   ,
		spa             ,
		stadium         ,
		zoo
	}
	
	public String id;
	public String name;
	public List<Type> types; 
	public Location location;
	public String address;

	public GooglePlace() {
		types = new ArrayList<GooglePlace.Type>();
	}
	
	public GooglePlace(String id,String name, List<Type> types, Location location) {
		this.id = id;
		this.name = name;
		this.types = types;
		this.location = location;
	}
}