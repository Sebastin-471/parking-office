package com.parkingoffice.controller;

import com.parkingoffice.core.SessionManager;
import com.parkingoffice.model.EspacioConfig;
import com.parkingoffice.repository.EspacioConfigRepository;
import com.parkingoffice.repository.MovimientoRepository;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class EspaciosController {

    @FXML private TextField txtCapacidad;
    @FXML private Label lblDisponibleActual;
    @FXML private Label lblMensaje;

    private EspacioConfigRepository espacioConfigRepository;
    private MovimientoRepository movimientoRepository;

    @FXML
    public void initialize() {
        espacioConfigRepository = new EspacioConfigRepository();
        movimientoRepository = new MovimientoRepository();

        if (!SessionManager.getInstance().isAdmin()) {
            lblMensaje.setText("Solo el administrador puede gestionar espacios.");
            return;
        }

        cargarConfiguracion();
    }

    private void cargarConfiguracion() {
        EspacioConfig config = espacioConfigRepository.findActivo();
        int capacidad = (config != null) ? config.getCapacidadMaxima() : 0;
        int ocupados = movimientoRepository.findActivos().size();
        int disponibles = Math.max(0, capacidad - ocupados);

        txtCapacidad.setText(String.valueOf(capacidad));
        lblDisponibleActual.setText("Espacios disponibles actualmente: " + disponibles + " / " + capacidad);
        lblMensaje.setText("");
    }

    @FXML
    private void guardar() {
        if (!SessionManager.getInstance().isAdmin()) {
            lblMensaje.setText("Solo el administrador puede modificar la configuración.");
            lblMensaje.setStyle("-fx-text-fill: #e74c3c;");
            return;
        }

        String capacidadStr = txtCapacidad.getText().trim();

        try {
            int capacidad = Integer.parseInt(capacidadStr);

            if (capacidad < 0) {
                lblMensaje.setText("La capacidad no puede ser negativa.");
                lblMensaje.setStyle("-fx-text-fill: #e74c3c;");
                return;
            }

            int ocupados = movimientoRepository.findActivos().size();
            if (capacidad < ocupados) {
                lblMensaje.setText("No se puede reducir la capacidad a un valor menor que los vehículos actualmente en el estacionamiento (" + ocupados + ").");
                lblMensaje.setStyle("-fx-text-fill: #e74c3c;");
                return;
            }

            EspacioConfig config = espacioConfigRepository.findActivo();
            if (config == null) {
                config = new EspacioConfig(capacidad);
            } else {
                config.setCapacidadMaxima(capacidad);
            }

            espacioConfigRepository.update(config);

            lblMensaje.setText("Configuración guardada exitosamente.");
            lblMensaje.setStyle("-fx-text-fill: #2ecc71;");
            cargarConfiguracion();

        } catch (NumberFormatException e) {
            lblMensaje.setText("Ingrese un número válido.");
            lblMensaje.setStyle("-fx-text-fill: #e74c3c;");
        } catch (Exception e) {
            lblMensaje.setText("Error al guardar: " + e.getMessage());
            lblMensaje.setStyle("-fx-text-fill: #e74c3c;");
        }
    }
}