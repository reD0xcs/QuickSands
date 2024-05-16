package gui;


import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class DefaultNavigationController implements NavigationController{

    private NavigationView view;
    private NavigationModel model;

    private ChangeListener modelChangeListener;
    private final Set<ChangeListener> changeListeners;

    public DefaultNavigationController(NavigationView view, NavigationModel model) {
        Objects.requireNonNull(model, "Model can not be null");
        Objects.requireNonNull(view, "View can not be null");
        this.view = view;
        this.model = model;

        view.setDelegate(new NavigationViewDelegate() {
            @Override
            public boolean next(NavigationView view) {
                return getModel().next();
            }

            @Override
            public boolean previous(NavigationView view) {
                return getModel().previous();
            }

            @Override
            public boolean canNavigateForward() {
                NavigationModel model = getModel();
                return model.canNavigateForward();
            }

            @Override
            public boolean canNavigateBack() {
                NavigationModel model = getModel();
                return model.canNavigateBack();
            }
        });

        changeListeners = new HashSet<>(25);

        modelChangeListener = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                getView().stateChanged();
                fireStateChanged();
            }
        };
        this.model.addChangeListener(modelChangeListener);
        getView().stateChanged();
    }

    @Override
    public NavigationView getView() {
        return view;
    }

    @Override
    public NavigationModel getModel() {
        return model;
    }

    protected void setModel(NavigationModel model) {
        this.model.removeChangeListener(modelChangeListener);
        this.model = model;
        this.model.addChangeListener(modelChangeListener);
        getView().stateChanged();
        fireStateChanged();
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

}
