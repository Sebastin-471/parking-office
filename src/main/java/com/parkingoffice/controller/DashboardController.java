package com.parkingoffice.controller;

import com.parkingoffice.App;
import com.parkingoffice.core.SessionManager;
import com.parkingoffice.model.Movimiento;
import com.parkingoffice.model.TipoVehiculo;
import com.parkingoffice.repository.MovimientoRepository;
import com.parkingoffice.repository.TipoVehiculoRepository;
import com.parkingoffice.service.ParkingService;
import com.parkingoffice.service.ReportService;
import com.parkingoffice.exception.ParkingException;
import com.parkingoffice.exception.ParkingFullException;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Pattern;

public class DashboardController {

    private static final Pattern PLACA_PATTERN = Pattern.compile("^[A-Z0-9]{3}-?[A-Z0-9]{3,4}$");

    @FXML private Label lblWelcome;
    @FXML private Label lblEspaciosDisponibles;
    @FXML private MenuItem menuTarifas;
    @FXML private MenuItem menuReportes;
    @FXML private MenuItem menuUsuarios;
    @FXML private MenuItem menuTiposVehiculo;
    @FXML private MenuItem menuHistorial;
    @FXML private MenuItem menuEspacios;
    
    // Panel Entrada
    @FXML private TextField txtPlacaEntrada;
    @FXML private ComboBox<TipoVehiculo> cmbTipoVehiculo;
    @FXML private Label lblMensajeEntrada;

    // Panel Salida
    @FXML private TextField txtPlacaSalida;
    @FXML private VBox pnlDetalleSalida;
    @FXML private Label lblDetalleTiempo;
    @FXML private Label lblDetalleMonto;
    @FXML private Label lblMensajeSalida;

    // Tabla
    @FXML private TableView<Movimiento> tblMovimientosActivos;
    @FXML private TableColumn<Movimiento, String> colPlaca;
    @FXML private TableColumn<Movimiento, String> colTipo;
    @FXML private TableColumn<Movimiento, String> colHoraIngreso;

    private ParkingService parkingService;
    private ReportService reportService;
    private TipoVehiculoRepository tipoVehiculoRepository;
    private MovimientoRepository movimientoRepository;
    
    private Movimiento movimientoSeleccionadoParaSalida;

    @FXML
    public void initialize() {
        parkingService = new ParkingService();
        reportService = new ReportService();
        tipoVehiculoRepository = new TipoVehiculoRepository();
        movimientoRepository = new MovimientoRepository();

        if (SessionManager.getInstance().getCurrentUser() != null) {
            lblWelcome.setText("Bienvenido, " + SessionManager.getInstance().getCurrentUser().getNombreCompleto());
        }

        if (!SessionManager.getInstance().isAdmin()) {
            menuTarifas.setVisible(false);
            menuReportes.setVisible(false);
            menuUsuarios.setVisible(false);
            menuTiposVehiculo.setVisible(false);
            menuHistorial.setVisible(false);
            menuEspacios.setVisible(false);
            lblEspaciosDisponibles.setVisible(false);
        }

        // Configurar ComboBox
        List<TipoVehiculo> tipos = tipoVehiculoRepository.findAllTipos();
        cmbTipoVehiculo.setItems(FXCollections.observableArrayList(tipos));
        cmbTipoVehiculo.setCellFactory(lv -> new ListCell<TipoVehiculo>() {
            @Override
            protected void updateItem(TipoVehiculo item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getNombre());
            }
        });
        cmbTipoVehiculo.setButtonCell(new ListCell<TipoVehiculo>() {
            @Override
            protected void updateItem(TipoVehiculo item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getNombre());
            }
        });

        // Configurar Tabla
        colPlaca.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getVehiculo().getPlaca()));
        colTipo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getVehiculo().getTipoVehiculo().getNombre()));
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        colHoraIngreso.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFechaIngreso().format(formatter)));
        
        cargarMovimientosActivos();
        asegurarDirectorioTickets();
    }
    
    private void asegurarDirectorioTickets() {
        try {
            Files.createDirectories(Paths.get("tickets"));
        } catch (IOException e) {
            System.err.println("No se pudo crear el directorio de tickets.");
        }
    }

    private void cargarMovimientosActivos() {
        List<Movimiento> activos = movimientoRepository.findActivos();
        tblMovimientosActivos.setItems(FXCollections.observableArrayList(activos));
        actualizarEspaciosDisponibles();
    }

    private void actualizarEspaciosDisponibles() {
        int disponibles = parkingService.getEspaciosDisponibles();
        int capacidad = parkingService.getCapacidadMaxima();
        if (capacidad > 0) {
            lblEspaciosDisponibles.setText("Espacios disponibles: " + disponibles + " / " + capacidad);
            lblEspaciosDisponibles.setStyle("-fx-text-fill: " + (disponibles > 0 ? "#2ecc71;" : "#e74c3c;"));
        } else {
            lblEspaciosDisponibles.setText("Límite de espacios no configurado");
            lblEspaciosDisponibles.setStyle("-fx-text-fill: #f39c12;");
        }
    }

    @FXML
    private void registrarEntrada() {
        String placa = txtPlacaEntrada.getText().trim();
        TipoVehiculo tipo = cmbTipoVehiculo.getValue();

        if (placa.isEmpty() || tipo == null) {
            lblMensajeEntrada.setText("Debe ingresar placa y tipo de vehículo.");
            lblMensajeEntrada.setStyle("-fx-text-fill: #e74c3c;");
            return;
        }

        if (!PLACA_PATTERN.matcher(placa.toUpperCase()).matches()) {
            lblMensajeEntrada.setText("Formato de placa inválido (Ej: ABC-123).");
            lblMensajeEntrada.setStyle("-fx-text-fill: #e74c3c;");
            return;
        }

        try {
            Movimiento mov = parkingService.registrarEntrada(placa.toUpperCase(), tipo, SessionManager.getInstance().getCurrentUser());
            
            // Generar Ticket
            String path = "tickets/entrada_" + mov.getId() + ".pdf";
            reportService.generarTicketEntrada(mov, path);

            lblMensajeEntrada.setText("Entrada registrada. Ticket generado en: " + path);
            lblMensajeEntrada.setStyle("-fx-text-fill: #2ecc71;");
            
            txtPlacaEntrada.clear();
            cmbTipoVehiculo.setValue(null);
            cargarMovimientosActivos();
            
        } catch (ParkingFullException e) {
            lblMensajeEntrada.setText(e.getMessage());
            lblMensajeEntrada.setStyle("-fx-text-fill: #e74c3c;");
        } catch (ParkingException e) {
            lblMensajeEntrada.setText(e.getMessage());
            lblMensajeEntrada.setStyle("-fx-text-fill: #e74c3c;");
        } catch (Exception e) {
            lblMensajeEntrada.setText("Error inesperado: " + e.getMessage());
            lblMensajeEntrada.setStyle("-fx-text-fill: #e74c3c;");
        }
    }

    @FXML
    private void buscarVehiculoSalida() {
        String placa = txtPlacaSalida.getText().trim().toUpperCase();
        if (placa.isEmpty() || !PLACA_PATTERN.matcher(placa).matches()) {
            lblMensajeSalida.setText("Ingrese una placa válida (Ej: ABC-123).");
            lblMensajeSalida.setStyle("-fx-text-fill: #e74c3c;");
            pnlDetalleSalida.setVisible(false);
            pnlDetalleSalida.setManaged(false);
            return;
        }

        // Find active vehicle by placa
        List<Movimiento> activos = movimientoRepository.findActivos();
        movimientoSeleccionadoParaSalida = activos.stream()
                .filter(m -> m.getVehiculo().getPlaca().equalsIgnoreCase(placa))
                .findFirst()
                .orElse(null);
                
        if (movimientoSeleccionadoParaSalida == null) {
            lblMensajeSalida.setText("No se encontró vehículo activo con esa placa.");
            lblMensajeSalida.setStyle("-fx-text-fill: #e74c3c;");
            pnlDetalleSalida.setVisible(false);
            pnlDetalleSalida.setManaged(false);
            return;
        }

        // Mostrar simulación antes de registrar
        Duration dur = Duration.between(movimientoSeleccionadoParaSalida.getFechaIngreso(), java.time.LocalDateTime.now());
        long hours = dur.toHours();
        long minutes = dur.toMinutesPart();
        
        lblMensajeSalida.setText("");
        pnlDetalleSalida.setVisible(true);
        pnlDetalleSalida.setManaged(true);
        lblDetalleTiempo.setText(String.format("Tiempo: %dh %dm", hours, minutes));
        lblDetalleMonto.setText("Total estimado según tarifa: " + movimientoSeleccionadoParaSalida.getTarifaAplicada().getMontoPorHora() + "/h");
    }

    @FXML
    private void procesarSalida() {
        if (movimientoSeleccionadoParaSalida == null) return;
        
        try {
            Movimiento actualizado = parkingService.registrarSalida(movimientoSeleccionadoParaSalida, SessionManager.getInstance().getCurrentUser());
            
            // Generar Comprobante
            String path = "tickets/salida_" + actualizado.getId() + ".pdf";
            reportService.generarComprobanteSalida(actualizado, path);
            
            lblMensajeSalida.setText("Salida procesada. Total: $" + actualizado.getTotalPagar() + ". Ticket en: " + path);
            lblMensajeSalida.setStyle("-fx-text-fill: #2ecc71;");
            
            pnlDetalleSalida.setVisible(false);
            pnlDetalleSalida.setManaged(false);
            txtPlacaSalida.clear();
            cargarMovimientosActivos();
            
        } catch (ParkingException e) {
            lblMensajeSalida.setText(e.getMessage());
            lblMensajeSalida.setStyle("-fx-text-fill: #e74c3c;");
        } catch (Exception e) {
            lblMensajeSalida.setText("Error inesperado: " + e.getMessage());
            lblMensajeSalida.setStyle("-fx-text-fill: #e74c3c;");
        }
    }

    @FXML
    private void logout() throws IOException {
        SessionManager.getInstance().logout();
        App.setRoot("login");
    }

    @FXML
    private void exitApp() {
        Platform.exit();
    }

    @FXML
    private void abrirTarifas() {
        if (!SessionManager.getInstance().isAdmin()) return;
        abrirVentana("/fxml/tarifas.fxml", "Gestión de Tarifas");
    }

    @FXML
    private void abrirReportes() {
        if (!SessionManager.getInstance().isAdmin()) return;
        abrirVentana("/fxml/reportes.fxml", "Reportes y Cierre de Caja");
    }

    @FXML
    private void abrirUsuarios() {
        if (!SessionManager.getInstance().isAdmin()) return;
        abrirVentana("/fxml/usuarios.fxml", "Gestión de Usuarios");
    }

    @FXML
    private void abrirTiposVehiculo() {
        if (!SessionManager.getInstance().isAdmin()) return;
        abrirVentana("/fxml/tiposVehiculo.fxml", "Gestión de Tipos de Vehículo");
    }

    @FXML
    private void abrirHistorial() {
        if (!SessionManager.getInstance().isAdmin()) return;
        abrirVentana("/fxml/historial.fxml", "Historial de Movimientos");
    }

    @FXML
    private void abrirEspacios() {
        if (!SessionManager.getInstance().isAdmin()) return;
        abrirVentana("/fxml/espacios.fxml", "Gestión de Espacios");
    }

    private void abrirVentana(String fxmlPath, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(titulo);
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
