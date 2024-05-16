package gui;

import javax.swing.event.ChangeListener;

public interface NavigationModel {
    public boolean next();
    public boolean previous();
    public int getIndex();
    public void addChangeListener(ChangeListener listener);
    public void removeChangeListener(ChangeListener listener);
    public boolean canNavigateForward();
    public boolean canNavigateBack();
}

