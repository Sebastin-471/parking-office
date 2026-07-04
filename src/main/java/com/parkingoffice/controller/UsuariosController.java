package com.parkingoffice.controller;

import com.parkingoffice.core.SessionManager;
import com.parkingoffice.model.Rol;
import com.parkingoffice.model.Usuario;
import com.parkingoffice.repository.RolRepository;
import com.parkingoffice.repository.UsuarioRepository;
import com.parkingoffice.service.AuthService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.util.List;

public class UsuariosController {

    @FXML private TableView<Usuario> tblUsuarios;
    @FXML private TableColumn<Usuario, String> colId;
    @FXML private TableColumn<Usuario, String> colUsername;
    @FXML private TableColumn<Usuario, String> colNombre;
    @FXML private TableColumn<Usuario, String> colRol;
    @FXML private TableColumn<Usuario, String> colActivo;

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private TextField txtNombreCompleto;
    @FXML private ComboBox<Rol> cmbRol;
    @FXML private CheckBox chkActivo;
    @FXML private VBox pnlFormulario;
    @FXML private Button btnEliminar;
    @FXML private Label lblMensaje;

    private UsuarioRepository usuarioRepository;
    private RolRepository rolRepository;
    private AuthService authService;
    private Usuario usuarioEditando;

    @FXML
    public void initialize() {
        usuarioRepository = new UsuarioRepository();
        rolRepository = new RolRepository();
        authService = new AuthService();

        colId.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getId().toString()));
        colUsername.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUsername()));
        colNombre.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombreCompleto()));
        colRol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRol().getNombre()));
        colActivo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getActivo() ? "Sí" : "No"));

        tblUsuarios.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            btnEliminar.setDisable(newVal == null);
        });

        tblUsuarios.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && tblUsuarios.getSelectionModel().getSelectedItem() != null) {
                cargarUsuario(tblUsuarios.getSelectionModel().getSelectedItem());
            }
        });

        cmbRol.setCellFactory(lv -> new ListCell<Rol>() {
            @Override
            protected void updateItem(Rol item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getNombre());
            }
        });
        cmbRol.setButtonCell(new ListCell<Rol>() {
            @Override
            protected void updateItem(Rol item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getNombre());
            }
        });

        cargarUsuarios();
        cargarRoles();
    }

    private void cargarUsuarios() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        tblUsuarios.setItems(FXCollections.observableArrayList(usuarios));
    }

    private void cargarRoles() {
        List<Rol> roles = rolRepository.findAllRoles();
        cmbRol.setItems(FXCollections.observableArrayList(roles));
    }

    @FXML
    private void nuevoUsuario() {
        usuarioEditando = null;
        txtUsername.clear();
        txtPassword.clear();
        txtNombreCompleto.clear();
        cmbRol.setValue(null);
        chkActivo.setSelected(true);
        pnlFormulario.setVisible(true);
        pnlFormulario.setManaged(true);
        txtUsername.requestFocus();
        lblMensaje.setText("");
    }

    private void cargarUsuario(Usuario usuario) {
        usuarioEditando = usuario;
        txtUsername.setText(usuario.getUsername());
        txtPassword.clear();
        txtNombreCompleto.setText(usuario.getNombreCompleto());
        cmbRol.setValue(usuario.getRol());
        chkActivo.setSelected(usuario.getActivo());
        pnlFormulario.setVisible(true);
        pnlFormulario.setManaged(true);
        lblMensaje.setText("");
    }

    @FXML
    private void guardarUsuario() {
        if (!SessionManager.getInstance().isAdmin()) {
            lblMensaje.setText("Solo el administrador puede gestionar usuarios.");
            return;
        }

        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();
        String nombreCompleto = txtNombreCompleto.getText().trim();
        Rol rol = cmbRol.getValue();

        if (username.isEmpty()) {
            lblMensaje.setText("El usuario es obligatorio.");
            return;
        }
        if (nombreCompleto.isEmpty()) {
            lblMensaje.setText("El nombre completo es obligatorio.");
            return;
        }
        if (rol == null) {
            lblMensaje.setText("Seleccione un rol.");
            return;
        }
        if (usuarioEditando == null && password.isEmpty()) {
            lblMensaje.setText("La contraseña es obligatoria para nuevos usuarios.");
            return;
        }
        if (usuarioEditando == null && password.length() < 6) {
            lblMensaje.setText("La contraseña debe tener al menos 6 caracteres.");
            return;
        }

        Usuario currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null && currentUser.getId().equals(usuarioEditando != null ? usuarioEditando.getId() : null)) {
            if (!chkActivo.isSelected()) {
                lblMensaje.setText("No puedes desactivarte a ti mismo.");
                return;
            }
        }

        try {
            Usuario usuario;
            boolean esNuevo = (usuarioEditando == null);

            Usuario existente = usuarioRepository.findByUsername(username);
            if (esNuevo && existente != null) {
                lblMensaje.setText("El nombre de usuario ya existe.");
                return;
            }
            if (!esNuevo && existente != null && !existente.getId().equals(usuarioEditando.getId())) {
                lblMensaje.setText("El nombre de usuario ya está en uso por otro usuario.");
                return;
            }

            if (esNuevo) {
                usuario = new Usuario();
            } else {
                usuario = usuarioEditando;
            }

            usuario.setUsername(username);
            if (!password.isEmpty()) {
                usuario.setPasswordHash(authService.hashPassword(password));
            }
            usuario.setNombreCompleto(nombreCompleto);
            usuario.setRol(rol);
            usuario.setActivo(chkActivo.isSelected());

            if (esNuevo) {
                usuarioRepository.save(usuario);
            } else {
                usuarioRepository.update(usuario);
            }

            lblMensaje.setText("Usuario guardado exitosamente.");
            lblMensaje.setStyle("-fx-text-fill: #2ecc71;");
            pnlFormulario.setVisible(false);
            pnlFormulario.setManaged(false);
            tblUsuarios.getSelectionModel().clearSelection();
            cargarUsuarios();

        } catch (Exception e) {
            String causa = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
            if (causa != null && causa.toLowerCase().contains("duplicate")) {
                lblMensaje.setText("El nombre de usuario ya existe.");
            } else {
                lblMensaje.setText("Error al guardar: " + causa);
            }
            lblMensaje.setStyle("-fx-text-fill: #e74c3c;");
        }
    }

    @FXML
    private void eliminarUsuario() {
        if (!SessionManager.getInstance().isAdmin()) return;

        Usuario seleccionado = tblUsuarios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) return;

        Usuario currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null && currentUser.getId().equals(seleccionado.getId())) {
            lblMensaje.setText("No puedes eliminarte a ti mismo.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText("Eliminar usuario: " + seleccionado.getUsername());
        confirm.setContentText("¿Estás seguro? Esta acción no se puede deshacer.");

        if (confirm.showAndWait().orElse(null) == ButtonType.OK) {
            try {
                usuarioRepository.delete(seleccionado.getId());
                lblMensaje.setText("Usuario eliminado exitosamente.");
                lblMensaje.setStyle("-fx-text-fill: #2ecc71;");
                tblUsuarios.getSelectionModel().clearSelection();
                cargarUsuarios();
            } catch (Exception e) {
                lblMensaje.setText("No se puede eliminar: el usuario tiene movimientos registrados.");
                lblMensaje.setStyle("-fx-text-fill: #e74c3c;");
            }
        }
    }

    @FXML
    private void cancelarEdicion() {
        pnlFormulario.setVisible(false);
        pnlFormulario.setManaged(false);
        usuarioEditando = null;
        lblMensaje.setText("");
    }
}
