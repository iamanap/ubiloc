package br.ufg.inf.ubicare.ubiloc.activities;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import com.anthonycr.grant.PermissionsManager;
import com.anthonycr.grant.PermissionsResultAction;

import java.util.ArrayList;
import java.util.Arrays;

import br.ufg.inf.ubicare.ubiloc.bluetooth.BluetoothDeviceStore;
import br.ufg.inf.ubicare.ubiloc.bluetooth.BluetoothScanner;
import br.ufg.inf.ubicare.ubiloc.bluetooth.BluetoothUtils;
import br.ufg.inf.ubicare.ubiloc.R;
import br.ufg.inf.ubicare.ubiloc.adapters.BeaconAdapter;
import uk.co.alt236.bluetoothlelib.device.BluetoothLeDevice;
import uk.co.alt236.bluetoothlelib.device.beacon.BeaconType;
import uk.co.alt236.bluetoothlelib.device.beacon.BeaconUtils;
import uk.co.alt236.bluetoothlelib.device.beacon.ibeacon.IBeaconDevice;

public class MainActivity extends AppCompatActivity {
    private BeaconAdapter mAdapter;
    private ArrayList<IBeaconDevice> beaconList;
    private BluetoothUtils mBluetoothUtils;
    private BluetoothScanner mScanner;
    private BluetoothDeviceStore mDeviceStore;

    private TextView locationTextView;
    private Handler mLocationHandler;
    private boolean handlerReseted = false;

    private final BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {

            final BluetoothLeDevice deviceLe = new BluetoothLeDevice(device, rssi, scanRecord, System.currentTimeMillis());
            if (BeaconUtils.getBeaconType(deviceLe) == BeaconType.IBEACON) {
                mDeviceStore.addDevice(deviceLe);
            }
            beaconList = new ArrayList<>();

            for (final BluetoothLeDevice dev : mDeviceStore.getDeviceList()) {
                final IBeaconDevice iBeacon = new IBeaconDevice(dev);
                beaconList.add(iBeacon);
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (beaconList.size() != 0) {
                        if (beaconList.size() == 3 && !handlerReseted) {
                            resetHandler();
                            handlerReseted = true;
                        }
                        mAdapter.setValues(beaconList);
                    }
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLocationHandler = new Handler();
        beaconList = new ArrayList<>();
        mDeviceStore = new BluetoothDeviceStore();
        mBluetoothUtils = new BluetoothUtils(this);
        mScanner = new BluetoothScanner(mLeScanCallback, mBluetoothUtils);
        askForPermission();

        // Set the adapter
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new BeaconAdapter(beaconList);
        recyclerView.setAdapter(mAdapter);

        locationTextView = (TextView) findViewById(R.id.location);
    }

    private void askForPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, new PermissionsResultAction() {

                        @Override
                        public void onGranted() {
                            final boolean isBluetoothOn = mBluetoothUtils.isBluetoothOn();
                            final boolean isBluetoothLePresent = mBluetoothUtils.isBluetoothLeSupported();
                            mBluetoothUtils.askUserToEnableBluetoothIfNeeded();
                            if (isBluetoothOn && isBluetoothLePresent) {
                                mScanner.scanLeDevice(-1, true);
                            }
                        }

                        @Override
                        public void onDenied(String permission) {
                            Toast.makeText(MainActivity.this,
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
    protected void onDestroy() {
        super.onDestroy();
//        beaconManager.unbind(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if (beaconManager.isBound(this)) beaconManager.setBackgroundMode(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (beaconManager.isBound(this)) beaconManager.setBackgroundMode(false);
    }

    private void resetHandler() {
        mLocationHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                double[][] positions = new double[][] { { 0.0, 2.0 }, { 3.0, 2.0 }, { 1.5, 4.0 }};
                double[] distances = new double[] {};
                for (IBeaconDevice device: beaconList) {
                    if (device.getMajor() == 31423) {
                        distances = addElement(distances, device.getAccuracy());
                    }
                }
                for (IBeaconDevice device: beaconList) {
                    if (device.getMajor() == 32357) {
                        distances = addElement(distances, device.getAccuracy());
                    }
                }
                for (IBeaconDevice device: beaconList) {
                    if (device.getMajor() == 44658) {
                        distances = addElement(distances, device.getAccuracy());
                    }
                }
//                NonLinearLeastSquaresSolver solver = new NonLinearLeastSquaresSolver(new TrilaterationFunction(positions, distances), new LevenbergMarquardtOptimizer());
//                LeastSquaresOptimizer.Optimum optimum = solver.solve();
//
//                double[] centroid = optimum.getPoint().toArray();
//                locationTextView.setText("Localização atual é X: " + String.format("%.2f", centroid[0]) + " , Y: " + String.format("%.2f", centroid[1]));
//                handlerReseted = false;
            }
        }, 3000);
    }

    static double[] addElement(double[] a, double e) {
        a  = Arrays.copyOf(a, a.length + 1);
        a[a.length - 1] = e;
        return a;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
    }
}
