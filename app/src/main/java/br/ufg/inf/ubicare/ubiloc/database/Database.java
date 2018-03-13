package br.ufg.inf.ubicare.ubiloc.database;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufg.inf.ubicare.ubiloc.data.PositionPojo;
import br.ufg.inf.ubicare.ubiloc.domain.Beacon;
import br.ufg.inf.ubicare.ubiloc.data.BeaconPojo;
import br.ufg.inf.ubicare.ubiloc.domain.Room;
import br.ufg.inf.ubicare.ubiloc.data.RoomPojo;

//
public class Database/* extends SQLiteOpenHelper*/ {
    private static Database sInstance;

    private String mUserKey;

    private Database(String key) {
        mUserKey = key;
    }

    public static Database with(String key) {
        if (sInstance == null) {
            sInstance = new Database(key);
        }
        return sInstance;
    }

    public String getKey() {
        return mUserKey;
    }

    public void addRoomsToServer(List<Room> rooms) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();

        for (Room room: rooms) {
            List<BeaconPojo> beaconPojos = new ArrayList<>();
            for (Beacon beacon: room.getBeacons()) {
                beaconPojos.add(new BeaconPojo(beacon.getUid(), beacon.getMajor(), beacon.getMinor(), beacon.getMacAddress(), beacon.getCoordinateX(), beacon.getCoordinateY()));
            }
            RoomPojo pojo = new RoomPojo(room.getName(), room.getWidth(), room.getHeight(), beaconPojos);
            ref.child(mUserKey).child("rooms").child(pojo.getName()).setValue(pojo);
        }
    }

    public void updateUserLocationAtRoomOnServer(String roomName, double[] coordinates) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();

        HashMap<String, Object> positionValues = new HashMap<>();
        positionValues.put("roomName", roomName);
        positionValues.put("x", coordinates[0]);
        positionValues.put("y", coordinates[1]);

        Map<String, Object> coordinateUpdate = new HashMap<>();
        coordinateUpdate.put("/" + mUserKey + "/position", positionValues);

        ref.updateChildren(coordinateUpdate);
    }

    public void getRoomsFromServer() {

    }

    public void getPositionFromServer(ValueEventListener listener) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();

        ref.child(mUserKey).child("position").addValueEventListener(listener);
    }

}
