package gui;

import javax.swing.*;

public interface NavigationView {
    public void setDelegate(NavigationViewDelegate delegate);
    public NavigationViewDelegate getDelegate();
    public JComponent getView();
    public void stateChanged();
}
