package myteam.com.messenger.model;

import java.io.Serializable;

public class User implements Serializable {
    private String uID;
    private String username;
    private String search;
    private String imageURL;
    private String status;


    public User(String uID, String username, String imageURL, String status) {
        this.uID = uID;
        this.username = username;
        this.imageURL = imageURL;
        this.status = status;
        this.search = username.toLowerCase();

    }

    public User() {

    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

}
