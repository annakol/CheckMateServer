package algo;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import model.TypeDictionary;

public class ConvertUtils {

	/**
	 * creates a map with the GOOGLE type name and the percent of the checkins the user did
	 * in this type based on the facebook type list and amounts that came from the client.
	 * @param facebookTypes
	 * @return
	 */
	public static Map<String, Integer> createGoogleTypeList(JsonArray facebookTypes) {
		Map<String, Integer> checkinTypes = new HashMap<String, Integer>();

		int sum = 0;
		for (int i = 0; i < facebookTypes.size(); i++) {
			sum += facebookTypes.get(i).getAsJsonObject().get("count").getAsInt();
		}

		for (int i = 0; i < facebookTypes.size(); i++) {
			JsonObject typeJson = facebookTypes.get(i).getAsJsonObject();
			int count = typeJson.get("count").getAsInt();
			String typeName = typeJson.get("type").getAsString();
			String googleTypeName = TypeDictionary.getInstance().get(typeName);
			if (checkinTypes.containsKey(googleTypeName)) {
				checkinTypes.put(googleTypeName, checkinTypes.get(googleTypeName) + (count * 100 / sum));

			} else {
				checkinTypes.put(googleTypeName, count * 100 / sum);
			}
		}

		return checkinTypes;
	}

}
