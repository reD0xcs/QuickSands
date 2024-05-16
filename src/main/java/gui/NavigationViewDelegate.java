package gui;

public interface NavigationViewDelegate {
    public boolean next(NavigationView view);
    public boolean previous(NavigationView view);
    public boolean canNavigateForward();
    public boolean canNavigateBack();
}
