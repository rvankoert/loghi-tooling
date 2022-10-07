package nl.knaw.huc.di.images.layoutds.DAO.pim;

import nl.knaw.huc.di.images.layoutds.DAO.GenericDAO;
import nl.knaw.huc.di.images.layoutds.SessionFactorySingleton;
import nl.knaw.huc.di.images.layoutds.models.pim.P2PaLAModel;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class P2PaLAModelDAO extends GenericDAO<P2PaLAModel> {

    public P2PaLAModelDAO() {
        super(P2PaLAModel.class);
    }

    public boolean exists(String hash) {
        try (Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
            Root<P2PaLAModel> objectRoot = criteriaQuery.from(P2PaLAModel.class);
            criteriaQuery.select(criteriaBuilder.count(objectRoot));

            criteriaQuery.where(criteriaBuilder.equal(objectRoot.get("hash"), hash));

            TypedQuery<Long> query = session.createQuery(criteriaQuery);
            return query.getSingleResult() > 0;
        }

    }

    public List<String> getNames() {
        try (Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

            CriteriaQuery<String> criteriaQuery = criteriaBuilder.createQuery(String.class);
            Root<P2PaLAModel> p2PaLAModelRoot = criteriaQuery.from(P2PaLAModel.class);
            criteriaQuery.select(p2PaLAModelRoot.get("path"));

            Query<String> query = session.createQuery(criteriaQuery);
            return query.getResultList();
        }
    }

    public UUID getUUIDByPath(String path) {
        try (Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

            CriteriaQuery<UUID> criteriaQuery = criteriaBuilder.createQuery(UUID.class);
            Root<P2PaLAModel> p2PaLAModelRoot = criteriaQuery.from(P2PaLAModel.class);
            criteriaQuery.select(p2PaLAModelRoot.get("uuid")).where(
                    criteriaBuilder.equal(p2PaLAModelRoot.get("path"), path)
            );

            Query<UUID> query = session.createQuery(criteriaQuery);
            return query.getSingleResult();
        }

    }
}
