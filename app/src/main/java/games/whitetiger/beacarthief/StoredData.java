package games.whitetiger.beacarthief;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class StoredData {

    StoredData() {
        super();
    }

    public void saveUserVehicles(Context context, List<Vehicle> vehicles) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = context.getSharedPreferences(IConstants.PREFERENCE_USER_VEHICLES, Context.MODE_PRIVATE);
        editor = settings.edit();
        Gson gson = new Gson();
        String jsonVehicles = gson.toJson(vehicles);
        editor.putString(IConstants.VEHICLES, jsonVehicles);
        editor.apply();
    }

    void saveVehicles(Context context, List<Vehicle> vehicles) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = context.getSharedPreferences(IConstants.PREFERENCE_GLOBAL, Context.MODE_PRIVATE);
        editor = settings.edit();
        Gson gson = new Gson();
        String jsonVehicles = gson.toJson(vehicles);
        editor.putString(IConstants.VEHICLES, jsonVehicles);
        editor.apply();
    }

    ArrayList getVehicles(Context context) {
        SharedPreferences settings;
        List<Vehicle> vehicles;
        settings = context.getSharedPreferences(IConstants.PREFERENCE_GLOBAL, Context.MODE_PRIVATE);
        if (settings.contains(IConstants.VEHICLES)) {
            String jsonVehicles = settings.getString(IConstants.VEHICLES, null);
            Gson gson = new Gson();
            Vehicle[] vehicleItems = gson.fromJson(jsonVehicles, Vehicle[].class);

            vehicles = Arrays.asList(vehicleItems);
            vehicles = new ArrayList(vehicles);
        } else
            return null;

        return (ArrayList<Vehicle>) vehicles;
    }

    ArrayList getUserVehicles(Context context) {
        SharedPreferences settings;
        List<Vehicle> userVehicles;
        settings = context.getSharedPreferences(IConstants.PREFERENCE_USER_VEHICLES, Context.MODE_PRIVATE);
        if (settings.contains(IConstants.VEHICLES)) {
            String jsonVehicles = settings.getString(IConstants.VEHICLES, null);
            Gson gson = new Gson();
            Vehicle[] vehicleItems = gson.fromJson(jsonVehicles, Vehicle[].class);

            userVehicles = Arrays.asList(vehicleItems);
            userVehicles = new ArrayList(userVehicles);
        } else
            return null;

        return (ArrayList<Vehicle>) userVehicles;
    }

    boolean containVehicle(Context context, Vehicle vehicle) {
        List<Vehicle> vehicles = getVehicles(context);
        return vehicles != null && vehicles.contains(vehicle);
    }

    public boolean containUserVehicle(Context context, Vehicle vehicle) {
        List<Vehicle> userVehicles = getUserVehicles(context);
        return userVehicles != null && userVehicles.contains(vehicle);
    }

    void addVehicle(Context context, Vehicle vehicle) {
        List<Vehicle> vehicles = getVehicles(context);
        if (vehicles == null)
            vehicles = new ArrayList<Vehicle>();
        vehicles.add(vehicle);
        saveVehicles(context, vehicles);
    }

    public void addUserVehicle(Context context, Vehicle vehicle) {
        List<Vehicle> userVehicles = getUserVehicles(context);
        if (userVehicles == null)
            userVehicles = new ArrayList<Vehicle>();
        userVehicles.add(vehicle);
        saveUserVehicles(context, userVehicles);
    }

    void removeVehicle(Context context, Vehicle vehicle) {
        ArrayList<Vehicle> vehicles = getVehicles(context);
        if (vehicles != null) {
            vehicles.remove(vehicle);
            saveVehicles(context, vehicles);
        }
    }

    public void removeUserVehicle(Context context, Vehicle vehicle) {
        ArrayList<Vehicle> userVehicles = getUserVehicles(context);
        if (userVehicles != null) {
            userVehicles.remove(vehicle);
            saveUserVehicles(context, userVehicles);
        }
    }

    void updateVehicle(Context context, Vehicle vehicle) {
        List<Vehicle> vehicles = getVehicles(context);
        if (containVehicle(context, vehicle)) {
            vehicles.set(vehicles.indexOf(vehicle), vehicle);
            saveVehicles(context, vehicles);
        }
    }

}
