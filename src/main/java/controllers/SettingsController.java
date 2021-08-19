package controllers;

import gui.SettingsPanel;
import models.User;
import repositories.user.UserRepository;

import javax.swing.*;

public class SettingsController {

    private final SettingsPanel view;

    public SettingsController(User user) {
        view = new SettingsPanel(user);

        var userRepo = new UserRepository();

        view.onClickSave(() -> {
            var previousPicture = user.getPicture();
            user.setPicture(view.getPicture());
            if (! userRepo.updateUser(user)) {
                view.warnCouldNotUpdate();
                user.setPicture(previousPicture);
            } else {
                view.alertUpdateSuccessful();
            }
        });
    }

    public JPanel getPanel() {
        return view.getPanel();
    }
}
