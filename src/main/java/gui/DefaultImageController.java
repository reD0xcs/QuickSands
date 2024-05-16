package gui;

import java.io.File;
import java.util.Objects;

public class DefaultImageController implements ImageViewController {

    private ImageView view;
    private ImageModel model;

    public DefaultImageController(ImageView view, ImageModel model) {
        Objects.requireNonNull(model, "Model can not be null");
        Objects.requireNonNull(view, "View can not be null");
        this.view = view;
        this.model = model;

        view.setDelegate(new ImageViewDelegate() {
            @Override
            public File imageAt(ImageView view, int index) {
                return getModel().getImageAt(index);
            }
        });
    }

    public void setModel(ImageModel model) {
        Objects.requireNonNull(model, "Model can not be null");
        this.model = model;
    }

    @Override
    public ImageModel getModel() {
        return model;
    }

    @Override
    public ImageView getView() {
        return view;
    }

    @Override
    public void loadImageAt(int index) {
        getView().loadImageAt(index);
    }

}
