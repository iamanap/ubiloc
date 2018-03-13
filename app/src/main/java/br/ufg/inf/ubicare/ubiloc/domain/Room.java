package br.ufg.inf.ubicare.ubiloc.domain;

import com.orm.SugarRecord;

import java.util.List;

public class Room extends SugarRecord<Room>{
    private String name;
    private float width;
    private float height;
    private double[] currentLocation;

    public Room() {}

    public Room(String name, float width, float height) {
        this.name = name;
        this.width = width;
        this.height = height;
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

    public double[] getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(double[] currentLocation) {
        this.currentLocation = currentLocation;
    }

    public List<Beacon> getBeacons(){
        return Beacon.find(Beacon.class, "room = ?", String.valueOf(this.getId()));
    }
}
