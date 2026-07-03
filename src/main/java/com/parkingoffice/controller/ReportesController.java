package com.parkingoffice.controller;

import com.parkingoffice.core.SessionManager;
import com.parkingoffice.model.Movimiento;
import com.parkingoffice.repository.MovimientoRepository;
import com.parkingoffice.service.ReportService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class ReportesController {

    @FXML private DatePicker dpInicio;
    @FXML private DatePicker dpFin;
    @FXML private Label lblTotalVehiculos;
    @FXML private Label lblTotalRecaudado;
    @FXML private Label lblMensaje;
    @FXML private Button btnExportar;

    private MovimientoRepository movimientoRepository;
    private ReportService reportService;

    private LocalDate inicioSeleccionado;
    private LocalDate finSeleccionado;
    private int totalVehiculos;
    private BigDecimal totalRecaudado;

    @FXML
    public void initialize() {
        movimientoRepository = new MovimientoRepository();
        reportService = new ReportService();
        dpInicio.setValue(LocalDate.now());
        dpFin.setValue(LocalDate.now());
    }

    @FXML
    private void generarReporte() {
        if (!SessionManager.getInstance().isAdmin()) {
            lblMensaje.setText("Solo el administrador puede ver reportes.");
            lblMensaje.setStyle("-fx-text-fill: #e74c3c;");
            return;
        }

        inicioSeleccionado = dpInicio.getValue();
        finSeleccionado = dpFin.getValue();

        if (inicioSeleccionado == null || finSeleccionado == null) {
            lblMensaje.setText("Seleccione un rango de fechas válido.");
            lblMensaje.setStyle("-fx-text-fill: #e74c3c;");
            return;
        }

        if (inicioSeleccionado.isAfter(finSeleccionado)) {
            lblMensaje.setText("La fecha de inicio no puede ser posterior a la fecha fin.");
            lblMensaje.setStyle("-fx-text-fill: #e74c3c;");
            return;
        }

        LocalDateTime inicioLDT = inicioSeleccionado.atStartOfDay();
        LocalDateTime finLDT = finSeleccionado.atTime(LocalTime.MAX);

        List<Movimiento> movimientos = movimientoRepository.findFinalizadosPorFecha(inicioLDT, finLDT);

        totalVehiculos = movimientos.size();
        totalRecaudado = movimientos.stream()
                .map(Movimiento::getTotalPagar)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        lblTotalVehiculos.setText(String.valueOf(totalVehiculos));
        lblTotalRecaudado.setText("$" + totalRecaudado.toString());

        btnExportar.setDisable(totalVehiculos == 0);
        lblMensaje.setText("");
    }

    @FXML
    private void exportarPDF() {
        if (!SessionManager.getInstance().isAdmin()) {
            lblMensaje.setText("Solo el administrador puede exportar reportes.");
            lblMensaje.setStyle("-fx-text-fill: #e74c3c;");
            return;
        }

        try {
            String path = "tickets/cierre_caja_" + LocalDate.now().toString() + ".pdf";
            reportService.generarReporteGeneral(inicioSeleccionado, finSeleccionado, totalVehiculos, totalRecaudado, path);
            lblMensaje.setText("Reporte PDF generado exitosamente en: " + path);
            lblMensaje.setStyle("-fx-text-fill: #2ecc71;");
        } catch (Exception e) {
            lblMensaje.setText("Error al generar PDF: " + e.getMessage());
            lblMensaje.setStyle("-fx-text-fill: #e74c3c;");
        }
    }
}
