package gui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DefaultImageModel implements ImageModel {

    private List<File> images;
    private int currentImage = -1;

    public DefaultImageModel(List<File> images) {
        this.images = new ArrayList<>(images);
    }

    @Override
    public File getImageAt(int index) {
        return images.get(index);
    }

    @Override
    public int size() {
        return images.size();
    }

}
