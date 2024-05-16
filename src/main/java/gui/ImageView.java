package gui;

import javax.swing.*;

public interface ImageView {
    public void loadImageAt(int index);
    public void setDelegate(ImageViewDelegate delegate);
    public ImageViewDelegate getDelegate();
    public JComponent getView();
}
