package com.parkingoffice.repository;

import com.parkingoffice.model.Rol;
import org.hibernate.Session;
import java.util.List;

public class RolRepository extends GenericRepository<Rol> {

    public RolRepository() {
        super(Rol.class);
    }

    public List<Rol> findAllRoles() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Rol", Rol.class).list();
        }
    }
}
