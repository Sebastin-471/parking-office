package com.parkingoffice.service;

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
        Usuario admin = usuarioRepository.findByUsername("admin");
        if (admin == null) {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Transaction transaction = session.beginTransaction();
                try {
                    Rol rolAdmin = session.createQuery("from Rol where nombre = 'ADMINISTRADOR'", Rol.class).uniqueResult();
                    if (rolAdmin == null) {
                        rolAdmin = new Rol("ADMINISTRADOR");
                        session.persist(rolAdmin);
                    }

                    Usuario nuevoAdmin = new Usuario();
                    nuevoAdmin.setUsername("admin");
                    nuevoAdmin.setPasswordHash(hashPassword("admin123"));
                    nuevoAdmin.setNombreCompleto("Administrador del Sistema");
                    nuevoAdmin.setRol(rolAdmin);
                    nuevoAdmin.setActivo(true);
                    
                    session.persist(nuevoAdmin);
                    transaction.commit();
                    System.out.println("Usuario 'admin' por defecto creado exitosamente.");
                } catch (Exception e) {
                    transaction.rollback();
                    e.printStackTrace();
                }
            }
        }
    }
}
