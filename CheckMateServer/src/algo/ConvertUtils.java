package algo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import model.MySqlDriver;
import model.Type;

public class ConvertUtils {

	/**
	 * creates a map with the GOOGLE type name and the percent of the checkins
	 * the user did in this type based on the facebook type list and amounts
	 * that came from the client.
	 * 
	 * @param facebookTypes
	 * @return
	 */
	public static Map<Type, Double> createGoogleTypeList(JsonArray facebookTypes) {
		Map<Type, Double> checkinTypes = new HashMap<Type, Double>();

		int sum = 0;
		for (int i = 0; i < facebookTypes.size(); i++) {
			sum += facebookTypes.get(i).getAsJsonObject().get("count").getAsInt();
		}

		List<String> facebookList = new ArrayList<String>();
		for (int i = 0; i < facebookTypes.size(); i++) {
			JsonObject typeJson = facebookTypes.get(i).getAsJsonObject();
			int count = typeJson.get("count").getAsInt();
			String typeName = typeJson.get("type").getAsString();
			Type googleType = MySqlDriver.getInstance().getGoogleType(typeName);
			if (googleType != null) {
				if (checkinTypes.containsKey(googleType)) {
					checkinTypes.put(googleType, checkinTypes.get(googleType) + (count * 100 / sum));

				} else {
					checkinTypes.put(googleType, (double) count * 100 / sum);
				}
			}
			else{
				facebookList.add(typeName);
			}

			// TODO: if there is a facebook without google type - get 3 google types of those
			// and of there isn't, get 3 more google types by the interests of the existing google types.
			// list -> to array -> random 3.

			/*if (!facebookList.isEmpty()){
				
				MySqlDriver.getInstance().getTypesByFacebookInterests(facebookList,checkinTypes.keySet());
			}
			else{
				MySqlDriver.getInstance().
			}*/
		}

		return checkinTypes;
	}
}
