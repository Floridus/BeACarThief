package games.whitetiger.beacarthief;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

public class GarageFragment extends Fragment {

    Activity activity;
    ListView vehicleList;
    VehicleListAdapter vehicleListAdapter;
    List<Vehicle> vehicles;
    StoredData storedData;

    // newInstance constructor for creating fragment with arguments
    public static GarageFragment newInstance() {
        return new GarageFragment();
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_garage, container, false);

        storedData = new StoredData();
        vehicles = storedData.getUserVehicles(getActivity());

        if (vehicles == null || vehicles.size() == 0) {
            showAlert(getResources().getString(R.string.no_vehicles_items),
                    getResources().getString(R.string.no_vehicles_msg));
        } else {
            vehicleList = (ListView) view.findViewById(R.id.list_vehicles);
            vehicleListAdapter = new VehicleListAdapter(activity, vehicles);
            vehicleList.setAdapter(vehicleListAdapter);

            vehicleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {

                }
            });
        }

        return view;
    }

    public void showAlert(String title, String message) {
        if (activity != null && !activity.isFinishing()) {
            AlertDialog alertDialog = new AlertDialog.Builder(activity)
                    .create();
            alertDialog.setTitle(title);
            alertDialog.setMessage(message);
            alertDialog.setCancelable(false);

            // setting OK Button
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            // activity.finish();
                            getFragmentManager().popBackStackImmediate();
                        }
                    });
            alertDialog.show();
        }
    }
}
