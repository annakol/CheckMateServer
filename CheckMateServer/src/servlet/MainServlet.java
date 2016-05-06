package servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
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
import model.GooglePlace;
import model.JsonLocation;
import model.Location;
import model.Place;

/**
 * Servlet implementation class MainServlet
 */
@WebServlet("/MainServlet")
public class MainServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final String LOCATION_PARAM = "location";
	private static final String CHECKIN_TYPES_PARAM = "types";
	private static final String LOCATION_ERROR = "No location was sent!";
	List<JsonLocation> finalLocations = new ArrayList<JsonLocation>();

	/**
	 * Default constructor.
	 */
	public MainServlet() {
		// TODO Auto-generated constructor stub
	}
	
	private List<Place> getAllPlacesFromGoogle(Location location, int radius ,Set<String> types){
		
		List<Place> places = new ArrayList<Place>(); 
		PlacesService service =  new PlacesService();
		for (String type : types) {
			places.addAll(service.getPlaces(location, radius, type));
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
		checkinTypes = "{'time':" + Calendar.getInstance().getTimeInMillis() + ", 'longitude':32.051324,"
				+ "'latitude':34.811146," + "'types':[" + "{'type':'Shopping Mall','count':5},"
				+ "{'type':'Cafe','count':7}," + "{'type':'Library','count':2}," + "{'type':'Gym','count':4},"
				+ "{'type':'Restaurant','count':14}," + "{'type':'Park','count':7}]}";

		JsonObject requestJson = new JsonParser().parse(checkinTypes).getAsJsonObject();
		JsonArray typesArray = requestJson.getAsJsonArray("types");

		Map<String, Integer> googleCheckinTypes = ConvertUtils.createGoogleTypeList(typesArray);
		List<Place> places = getAllPlacesFromGoogle(new Location("32.051324", "34.811146"), 5000 ,googleCheckinTypes.keySet());
		
		
		
		
		for (Place place : places) {
			System.out.println(place);
			
			
		}
		
		
		
	}
}
