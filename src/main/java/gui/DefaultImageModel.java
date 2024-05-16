package gui;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DefaultImageModel implements ImageModel {

    private List<Image> images;
    private int currentImage = -1;

    public DefaultImageModel(List<Image> images) {
        this.images = new ArrayList<>(images);
    }

    @Override
    public Image getImageAt(int index) {
        return images.get(index);
    }

    @Override
    public int size() {
        return images.size();
    }

}
