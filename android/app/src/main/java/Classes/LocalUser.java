package Classes;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

public class LocalUser {
    public static final String SHAREDPREFS    = "sharedPrefs";
    public static final String KEY_UID        = "uid";
    public static final String KEY_STATE      = "state";
    public static final String KEY_DISTRICT   = "district";
    public static final String KEY_PIN        = "pin";
    public static final String KEY_PLUS18     = "plus18";
    public static final String KEY_PLUS45     = "plus45";
    public static final String KEY_COVISHIELD = "covishield";
    public static final String KEY_COVAXIN    = "covaxin";
    public static final String KEY_SPUTNIKV   = "sputnikv";
    public static final String KEY_FREE       = "free";
    public static final String KEY_PAID       = "paid";

    private long     pin        = -1,
                    state      = -1,
                    district   = -1;
    private String  uid        = null;
    private boolean plus18     = false,
                    plus45     = false,
                    covishield = false,
                    covaxin    = false,
                    sputnikV   = false,
                    free       = false,
                    paid       = false;

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor sharePrefEditor;


    public LocalUser(Context context, String uid){
        sharedPref = context.getSharedPreferences(uid, Context.MODE_PRIVATE);
        sharePrefEditor = sharedPref.edit();
        this.uid        = uid;
        this.state      = sharedPref.getLong    (KEY_STATE      ,-1);
        this.district   = sharedPref.getLong    (KEY_DISTRICT   ,-1);
        this.pin        = sharedPref.getLong    (KEY_PIN        ,-1);
        this.plus18     = sharedPref.getBoolean(KEY_PLUS18     ,false);
        this.plus45     = sharedPref.getBoolean(KEY_PLUS45     ,false);
        this.covishield = sharedPref.getBoolean(KEY_COVISHIELD ,false);
        this.covaxin    = sharedPref.getBoolean(KEY_COVAXIN    ,false);
        this.sputnikV   = sharedPref.getBoolean(KEY_SPUTNIKV   ,false);
        this.free       = sharedPref.getBoolean(KEY_FREE       ,false);
        this.paid       = sharedPref.getBoolean(KEY_PAID       ,false);
    }

    public void localUserLoadFromPreferences(){

    }

    public void createForTheFirstTime(){

    }

    public Map<String, Object> getLocalUserForFirebase(){
        Map<String, Object> user = new HashMap<>();
        user.put(KEY_UID        , uid);
        user.put(KEY_STATE      , (int)state);
        user.put(KEY_DISTRICT   , (int)district);
        user.put(KEY_PIN        , (int)pin);
        user.put(KEY_PLUS18     , plus18);
        user.put(KEY_PLUS45     , plus45);
        user.put(KEY_COVISHIELD , covishield);
        user.put(KEY_COVAXIN    , covaxin);
        user.put(KEY_SPUTNIKV   , sputnikV);
        user.put(KEY_FREE       , free);
        user.put(KEY_PAID       , paid);
        return user;
    }
    public void setLocalUserFromFirebase(Map<String, Object> input){
        setDistrict  ((long)   input.get(KEY_DISTRICT));
        setState     ((long)   input.get(KEY_STATE));
        setPin       ((long)   input.get(KEY_PIN));
        setPlus18    ((boolean)input.get(KEY_PLUS18));
        setPlus45    ((boolean)input.get(KEY_PLUS45));
        setCovishield((boolean)input.get(KEY_COVISHIELD));
        setCovaxin   ((boolean)input.get(KEY_COVAXIN));
        setSputnikV  ((boolean)input.get(KEY_SPUTNIKV));
        setFree      ((boolean)input.get(KEY_FREE));
        setPaid      ((boolean)input.get(KEY_PAID ));
    }

    private void savePref(String key, Object value){
        if(value instanceof String)  sharePrefEditor.putString (key, (String)  value);
        if(value instanceof Integer) sharePrefEditor.putInt    (key, (int)     value);
        if(value instanceof Long)    sharePrefEditor.putLong   (key, (long)    value);
        if(value instanceof Boolean) sharePrefEditor.putBoolean(key, (boolean) value);
        sharePrefEditor.commit();
    }

    public void setPin(long pin) {
        this.pin = pin;
        savePref(KEY_PIN, pin);
    }

    public void setState(long state) {
        this.state = state;
        savePref(KEY_STATE, state);
    }

    public void setDistrict(long district) {
        this.district = district;
        savePref(KEY_DISTRICT, district);
    }

    public void setUid(String uid) {
        this.uid = uid;
        savePref(KEY_UID, uid);
    }

    public String getUid(){
        return uid;
    }

    public void setPlus18(boolean plus18) {
        this.plus18 = plus18;
        savePref(KEY_PLUS18, plus18);
    }

    public void setPlus45(boolean plus45) {
        this.plus45 = plus45;
        savePref(KEY_PLUS45, plus45);
    }

    public void setCovishield(boolean covishield) {
        this.covishield = covishield;
        savePref(KEY_COVISHIELD, covishield);
    }

    public void setCovaxin(boolean covaxin) {
        this.covaxin = covaxin;
        savePref(KEY_COVAXIN,covaxin);
    }

    public void setSputnikV(boolean sputnikV) {
        this.sputnikV = sputnikV;
        savePref(KEY_SPUTNIKV,sputnikV);
    }

    public void setFree(boolean free) {
        this.free = free;
        savePref(KEY_FREE, free);
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
        savePref(KEY_PAID, paid);
    }

    public long getPin() {
        return pin;
    }

    public long getState() {
        return state;
    }

    public long getDistrict() {
        return district;
    }

    public boolean isPlus18() {
        return plus18;
    }

    public boolean isPlus45() {
        return plus45;
    }

    public boolean isCovishield() {
        return covishield;
    }

    public boolean isCovaxin() {
        return covaxin;
    }

    public boolean isSputnikV() {
        return sputnikV;
    }

    public boolean isFree() {
        return free;
    }

    public boolean isPaid() {
        return paid;
    }

    public boolean isLogged(){
        return uid!=null;
    }

    @Override
    public String toString(){
        StringBuffer sb = new StringBuffer();
        sb.append("====").append(uid).append("====").append("\n");
        sb.append(KEY_DISTRICT).append(" ").append(district).append("\n");
        sb.append(KEY_STATE).append(" ").append(state).append("\n");
        sb.append(KEY_PIN).append(" ").append(pin).append("\n");
        sb.append(KEY_PLUS18).append(" ").append(plus18).append("\n");
        sb.append(KEY_PLUS45).append(" ").append(plus45).append("\n");
        sb.append(KEY_COVISHIELD).append(" ").append(covishield).append("\n");
        sb.append(KEY_COVAXIN).append(" ").append(covaxin).append("\n");
        sb.append(KEY_SPUTNIKV).append(" ").append(sputnikV).append("\n");
        sb.append(KEY_FREE).append(" ").append(free).append("\n");
        sb.append(KEY_PAID).append(" ").append(paid).append("\n");
        sb.append("====").append(uid).append("====").append("\n");
        return sb.toString();
    }

}
