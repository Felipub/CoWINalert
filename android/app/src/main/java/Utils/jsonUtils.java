package Utils;

import android.app.Activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class jsonUtils {

    public static JSONArray getListOfStatesAndDistricts(Activity activity){
        InputStream is = null;
        try {
            is = activity.getAssets().open("states.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String myJson = new String(buffer, "UTF-8");

            JSONObject obj = new JSONObject(myJson);
            JSONArray finalList = obj.getJSONArray("states");
            return  finalList;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }


}
