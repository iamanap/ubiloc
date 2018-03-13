package br.ufg.inf.ubicare.ubiloc.data;

/**
 * Created by anapaula on 16/09/17.
 */

public class PositionPojo {
    private double x;
    private double y;
    private String roomName;

    public PositionPojo() {}

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }
}
