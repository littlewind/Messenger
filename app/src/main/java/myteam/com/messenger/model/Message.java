package myteam.com.messenger.model;

public class Message {
    private String message;
    private boolean isSeen;
    private String senderID;

    public Message() {
    }

    public Message(String message, boolean isSeen, String senderID) {
        this.message = message;
        this.isSeen = isSeen;
        this.senderID = senderID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }
}
