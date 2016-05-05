package module;

import java.util.ArrayList;
import java.util.List;

public class Place {
	
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

	public Place() {
		types = new ArrayList<Place.Type>();
	}
	
	public Place(String id,String name, List<Type> types, Location location) {
		this.id = id;
		this.name = name;
		this.types = types;
		this.location = location;
	}
}