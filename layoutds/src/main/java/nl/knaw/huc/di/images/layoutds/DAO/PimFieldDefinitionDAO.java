package nl.knaw.huc.di.images.layoutds.DAO;

import nl.knaw.huc.di.images.layoutds.SessionFactorySingleton;
import nl.knaw.huc.di.images.layoutds.models.pim.PimFieldDefinition;
import nl.knaw.huc.di.images.layoutds.models.pim.PimFieldSet;
import nl.knaw.huc.di.images.layoutds.models.pim.PimUser;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class PimFieldDefinitionDAO extends GenericDAO<PimFieldDefinition> {

    public PimFieldDefinitionDAO() {
        super(PimFieldDefinition.class);
    }

    public PimFieldDefinition getByUUID(UUID uuid) {
        Session session = SessionFactorySingleton.getSessionFactory().openSession();

        Transaction transaction = session.beginTransaction();
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

        CriteriaQuery<PimFieldDefinition> criteriaQuery = criteriaBuilder.createQuery(PimFieldDefinition.class);
        Root<PimFieldDefinition> pimFieldDefinitionRoot = criteriaQuery.from(PimFieldDefinition.class);

        criteriaQuery.where(criteriaBuilder.equal(pimFieldDefinitionRoot.get("uuid"), uuid));
        TypedQuery<PimFieldDefinition> query = session.createQuery(criteriaQuery);
        List<PimFieldDefinition> pimFieldDefinitions = query.getResultList();

        transaction.commit();
        session.close();
        if (pimFieldDefinitions.size() == 1) {
            return pimFieldDefinitions.get(0);
        } else {
            return null;
        }
    }

    public Stream<PimFieldDefinition> getByFieldSet(Session session, PimFieldSet fieldSet, PimUser pimUser, boolean onlyOwnData) {
        final CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

        final CriteriaQuery<PimFieldDefinition> criteria = criteriaBuilder.createQuery(PimFieldDefinition.class);
        final Root<PimFieldDefinition> pimField = criteria.from(PimFieldDefinition.class);

        if (onlyOwnData && pimUser != null) {
            final Join<PimFieldDefinition, PimFieldSet> pimFieldSet = pimField.join("pimFieldSet");
            criteria.where(criteriaBuilder.and(
                    criteriaBuilder.equal(pimField.get("pimFieldSet"), fieldSet),
                    criteriaBuilder.equal(pimFieldSet.get("owner"), pimUser)
            ));
        } else {
            criteria.where(criteriaBuilder.equal(pimField.get("pimFieldSet"), fieldSet));
        }

        return session.createQuery(criteria).stream();
    }
}
