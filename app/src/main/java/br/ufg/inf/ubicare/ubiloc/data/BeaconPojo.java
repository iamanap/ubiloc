package br.ufg.inf.ubicare.ubiloc.data;

/**
 * Created by anapaula on 12/09/17.
 */

public class BeaconPojo {

    private String uid;
    private int major;
    private int minor;
    private String macAddress;
    private double coordinateX;
    private double coordinateY;

    public BeaconPojo(String uid, int major, int minor, String macAddress, double coordinateX, double coordinateY) {
        this.uid = uid;
        this.major = major;
        this.minor = minor;
        this.macAddress = macAddress;
        this.coordinateX = coordinateX;
        this.coordinateY = coordinateY;
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
