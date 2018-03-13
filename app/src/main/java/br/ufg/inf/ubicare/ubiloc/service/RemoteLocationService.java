package br.ufg.inf.ubicare.ubiloc.service;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;

import com.lemmingapex.trilateration.NonLinearLeastSquaresSolver;
import com.lemmingapex.trilateration.TrilaterationFunction;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.ufg.inf.ubicare.ubiloc.R;
import br.ufg.inf.ubicare.ubiloc.activities.OptionsActivity;
import br.ufg.inf.ubicare.ubiloc.bluetooth.BluetoothDeviceStore;
import br.ufg.inf.ubicare.ubiloc.bluetooth.BluetoothServiceScanner;
import br.ufg.inf.ubicare.ubiloc.bluetooth.BluetoothServiceUtils;
import br.ufg.inf.ubicare.ubiloc.database.Database;
import br.ufg.inf.ubicare.ubiloc.domain.Beacon;
import br.ufg.inf.ubicare.ubiloc.domain.Room;
import br.ufg.inf.ubicare.ubiloc.domain.User;
import uk.co.alt236.bluetoothlelib.device.BluetoothLeDevice;
import uk.co.alt236.bluetoothlelib.device.beacon.BeaconType;
import uk.co.alt236.bluetoothlelib.device.beacon.BeaconUtils;
import uk.co.alt236.bluetoothlelib.device.beacon.ibeacon.IBeaconDevice;

public class RemoteLocationService extends Service {

    private ArrayList<IBeaconDevice> beaconList;
    private BluetoothServiceUtils mBluetoothUtils;
    private BluetoothServiceScanner mScanner;
    private BluetoothDeviceStore mDeviceStore;
    private Database mDatabase;
    private List<Room> localRooms;
    private Room currentRoom;
    private Handler mHandler;

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


        }
    };

    private IBeaconDevice getClosestBeacon() {
        IBeaconDevice closestDevice = beaconList.get(0);
        for (int i = 1; i < beaconList.size(); i++) {
            IBeaconDevice device = beaconList.get(i);
            if (device.getAccuracy() < closestDevice.getAccuracy()) {
                closestDevice = device;
            }
        }
        return closestDevice;
    }

    private Room getBeaconsRoom(IBeaconDevice device) {
        for (Room room: localRooms) {
            for (Beacon beacon: room.getBeacons()) {
                if (beacon.getMacAddress().equals(device.getAddress())) {
                    return room;
                }
            }
        }
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        beaconList = new ArrayList<>();
        localRooms = Room.listAll(Room.class);
        mDeviceStore = new BluetoothDeviceStore();
        mBluetoothUtils = new BluetoothServiceUtils(getApplicationContext());
        mScanner = new BluetoothServiceScanner(mLeScanCallback, mBluetoothUtils);
        mDatabase = Database.with(User.listAll(User.class).get(0).getKey());
        final boolean isBluetoothOn = mBluetoothUtils.isBluetoothOn();
        final boolean isBluetoothLePresent = mBluetoothUtils.isBluetoothLeSupported();
        if (isBluetoothOn && isBluetoothLePresent) {
            mScanner.scanLeDevice(-1, true);
        }

//        HandlerThread thread = new HandlerThread("RemoteLocationService",
//                Process.THREAD_PRIORITY_BACKGROUND);
//        thread.start();

        Intent notificationIntent = new Intent(this, OptionsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("UbiCareLoc")
                .setOngoing(true)
                .setContentText("Enviando localização")
                .setContentIntent(pendingIntent).build();

        startForeground(1337, notification);

        HandlerThread mHandlerThread = new HandlerThread("HandlerThread");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
        mHandler.post(loop);

//        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        Intent inte = new Intent(this, RemoteLocationService.class);
//        PendingIntent pendIntent = PendingIntent.getService(this, 0, inte, 0);
//        alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000, pendIntent);
    }

    private Runnable loop = new Runnable() {
        @Override
        public void run() {
            System.out.println("loop bom");
            calculateAndSendLocation();
            mHandler.postDelayed(this, 1500);
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        calculateAndSendLocation();

        return START_STICKY;
    }

//    private void resetHandler() {
//        mServiceHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        }, 1000);
//    }

    static double[] addElement(double[] a, double e) {
        a  = Arrays.copyOf(a, a.length + 1);
        a[a.length - 1] = e;
        return a;
    }

    static double[][] addFloatElement(double[][] a, double e[]) {
        a  = Arrays.copyOf(a, a.length + 1);
        a[a.length - 1] = e;
        return a;
    }

    private void calculateAndSendLocation() {
        if (beaconList.size() >= 3) {
            currentRoom = getBeaconsRoom(getClosestBeacon());
            if (currentRoom != null) {
                double[][] positions = new double[][]{};
                double[] distances = new double[]{};
                for (IBeaconDevice device : beaconList) {
                    for (Beacon beacon : currentRoom.getBeacons()) {
                        if (device.getAddress().equals(beacon.getMacAddress())) {
                            positions = addFloatElement(positions, new double[]{beacon.getCoordinateX(), beacon.getCoordinateY()});
                            distances = addElement(distances, device.getAccuracy());
                        }
                    }
                }
                if (positions.length > 2 && distances.length > 2) {
                    NonLinearLeastSquaresSolver solver = new NonLinearLeastSquaresSolver(new TrilaterationFunction(positions, distances), new LevenbergMarquardtOptimizer());
                    LeastSquaresOptimizer.Optimum optimum = solver.solve();

                    double[] centroid = optimum.getPoint().toArray();
                    System.out.println("COORDENADAS " + centroid[0] + ", " + centroid[1]);
                    mDatabase.updateUserLocationAtRoomOnServer(Room.listAll(Room.class).get(0).getName(), centroid);
                }
            }
        }

    }
}
