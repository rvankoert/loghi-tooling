package nl.knaw.huc.di.images.layoutds.DAO.pim;

import nl.knaw.huc.di.images.layoutds.DAO.GenericDAO;
import nl.knaw.huc.di.images.layoutds.models.pim.JobType;
import nl.knaw.huc.di.images.layoutds.models.pim.PimJob;
import nl.knaw.huc.di.images.layoutds.models.pim.PimUser;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PimJobDAO extends GenericDAO<PimJob> {

    public PimJobDAO() {
        super(PimJob.class);
    }

    public PimJob getFirstAvailable(Session session) {
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

        CriteriaQuery<PimJob> criteriaQuery = criteriaBuilder.createQuery(PimJob.class);
        Root<PimJob> jobRoot = criteriaQuery.from(PimJob.class);
        criteriaQuery.select(jobRoot).where(
                criteriaBuilder.and(
                        criteriaBuilder.or(
                                criteriaBuilder.isNull(jobRoot.get("inProgress")),
                                criteriaBuilder.isFalse(jobRoot.get("inProgress"))
                        ),
                        criteriaBuilder.or(
                                criteriaBuilder.isNull(jobRoot.get("readyToRun")),
                                criteriaBuilder.isTrue(jobRoot.get("readyToRun"))
                        ),
                        criteriaBuilder.notEqual(jobRoot.get("jobType"), JobType.SiameseNetwork),
                        criteriaBuilder.notEqual(jobRoot.get("jobType"), JobType.P2PaLA),
                        criteriaBuilder.isNull(jobRoot.get("deleted")),
                        criteriaBuilder.isNull(jobRoot.get("done"))
                )
        ).orderBy(
                criteriaBuilder.asc(jobRoot.get("priority")),
                criteriaBuilder.asc(jobRoot.get("created"))
        );
        TypedQuery<PimJob> query = session.createQuery(criteriaQuery);
        List<PimJob> results = query.setMaxResults(1).getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null;

    }

    public List<PimJob> getNoneDeleted(Session session, PimUser pimUser) {
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

        CriteriaQuery<PimJob> criteriaQuery = criteriaBuilder.createQuery(PimJob.class);
        Root<PimJob> jobRoot = criteriaQuery.from(PimJob.class);
        if (pimUser.isAdmin()) {
            criteriaQuery.select(jobRoot).where(criteriaBuilder.isNull(jobRoot.get("deleted")))
                    .orderBy(criteriaBuilder.desc(jobRoot.get("created")));
        } else {
            criteriaQuery.select(jobRoot).where(criteriaBuilder.and(
                    criteriaBuilder.isNull(jobRoot.get("deleted")),
                    criteriaBuilder.equal(jobRoot.get("pimUser"), pimUser)
            ))
                    .orderBy(criteriaBuilder.desc(jobRoot.get("created")));
        }
        TypedQuery<PimJob> query = session.createQuery(criteriaQuery);

        return query.getResultList();
    }

    public List<PimJob> getDoneJobs(Session session) {
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

        CriteriaQuery<PimJob> criteriaQuery = criteriaBuilder.createQuery(PimJob.class);
        Root<PimJob> jobRoot = criteriaQuery.from(PimJob.class);
        criteriaQuery.select(jobRoot).where(criteriaBuilder.isNotNull(jobRoot.get("done")));
        TypedQuery<PimJob> query = session.createQuery(criteriaQuery);

        return query.getResultList();
    }

    public List<PimJob> getUnfinishedJobs(Session session) {
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

        CriteriaQuery<PimJob> criteriaQuery = criteriaBuilder.createQuery(PimJob.class);
        Root<PimJob> jobRoot = criteriaQuery.from(PimJob.class);
        criteriaQuery.select(jobRoot).where(
                criteriaBuilder.and(
                        criteriaBuilder.isNull(jobRoot.get("done")),
                        criteriaBuilder.isNotNull(jobRoot.get("startDate"))
                )
        );
        TypedQuery<PimJob> query = session.createQuery(criteriaQuery);

        return query.getResultList();
    }

    public int deleteFinishedJobsOlderThan(Session session, int maximumAge) {
        final Transaction transaction = session.beginTransaction();

        final CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        final CriteriaDelete<PimJob> criteria = criteriaBuilder.createCriteriaDelete(PimJob.class);
        final Root<PimJob> root = criteria.from(PimJob.class);

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, -maximumAge);
        final Date minDate = calendar.getTime();

        criteria.where(
                criteriaBuilder.lessThan(root.get("done"), minDate),
                criteriaBuilder.isNull(root.get("nextJob")),
                criteriaBuilder.isNull(root.get("parent")),
                criteriaBuilder.isEmpty(root.get("children"))
        );

        final int removedJobs = session.createQuery(criteria).executeUpdate();

        transaction.commit();

        return removedJobs;
    }

    public List<PimJob> getFinishedJobsOlderThan(Session session, int maximumAge) {

        final CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        final CriteriaQuery<PimJob> criteria = criteriaBuilder.createQuery(PimJob.class);
        final Root<PimJob> root = criteria.from(PimJob.class);

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, -maximumAge);
        final Date minDate = calendar.getTime();

        criteria.where(
                criteriaBuilder.lessThan(root.get("done"), minDate),
                criteriaBuilder.isNull(root.get("nextJob")),
                criteriaBuilder.isNull(root.get("parent")),
                criteriaBuilder.isEmpty(root.get("children"))
        );

        List<PimJob> removableJobs = session.createQuery(criteria).getResultList();

        return removableJobs;
    }


    public PimJob getNoneDeletedByType(Session session, List<JobType> jobtypes) {
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

        CriteriaQuery<PimJob> criteriaQuery = criteriaBuilder.createQuery(PimJob.class);
        Root<PimJob> jobRoot = criteriaQuery.from(PimJob.class);
        CriteriaBuilder.In<JobType> inClause = criteriaBuilder.in(jobRoot.get("jobType"));
        for (JobType jobType : jobtypes) {
            inClause.value(jobType);
        }

        criteriaQuery.select(jobRoot).where(
                criteriaBuilder.and(
                        criteriaBuilder.isNull(jobRoot.get("startDate")),
                        criteriaBuilder.isNull(jobRoot.get("deleted")),
                        inClause
                )
        );
        TypedQuery<PimJob> query = session.createQuery(criteriaQuery);
        query.setMaxResults(1);

        List<PimJob> results = query.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null;
    }
}
