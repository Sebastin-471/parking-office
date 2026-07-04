package com.parkingoffice.repository;

import com.parkingoffice.model.EspacioConfig;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class EspacioConfigRepository extends GenericRepository<EspacioConfig> {

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
            e.printStackTrace();
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
            e.printStackTrace();
        }
    }
}