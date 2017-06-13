package games.whitetiger.beacarthief;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class VehicleListAdapter extends ArrayAdapter<Vehicle> {

    private Context context;
    List<Vehicle> vehicles;

    public VehicleListAdapter(Context context, List<Vehicle> vehicles) {
        super(context, R.layout.list_item_vehicle, vehicles);
        this.context = context;
        this.vehicles = vehicles;
    }

    private class ViewHolder {
        TextView vehicleModelText;
        TextView vehicleValueText;
        Button sellBtn;
    }

    @Override
    public int getCount() {
        return vehicles.size();
    }

    @Override
    public Vehicle getItem(int position) {
        return vehicles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_vehicle, null);
            holder = new ViewHolder();

            holder.vehicleModelText = (TextView) convertView.findViewById(R.id.model);
            holder.vehicleValueText = (TextView) convertView.findViewById(R.id.value);
            holder.sellBtn = (Button) convertView.findViewById(R.id.sell);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Vehicle vehicle = getItem(position);
        holder.vehicleModelText.setText(vehicle.getMake() + " " + vehicle.getModel());
        holder.vehicleValueText.setText(Helper.fromHtml(
                Helper.thousandSeperator(vehicle.getValue()) + " €"));

        return convertView;
    }

    @Override
    public void add(Vehicle vehicle) {
        super.add(vehicle);
        vehicles.add(vehicle);
        notifyDataSetChanged();
    }

    @Override
    public void remove(Vehicle vehicle) {
        super.remove(vehicle);
        vehicles.remove(vehicle);
        notifyDataSetChanged();
    }
}