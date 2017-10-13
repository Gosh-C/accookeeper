package gosh.com.accookeepersdk;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import gosh.com.accookeepersdk.model.SheetField;
import gosh.com.accookeepersdk.utils.PrefUtils;

/**
 * Created by goshchan on 9/10/2017.
 */

public class SheetsConfig {
    private final static String SHEET_NAME = "SHEET_NAME";
    private final static String FIELDS = "FIELDS";

    private static List<String> sSheetsName;
    private static HashMap<String, LinkedList<SheetField>> sSheetFields;

    public static void resetSheet(){
        sSheetsName = null;
        sSheetFields = null;
    }

    public static List<SheetField> getSheetFields(Context context, String sheet)  throws JSONException {
        if(sSheetFields == null || sSheetFields.size() == 0){
            createSheetsNameAndFields(context);
        }
        return sSheetFields.get(sheet);
    }

    public static List<String> getSheetName(Context context) throws JSONException{
        if(sSheetsName == null || sSheetsName.size() == 0){
            createSheetsNameAndFields(context);
        }
        return sSheetsName;
    }

    private static void createSheetsNameAndFields(Context context) throws JSONException {
        String config = PrefUtils.getSheetConfigJson(context);
        sSheetsName = new ArrayList<>();
        sSheetFields = new HashMap<>();
        JSONArray array = new JSONArray(config);
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = (JSONObject) array.get(i);
            String s = obj.getString(SHEET_NAME);
            sSheetsName.add(s);

            JSONArray fieldsArray = obj.getJSONArray(FIELDS);
            LinkedList<SheetField> list = new LinkedList<>();
            for(int j = 0; j < fieldsArray.length(); j++){
                JSONObject jo = (JSONObject) fieldsArray.get(j);
                String field = jo.getString("FIELD");
                String type = jo.getString("TYPE");
                Boolean mandate = jo.getBoolean("MANDATORY");
                SheetField sf = new SheetField(field, type, mandate);
                list.add(sf);
            }
            sSheetFields.put(s, list);
        }
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
            sheet.put(SHEET_NAME, sheetName);
            sheet.put(FIELDS, row);
            sheetsArray.put(sheet);
        }

        return sheetsArray.toString();
    }

}
