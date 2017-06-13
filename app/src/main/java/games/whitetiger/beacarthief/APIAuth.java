package games.whitetiger.beacarthief;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

class APIAuth {

    private SharedPreferences sharedPref;

    APIAuth(Activity activity) {
        sharedPref = activity.getSharedPreferences(IConstants.PREFERENCE_USER, Context.MODE_PRIVATE);
    }

    /**
     * Check if is user logged in
     *
     * @return boolean
     */
    boolean isLoggedIn() {
        String apiKey = sharedPref.getString(IConstants.API_KEY, "");

        return apiKey != "";
    }

    /**
     * Save user data in the device store
     *
     * @param user
     */
    void saveLoggedInData(JSONObject user) {
        SharedPreferences.Editor editor;
        editor = sharedPref.edit();
        try {
            editor.putString(IConstants.USERNAME, user.getString("username"));
            editor.putString(IConstants.EMAIL, user.getString("email"));
            editor.putString(IConstants.API_KEY, user.getString("apiKey"));
            editor.putInt(IConstants.LEVEL, user.getInt("level"));
            editor.putInt(IConstants.EXPERIENCE, user.getInt("experience"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        editor.apply();
    }

    /**
     * Delete user data
     */
    void logout() {
        SharedPreferences.Editor editor;
        editor = sharedPref.edit();
        editor.putString(IConstants.USERNAME, "");
        editor.putString(IConstants.EMAIL, "");
        editor.putString(IConstants.API_KEY, "");
        editor.putInt(IConstants.LEVEL, 1);
        editor.putInt(IConstants.EXPERIENCE, 0);
        editor.apply();
    }

    String getAPIKey() {
        return sharedPref.getString(IConstants.API_KEY, "");
    }

    String getUsername() {
        return sharedPref.getString(IConstants.USERNAME, "");
    }

    int getLevel() {
        return sharedPref.getInt(IConstants.LEVEL, 1);
    }

    int getExperience() {
        return sharedPref.getInt(IConstants.EXPERIENCE, 0);
    }

    void updateExp(int exp) {
        int currentExp = getExperience();
        int currentLvl = getLevel();

        currentExp += exp;

        SharedPreferences.Editor editor;
        editor = sharedPref.edit();

        if (currentExp >= Helper.getMaxExpForLevel(currentLvl)) {
            currentLvl ++;
            currentExp = 0;
            editor.putInt(IConstants.LEVEL, currentLvl);
        }

        editor.putInt(IConstants.EXPERIENCE, currentExp);

        editor.apply();

        RequestParams params = new RequestParams();
        params.put("username", getUsername());
        params.put("level", currentLvl);
        params.put("experience", currentExp);

        RestClient.post("user/update", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            }
        }, getAPIKey());
    }

}
