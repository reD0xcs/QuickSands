package gui;

import Components.RButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DefaultNavigationView extends JPanel implements NavigationView {

    private JButton nextButton;
    private JButton previousButton;

    private NavigationViewDelegate delegate;

    public DefaultNavigationView() {
        setLayout(new GridBagLayout());
        nextButton = new JButton(">");
        previousButton = new JButton("<");

        nextButton.setEnabled(false);
        previousButton.setEnabled(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(previousButton, gbc);
        gbc.anchor = GridBagConstraints.EAST;
        add(nextButton, gbc);

        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                NavigationViewDelegate delegate = getDelegate();
                if (delegate != null) {
                    nextButton.setEnabled(delegate.next(DefaultNavigationView.this));
                }
            }
        });
        previousButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                NavigationViewDelegate delegate = getDelegate();
                if (delegate != null) {
                    previousButton.setEnabled(delegate.previous(DefaultNavigationView.this));
                }
            }
        });
    }

    @Override
    public NavigationViewDelegate getDelegate() {
        return delegate;
    }

    @Override
    public JComponent getView() {
        return this;
    }

    @Override
    public void setDelegate(NavigationViewDelegate delegate) {
        this.delegate = delegate;
        stateChanged();
    }

    @Override
    public void stateChanged() {
        nextButton.setEnabled(delegate != null && delegate.canNavigateForward());
        previousButton.setEnabled(delegate != null && delegate.canNavigateBack());
    }

}
