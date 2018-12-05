package grupp3.iths.se.wineanddineparalell;

public class ReviewInfo {

   // private ImageView profilePic;
    private float user_rating, user_cost;
    private String user_name, user_review;

    public ReviewInfo() {

    }

    public ReviewInfo(String user_name, String user_review, float user_rating, float user_cost) {
        this.user_name = user_name;
        this.user_review = user_review;
        this.user_rating = user_rating;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getUser_review() {
        return user_review;
    }

    public float getUser_rating() {
        return user_rating;
    }

    public float getUser_cost() {
        return user_cost;
    }
}
