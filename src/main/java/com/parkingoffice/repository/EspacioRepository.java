package com.parkingoffice.repository;

import com.parkingoffice.model.Espacio;
import org.hibernate.Session;
import java.util.List;

public class EspacioRepository extends GenericRepository<Espacio> {

    public EspacioRepository() {
        super(Espacio.class);
    }

    public List<Espacio> findDisponibles() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Espacio where ocupado = false", Espacio.class).list();
        }
    }

    public Espacio findByCodigo(String codigo) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Espacio where codigo = :codigo", Espacio.class)
                    .setParameter("codigo", codigo)
                    .uniqueResult();
        }
    }
}
