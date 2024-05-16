package gui;

public interface ImageViewController {
    public ImageModel getModel();
    public ImageView getView();
    public void loadImageAt(int index);
}