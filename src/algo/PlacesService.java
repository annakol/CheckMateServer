package algo;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import module.Location;

public class PlacesService {

    private static final String API_SERVER_KEY = "AIzaSyB9y8JYZK-7D2Cag6oFEK5km8DOg5SAn6w";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_SEARCH = "/nearbysearch";
    private static final String OUT_JSON = "/json";
    
	public String getPlaces(Location currLoc, int radius) {
		HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE);
            sb.append(TYPE_SEARCH);
            sb.append(OUT_JSON);
            sb.append("?key=" + API_SERVER_KEY);
            sb.append("&location=" + currLoc.lat + "," +  currLoc.lng);
            sb.append("&radius=" + String.valueOf(radius));
            //sb.append("&opennow");
            
            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream(),"UTF-8");

            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
        	e.printStackTrace();
        } catch (IOException e) {
        	e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        
        return jsonResults.toString();
	}
}
