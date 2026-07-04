package com.parkingoffice.controller;

import com.parkingoffice.core.SessionManager;
import com.parkingoffice.model.Tarifa;
import com.parkingoffice.model.TipoVehiculo;
import com.parkingoffice.repository.TarifaRepository;
import com.parkingoffice.repository.TipoVehiculoRepository;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.math.BigDecimal;
import java.util.List;

public class TarifasController {

    @FXML private ListView<TipoVehiculo> lstVehiculos;
    @FXML private Label lblVehiculoSeleccionado;
    @FXML private TextField txtMontoHora;
    @FXML private TextField txtMontoMinuto;
    @FXML private Label lblMensaje;

    private TipoVehiculoRepository tipoVehiculoRepository;
    private TarifaRepository tarifaRepository;
    private TipoVehiculo vehiculoSeleccionado;
    private Tarifa tarifaActual;

    @FXML
    public void initialize() {
        tipoVehiculoRepository = new TipoVehiculoRepository();
        tarifaRepository = new TarifaRepository();

        cargarVehiculos();

        lstVehiculos.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                vehiculoSeleccionado = newVal;
                mostrarTarifaActual(newVal);
            }
        });
        
        // Custom cell format for ListView
        lstVehiculos.setCellFactory(lv -> new ListCell<TipoVehiculo>() {
            @Override
            protected void updateItem(TipoVehiculo item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getNombre());
            }
        });
    }

    private void cargarVehiculos() {
        List<TipoVehiculo> tipos = tipoVehiculoRepository.findAllTipos();
        lstVehiculos.setItems(FXCollections.observableArrayList(tipos));
    }

    private void mostrarTarifaActual(TipoVehiculo tipo) {
        lblVehiculoSeleccionado.setText(tipo.getNombre() + " - " + tipo.getDescripcion());
        tarifaActual = tarifaRepository.findActivaByTipoVehiculo(tipo.getId());
        
        if (tarifaActual != null) {
            txtMontoHora.setText(tarifaActual.getMontoPorHora().toString());
            txtMontoMinuto.setText(tarifaActual.getMontoPorMinuto().toString());
        } else {
            txtMontoHora.setText("0.00");
            txtMontoMinuto.setText("0.00");
        }
        lblMensaje.setText("");
    }

    @FXML
    private void guardarTarifa() {
        if (!SessionManager.getInstance().isAdmin()) {
            lblMensaje.setText("Solo el administrador puede modificar tarifas.");
            lblMensaje.setStyle("-fx-text-fill: #e74c3c;");
            return;
        }

        if (vehiculoSeleccionado == null) {
            lblMensaje.setText("Seleccione un vehículo primero.");
            lblMensaje.setStyle("-fx-text-fill: #e74c3c;");
            return;
        }

        try {
            BigDecimal mHora = new BigDecimal(txtMontoHora.getText());
            BigDecimal mMinuto = new BigDecimal(txtMontoMinuto.getText());

            if (mHora.compareTo(BigDecimal.ZERO) <= 0 || mMinuto.compareTo(BigDecimal.ZERO) <= 0) {
                lblMensaje.setText("Los montos deben ser mayores a cero.");
                lblMensaje.setStyle("-fx-text-fill: #e74c3c;");
                return;
            }

            if (tarifaActual != null) {
                tarifaActual.setActiva(false);
                tarifaRepository.update(tarifaActual);
            }

            Tarifa nuevaTarifa = new Tarifa();
            nuevaTarifa.setTipoVehiculo(vehiculoSeleccionado);
            nuevaTarifa.setMontoPorHora(mHora);
            nuevaTarifa.setMontoPorMinuto(mMinuto);
            nuevaTarifa.setActiva(true);

            tarifaRepository.save(nuevaTarifa);
            
            tarifaActual = nuevaTarifa;
            lblMensaje.setText("Tarifa actualizada exitosamente.");
            lblMensaje.setStyle("-fx-text-fill: #2ecc71;");
            
        } catch (Exception e) {
            lblMensaje.setText("Error: Verifique que los montos sean números válidos.");
            lblMensaje.setStyle("-fx-text-fill: #e74c3c;");
        }
    }
}
