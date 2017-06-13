package games.whitetiger.beacarthief;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import cz.msebera.android.httpclient.Header;

public class MapFragment extends Fragment implements OnMapReadyCallback, ConnectionCallbacks,
        OnConnectionFailedListener, LocationListener, ICallback, GoogleMap.OnMarkerClickListener {

    View myView;
    private MapView mapView;
    private GoogleApiClient mGoogleApiClient;
    private GoogleMap googleMap;
    LocationRequest mLocationRequest;
    private float zoomLevel = 18;
    private Marker user;
    private Calendar calender;
    private int hour;
    private Location lastLocation;
    private StoredData storedData;
    private APIAuth auth;
    private ArrayList<Marker> mMarkerArray = new ArrayList<>();

    private int LOCATION_REFRESH_TIME = 3000;
    private int LOCATION_FAST_REFRESH_TIME = 1000;

    private static final int INTERVAL_TIME = 1000 * 10;
    private static final String TAG = MapFragment.class.getSimpleName();
    private static final int LOCATION_REQUEST_CODE = 101;

    // newInstance constructor for creating fragment with arguments
    public static MapFragment newInstance() {
        return new MapFragment();
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_map, container, false);

        requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, LOCATION_REQUEST_CODE);

        mapView = (MapView) myView.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            initGoogleApiClient();
            connectClient();
        }

        calender = Calendar.getInstance();
        hour = calender.get(Calendar.HOUR_OF_DAY);
        storedData = new StoredData();
        auth = new APIAuth(getActivity());

        return myView;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        Log.i(TAG, "onMapReady()");
        googleMap = map;
        int style;

        if (hour > 6 && hour < 18) {
            style = R.raw.day_style;
        } else {
            style = R.raw.night_style;
        }

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                            getActivity(), style));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        } catch (NullPointerException e) {
            Log.e(TAG, "NullPointerException. Error: ", e);
        }

        googleMap.setBuildingsEnabled(false);
        googleMap.getUiSettings().setZoomGesturesEnabled(false);
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setIndoorLevelPickerEnabled(false);
        googleMap.getUiSettings().setTiltGesturesEnabled(false);
        googleMap.getUiSettings().setScrollGesturesEnabled(false);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.setOnMarkerClickListener(this);

        if (ActivityCompat.checkSelfPermission(
                getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                        getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Log.e(TAG, "Permissions denied");
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "onLocationChanged()");

        if (isBetterLocation(location, lastLocation)) {
            lastLocation = location;
            getVehiclesWithinRange();
            checkVehicles();

            LatLng actual = new LatLng(location.getLatitude(), location.getLongitude());

            if (user != null) {
                user.setPosition(actual);
            } else {
                user = googleMap.addMarker(new MarkerOptions()
                        .position(actual)
                        .flat(true)
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_thief)));
            }

            //when the location changes, update the map to the new location
            CameraUpdate center = CameraUpdateFactory.newLatLngZoom(actual, zoomLevel);
            googleMap.moveCamera(center);

            if (Helper.isAnEvent(IConstants.VEHICLE_GENERATE_CHANCE)) {
                Log.d(TAG, "is an Event");

                generateNewVehicle(actual);
            }
        }
    }

    private void initGoogleApiClient() {
        Log.i(TAG, "initGoogleApiClient()");

        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    /** Determines whether one Location reading is better than the current Location fix
     * @param location  The new Location that you want to evaluate
     * @param currentBestLocation  The current Location fix, to which you want to compare the new one
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > INTERVAL_TIME;
        boolean isSignificantlyOlder = timeDelta < -INTERVAL_TIME;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    private double meterDistanceBetweenPoints(double lat_a, double lng_a, double lat_b, double lng_b) {
        double pk = (180.d/Math.PI);

        double a1 = lat_a / pk;
        double a2 = lng_a / pk;
        double b1 = lat_b / pk;
        double b2 = lng_b / pk;

        double t1 = Math.cos(a1)*Math.cos(a2)*Math.cos(b1)*Math.cos(b2);
        double t2 = Math.cos(a1)*Math.sin(a2)*Math.cos(b1)*Math.sin(b2);
        double t3 = Math.sin(a1)*Math.sin(b1);
        double tt = Math.acos(t1 + t2 + t3);

        return 6366000 * tt;
    }

    private LatLng getRandomLocation(LatLng point) {
        //This is to generate 10 random points
        double x0 = point.latitude;
        double y0 = point.longitude;
        double foundLatitude = 0, foundLongitude = 0;

        Random random = new Random();

        double distance = -1;

        while(distance > IConstants.RADIUS || distance == -1) {
            // Convert radius from meters to degrees
            double radiusInDegrees = IConstants.RADIUS / 111000f;

            double u = random.nextDouble();
            double v = random.nextDouble();
            double w = radiusInDegrees * Math.sqrt(u);
            double t = 2 * Math.PI * v;
            double x = w * Math.cos(t);
            double y = w * Math.sin(t);

            // Adjust the x-coordinate for the shrinking of the east-west distances
            double new_x = x / Math.cos(y0);

            foundLatitude = new_x + x0;
            foundLongitude = y + y0;
            distance = meterDistanceBetweenPoints(point.latitude, point.longitude, foundLatitude, foundLongitude);
            Log.d(TAG, "distance: " + distance);
        }

        return new LatLng(foundLatitude, foundLongitude);
    }

    private void generateNewVehicle(LatLng actual) {
        LatLng randomPoint = getRandomLocation(actual);

        RequestParams params = new RequestParams();
        params.put("level", auth.getLevel());
        params.put("longitude", randomPoint.longitude);
        params.put("latitude", randomPoint.latitude);

        RestClient.post("vehicle/create", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                handleJSONObject(response);
            }
        }, getString(R.string.api_key));
    }

    private void getVehiclesWithinRange() {
        RestClient.get("vehicle/active", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                handleVehicles(response);
            }
        }, getString(R.string.api_key));
    }

    @Override
    public void onStart() {
        connectClient();
        super.onStart();
    }

    @Override
    public void onStop() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onPause() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        connectClient();
        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionsResult()");
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), "Unable to show location - permission required", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    protected void requestPermission(String permissionType, int requestCode) {
        Log.i(TAG, "requestPermission()");
        int permission = ContextCompat.checkSelfPermission(getActivity(), permissionType);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{permissionType}, requestCode);
        }
    }

    protected void createLocationRequest() {
        Log.i(TAG, "createLocationRequest()");
        if (mLocationRequest == null) {
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(LOCATION_REFRESH_TIME);
            mLocationRequest.setFastestInterval(LOCATION_FAST_REFRESH_TIME);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }
    }

    protected void startLocationUpdates() {
        Log.i(TAG, "startLocationUpdates()");
        if (ActivityCompat.checkSelfPermission(
                getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                        getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Log.e(TAG, "Permissions denied");
        }
        createLocationRequest();
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    protected void connectClient() {
        GoogleApiAvailability mGoogleApiAvailability = GoogleApiAvailability.getInstance();
        int result = mGoogleApiAvailability.isGooglePlayServicesAvailable(getContext());

        // Connect the client.
        if (result == ConnectionResult.SUCCESS && mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "onConnected()");
        if (mGoogleApiClient.isConnected()) {
            if (ActivityCompat.checkSelfPermission(
                    getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(
                            getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                Log.e(TAG, "Permissions denied");
            }
            // Display the connection status
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            //initialize the location
            if (location != null) {
                onLocationChanged(location);
            }
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "Google_Api_Client:connection_suspended: " + i);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "Google_Api_Client:connection_failed: " + connectionResult.getErrorCode());
    }

    @Override
    public void handleJSONObject(JSONObject object) {
        try {
            int responseCode = object.getInt("responseCode");

            switch (responseCode) {
                case IConstants.VEHICLE_CREATED_SUCCESSFULLY:
                    JSONObject jsonVehicle = object.getJSONObject("vehicle");

                    Vehicle vehicle = new Vehicle(
                            jsonVehicle.getInt("id"),
                            jsonVehicle.getString("make"),
                            jsonVehicle.getString("model"),
                            jsonVehicle.getDouble("longitude"),
                            jsonVehicle.getDouble("latitude"),
                            jsonVehicle.getInt("value"),
                            jsonVehicle.getInt("min_level"),
                            jsonVehicle.getBoolean("is_active"),
                            jsonVehicle.getString("created_at")
                    );

                    storedData.addVehicle(getActivity(), vehicle);
                    addVehicleMarker(vehicle);

                    break;
                case IConstants.AUTHORIZATION_FAIL:
                    Toast.makeText(getActivity(), getString(R.string.authorization_fail), Toast.LENGTH_LONG).show();
                    break;
                case IConstants.API_SQL_FAIL:
                    Toast.makeText(getActivity(), getString(R.string.api_sql_fail), Toast.LENGTH_LONG).show();
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void handleVehicles(JSONObject object) {
        try {
            int responseCode = object.getInt("responseCode");
            if (responseCode == 0) {
                JSONArray vehicles = object.getJSONArray("vehicles");
                Vehicle vehicle;
                double distance;
                boolean isActive;

                for (int i = 0; i < vehicles.length(); i ++) {
                    JSONObject jsonVehicle = vehicles.getJSONObject(i);
                    // Value 1 cannot be converted to boolean
                    isActive = jsonVehicle.getInt("is_active") == 1;

                    vehicle = new Vehicle(
                            jsonVehicle.getInt("id"),
                            jsonVehicle.getString("make"),
                            jsonVehicle.getString("model"),
                            jsonVehicle.getDouble("longitude"),
                            jsonVehicle.getDouble("latitude"),
                            jsonVehicle.getInt("value"),
                            jsonVehicle.getInt("min_level"),
                            isActive,
                            jsonVehicle.getString("created_at")
                    );
                    distance = meterDistanceBetweenPoints(vehicle.getLatitude(), vehicle.getLongitude(),
                            lastLocation.getLatitude(), lastLocation.getLongitude());
                    if (distance <= IConstants.RADIUS) {
                        if (!storedData.containVehicle(getActivity(), vehicle)) {
                            storedData.addVehicle(getActivity(), vehicle);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void checkVehicles() {
        for (Marker marker : mMarkerArray) {
            marker.remove();
        }

        ArrayList<Vehicle> vehicles = storedData.getVehicles(getActivity());
        double distance;
        if (vehicles != null) {
            for (int i = 0; i < vehicles.size(); i++) {
                Vehicle vehicle = vehicles.get(i);
                distance = meterDistanceBetweenPoints(vehicle.getLatitude(), vehicle.getLongitude(),
                        lastLocation.getLatitude(), lastLocation.getLongitude());
                if (distance <= IConstants.RADIUS && !vehicle.timeIsUp() && vehicle.isActive()) {
                    addVehicleMarker(vehicle);
                } else {
                    storedData.removeVehicle(getActivity(), vehicle);
                }
            }
        }
    }

    private void addVehicleMarker(Vehicle vehicle) {
        Marker marker = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(vehicle.getLatitude(), vehicle.getLongitude()))
                .title(vehicle.getMake() + " " + vehicle.getModel())
                .flat(true)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car)));
        marker.setTag(vehicle);

        mMarkerArray.add(marker);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.getTag() != null) {
            Vehicle vehicle = (Vehicle) marker.getTag();
            if (vehicle.timeIsUp()) {
                marker.remove();
                Toast.makeText(getActivity(), getString(R.string.vehicle_is_away), Toast.LENGTH_LONG).show();
                return false;
            }

            Gson gson = new Gson();
            String jsonVehicle = gson.toJson(vehicle);

            VehicleFragment vehicleFragment = new VehicleFragment();
            Bundle bundle = new Bundle();
            bundle.putString("vehicle", jsonVehicle);
            vehicleFragment.setArguments(bundle);

            getActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, vehicleFragment)
                    .commit();
        }
        return false;
    }
}
