package gui;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.HashSet;
import java.util.Set;


public class DefaultNavigationModel implements NavigationModel {

    private final int minValue;
    private final int maxValue;
    private int currentIndex;
    private final Set<ChangeListener> changeListeners;

    public DefaultNavigationModel(int minValue, int maxValue) {
        this.maxValue = maxValue;
        this.minValue = minValue;
        currentIndex = minValue;

        changeListeners = new HashSet<>(25);
    }

    @Override
    public boolean next() {
        if (currentIndex + 1 < maxValue) {
            currentIndex++;
            fireStateChanged();
        }
        return canNavigateForward();
    }

    @Override
    public boolean previous() {
        if (currentIndex >= minValue) {
            currentIndex--;
            fireStateChanged();
        }
        return canNavigateBack();
    }

    @Override
    public int getIndex() {
        return currentIndex;
    }

    @Override
    public void addChangeListener(ChangeListener listener) {
        changeListeners.add(listener);
    }

    @Override
    public void removeChangeListener(ChangeListener listener) {
        changeListeners.remove(listener);
    }

    protected void fireStateChanged() {
        ChangeEvent evt = new ChangeEvent(this);
        for (ChangeListener listener : changeListeners) {
            listener.stateChanged(evt);
        }
    }

    @Override
    public boolean canNavigateForward() {
        return size() > 0 && currentIndex < maxValue - 1;
    }

    @Override
    public boolean canNavigateBack() {
        return size() > 0 && currentIndex > minValue;
    }

    public int size() {
        return maxValue - minValue;
    }

}
