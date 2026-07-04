package com.parkingoffice.controller;

import com.parkingoffice.core.SessionManager;
import com.parkingoffice.model.TipoVehiculo;
import com.parkingoffice.repository.TipoVehiculoRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.util.List;

public class TiposVehiculoController {

    @FXML private TableView<TipoVehiculo> tblTipos;
    @FXML private TableColumn<TipoVehiculo, String> colId;
    @FXML private TableColumn<TipoVehiculo, String> colNombre;
    @FXML private TableColumn<TipoVehiculo, String> colDescripcion;

    @FXML private TextField txtNombre;
    @FXML private TextArea txtDescripcion;
    @FXML private VBox pnlFormulario;
    @FXML private Button btnEliminar;
    @FXML private Label lblMensaje;

    private TipoVehiculoRepository tipoVehiculoRepository;
    private TipoVehiculo tipoEditando;

    @FXML
    public void initialize() {
        tipoVehiculoRepository = new TipoVehiculoRepository();

        colId.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getId().toString()));
        colNombre.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombre()));
        colDescripcion.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescripcion()));

        tblTipos.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            btnEliminar.setDisable(newVal == null);
        });

        tblTipos.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && tblTipos.getSelectionModel().getSelectedItem() != null) {
                cargarTipo(tblTipos.getSelectionModel().getSelectedItem());
            }
        });

        cargarTipos();
    }

    private void cargarTipos() {
        List<TipoVehiculo> tipos = tipoVehiculoRepository.findAllTipos();
        tblTipos.setItems(FXCollections.observableArrayList(tipos));
    }

    @FXML
    private void nuevoTipo() {
        if (!SessionManager.getInstance().isAdmin()) return;
        tipoEditando = null;
        txtNombre.clear();
        txtDescripcion.clear();
        pnlFormulario.setVisible(true);
        pnlFormulario.setManaged(true);
        txtNombre.requestFocus();
        lblMensaje.setText("");
    }

    private void cargarTipo(TipoVehiculo tipo) {
        if (!SessionManager.getInstance().isAdmin()) return;
        tipoEditando = tipo;
        txtNombre.setText(tipo.getNombre());
        txtDescripcion.setText(tipo.getDescripcion());
        pnlFormulario.setVisible(true);
        pnlFormulario.setManaged(true);
        lblMensaje.setText("");
    }

    @FXML
    private void guardarTipo() {
        if (!SessionManager.getInstance().isAdmin()) {
            lblMensaje.setText("Solo el administrador puede gestionar tipos de vehículo.");
            return;
        }

        String nombre = txtNombre.getText().trim().toUpperCase();
        String descripcion = txtDescripcion.getText().trim();

        if (nombre.isEmpty()) {
            lblMensaje.setText("El nombre es obligatorio.");
            return;
        }

        List<TipoVehiculo> existentes = tipoVehiculoRepository.findAllTipos();
        boolean duplicado = existentes.stream()
                .anyMatch(t -> t.getNombre().equalsIgnoreCase(nombre)
                        && (tipoEditando == null || !t.getId().equals(tipoEditando.getId())));
        if (duplicado) {
            lblMensaje.setText("Ya existe un tipo de vehículo con ese nombre.");
            return;
        }

        try {
            if (tipoEditando == null) {
                TipoVehiculo nuevo = new TipoVehiculo();
                nuevo.setNombre(nombre);
                nuevo.setDescripcion(descripcion);
                tipoVehiculoRepository.save(nuevo);
            } else {
                tipoEditando.setNombre(nombre);
                tipoEditando.setDescripcion(descripcion);
                tipoVehiculoRepository.update(tipoEditando);
            }

            lblMensaje.setText("Tipo de vehículo guardado exitosamente.");
            lblMensaje.setStyle("-fx-text-fill: #2ecc71;");
            pnlFormulario.setVisible(false);
            pnlFormulario.setManaged(false);
            tblTipos.getSelectionModel().clearSelection();
            cargarTipos();

        } catch (Exception e) {
            lblMensaje.setText("Error al guardar: es posible que el nombre ya exista.");
            lblMensaje.setStyle("-fx-text-fill: #e74c3c;");
        }
    }

    @FXML
    private void eliminarTipo() {
        if (!SessionManager.getInstance().isAdmin()) return;

        TipoVehiculo seleccionado = tblTipos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText("Eliminar tipo: " + seleccionado.getNombre());
        confirm.setContentText("¿Estás seguro? No se podrá eliminar si hay vehículos o tarifas asociados.");

        if (confirm.showAndWait().orElse(null) == ButtonType.OK) {
            try {
                tipoVehiculoRepository.delete(seleccionado.getId());
                lblMensaje.setText("Tipo de vehículo eliminado exitosamente.");
                lblMensaje.setStyle("-fx-text-fill: #2ecc71;");
                tblTipos.getSelectionModel().clearSelection();
                cargarTipos();
            } catch (Exception e) {
                lblMensaje.setText("No se puede eliminar: hay vehículos o tarifas asociados a este tipo.");
                lblMensaje.setStyle("-fx-text-fill: #e74c3c;");
            }
        }
    }

    @FXML
    private void cancelarEdicion() {
        pnlFormulario.setVisible(false);
        pnlFormulario.setManaged(false);
        tipoEditando = null;
        lblMensaje.setText("");
    }
}
