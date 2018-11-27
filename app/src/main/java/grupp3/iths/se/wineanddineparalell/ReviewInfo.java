package grupp3.iths.se.wineanddineparalell;

import android.widget.ImageView;

public class ReviewInfo {

    private ImageView profilePic;
    private int ratingStar;
    private String userName, commet, commetDate;

    public ReviewInfo() {

    }

    public ReviewInfo(ImageView profilePic, int ratingStar, String userName, String commet, String commetDate) {
        this.profilePic = profilePic;
        this.ratingStar = ratingStar;
        this.userName = userName;
        this.commet = commet;
        this.commetDate = commetDate;
    }

    public ImageView getProfilePic() {
        return profilePic;
    }

    public int getRatingStar() {
        return ratingStar;
    }

    public String getUserName() {
        return userName;
    }

    public String getCommet() {
        return commet;
    }

    public String getCommetDate() {
        return commetDate;
    }
}
