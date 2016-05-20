package servlet;

import java.io.IOException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import algo.ConvertUtils;
import algo.PlacesService;
import algo.RatingManager;
import model.Location;
import model.Place;
import model.Type;

/**
 * Servlet implementation class MainServlet
 */
@WebServlet("/MainServlet")
public class MainServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final String LOCATION_PARAM = "location";
	private static final String CHECKIN_TYPES_PARAM = "types";
	private static final String LOCATION_ERROR = "No location was sent!";

	/**
	 * Default constructor.
	 */
	public MainServlet() {
		// TODO Auto-generated constructor stub
	}

	private Map<Type, List<Place>> getAllPlaces(Location location, int radius, Set<Type> types) {

		Map<Type, List<Place>> places = new HashMap<Type, List<Place>>();
		List<Place> curr;
		PlacesService service = new PlacesService();
		for (Type type : types) {
			curr = service.getPlaces(location, radius, type.getName());
			System.out.println("getAllPlaces  - " + type.getName());
			if ((curr != null) && (!curr.isEmpty())) {
				Collections.sort(curr, new Comparator<Place>() {
					@Override
					public int compare(Place place1, Place place2) {
						return (place2.getRate().compareTo(place1.getRate()));
					}
				});
				places.put(type, curr);
			}
		}

		return places;
	}

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String locStr = request.getParameter(LOCATION_PARAM);
		Location currLoc = null;

		if (locStr != null) {
			String[] locArr = locStr.split(",");
			if (locArr.length == 2) {
				currLoc = new Location(locArr[0], locArr[1]);
			}
		}

		String checkinTypes = request.getParameter(CHECKIN_TYPES_PARAM);
		// checkinTypes =
		// "{\"longitude\":32.051324,\"latitude\":34.811146,\"types\":[{\"type\":\"Shopping
		// Mall\",\"count\":5},{\"type\":\"Cafe\",\"count\":7},{\"type\":\"Library\",\"count\":2},{\"type\":\"Gym\",\"count\":4},{\"type\":\"Restaurant\",\"count\":14}]}";
		checkinTypes = "{'userId': 1, " + "'time':" + Calendar.getInstance().getTimeInMillis()
				+ ", 'longitude':34.819934," + "'latitude':32.088674," + "'types':[" + "{'type':'Cafe','count':7}]}";
		//32.051324, 34.811146
		//32.088674, 34.819934
		JsonObject requestJson = new JsonParser().parse(checkinTypes).getAsJsonObject();
		JsonArray typesArray = requestJson.getAsJsonArray("types");

		Integer userId = requestJson.get("userId").getAsInt();

		Location currentLocation = new Location(requestJson.get("latitude").getAsString(),
				requestJson.get("longitude").getAsString());

		RatingManager.getInstance().init(currentLocation, userId);

		// creates a <type, percent> map.
		Map<Type, Double> googleCheckinTypes = ConvertUtils.createGoogleTypeList(typesArray);

		Map<Type, List<Place>> places = getAllPlaces(currentLocation, 3000, googleCheckinTypes.keySet());

		//List<Place> top30places;
		
		for (Map.Entry<Type, List<Place>> entry : places.entrySet())
		{
		    System.out.println(entry.getKey());	 
		    for (Place p : entry.getValue()) {
				System.out.println(p);
			}
		}
		
		Gson gson = new GsonBuilder().create();

		response.setContentType("application/json");
		response.setCharacterEncoding("utf-8");
		gson.toJson(places.toString(), response.getWriter());

	}
}
