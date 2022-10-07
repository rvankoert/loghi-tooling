package nl.knaw.huc.di.images.layoutds.DAO;

import nl.knaw.huc.di.images.layoutds.models.pim.Membership;
import nl.knaw.huc.di.images.layoutds.models.pim.PimGroup;
import nl.knaw.huc.di.images.layoutds.models.pim.PimUser;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import java.util.stream.Stream;

public class PimGroupDAO extends GenericDAO<PimGroup> {

    public static final Logger LOG = LoggerFactory.getLogger(PimGroupDAO.class);

    public PimGroupDAO() {
        super(PimGroup.class);
    }

    public Stream<PimGroup> getAutoCompleteForAdmin(Session session, String filter, int limit, int skip) {
        final CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        final CriteriaQuery<PimGroup> criteriaQuery = criteriaBuilder.createQuery(PimGroup.class);

        final Root<PimGroup> root = criteriaQuery.from(PimGroup.class);

        criteriaQuery.where(criteriaBuilder.like(root.get("name"), "%" + filter + "%"));

        final Query<PimGroup> query = session.createQuery(criteriaQuery);
        query.setMaxResults(limit);
        query.setFirstResult(skip);

        return query.getResultStream();
    }

    public Stream<PimGroup> getAutoComplete(Session session, PimUser pimUser, String filter, int limit, int skip) {
        final CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        final CriteriaQuery<PimGroup> criteriaQuery = criteriaBuilder.createQuery(PimGroup.class);

        final Root<PimGroup> root = criteriaQuery.from(PimGroup.class);
        final Join<PimGroup, Membership> memberships = root.join("memberships");
        final Join<Membership, PimUser> user = memberships.join("pimUser");

        criteriaQuery.where(criteriaBuilder.and(
                criteriaBuilder.like(root.get("name"), "%" + filter + "%"),
                criteriaBuilder.equal(user.get("id"), pimUser.getId())
        ));

        final Query<PimGroup> query = session.createQuery(criteriaQuery);
        query.setMaxResults(limit);
        query.setFirstResult(skip);

        return query.getResultStream();
    }
}
