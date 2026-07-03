package com.parkingoffice.repository;

import com.parkingoffice.model.TipoVehiculo;
import org.hibernate.Session;
import java.util.List;

public class TipoVehiculoRepository extends GenericRepository<TipoVehiculo> {

    public TipoVehiculoRepository() {
        super(TipoVehiculo.class);
    }

    public List<TipoVehiculo> findAllTipos() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from TipoVehiculo", TipoVehiculo.class).list();
        }
    }
}
