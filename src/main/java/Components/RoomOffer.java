package Components;

import java.awt.*;
import java.util.ArrayList;

public class RoomOffer {
    private String roomId;
    private String roomDescription;
    private Double roomPricePerNight;
    private roomTypes roomType;
    private ArrayList<Image> roomImages;

    public RoomOffer(String id, String description, Double pricePerNight, String room, ArrayList<Image> images) {
        roomId = id;
        roomDescription = description;
        roomPricePerNight = pricePerNight;
        roomType = roomTypes.valueOf(room);
        roomImages = images;
    }
    public String getRoomId() {
        return roomId;
    }

    public String getRoomDescription() {
        return roomDescription;
    }

    public Double getRoomPricePerNight() {
        return roomPricePerNight;
    }

    public String getRoomType() {
        return roomType.toString();
    }

    public ArrayList<Image> getRoomImages() {
        return roomImages;
    }
}
