package com.parkingoffice.repository;

import com.parkingoffice.model.EspacioConfig;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EspacioConfigRepository extends GenericRepository<EspacioConfig> {
    private static final Logger logger = LoggerFactory.getLogger(EspacioConfigRepository.class);

    public EspacioConfigRepository() {
        super(EspacioConfig.class);
    }

    public EspacioConfig findActivo() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from EspacioConfig where activo = true", EspacioConfig.class)
                    .uniqueResult();
        }
    }

    public EspacioConfig getInstance() {
        EspacioConfig config = findActivo();
        if (config == null) {
            config = new EspacioConfig(0);
            save(config);
            return findActivo();
        }
        return config;
    }

    public void save(EspacioConfig entity) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(entity);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error al guardar EspacioConfig.", e);
        }
    }

    public void update(EspacioConfig entity) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(entity);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error al actualizar EspacioConfig.", e);
        }
    }
}