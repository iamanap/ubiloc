package br.ufg.inf.ubicare.ubiloc.data;

import java.util.List;

/**
 * Created by anapaula on 12/09/17.
 */

public class RoomPojo {

    private String name;
    private float width;
    private float height;
    private List<BeaconPojo> beacons;
    private double[] currentLocation;

    public RoomPojo(String name, float width, float height, List<BeaconPojo> beacons) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.beacons = beacons;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setBeacons(List<BeaconPojo> beacons) {
        this.beacons = beacons;
    }

    public List<BeaconPojo> getBeacons() {
        return beacons;
    }

    public double[] getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(double[] currentLocation) {
        this.currentLocation = currentLocation;
    }
}
