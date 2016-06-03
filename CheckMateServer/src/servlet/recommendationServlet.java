package servlet;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

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

import algo.RecommendationManager;
import model.Location;
import model.Place;

/**
 * Servlet implementation class tryServlet
 */
@WebServlet("/recommendation")
public class recommendationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final String LOCATION_PARAM = "location";
	private static final String CHECKIN_TYPES_PARAM = "types";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public recommendationServlet() {
		super();
		// TODO Auto-generated constructor stub

	}

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		// super.service(request, response);

		String locStr = request.getParameter(LOCATION_PARAM);
		Location currLoc = null;

		if (locStr != null) {
			String[] locArr = locStr.split(",");
			if (locArr.length == 2) {
				currLoc = new Location(locArr[0], locArr[1]);
			}
		}

		String checkinTypes = request.getParameter(CHECKIN_TYPES_PARAM);
		checkinTypes = "{'userId': 1, " + "'time':" + Calendar.getInstance().getTimeInMillis()
				+ ", 'longitude':34.819934," + "'latitude':32.088674," + "'types':[" + "{'type':'Cafe','count':7}]}";
		JsonObject requestJson = new JsonParser().parse(checkinTypes).getAsJsonObject();
		JsonArray typesArray = requestJson.getAsJsonArray("types");

		
		Long timeMillis = requestJson.get("time").getAsLong();
		Calendar time = Calendar.getInstance();
		time.setTimeInMillis(timeMillis);;
		Integer userId = requestJson.get("userId").getAsInt();

		Location currentLocation = new Location(requestJson.get("latitude").getAsString(),
				requestJson.get("longitude").getAsString());

		List<Place> recommendedPlaces = RecommendationManager.getRecommendedPlaces(currentLocation, typesArray, userId, time);

		Gson gson = new GsonBuilder().create();

		response.setContentType("application/json");
		response.setCharacterEncoding("utf-8");
		gson.toJson(recommendedPlaces, response.getWriter());

	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
