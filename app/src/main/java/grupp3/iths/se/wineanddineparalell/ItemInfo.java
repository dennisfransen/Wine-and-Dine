package grupp3.iths.se.wineanddineparalell;


public class ItemInfo {
    public int image;
    public String nameOfRest;
    public String dist;
    public String price;
    public String score;

    /**
     * Constructor for single item in recyclerview
     * @param drawable
     * @param name
     * @param distance
     * @param price
     * @param score
     */
    public ItemInfo(int drawable, String name, String distance, String price, String score) {
        image = drawable;
        nameOfRest = name;
        dist = distance;
        this.price = price;
        this.score = score;
    }

    /**
     * Getters and setters for all fields in ItemInfo class
     * @return
     */
    public int getImage() {
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

    public void setImage(int image) {
        this.image = image;
    }

    public void setNameOfRest(String nameOfRest) {
        this.nameOfRest = nameOfRest;
    }

    public void setDist(String dist) {
        this.dist = dist;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setScore(String score) {
        this.score = score;
    }
}
