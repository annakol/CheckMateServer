package servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import algo.PlacesService;
import module.FacebookLocationTypes;
import module.JsonLocation;
import module.Location;

/**
 * Servlet implementation class MainServlet
 */
@WebServlet("/MainServlet")
public class MainServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final String LOCATION_PARAM = "location";
    private static final String CHECKIN_TYPES_PARAM = "types";
    private static final String LOCATION_ERROR = "No location was sent!";
    Map<String, Integer> checkinsTypes = new HashMap<String, Integer>();
    List<JsonLocation> finalLocations = new ArrayList<JsonLocation>();
    /**
     * Default constructor. 
     */
    public MainServlet() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//FacebookLocationTypes.Type a ;
				//a = FacebookLocationTypes.Type.amusement_park;
				//System.out.println(a);
				
				String locStr = request.getParameter(LOCATION_PARAM);
				Location currLoc = null;
				
				if (locStr != null) {
					String[] locArr = locStr.split(",");
					if (locArr.length == 2) {
						currLoc = new Location(locArr[0],locArr[1]);
					}
				}
				
				String checkinTypes = request.getParameter(CHECKIN_TYPES_PARAM);
				//checkinTypes = "{\"longitude\":32.051324,\"latitude\":34.811146,\"types\":[{\"type\":\"Shopping Mall\",\"count\":5},{\"type\":\"Cafe\",\"count\":7},{\"type\":\"Library\",\"count\":2},{\"type\":\"Gym\",\"count\":4},{\"type\":\"Restaurant\",\"count\":14}]}";
				checkinTypes= "{'longitude':32.051324,'latitude':34.811146,'types':[{'type':'Shopping Mall','count':5},{'type':'Cafe','count':7},{'type':'Library','count':2},{'type':'Gym','count':4},{'type':'Restaurant','count':14},{'type':'Park','count':7}]}";
				JsonObject facebookTypes = new JsonParser().parse(checkinTypes).getAsJsonObject();
				
				int sum = 0;
				JsonArray typesArray = facebookTypes.getAsJsonArray("types");
				for(int i=0; i < typesArray.size(); i++){
					sum += typesArray.get(i).getAsJsonObject().get("count").getAsInt();
				}
				
				for(int i=0; i < typesArray.size(); i++){
					checkinsTypes.put(FacebookLocationTypes.getInstance().get(typesArray.get(i).getAsJsonObject().get("type").getAsString()),
							typesArray.get(i).getAsJsonObject().get("count").getAsInt() * 100 / sum);
					
				}
				
				currLoc = new Location("32.048398", "34.812180");
				if (currLoc == null) {
					response.getWriter().append(LOCATION_ERROR);
				} else {
			        PlacesService service = new PlacesService();
			        
			        String jsonResults = 
			        		service.getPlaces(new Location(currLoc.lat, currLoc.lng), 2000,null);


			        while (jsonResults != null) {
				        JsonObject o = new JsonParser().parse(jsonResults).getAsJsonObject();
				        JsonArray results = o.getAsJsonArray("results");
				        String nextPage = null;
				        if (o.get("next_page_token") !=null) {
				        	nextPage = o.get("next_page_token").getAsString();
				        }
				        JsonObject curr;
				        JsonArray types;
				        for(int i=0; i < results.size(); i++){
				        	curr = (JsonObject) results.get(i);
				        	types = curr.getAsJsonArray("types");
				        	String name = curr.get("name").getAsString();
				        	
				        	System.out.println(i + " " + types + " " + name);
				        	int weigth = 0;
					        for(int j=0; j < types.size(); j++){
					        	
					        	if(checkinsTypes.containsKey(types.get(j).getAsString())){
					        		weigth += checkinsTypes.get(types.get(j).getAsString());
					        	}		        	
					        }
					        
					        if(weigth > 0){
					        	JsonLocation loc = new JsonLocation(curr,weigth);
					        	finalLocations.add(loc);
					        }
				        }
				        
				        if (nextPage != null) {
				        	jsonResults = service.getPlaces(new Location(currLoc.lat, currLoc.lng),2000,nextPage);
				        } else {
				        	jsonResults = null;
				        }
			        }
			        
				   java.util.Collections.sort(finalLocations);
				       
			        for (JsonLocation jl : finalLocations) {
			        	System.out.println(jl.location);
			        	System.out.println("");
			        	System.out.println(jl.weight);
			        	System.out.println("");
			        }
			        
			        Gson gson = new GsonBuilder().create();
			        
			    	//response.setContentType("application/json"); 
			    	//response.setCharacterEncoding("utf-8");
			    	//gson.toJson(jsonResults.toString(), response.getWriter());
				} 
	}

}
