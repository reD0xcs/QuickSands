package Components;

import java.awt.*;
import java.util.ArrayList;

public class HotelOffer {
    private String hotelName;
    private String hoteldescription;
    private Double hotelprice;
    private ArrayList<Image> hotelImages;

    public HotelOffer(String name, String description, Double price, ArrayList<Image> images) {
        hotelName = name;
        hoteldescription = description;
        hotelprice = price;
        hotelImages = images;
    }
    public String getHotelName() {
        return hotelName;
    }
    public String getHoteldescription() {
        return hoteldescription;
    }
    public Double getHotelprice() {
        return hotelprice;
    }
    public ArrayList<Image> getHotelImages() {
        return hotelImages;
    }
}
