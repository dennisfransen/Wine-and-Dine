package grupp3.iths.se.wineanddineparalell;


import android.widget.ImageView;
// Model class for Restaurant
public class ItemInfo {
    public ImageView image;
    private String restaurant_name, restaurant_address, restaurant_phone_number, restaurant_website;
    double restaurant_star_rating, restaurant_cost_rating;
    int restaurant_number_of_reviews,restaurant_number_of_cost_reviews;
    private boolean restaurant_food_type, restaurant_drink_type, restaurant_add_to_wishlist;

    public ItemInfo(){
        // empty constructor needed
    }

    //Constructor needed for FirebaseRecyclerAdapter to add these directly in each card
    public ItemInfo(ImageView image, String restaurant_name, float restaurant_star_rating, float restaurant_cost_rating) {
        this.image = image;
        this.restaurant_name = restaurant_name;
        this.restaurant_star_rating = restaurant_star_rating;
        this.restaurant_cost_rating = restaurant_cost_rating;
    }

    public ItemInfo(ImageView image, String restaurant_name, String restaurant_address, String restaurant_phone_number, String restaurant_website, int restaurant_star_rating, int restaurant_cost_rating, boolean restaurant_food_type, boolean restaurant_drink_type) {
        this.image = image;
        this.restaurant_name = restaurant_name;
        this.restaurant_address = restaurant_address;
        this.restaurant_phone_number = restaurant_phone_number;
        this.restaurant_website = restaurant_website;
        this.restaurant_star_rating = restaurant_star_rating;
        this.restaurant_cost_rating = restaurant_cost_rating;
        this.restaurant_food_type = restaurant_food_type;
        this.restaurant_drink_type = restaurant_drink_type;
    }

    // Getters
    public ImageView getImage() {
        return image;
    }

    public String getRestaurant_name() {
        return restaurant_name;
    }

    public String getRestaurant_phone_number() {
        return restaurant_phone_number;
    }

    public String getRestaurant_website() {
        return restaurant_website;
    }

    public String getRestaurant_address() {
        return restaurant_address;
    }

    public double getRestaurant_star_rating() {
        return restaurant_star_rating;
    }

    public double getRestaurant_cost_rating() {
        return restaurant_cost_rating;
    }

    public boolean isRestaurant_food_type() {
        return restaurant_food_type;
    }

    public boolean isRestaurant_drink_type() {
        return restaurant_drink_type;
    }

    public boolean isRestaurant_add_to_wishlist() {
        return restaurant_add_to_wishlist;
    }

    public void setRestaurant_add_to_wishlist(boolean restaurant_add_to_wishlist) {
        this.restaurant_add_to_wishlist = restaurant_add_to_wishlist;
    }
}
