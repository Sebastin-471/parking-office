package com.parkingoffice.controller;

import com.parkingoffice.App;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class DashboardController {

    @FXML private Label lblWelcome;
    
    // Panel Entrada
    @FXML private TextField txtPlacaEntrada;
    @FXML private ComboBox<String> cmbTipoVehiculo;
    @FXML private ComboBox<String> cmbEspacio;
    @FXML private Label lblMensajeEntrada;

    // Panel Salida
    @FXML private TextField txtPlacaSalida;
    @FXML private VBox pnlDetalleSalida;
    @FXML private Label lblDetalleTiempo;
    @FXML private Label lblDetalleMonto;
    @FXML private Label lblMensajeSalida;

    // Tabla
    @FXML private TableView<?> tblMovimientosActivos;
    @FXML private TableColumn<?, ?> colPlaca;
    @FXML private TableColumn<?, ?> colTipo;
    @FXML private TableColumn<?, ?> colEspacio;
    @FXML private TableColumn<?, ?> colHoraIngreso;

    @FXML
    public void initialize() {
        lblWelcome.setText("Dashboard Principal");
        // Aquí se deberían cargar los datos reales desde la base de datos
        // usando ParkingService, EspacioRepository, etc.
    }

    @FXML
    private void registrarEntrada() {
        lblMensajeEntrada.setText("Funcionalidad de registro de entrada en desarrollo...");
        lblMensajeEntrada.setStyle("-fx-text-fill: #f39c12;");
    }

    @FXML
    private void buscarVehiculoSalida() {
        if (txtPlacaSalida.getText().isEmpty()) {
            lblMensajeSalida.setText("Ingrese una placa válida.");
            lblMensajeSalida.setStyle("-fx-text-fill: #e74c3c;");
            return;
        }
        
        // Simulación
        lblMensajeSalida.setText("");
        pnlDetalleSalida.setVisible(true);
        pnlDetalleSalida.setManaged(true);
        lblDetalleTiempo.setText("Tiempo: 2h 15m");
        lblDetalleMonto.setText("Total a Pagar: $4.50");
    }

    @FXML
    private void procesarSalida() {
        lblMensajeSalida.setText("Salida registrada y ticket generado.");
        lblMensajeSalida.setStyle("-fx-text-fill: #2ecc71;");
        pnlDetalleSalida.setVisible(false);
        pnlDetalleSalida.setManaged(false);
        txtPlacaSalida.clear();
    }

    @FXML
    private void logout() throws IOException {
        App.setRoot("login");
    }

    @FXML
    private void exitApp() {
        Platform.exit();
    }
}
