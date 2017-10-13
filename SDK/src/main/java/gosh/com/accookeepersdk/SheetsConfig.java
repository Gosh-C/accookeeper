package gosh.com.accookeepersdk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by goshchan on 9/10/2017.
 */

public class SheetsConfig {
    private static String sJsonConfig;
    private static List<String> sSheetsName;

    public static List<String> getSheetName(){
        if(sSheetsName == null){

        }
        return sSheetsName;
    }

    private void createSheetsName() throws JSONException{
        if(sJsonConfig == null){

        }
        JSONObject o = new JSONObject(sJsonConfig);
    }

    public static String toJsonString(HashMap<String, LinkedList<LinkedHashMap<String, String>>> sheetsConfig) throws JSONException{
        Set<String> keys = sheetsConfig.keySet();
        JSONArray sheetsArray = new JSONArray();
        for(String sheetName : keys){
            JSONObject sheet = new JSONObject();
            LinkedList<LinkedHashMap<String, String>> rows = sheetsConfig.get(sheetName);
            JSONArray row = new JSONArray();
            for(LinkedHashMap<String, String> configs : rows){
                JSONObject config = new JSONObject();
                Set<String> subKeys = configs.keySet();
                for(String key : subKeys){
                    String value = configs.get(key);
                    config.put(key, value);
                }
                row.put(config);
            }
            sheet.put(sheetName, row);
            sheetsArray.put(sheet);
        }

        return sheetsArray.toString();
    }

}
