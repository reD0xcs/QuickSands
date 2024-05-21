package Components;

import java.awt.*;
import java.util.ArrayList;

public class HotelOffer {
    private String hotelName;
    private String hoteldescription;
    private Double hotelprice;
    private ArrayList<Image> hotelImages;
    private String hotelLocation;

    public HotelOffer(String name, String description, Double price, ArrayList<Image> images, String location) {
        hotelName = name;
        hoteldescription = description;
        hotelprice = price;
        hotelImages = images;
        hotelLocation = location;
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
    public String getHotelLocation() {
        return hotelLocation;
    }
}
