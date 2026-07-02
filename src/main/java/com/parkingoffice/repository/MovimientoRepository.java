package com.parkingoffice.repository;

import com.parkingoffice.model.Movimiento;
import org.hibernate.Session;
import java.util.List;
import java.time.LocalDateTime;

public class MovimientoRepository extends GenericRepository<Movimiento> {

    public MovimientoRepository() {
        super(Movimiento.class);
    }

    public List<Movimiento> findActivos() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Movimiento where estado = 'ACTIVO'", Movimiento.class).list();
        }
    }

    public Movimiento findActivoByVehiculo(Integer vehiculoId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Movimiento where estado = 'ACTIVO' and vehiculo.id = :vehiculoId", Movimiento.class)
                    .setParameter("vehiculoId", vehiculoId)
                    .uniqueResult();
        }
    }

    public List<Movimiento> findFinalizadosPorFecha(LocalDateTime inicio, LocalDateTime fin) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Movimiento where estado = 'FINALIZADO' and fechaSalida >= :inicio and fechaSalida <= :fin", Movimiento.class)
                    .setParameter("inicio", inicio)
                    .setParameter("fin", fin)
                    .list();
        }
    }
}
