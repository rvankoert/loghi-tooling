package nl.knaw.huc.di.images.layoutds.DAO;

import nl.knaw.huc.di.images.layoutds.models.pim.ApiKey;
import nl.knaw.huc.di.images.layoutds.models.pim.PimUser;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import java.util.Optional;

public class ApiKeyDao extends GenericDAO<ApiKey> {
    public ApiKeyDao() {
        super(ApiKey.class);
    }

    public Optional<ApiKey> getByUserName(Session session, String userName) {
        final CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        final CriteriaQuery<ApiKey> criteriaQuery = criteriaBuilder.createQuery(ApiKey.class);
        final Root<ApiKey> root = criteriaQuery.from(ApiKey.class);
        final Join<ApiKey, PimUser> pimUser = root.join("pimUser");
        criteriaQuery.where(criteriaBuilder.equal(pimUser.get("name"), userName));

        final Query<ApiKey> query = session.createQuery(criteriaQuery);
        return query.getResultList().stream().findFirst();
    }
}
