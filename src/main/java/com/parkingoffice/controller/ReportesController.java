package com.parkingoffice.controller;

import com.parkingoffice.core.SessionManager;
import com.parkingoffice.model.Movimiento;
import com.parkingoffice.repository.MovimientoRepository;
import com.parkingoffice.service.ChartService;
import com.parkingoffice.service.ReportService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
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
    @FXML private Button btnImprimir;
    @FXML private ImageView imgGraficoOcupacion;
    @FXML private ImageView imgGraficoRecaudacion;
    @FXML private ImageView imgGraficoDistribucion;

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
        btnImprimir.setDisable(totalVehiculos == 0);
        lblMensaje.setText("");

        ChartService chartService = new ChartService();
        try {
            BufferedImage imgOcupacion = chartService.generarGraficoOcupacionTemporal(movimientos);
            BufferedImage imgRecaudacion = chartService.generarGraficoRecaudacionDiaria(movimientos);
            BufferedImage imgDistribucion = chartService.generarGraficoDistribucionTiposVehiculo(movimientos);

            imgGraficoOcupacion.setImage(bufferedImageToFXImage(imgOcupacion));
            imgGraficoRecaudacion.setImage(bufferedImageToFXImage(imgRecaudacion));
            imgGraficoDistribucion.setImage(bufferedImageToFXImage(imgDistribucion));
        } catch (Exception e) {
            lblMensaje.setText("Error al generar gráficos: " + e.getMessage());
            lblMensaje.setStyle("-fx-text-fill: #e74c3c;");
        }
    }

    private Image bufferedImageToFXImage(BufferedImage bufferedImage) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(bufferedImage, "png", baos);
            try (ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray())) {
                return new Image(bais);
            }
        }
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

    @FXML
    private void imprimirReporte() {
        if (!SessionManager.getInstance().isAdmin()) {
            lblMensaje.setText("Solo el administrador puede imprimir reportes.");
            lblMensaje.setStyle("-fx-text-fill: #e74c3c;");
            return;
        }

        VBox contenidoImprimible = new VBox(10);
        contenidoImprimible.setStyle("-fx-padding: 20px; -fx-background-color: white;");

        Text titulo = new Text("PARKING OFFICE - REPORTE DE CAJA");
        titulo.setFont(javafx.scene.text.Font.font("Arial", 18));

        Text periodo = new Text("Período: " + inicioSeleccionado + " a " + finSeleccionado);
        Text totalVehic = new Text("Total Vehículos Atendidos: " + totalVehiculos);
        Text totalRec = new Text("Total Recaudado: $" + totalRecaudado);

        contenidoImprimible.getChildren().addAll(titulo, periodo, totalVehic, totalRec);

        boolean impreso = reportService.imprimirNodo(contenidoImprimible, lblMensaje.getScene().getWindow());
        if (impreso) {
            lblMensaje.setText("Reporte enviado a impresión.");
            lblMensaje.setStyle("-fx-text-fill: #2ecc71;");
        } else {
            lblMensaje.setText("Impresión cancelada o no disponible.");
            lblMensaje.setStyle("-fx-text-fill: #e74c3c;");
        }
    }
}
