package nl.knaw.huc.di.images.layoutds.DAO;

import nl.knaw.huc.di.images.layoutds.SessionFactorySingleton;
import nl.knaw.huc.di.images.layoutds.models.pim.Membership;
import nl.knaw.huc.di.images.layoutds.models.pim.PimGroup;
import nl.knaw.huc.di.images.layoutds.models.pim.PimUser;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Optional;

public class MembershipDAO extends GenericDAO<Membership> {
    public MembershipDAO() {
        super(Membership.class);
    }

    public Optional<Membership> findByGroupAndUser(PimGroup pimGroup, PimUser pimUser) {
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            return this.findByGroupAndUser(session, pimGroup, pimUser);
        }
    }

    public Optional<Membership> findByGroupAndUser(Session session, PimGroup pimGroup, PimUser pimUser) {
        final CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

        final CriteriaQuery<Membership> criteriaQuery = criteriaBuilder.createQuery(Membership.class);
        final Root<Membership> root = criteriaQuery.from(Membership.class);
        criteriaQuery.where(criteriaBuilder.and(
                criteriaBuilder.equal(root.get("pimGroup"), pimGroup),
                criteriaBuilder.equal(root.get("pimUser"), pimUser)
        ));

        final Query<Membership> query = session.createQuery(criteriaQuery);

        return query.stream().findFirst();
    }
}
