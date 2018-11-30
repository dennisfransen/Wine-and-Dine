package grupp3.iths.se.wineanddineparalell;

import android.widget.ImageView;

public class ReviewInfo {

   // private ImageView profilePic;
   // private int ratingStar;
    private String user_name, user_review;

    public ReviewInfo() {

    }

    public ReviewInfo(String user_name, String user_review) {
        this.user_name = user_name;
        this.user_review = user_review;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getUser_review() {
        return user_review;
    }
}
