package grupp3.iths.se.wineanddineparalell;


import android.widget.ImageView;

public class ItemInfo {
    public ImageView image;
    public String nameOfRest;
    public String dist;
    public String price;
    public String score;

    public ItemInfo(){
        // empty constructor needed
    }


    public ItemInfo(ImageView image, String nameOfRest, String dist, String price, String score) {
        this.image = image;
        this.nameOfRest = nameOfRest;
        this.dist = dist;
        this.price = price;
        this.score = score;
    }

    /**
     * Getters and setters for all fields in ItemInfo class
     * @return
     */
    public ImageView getImage() {
        return image;
    }

    public String getNameOfRest() {
        return nameOfRest;
    }

    public String getDist() {
        return dist;
    }

    public String getPrice() {
        return price;
    }

    public String getScore() {
        return score;
    }
}
