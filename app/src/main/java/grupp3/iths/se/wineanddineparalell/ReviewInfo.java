package grupp3.iths.se.wineanddineparalell;

import android.widget.ImageView;

public class ReviewInfo {
    private String username;
    private String comment;
    private String date;
    private int rating;
    private ImageView profile;

    public ReviewInfo(){
        //empty constructor needed
    }

    public ReviewInfo(String username, String comment, String date, int rating, ImageView profile) {
        this.username = username;
        this.comment = comment;
        this.date = date;
        this.rating = rating;
        this.profile = profile;
    }

    public String getUsername() {
        return username;
    }

    public String getComment() {
        return comment;
    }

    public String getDate() {
        return date;
    }

    public int getRating() {
        return rating;
    }

    public ImageView getProfile() {
        return profile;
    }
}
