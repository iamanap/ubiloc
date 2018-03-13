package br.ufg.inf.ubicare.ubiloc.domain;

import com.orm.SugarRecord;

public class Beacon extends SugarRecord<Beacon> {
    private String uid;
    private int major;
    private int minor;
    private String macAddress;
    private double coordinateX;
    private double coordinateY;
    private Room room;

    public Beacon() {}

    public Beacon(String uid, int major, int minor, String macAddress, double coordinateX, double coordinateY, Room room) {
        this.uid = uid;
        this.major = major;
        this.minor = minor;
        this.macAddress = macAddress;
        this.coordinateX = coordinateX;
        this.coordinateY = coordinateY;
        this.room = room;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public double getCoordinateX() {
        return coordinateX;
    }

    public void setCoordinateX(double coordinateX) {
        this.coordinateX = coordinateX;
    }

    public double getCoordinateY() {
        return coordinateY;
    }

    public void setCoordinateY(double coordinateY) {
        this.coordinateY = coordinateY;
    }
}
