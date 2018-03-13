package br.ufg.inf.ubicare.ubiloc.fragments;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anthonycr.grant.PermissionsManager;
import com.anthonycr.grant.PermissionsResultAction;

import java.util.ArrayList;
import java.util.List;

import br.ufg.inf.ubicare.ubiloc.R;
import br.ufg.inf.ubicare.ubiloc.activities.SetupActivity;
import br.ufg.inf.ubicare.ubiloc.bluetooth.BluetoothDeviceStore;
import br.ufg.inf.ubicare.ubiloc.bluetooth.BluetoothScanner;
import br.ufg.inf.ubicare.ubiloc.bluetooth.BluetoothUtils;
import br.ufg.inf.ubicare.ubiloc.domain.Beacon;
import br.ufg.inf.ubicare.ubiloc.domain.Room;
import uk.co.alt236.bluetoothlelib.device.BluetoothLeDevice;
import uk.co.alt236.bluetoothlelib.device.beacon.BeaconType;
import uk.co.alt236.bluetoothlelib.device.beacon.BeaconUtils;
import uk.co.alt236.bluetoothlelib.device.beacon.ibeacon.IBeaconDevice;

public class SetupRoomBeaconsFragment extends Fragment {

    public interface SetupRoomBeaconsFragmentCallback {
        public void onDoneDetecting(String roomName);
        public void onStartDetecting();
    }

    private ViewHolder mHolder;
    private Room mRoom;
    private List<Beacon> beaconList;
    private int currentBeacon = 1;
    private boolean canDetect = false;
    private SetupRoomBeaconsFragmentCallback mCallback;

    private BluetoothUtils mBluetoothUtils;
    private BluetoothScanner mScanner;
    private BluetoothDeviceStore mDeviceStore;


    private final BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {

            final BluetoothLeDevice deviceLe = new BluetoothLeDevice(device, rssi, scanRecord, System.currentTimeMillis());
            if (BeaconUtils.getBeaconType(deviceLe) == BeaconType.IBEACON) {
                mDeviceStore.addDevice(deviceLe);
            }

            for (final BluetoothLeDevice dev : mDeviceStore.getDeviceList()) {
                final IBeaconDevice iBeacon = new IBeaconDevice(dev);
                if (canDetect && iBeacon.getAccuracy() < 0.5) {
                    boolean exists = false;
                    for (Beacon beacon: beaconList) {
                        if (beacon.getMacAddress().equals(iBeacon.getAddress())) {
                            exists = true;
                        }
                    }
                    if (!exists) {
                        canDetect = false;
                        mHolder.detect.setVisibility(View.VISIBLE);
                        mHolder.progressBar.setVisibility(View.GONE);
                        double[]coordinates;
                        if (currentBeacon == 1) {
                            coordinates = new double[] {mRoom.getWidth()/2, 0};
                        } else if (currentBeacon == 2) {
                            coordinates = new double[] {0, mRoom.getHeight()/2};
                        } else {
                            coordinates = new double[] {mRoom.getWidth(), mRoom.getHeight()/2};
                        }
                        beaconList.add(new Beacon(iBeacon.getUUID(), iBeacon.getMajor(), iBeacon.getMinor(), iBeacon.getAddress(), coordinates[0], coordinates[1], mRoom));
                        currentBeacon++;
                        onDoneAdd();
                    }
                    break;
                }
            }
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (SetupActivity) context;
        } catch (ClassCastException e) {
            Log.e("Custom TAG", "ClassCastException", e);
        }
    }

    @Override
    public void onDetach() {
        mCallback = null;
        super.onDetach();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_setup_room, container, false);

        mBluetoothUtils = new BluetoothUtils(getActivity());
        mDeviceStore = new BluetoothDeviceStore();
        mScanner = new BluetoothScanner(mLeScanCallback, mBluetoothUtils);

        String roomName = getArguments().getString(getString(R.string.intent_room));
        mRoom = Room.find(Room.class, "name = ?", roomName).get(0);
        beaconList = new ArrayList<>();
        initViews(root);
        return root;
    }

    private void initViews(View root) {
        mHolder = new ViewHolder();

        mHolder.beacon1Layout = root.findViewById(R.id.beacon1_layout);
        mHolder.beacon1Layout.bringToFront();

        mHolder.info = (TextView) root.findViewById(R.id.info);
        mHolder.topWidth = (TextView) root.findViewById(R.id.width_top);
        mHolder.bottomWidth = (TextView) root.findViewById(R.id.width_bottom);
        mHolder.leftHeight = (TextView) root.findViewById(R.id.height_left);
        mHolder.rightHeight = (TextView) root.findViewById(R.id.height_right);

        mHolder.topWidth.setText("" + mRoom.getWidth());
        mHolder.bottomWidth.setText("" + mRoom.getWidth());
        mHolder.leftHeight.setText("" + mRoom.getHeight());
        mHolder.rightHeight.setText("" + mRoom.getHeight());

        mHolder.roomBoxLayout = (RelativeLayout) root.findViewById(R.id.box_layout);
        ViewTreeObserver vto = mHolder.roomBoxLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                System.out.println(mHolder.roomBoxLayout.getWidth());
                if (mRoom.getWidth() > mRoom.getHeight()) {
                    mHolder.roomBoxLayout.getLayoutParams().height = mHolder.roomBoxLayout.getWidth() - 200;
                } else {
                    mHolder.roomBoxLayout.getLayoutParams().height = mHolder.roomBoxLayout.getWidth() + 200;
                }
            }
        });

        mHolder.phone1 = (ImageView) root.findViewById(R.id.phone1);
        mHolder.phone2 = (ImageView) root.findViewById(R.id.phone2);
        mHolder.phone3 = (ImageView) root.findViewById(R.id.phone3);

        mHolder.progressBar = (ProgressBar) root.findViewById(R.id.progress);

        mHolder.detect = (Button) root.findViewById(R.id.detect_button);
        mHolder.detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                canDetect = true;
                mHolder.detect.setVisibility(View.GONE);
                mHolder.progressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }

    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    public void onDoneAdd() {
        if (currentBeacon > 3) {
            mRoom.save();
            for (Beacon beacon: beaconList) {
                beacon.save();
            }
            mHolder.detect.setVisibility(View.GONE);
            mCallback.onDoneDetecting(mRoom.getName());
        } else {
            startDetecting();
        }
    }

    private void startDetecting() {
        if (currentBeacon == 1) {
            mCallback.onStartDetecting();
            askForPermission();
            mHolder.info.setText("Para o funcionamento do sistema é necessário detectar cada beacon pertencente ao cômodo. \nEncoste o celular na parede ao lado do 1º\n" +
                    "beacon e dê inicio à detecção:");
            mHolder.phone1.setVisibility(View.VISIBLE);
        } else if (currentBeacon == 2) {
            mHolder.detect.setVisibility(View.VISIBLE);
            mHolder.phone1.setVisibility(View.GONE);
            mHolder.info.setText("Beacon 1 detectado! \nEncoste o celular na parede ao lado do 2º\n" +
                    "beacon e dê inicio à detecção:");
            mHolder.phone2.setVisibility(View.VISIBLE);
        } else if (currentBeacon == 3) {
            mHolder.detect.setVisibility(View.VISIBLE);
            mHolder.phone2.setVisibility(View.GONE);
            mHolder.info.setText("Beacon 2 detectado! \nEncoste o celular na parede ao lado do 3º\n" +
                    "beacon e dê inicio à detecção:");
            mHolder.phone3.setVisibility(View.VISIBLE);
        } else {
            mHolder.detect.setVisibility(View.GONE);
            mHolder.phone3.setVisibility(View.GONE);
            mHolder.info.setText("Todos os beacons foram detectados com sucesso! :D");
            mHolder.progressBar.setVisibility(View.GONE);
        }
    }

    private void askForPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(getActivity(),
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, new PermissionsResultAction() {

                        @Override
                        public void onGranted() {
                            final boolean isBluetoothOn = mBluetoothUtils.isBluetoothOn();
                            final boolean isBluetoothLePresent = mBluetoothUtils.isBluetoothLeSupported();
                            mBluetoothUtils.askUserToEnableBluetoothIfNeeded();
                            if (isBluetoothOn && isBluetoothLePresent) {
                                mScanner.scanLeDevice(-1, true);
                                mHolder.detect.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onDenied(String permission) {
                            Toast.makeText(getActivity(),
                                    "Denied",
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }
                    });
        } else {
            final boolean isBluetoothOn = mBluetoothUtils.isBluetoothOn();
            final boolean isBluetoothLePresent = mBluetoothUtils.isBluetoothLeSupported();
            mBluetoothUtils.askUserToEnableBluetoothIfNeeded();
            if (isBluetoothOn && isBluetoothLePresent) {
                mScanner.scanLeDevice(-1, true);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
    }

    private class ViewHolder {
        TextView topWidth;
        TextView bottomWidth;
        TextView leftHeight;
        TextView rightHeight;
        TextView info;
        RelativeLayout roomBoxLayout;
        View beacon1Layout;
        ImageView phone1;
        ImageView phone2;
        ImageView phone3;
        ProgressBar progressBar;
        Button detect;
    }
}
