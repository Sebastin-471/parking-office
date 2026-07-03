package com.parkingoffice.repository;

import com.parkingoffice.model.Tarifa;
import org.hibernate.Session;

public class TarifaRepository extends GenericRepository<Tarifa> {

    public TarifaRepository() {
        super(Tarifa.class);
    }

    public Tarifa findActivaByTipoVehiculo(Integer tipoVehiculoId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Tarifa where activa = true and tipoVehiculo.id = :tipoId", Tarifa.class)
                    .setParameter("tipoId", tipoVehiculoId)
                    .uniqueResult();
        }
    }
}
