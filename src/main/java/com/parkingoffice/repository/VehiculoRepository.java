package com.parkingoffice.repository;

import com.parkingoffice.model.Vehiculo;
import org.hibernate.Session;

public class VehiculoRepository extends GenericRepository<Vehiculo> {

    public VehiculoRepository() {
        super(Vehiculo.class);
    }

    public Vehiculo findByPlaca(String placa) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Vehiculo where placa = :placa", Vehiculo.class)
                    .setParameter("placa", placa)
                    .uniqueResult();
        }
    }
}
