package nl.knaw.huc.di.images.layoutds.DAO.pim;

import nl.knaw.huc.di.images.layoutds.DAO.GenericDAO;
import nl.knaw.huc.di.images.layoutds.SessionFactorySingleton;
import nl.knaw.huc.di.images.layoutds.models.pim.P2PaLaJob;
import nl.knaw.huc.di.images.layoutds.models.pim.PimJob;
import nl.knaw.huc.di.images.layoutds.models.pim.SiameseNetworkJob;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class P2PaLAJobDAO extends GenericDAO<P2PaLaJob> {

    public P2PaLAJobDAO() {
        super(P2PaLaJob.class);
    }

    public List<P2PaLaJob> getAllOpen(int limit) {
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

            final CriteriaQuery<P2PaLaJob> criteriaQuery = criteriaBuilder.createQuery(P2PaLaJob.class);
            final Root<P2PaLaJob> root = criteriaQuery.from(P2PaLaJob.class);

            criteriaQuery.select(root).where(
                    criteriaBuilder.and(
                            criteriaBuilder.isNull(root.get("done")),
                            criteriaBuilder.isNull(root.get("started"))
                    )
            );

            final Query<P2PaLaJob> query = session.createQuery(criteriaQuery);
            return query.setMaxResults(limit).getResultList();
        }
    }

    public P2PaLaJob getByWrapper(Session session, PimJob pimJob) {
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

        CriteriaQuery<P2PaLaJob> criteriaQuery = criteriaBuilder.createQuery(P2PaLaJob.class);
        Root<P2PaLaJob> jobRoot = criteriaQuery.from(P2PaLaJob.class);
        criteriaQuery.where(criteriaBuilder.equal(jobRoot.get("wrapper"), pimJob));
        TypedQuery<P2PaLaJob> query = session.createQuery(criteriaQuery);

        return query.getSingleResult();

    }


    public Iterable<P2PaLaJob> getDoneJobs(Session session) {
        final CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        final CriteriaQuery<P2PaLaJob> criteriaQuery = criteriaBuilder.createQuery(P2PaLaJob.class);
        final Root<P2PaLaJob> root = criteriaQuery.from(P2PaLaJob.class);

        criteriaQuery.where(criteriaBuilder.isNotNull(root.get("done")));
        final Query<P2PaLaJob> query = session.createQuery(criteriaQuery);

        return query.getResultList();
    }
}
