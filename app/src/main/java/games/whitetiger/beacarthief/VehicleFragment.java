package games.whitetiger.beacarthief;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class VehicleFragment extends Fragment implements ICallback {

    private APIAuth auth;
    private StoredData storedData;
    private Vehicle vehicle;

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vehicle, container, false);
        auth = new APIAuth(getActivity());
        storedData = new StoredData();

        Bundle args = getArguments();
        String jsonVehicle = args.getString("vehicle");
        Gson gson = new Gson();
        vehicle = gson.fromJson(jsonVehicle, Vehicle.class);

        TextView modelText, valueText, levelText;
        Button stealBtn;

        modelText = (TextView) view.findViewById(R.id.model);
        valueText = (TextView) view.findViewById(R.id.value);
        levelText = (TextView) view.findViewById(R.id.level);
        stealBtn = (Button) view.findViewById(R.id.steal);

        modelText.setText(vehicle.getMake() + " " + vehicle.getModel());
        String vehicleValueSource = "<b>Fahrzeugwert:</b> " + Helper.thousandSeperator(vehicle.getValue()) + " €";
        String minLevelSource = "<b>Mindest-Level:</b> " + vehicle.getMinLevel();
        valueText.setText(Helper.fromHtml(vehicleValueSource));
        levelText.setText(Helper.fromHtml(minLevelSource));

        int level = auth.getLevel();

        if (level < vehicle.getMinLevel()) {
            levelText.setTextColor(ContextCompat.getColor(getContext(), R.color.error_msg));
            stealBtn.setEnabled(false);
        }

        stealBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                vehicle.setActive(false);
                storedData.updateVehicle(getActivity(), vehicle);

                // Vehicle successfully stolen
                if (Helper.isAnEvent(99)) {
                    storedData.addUserVehicle(getActivity(), vehicle);
                    stealVehicle();
                // Bust during stealing
                } else {
                    MainActivity activity = (MainActivity) getActivity();
                    activity.changeSelectedItem(R.id.action_map);

                    MapFragment.newInstance();
                }
            }
        });

        return view;
    }

    private void stealVehicle() {
        auth.updateExp(IConstants.VEHICLE_EARN_EXPERIENCE);
        Toast.makeText(getActivity(), "+ " + IConstants.VEHICLE_EARN_EXPERIENCE + " Exp", Toast.LENGTH_LONG).show();
        RequestParams params = new RequestParams();
        params.put("vehicle_id", vehicle.getId());
        params.put("username", auth.getUsername());

        RestClient.post("user/vehicle", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                handleJSONObject(response);
            }
        }, auth.getAPIKey());
    }

    @Override
    public void handleJSONObject(JSONObject object) {
        MainActivity activity = (MainActivity) getActivity();
        activity.changeSelectedItem(R.id.action_garage);

        GarageFragment.newInstance();
    }
}
