package com.parkingoffice.controller;

import com.parkingoffice.App;
import com.parkingoffice.service.AuthService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Label lblError;

    private final AuthService authService = new AuthService();

    @FXML
    private void login() throws IOException {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            lblError.setText("Por favor ingrese usuario y contraseña.");
            return;
        }

        if (authService.login(username, password)) {
            App.setRoot("dashboard");
        } else {
            lblError.setText("Credenciales incorrectas o usuario inactivo.");
        }
    }
}
