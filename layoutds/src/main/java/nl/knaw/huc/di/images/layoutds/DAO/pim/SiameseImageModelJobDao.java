package nl.knaw.huc.di.images.layoutds.DAO.pim;

import nl.knaw.huc.di.images.layoutds.DAO.GenericDAO;
import nl.knaw.huc.di.images.layoutds.models.pim.SiameseImageModelJob;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SiameseImageModelJobDao extends GenericDAO<SiameseImageModelJob> {
    public SiameseImageModelJobDao() {
        super(SiameseImageModelJob.class);
    }

    public List<UUID> getUUIDsByModelName(Session session, String modelName, int maxResults) {
        final CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        final CriteriaQuery<UUID> criteriaQuery = criteriaBuilder.createQuery(UUID.class);
        final Root<SiameseImageModelJob> from = criteriaQuery.from(SiameseImageModelJob.class);
        criteriaQuery.where(criteriaBuilder.equal(from.get("modelName"), modelName));
        criteriaQuery.select(from.get("uuid"));
        // order by "random-ish" uuid
        criteriaQuery.orderBy(criteriaBuilder.asc(from.get("uuid")));
        final Query<UUID> query = session.createQuery(criteriaQuery);
        query.setMaxResults(maxResults);
        return query.getResultList();
    }

    public Optional<SiameseImageModelJob> getJob(Session session) {
        final CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        final CriteriaQuery<SiameseImageModelJob> criteriaQuery = criteriaBuilder.createQuery(SiameseImageModelJob.class);
        final Root<SiameseImageModelJob> from = criteriaQuery.from(SiameseImageModelJob.class);
        // bogus statement to make hibernate retrieve any data
        criteriaQuery.where(criteriaBuilder.isNotNull(from.get("id")));
        criteriaQuery.orderBy(criteriaBuilder.asc(from.get("uuid")));

        final Query<SiameseImageModelJob> query = session.createQuery(criteriaQuery);
        query.setMaxResults(1);

        return query.stream().findAny();
    }
}
