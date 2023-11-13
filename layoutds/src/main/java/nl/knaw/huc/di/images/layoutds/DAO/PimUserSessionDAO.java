package nl.knaw.huc.di.images.layoutds.DAO;

import nl.knaw.huc.di.images.layoutds.SessionFactorySingleton;
import nl.knaw.huc.di.images.layoutds.models.pim.PimUserSession;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

public class PimUserSessionDAO extends GenericDAO<PimUserSession> {
    public PimUserSessionDAO() {
        super(PimUserSession.class);
    }

    public Optional<PimUserSession> getBySessionId(String sessionId) {
        try (Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();


            Optional<PimUserSession> userSession = getBySessionId(session, sessionId);

            transaction.commit();
            session.close();

            return userSession;
        }
    }

    public Optional<PimUserSession> getBySessionId(Session session, String sessionId) {
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<PimUserSession> criteriaQuery = criteriaBuilder.createQuery(PimUserSession.class);
        Root<PimUserSession> root = criteriaQuery.from(PimUserSession.class);
        criteriaQuery.where(criteriaBuilder.equal(root.get("sessionId"), sessionId));
        criteriaQuery.select(root);

        Query<PimUserSession> query = session.createQuery(criteriaQuery);
        return query.stream().findFirst();
    }

    /**
     * @param maximumSessionAge maximum age in hours
     */
    public void deleteOldSessions(Session session, int maximumSessionAge) {
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaDelete<PimUserSession> delete = criteriaBuilder.createCriteriaDelete(PimUserSession.class);
        Root<PimUserSession> root = delete.from(PimUserSession.class);

        delete.where(criteriaBuilder.lessThan(root.get("lastActive"), Timestamp.valueOf(LocalDateTime.now().minusHours(maximumSessionAge))));

        Query deleteQuery = session.createQuery(delete);
        deleteQuery.executeUpdate();
    }
}
