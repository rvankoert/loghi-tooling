package nl.knaw.huc.di.images.layoutds.DAO.pim;

import nl.knaw.huc.di.images.layoutds.DAO.GenericDAO;
import nl.knaw.huc.di.images.layoutds.models.pim.PimJob;
import nl.knaw.huc.di.images.layoutds.models.pim.SiameseNetworkJob;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.stream.Stream;

public class SiameseNetworkJobDAO extends GenericDAO<SiameseNetworkJob> {

    public SiameseNetworkJobDAO() {
        super(SiameseNetworkJob.class);
    }

    public Stream<SiameseNetworkJob> getUnstartedReadyJobs(Session session) {
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

        CriteriaQuery<SiameseNetworkJob> criteriaQuery = criteriaBuilder.createQuery(SiameseNetworkJob.class);
        Root<SiameseNetworkJob> jobRoot = criteriaQuery.from(SiameseNetworkJob.class);
        criteriaQuery.select(jobRoot).where(
                criteriaBuilder.and(
                        criteriaBuilder.isNull(jobRoot.get("startDate")),
                        criteriaBuilder.isTrue(jobRoot.get("ready"))
                )
        );
        TypedQuery<SiameseNetworkJob> query = session.createQuery(criteriaQuery);

        return query.getResultStream();
    }

    public List<SiameseNetworkJob> getJobsOrderedByCreated(Session session) {
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

        CriteriaQuery<SiameseNetworkJob> criteriaQuery = criteriaBuilder.createQuery(SiameseNetworkJob.class);
        Root<SiameseNetworkJob> jobRoot = criteriaQuery.from(SiameseNetworkJob.class);
        criteriaQuery.orderBy(criteriaBuilder.desc(jobRoot.get("created")));
        TypedQuery<SiameseNetworkJob> query = session.createQuery(criteriaQuery);

        return query.getResultList();
    }

    public SiameseNetworkJob getByWrapper(Session session, PimJob pimJob) {
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

        CriteriaQuery<SiameseNetworkJob> criteriaQuery = criteriaBuilder.createQuery(SiameseNetworkJob.class);
        Root<SiameseNetworkJob> jobRoot = criteriaQuery.from(SiameseNetworkJob.class);
        criteriaQuery.where(criteriaBuilder.equal(jobRoot.get("wrapper"), pimJob));
        TypedQuery<SiameseNetworkJob> query = session.createQuery(criteriaQuery);

        return query.getSingleResult();

    }

    public Iterable<SiameseNetworkJob> getDoneJobs(Session session) {
        final CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        final CriteriaQuery<SiameseNetworkJob> criteriaQuery = criteriaBuilder.createQuery(SiameseNetworkJob.class);
        final Root<SiameseNetworkJob> root = criteriaQuery.from(SiameseNetworkJob.class);

        criteriaQuery.where(criteriaBuilder.isNotNull(root.get("done")));
        final Query<SiameseNetworkJob> query = session.createQuery(criteriaQuery);

        return query.getResultList();
    }
}
