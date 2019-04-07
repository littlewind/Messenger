package myteam.com.messenger.model;

public class Room {
    private String roomID;

    public Room() {
    }

    public Room(String roomID) {
        this.roomID = roomID;
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }
}
