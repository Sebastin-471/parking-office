package com.parkingoffice.service;

import com.parkingoffice.core.ConfigLoader;
import com.parkingoffice.model.Usuario;
import com.parkingoffice.model.Rol;
import com.parkingoffice.repository.UsuarioRepository;
import com.parkingoffice.repository.HibernateUtil;
import org.mindrot.jbcrypt.BCrypt;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private Usuario loggedInUser;

    public AuthService() {
        this.usuarioRepository = new UsuarioRepository();
    }

    public Usuario login(String username, String password) {
        Usuario usuario = usuarioRepository.findByUsername(username);
        if (usuario != null && usuario.getActivo()) {
            if (BCrypt.checkpw(password, usuario.getPasswordHash())) {
                this.loggedInUser = usuario;
                return usuario;
            }
        }
        return null;
    }

    public void logout() {
        this.loggedInUser = null;
    }

    public Usuario getLoggedInUser() {
        return loggedInUser;
    }

    public String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    public void createDefaultAdminIfNotExists() {
        String defaultUsername = ConfigLoader.getProperty("admin.default.username");
        if (defaultUsername == null) defaultUsername = "admin";

        Usuario admin = usuarioRepository.findByUsername(defaultUsername);
        if (admin == null) {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Transaction transaction = session.beginTransaction();
                try {
                    Rol rolAdmin = session.createQuery("from Rol where nombre = 'ADMINISTRADOR'", Rol.class).uniqueResult();
                    if (rolAdmin == null) {
                        rolAdmin = new Rol("ADMINISTRADOR");
                        session.persist(rolAdmin);
                    }

                    String defaultPassword = ConfigLoader.getProperty("admin.default.password");
                    if (defaultPassword == null) defaultPassword = "admin123";

                    Usuario nuevoAdmin = new Usuario();
                    nuevoAdmin.setUsername(defaultUsername);
                    nuevoAdmin.setPasswordHash(hashPassword(defaultPassword));
                    nuevoAdmin.setNombreCompleto("Administrador del Sistema");
                    nuevoAdmin.setRol(rolAdmin);
                    nuevoAdmin.setActivo(true);
                    
                    session.persist(nuevoAdmin);
                    transaction.commit();
                    System.out.println("Usuario '" + defaultUsername + "' por defecto creado exitosamente.");
                } catch (Exception e) {
                    transaction.rollback();
                    e.printStackTrace();
                }
            }
        }
    }
}
