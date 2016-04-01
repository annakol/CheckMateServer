package main;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.gson.Gson;

/**
 * Servlet implementation class MainServlet
 */
@WebServlet("/MainServlet")
public class MainServlet extends HttpServlet {
	  /*
	   * Default JSON factory to use to deserialize JSON.
	   */
	  private static final JacksonFactory JSON_FACTORY = new JacksonFactory();

	  /*
	   * Gson object to serialize JSON responses to requests to this servlet.
	   */
	  private static final Gson GSON = new Gson();

	  /*
	   * Creates a client secrets object from the client_secrets.json file.
	   */
	  private static GoogleClientSecrets clientSecrets;
	  
	  /*
	   * This is the Client ID that you generated in the API Console.
	   */
	  //private static final String CLIENT_ID = clientSecrets.getWeb().getClientId();

	  /*
	   * This is the Client Secret that you generated in the API Console.
	   */
	  //private static final String CLIENT_SECRET = clientSecrets.getWeb().getClientSecret();
	  
/*	  static {
		    try {
		      Reader reader = new FileReader("C:\\Users\\Anna\\Projects\\CheckMate\\CheckMateServer\\client_secrets.json");
		      clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, reader);
		    } catch (IOException e) {
		      throw new Error("No client_secrets.json found", e);
		    }
		  }*/
	  
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MainServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";

    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String TYPE_DETAILS = "/details";
    private static final String TYPE_SEARCH = "/nearbysearch";

    private static final String OUT_JSON = "/json";
	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ArrayList<Place> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE);
            sb.append(TYPE_SEARCH);
            sb.append(OUT_JSON);
            sb.append("?sensor=false");
            sb.append("&key=" + "AIzaSyB9y8JYZK-7D2Cag6oFEK5km8DOg5SAn6w");
            sb.append("&location=" +  32.051324  + "," +  34.811146);
            sb.append("&radius=" + String.valueOf(100));

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
        
        System.out.println(jsonResults.toString());
/*    	Gson gson = new GsonBuilder().create();
    	gson.toJson(jsonResults.toString(), System.out);
    	gson.*/
	}

}
