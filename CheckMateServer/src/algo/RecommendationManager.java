package algo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import model.Interest;
import model.Location;
import model.MySqlDriver;
import model.Place;
import model.Type;
import services.PlacesService;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class RecommendationManager {
	
	private static final int RADIUS = 1000;
	private static final double OTHER_PERCENTS = 0.10;
	private static final int TOTAL_RECOMMENDATION_AMOUNT = 30;

	private static final MySqlDriver db = MySqlDriver.getInstance();

	
	private static void computeInterests(Map<Interest,Integer> allInterest, List<Interest> interests) {
		for (Interest interest : interests) {
			Integer prevValue = allInterest.get(interest);
			allInterest.put(interest,  prevValue == null ? 1 : prevValue + 1);
		}
	}
	
	public static List<Place> getRecommendedPlaces(Location location, JsonArray facebookTypes, int userId) {

		//convertListJson(facebookTypes);

		Map<Type, Integer> checkinTypes = new HashMap<Type, Integer>();
		List<Place> currTypePlaces;
		Map<Type, List<Place>> allPlaces = new HashMap<Type, List<Place>>();
		List<String> noGoogleTypeFbList = new ArrayList<String>();
		Map<Interest,Integer> allInterest = new HashMap<Interest,Integer>();
		List<Interest> interests = new ArrayList<Interest>();
		
		// set the places service with the current location and the radius
		PlacesService service = new PlacesService(location, RADIUS);
		
		// init the rating manager with the current location and user
		RatingManager.getInstance().init(location, userId);
		
		for (int i = 0; i < facebookTypes.size(); i++) {
			JsonObject typeJson = facebookTypes.get(i).getAsJsonObject();
			int count = typeJson.get("count").getAsInt();
			String typeName = typeJson.get("type").getAsString();
			
			// get the google type by the facebook type name
			Type googleType = db.getGoogleType(typeName);

			interests = db.getInterestsByFacebookType(typeName);
			computeInterests(allInterest,interests);
			
			// if there is a google type linked to the current facebook type 
			if (googleType != null) {
				if (!checkinTypes.containsKey(googleType)) {

					// add the type with its amount
					checkinTypes.put(googleType, count);

					// get places list from google by the type.
					currTypePlaces = service.getPlaces(googleType.getName());
					// if there are any places, sort them by the rating (desc)
					if ((currTypePlaces != null) && (!currTypePlaces.isEmpty())) {
						Collections.sort(currTypePlaces, new Comparator<Place>() {
							@Override
							public int compare(Place place1, Place place2) {
								return (place2.getRate().compareTo(place1.getRate()));
							}
						});
						allPlaces.put(googleType, currTypePlaces);
					}
				} else {
					checkinTypes.put(googleType, checkinTypes.get(googleType) + count);
				}
			// if there is no google type, get the interest linked to the facebook type.
			} else {
				noGoogleTypeFbList.add(typeName);
			}
		}
		
		sortByComparator(allInterest);
		
		List<Type> otherTypes = new ArrayList<Type>();
		List<Place> otherPlaces = new ArrayList<Place>();
		for (Interest interest : allInterest.keySet()) {
			List<Type> googTypes = db.getGoogleTypesByInterest(interest.getId());
			for (Type googType : googTypes) {
				if (!checkinTypes.containsKey(googType)) {
					otherTypes.add(googType);
					
					// get places list from google by the type.
					otherPlaces.addAll(service.getPlaces(googType.getName()));
				}
			}
		}
		
		if ((otherPlaces != null) && (!otherPlaces.isEmpty())) {
			Collections.sort(otherPlaces, new Comparator<Place>() {
				@Override
				public int compare(Place place1, Place place2) {
					return (place2.getRate().compareTo(place1.getRate()));
				}
			});
		}
		
		allPlaces.put(Type.other , otherPlaces);
		
		Map<Type,Integer> finalAmounts = calcTypeAmount(checkinTypes);
		
		List<Place> finalPlaces = new ArrayList<Place>();
		
		for (Type type : finalAmounts.keySet()) {
			 finalPlaces.addAll(getTop(allPlaces.get(type), finalAmounts.get(type))); 
		}
		
		//TODO: extract the comparator
		Collections.sort(finalPlaces, new Comparator<Place>() {
			@Override
			public int compare(Place place1, Place place2) {
				return (place2.getRate().compareTo(place1.getRate()));
			}
		});
		
		return finalPlaces;
	}
	
	private static Map<Interest, Integer> sortByComparator(Map<Interest, Integer> unsortMap) {

		// Convert Map to List
		List<Map.Entry<Interest, Integer>> list = 
			new LinkedList<Map.Entry<Interest, Integer>>(unsortMap.entrySet());

		// Sort list with comparator, to compare the Map values
		Collections.sort(list, new Comparator<Map.Entry<Interest, Integer>>() {
			public int compare(Map.Entry<Interest, Integer> o1,
                                           Map.Entry<Interest, Integer> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});

		// Convert sorted map back to a Map
		Map<Interest, Integer> sortedMap = new LinkedHashMap<Interest, Integer>();
		for (Iterator<Map.Entry<Interest, Integer>> it = list.iterator(); it.hasNext();) {
			Map.Entry<Interest, Integer> entry = it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
	
	private static List<Place> getTop(List<Place> places, int amount){
		
		List<Place> top = new ArrayList<Place>();
		for (Place place : places) {
			if (place.getAllGoogleData()){
				top.add(place);
				if (--amount <= 0){
					break;
				}
			}
		}
		
		return top;
	}

	private static Map<Type, Integer> calcTypeAmount(Map<Type, Integer> typeCount){
		
		int total = TOTAL_RECOMMENDATION_AMOUNT;
		
		// remove the "other" category.
		total = (int)(total * (1 - OTHER_PERCENTS));
	
		// get the total sum of the type counts
		int sum = typeCount.values().stream().mapToInt(Integer::intValue).sum();

		// create a new map with the amounts based on the final amount according to the 
		// percents of the types, rounding down.
		Map<Type, Integer> m = new HashMap<Type, Integer>();	
		for (Map.Entry<Type, Integer> entry : typeCount.entrySet())
		{
			m.put(entry.getKey(), total * (entry.getValue() / sum));
		}

		// get the final sum of the types and set the "other" type as the rest
		int finalSum = m.values().stream().mapToInt(Integer::intValue).sum();
		m.put(Type.other, TOTAL_RECOMMENDATION_AMOUNT - finalSum);
		
		return m;
	}
	
	private static Map<Type, Integer> convertListJson(JsonArray facebookTypes) {
		Map<Type, Integer> facebookTypesMap = new HashMap<Type, Integer>();
		for (int i = 0; i < facebookTypes.size(); i++) {
			JsonObject typeJson = facebookTypes.get(i).getAsJsonObject();
			int count = typeJson.get("count").getAsInt();
			String typeName = typeJson.get("type").getAsString();
			Type type = db.getGoogleType(typeName);
			facebookTypesMap.put(type, count);
		}
		return facebookTypesMap;
	}
}
