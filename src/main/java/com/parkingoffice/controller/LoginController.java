package com.parkingoffice.controller;

import com.parkingoffice.App;
import com.parkingoffice.service.AuthService;
import com.parkingoffice.core.SessionManager;
import com.parkingoffice.model.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.regex.Pattern;

public class LoginController {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Label lblError;

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,50}$");

    private final AuthService authService = new AuthService();

    @FXML
    private void login() throws IOException {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            lblError.setText("Por favor ingrese usuario y contraseña.");
            return;
        }

        if (!USERNAME_PATTERN.matcher(username).matches()) {
            lblError.setText("Usuario: mínimo 3 caracteres alfanuméricos.");
            return;
        }

        if (password.length() < 6) {
            lblError.setText("Contraseña debe tener al menos 6 caracteres.");
            return;
        }

        Usuario usuario = authService.login(username, password);
        if (usuario != null) {
            SessionManager.getInstance().setCurrentUser(usuario);
            App.setRoot("dashboard");
        } else {
            lblError.setText("Credenciales incorrectas o usuario inactivo.");
        }
    }
}
