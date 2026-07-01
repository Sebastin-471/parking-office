package com.parkingoffice.repository;

import com.parkingoffice.model.Usuario;
import org.hibernate.Session;

public class UsuarioRepository extends GenericRepository<Usuario> {

    public UsuarioRepository() {
        super(Usuario.class);
    }

    public Usuario findByUsername(String username) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Usuario where username = :username", Usuario.class)
                    .setParameter("username", username)
                    .uniqueResult();
        }
    }
}
