package main.java.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import main.java.algo.PlacesService;
import main.java.module.Location;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Servlet implementation class MainServlet
 */
@WebServlet("/MainServlet")
public class MainServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MainServlet() {
        super();
    }

    private static final String LOCATION_PARAM = "location";
    private static final String LOCATION_ERROR = "No location was sent!";
    
	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String locStr = request.getParameter(LOCATION_PARAM);
		Location currLoc = null;
		
		if (locStr != null) {
			String[] locArr = locStr.split(",");
			if (locArr.length == 2) {
				currLoc = new Location(locArr[0],locArr[1]);
			}
		}
		
		if (currLoc == null) {
			response.getWriter().append(LOCATION_ERROR);
		} else {
	        PlacesService service = new PlacesService();
	        
	        String jsonResults = 
	        		service.getPlaces(new Location(currLoc.lat, currLoc.lng), 100);
	        
	        System.out.println(jsonResults.toString());
	        Gson gson = new GsonBuilder().create();
	        
	    	response.setContentType("application/json"); 
	    	response.setCharacterEncoding("utf-8");
	    	gson.toJson(jsonResults.toString(), response.getWriter());
		} 
	}
}
