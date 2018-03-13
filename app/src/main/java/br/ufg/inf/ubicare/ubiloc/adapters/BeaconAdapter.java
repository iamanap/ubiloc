package br.ufg.inf.ubicare.ubiloc.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.ufg.inf.ubicare.ubiloc.R;
import uk.co.alt236.bluetoothlelib.device.beacon.ibeacon.IBeaconDevice;

public class BeaconAdapter extends RecyclerView.Adapter<BeaconAdapter.ViewHolder> {

    private List<IBeaconDevice> mValues;

    public BeaconAdapter(List<IBeaconDevice> items) {
        mValues = items;
    }

    public void setValues(List<IBeaconDevice> beacons) {
        mValues = beacons;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.beacon_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mView.setTag(position);
        holder.id.setText("Id: " + holder.mItem.getUUID());
        holder.major.setText("Major: " + holder.mItem.getMajor());
        holder.minor.setText("Minor: " + holder.mItem.getMinor());
        holder.macAddress.setText("Mac address: " + holder.mItem.getAddress());
        holder.distance.setText("Distance: " + String.format("%.1f", holder.mItem.getAccuracy()) + "m");
        holder.rssi.setText("RSSI: " + holder.mItem.getRssi());
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public IBeaconDevice mItem;
        public final TextView id;
        public final TextView major;
        public final TextView minor;
        public final TextView macAddress;
        public final TextView distance;
        public final TextView rssi;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            id = (TextView) view.findViewById(R.id.beacon_id);
            major = (TextView) view.findViewById(R.id.beacon_major);
            minor = (TextView) view.findViewById(R.id.beacon_minor);
            macAddress = (TextView) view.findViewById(R.id.beacon_mac_address);
            distance = (TextView) view.findViewById(R.id.beacon_distance);
            rssi = (TextView) view.findViewById(R.id.beacon_rssi);
        }

    }
}
