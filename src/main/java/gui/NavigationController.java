package gui;

import javax.swing.event.ChangeListener;

public interface NavigationController {
    public NavigationView getView();
    public NavigationModel getModel();
    public void addChangeListener(ChangeListener listener);
    public void removeChangeListener(ChangeListener listener);
}
