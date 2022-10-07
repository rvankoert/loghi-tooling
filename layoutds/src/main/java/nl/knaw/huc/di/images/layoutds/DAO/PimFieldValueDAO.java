package nl.knaw.huc.di.images.layoutds.DAO;

import nl.knaw.huc.di.images.layoutds.models.DocumentImageSet;
import nl.knaw.huc.di.images.layoutds.models.pim.PimFieldDefinition;
import nl.knaw.huc.di.images.layoutds.models.pim.PimFieldValue;
import nl.knaw.huc.di.images.layoutds.models.pim.PimRecord;
import nl.knaw.huc.di.images.layoutds.models.pim.PimUser;
import org.hibernate.Session;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.List;
import java.util.stream.Stream;

public class PimFieldValueDAO extends GenericDAO<PimFieldValue> {

    public PimFieldValueDAO() {
        super(PimFieldValue.class);
    }


    public List<PimFieldValue> get(Session session, PimUser pimUser, PimRecord pimRecord) {

        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

        CriteriaQuery<PimFieldValue> criteriaQuery = criteriaBuilder.createQuery(PimFieldValue.class);
        Root<PimFieldValue> pimFieldSetRoot = criteriaQuery.from(PimFieldValue.class);

        criteriaQuery.where(
                criteriaBuilder.and(
                        criteriaBuilder.equal(pimFieldSetRoot.get("creator"), pimUser),
                        criteriaBuilder.equal(pimFieldSetRoot.get("pimRecord"), pimRecord)
                )
        );
        TypedQuery<PimFieldValue> query = session.createQuery(criteriaQuery);
        List<PimFieldValue> pimFieldValues = query.getResultList();

        return pimFieldValues;
    }

    public PimFieldValue get(Session session, PimRecord pimRecord, PimUser pimUser, PimFieldDefinition originalField) throws DuplicateDataException {

        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

        CriteriaQuery<PimFieldValue> criteriaQuery = criteriaBuilder.createQuery(PimFieldValue.class);
        Root<PimFieldValue> pimFieldSetRoot = criteriaQuery.from(PimFieldValue.class);

        criteriaQuery.where(
                criteriaBuilder.and(
                        criteriaBuilder.equal(pimFieldSetRoot.get("creator"), pimUser),
                        criteriaBuilder.equal(pimFieldSetRoot.get("pimRecord"), pimRecord),
                        criteriaBuilder.equal(pimFieldSetRoot.get("field"), originalField)
                )
        );
        TypedQuery<PimFieldValue> query = session.createQuery(criteriaQuery);
        List<PimFieldValue> pimFieldValues = query.getResultList();

        if (pimFieldValues.size() == 0) {
            return null;
        }
        if (pimFieldValues.size() > 1) {
            throw new DuplicateDataException("duplicate data");
        }
        return pimFieldValues.get(0);
    }


    public Stream<DocumentImageSet> getAnnotatedDocumentImageSets(Session session) {
        final CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        final CriteriaQuery<DocumentImageSet> criteriaQuery = criteriaBuilder.createQuery(DocumentImageSet.class);
        final Root<DocumentImageSet> root = criteriaQuery.from(DocumentImageSet.class);

        final Subquery<PimFieldValue> subquery = criteriaQuery.subquery(PimFieldValue.class);
        final Root<PimFieldValue> pimFieldValueRoot = subquery.from(PimFieldValue.class);
        final Join<PimFieldValue, PimFieldDefinition> field = pimFieldValueRoot.join("field");

        subquery.where(criteriaBuilder.equal(field.get("name"), "datasetUri"));
        subquery.select(pimFieldValueRoot.get("value"));
        subquery.groupBy(pimFieldValueRoot.get("value"));

        criteriaQuery.where(criteriaBuilder.in(root.get("remoteUri")).value(subquery));

        return session.createQuery(criteriaQuery).stream();
    }

    /**
     * This method will fail when two PimFieldSets have a PimField with the same name
     * @deprecated use getByUriAndField
     */
    @Deprecated
    public String getByUriAndFieldName(Session session, String remoteuri, String labelName) {
        final CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        final CriteriaQuery<PimFieldValue> criteriaQuery = criteriaBuilder.createQuery(PimFieldValue.class);

        final Root<PimFieldValue> pimFieldValueRoot = criteriaQuery.from(PimFieldValue.class);
        final Join<PimFieldValue, PimFieldDefinition> fieldDefinition = pimFieldValueRoot.join("field");
        final Join<PimFieldValue, PimRecord> record = pimFieldValueRoot.join("pimRecord");

        criteriaQuery.where(
                criteriaBuilder.and(
                        criteriaBuilder.like(fieldDefinition.get("name"), labelName),
                        criteriaBuilder.equal(record.get("parent"), remoteuri)
                )
        );
        criteriaQuery.select(pimFieldValueRoot);
        List<PimFieldValue> resultList = session.createQuery(criteriaQuery).setMaxResults(10).getResultList();
        if (resultList.size() > 0) {
            return resultList.get(0).getValue();
        }
        return null;
    }

    public String getByUriAndField(Session session, String remoteuri, PimFieldDefinition pimField) {
        final CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        final CriteriaQuery<PimFieldValue> criteriaQuery = criteriaBuilder.createQuery(PimFieldValue.class);

        final Root<PimFieldValue> pimFieldValueRoot = criteriaQuery.from(PimFieldValue.class);
        final Join<PimFieldValue, PimRecord> record = pimFieldValueRoot.join("pimRecord");

        criteriaQuery.where(
                criteriaBuilder.and(
                        criteriaBuilder.equal(pimFieldValueRoot.get("field"), pimField),
                        criteriaBuilder.equal(record.get("parent"), remoteuri)
                )
        );
        criteriaQuery.select(pimFieldValueRoot);
        List<PimFieldValue> resultList = session.createQuery(criteriaQuery).setMaxResults(10).getResultList();
        if (resultList.size() > 0) {
            return resultList.get(0).getValue();
        }
        return null;
    }
}
