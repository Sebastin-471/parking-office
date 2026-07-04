package com.parkingoffice.controller;

import com.parkingoffice.core.SessionManager;
import com.parkingoffice.model.Movimiento;
import com.parkingoffice.repository.MovimientoRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class HistorialController {

    @FXML private DatePicker dpInicio;
    @FXML private DatePicker dpFin;
    @FXML private TextField txtPlaca;
    @FXML private TextField txtOperador;
    @FXML private Label lblMensaje;

    @FXML private TableView<Movimiento> tblHistorial;
    @FXML private TableColumn<Movimiento, String> colId;
    @FXML private TableColumn<Movimiento, String> colPlaca;
    @FXML private TableColumn<Movimiento, String> colTipo;
    @FXML private TableColumn<Movimiento, String> colFechaIngreso;
    @FXML private TableColumn<Movimiento, String> colFechaSalida;
    @FXML private TableColumn<Movimiento, String> colOperadorIngreso;
    @FXML private TableColumn<Movimiento, String> colOperadorSalida;
    @FXML private TableColumn<Movimiento, String> colTotal;
    @FXML private TableColumn<Movimiento, String> colEstado;

    private MovimientoRepository movimientoRepository;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @FXML
    public void initialize() {
        movimientoRepository = new MovimientoRepository();

        colId.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getId().toString()));
        colPlaca.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getVehiculo().getPlaca()));
        colTipo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getVehiculo().getTipoVehiculo().getNombre()));
        colFechaIngreso.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFechaIngreso().format(formatter)));
        colFechaSalida.setCellValueFactory(cellData -> {
            if (cellData.getValue().getFechaSalida() != null) {
                return new SimpleStringProperty(cellData.getValue().getFechaSalida().format(formatter));
            }
            return new SimpleStringProperty("-");
        });
        colOperadorIngreso.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUsuarioIngreso().getNombreCompleto()));
        colOperadorSalida.setCellValueFactory(cellData -> {
            if (cellData.getValue().getUsuarioSalida() != null) {
                return new SimpleStringProperty(cellData.getValue().getUsuarioSalida().getNombreCompleto());
            }
            return new SimpleStringProperty("-");
        });
        colTotal.setCellValueFactory(cellData -> {
            if (cellData.getValue().getTotalPagar() != null) {
                return new SimpleStringProperty("$" + cellData.getValue().getTotalPagar().toString());
            }
            return new SimpleStringProperty("-");
        });
        colEstado.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEstado()));

        dpInicio.setValue(LocalDate.now().withDayOfMonth(1));
        dpFin.setValue(LocalDate.now());

        buscar();
    }

    @FXML
    private void buscar() {
        if (!SessionManager.getInstance().isAdmin()) {
            lblMensaje.setText("Solo el administrador puede ver el historial.");
            return;
        }

        LocalDate inicio = dpInicio.getValue();
        LocalDate fin = dpFin.getValue();

        if (inicio == null || fin == null) {
            lblMensaje.setText("Seleccione un rango de fechas.");
            return;
        }
        if (inicio.isAfter(fin)) {
            lblMensaje.setText("La fecha de inicio no puede ser posterior a la fecha fin.");
            return;
        }

        LocalDateTime inicioLDT = inicio.atStartOfDay();
        LocalDateTime finLDT = fin.atTime(LocalTime.MAX);

        List<Movimiento> movimientos = movimientoRepository.findAllByFechaIngresoBetween(inicioLDT, finLDT);

        String placaFiltro = txtPlaca.getText().trim().toLowerCase();
        String operadorFiltro = txtOperador.getText().trim().toLowerCase();

        if (!placaFiltro.isEmpty()) {
            movimientos = movimientos.stream()
                    .filter(m -> m.getVehiculo().getPlaca().toLowerCase().contains(placaFiltro))
                    .collect(Collectors.toList());
        }
        if (!operadorFiltro.isEmpty()) {
            movimientos = movimientos.stream()
                    .filter(m -> m.getUsuarioIngreso().getNombreCompleto().toLowerCase().contains(operadorFiltro)
                            || (m.getUsuarioSalida() != null && m.getUsuarioSalida().getNombreCompleto().toLowerCase().contains(operadorFiltro)))
                    .collect(Collectors.toList());
        }

        tblHistorial.setItems(FXCollections.observableArrayList(movimientos));
        lblMensaje.setText("Mostrando " + movimientos.size() + " movimiento(s).");
        lblMensaje.setStyle("-fx-text-fill: #2c3e50;");
    }
}
