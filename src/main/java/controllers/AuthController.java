package controllers;

import exceptions.NameAlreadyInUseException;
import exceptions.UnauthorizedUserException;
import exceptions.UnsafePasswordException;
import gui.AuthPanel;
import models.User;
import services.login.ILoginService;
import services.login.LoginService;

import javax.swing.*;
import java.util.function.Consumer;

public class AuthController {

    private final ILoginService loginService;
    private final AuthPanel view;
    private final JFrame frame;
    private       Consumer<User> onSuccess;

    public AuthController()
    {
        view = new AuthPanel();
        frame = new JFrame();
        frame.setContentPane(view.getJPanel());

        loginService = new LoginService();
        view.onLogin(this::login);
        view.onRegistration(this::register);

    }

    private void login(String name, String password)
    {
        if (name.isEmpty() || password.isEmpty()) {
            view.warn("Username and password are required");
            return;
        }
        try {
            User user = loginService.authenticate(name, password);
            onSuccess.accept(user);
            frame.dispose();
        } catch (UnauthorizedUserException e) {
            view.warn("Incorrect username or password!");
        }
    }

    private void register(String name, String password)
    {
        if (name.isEmpty() || password.isEmpty()) {
            view.warn("Username and password are required!");
            return;
        }
        try {
            var user = new User(name, password);
            if (loginService.createUser(user)) {
                onSuccess.accept(user);
                frame.dispose();
            }
        } catch (NameAlreadyInUseException e) {
            view.warn("Someone already uses the name " + name);
        } catch (UnsafePasswordException e) {
            view.warn("Unsafe password!");
            // TODO tell what the password requirements are
            //     also, do it when the user moves the focus away from the password text field
        }
    }

    // This is mostly to insulate this class from the way login/registration
    // is actually handled -- userSession.initialize(user), initializeMainPanel() --
    // because it's bad and it should be changed later
    public void onSuccess(Consumer<User> action)
    {
        this.onSuccess = action;
    }

    public void display()
    {
        frame.pack();
        frame.setVisible(true);
    }

}
