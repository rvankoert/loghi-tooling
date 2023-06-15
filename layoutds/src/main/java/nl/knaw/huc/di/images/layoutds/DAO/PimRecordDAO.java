package nl.knaw.huc.di.images.layoutds.DAO;

import nl.knaw.huc.di.images.layoutds.models.pim.*;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public class PimRecordDAO extends GenericDAO<PimRecord> {

    public PimRecordDAO() {
        super(PimRecord.class);
    }


    public PimRecord get(Session session, String uri, PimUser pimUser) throws DuplicateDataException {

        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

        CriteriaQuery<PimRecord> criteriaQuery = criteriaBuilder.createQuery(PimRecord.class);
        Root<PimRecord> pimRecordRoot = criteriaQuery.from(PimRecord.class);

        criteriaQuery.where(
                criteriaBuilder.and(
                        criteriaBuilder.equal(pimRecordRoot.get("parent"), uri),
                        criteriaBuilder.equal(pimRecordRoot.get("creator"), pimUser)
                )
        );
        TypedQuery<PimRecord> query = session.createQuery(criteriaQuery);
        List<PimRecord> pimRecords = query.getResultList();

        if (pimRecords.size() > 1) {
            throw new DuplicateDataException("duplicate data");
        }
        if (pimRecords.size() == 0) {
            return null;
        }
        return pimRecords.get(0);
    }

    public PimFieldValue get(Session session, String uri, PimUser pimUser, PimFieldDefinition originalField) throws DuplicateDataException {

        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

        CriteriaQuery<PimFieldValue> criteriaQuery = criteriaBuilder.createQuery(PimFieldValue.class);
        Root<PimFieldValue> pimFieldValueRoot = criteriaQuery.from(PimFieldValue.class);

        criteriaQuery.where(
                criteriaBuilder.and(
                        criteriaBuilder.equal(pimFieldValueRoot.get("creator"), pimUser),
                        criteriaBuilder.equal(pimFieldValueRoot.get("parent"), uri),
                        criteriaBuilder.equal(pimFieldValueRoot.get("field"), originalField)
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

    public Stream<PimRecord> getRecordsByFieldSet(Session session, PimFieldSet pimFieldSet, PimUser pimUser, boolean onlyOwnData, Set<String> imageUrls) {
        final CriteriaBuilder builder = session.getCriteriaBuilder();
        final CriteriaQuery<PimRecord> criteria = builder.createQuery(PimRecord.class);
        final Root<PimRecord> recordRoot = criteria.from(PimRecord.class);

        final Join<PimRecord, PimFieldValue> fieldValues = recordRoot.join("fieldValues");
        final Join<PimFieldValue, PimFieldDefinition> field = fieldValues.join("field");

        if (onlyOwnData) {
            criteria.where(
                    builder.and(
                            builder.equal(recordRoot.get("creator"), pimUser),
                            builder.equal(field.get("pimFieldSet"), pimFieldSet)
                    )
            );
        } else {
            criteria.where(builder.equal(field.get("pimFieldSet"), pimFieldSet));
        }
        if (imageUrls != null) {
            criteria.where(recordRoot.get("parent").in(imageUrls));
        }

        criteria.distinct(true);

        final Query<PimRecord> query = session.createQuery(criteria);

        return query.stream();
    }

    public Stream<PimRecord> getRecordsByDataset(Session session, String dataset, boolean onlyOwnData, PimUser pimUser) {
        final CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        final CriteriaQuery<PimRecord> criteriaQuery = criteriaBuilder.createQuery(PimRecord.class);
        final Root<PimRecord> recordRoot = criteriaQuery.from(PimRecord.class);

        final Join<PimRecord, PimFieldValue> fieldValue = recordRoot.join("fieldValues");
        final Join<PimFieldValue, PimFieldDefinition> field = fieldValue.join("field");

        if (onlyOwnData) {
            criteriaQuery.where(criteriaBuilder.and(
                    criteriaBuilder.equal(field.get("name"), "datasetUri"),
                    criteriaBuilder.equal(fieldValue.get("value"), dataset),
                    criteriaBuilder.equal(recordRoot.get("creator"), pimUser)
            ));
        } else {
            criteriaQuery.where(criteriaBuilder.and(
                    criteriaBuilder.equal(field.get("name"), "datasetUri"),
                    criteriaBuilder.equal(fieldValue.get("value"), dataset)
            ));
        }

        criteriaQuery.distinct(true);


        return session.createQuery(criteriaQuery).stream();
    }

    public Stream<PimRecord> getRecordsByDatasetAndPrimaryGroup(Session session, String dataset, boolean onlyOwnData, PimUser pimUser) {
        final CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        final CriteriaQuery<PimRecord> criteriaQuery = criteriaBuilder.createQuery(PimRecord.class);
        final Root<PimRecord> recordRoot = criteriaQuery.from(PimRecord.class);

        final Join<PimRecord, PimFieldValue> fieldValue = recordRoot.join("fieldValues");
        final Join<PimFieldValue, PimFieldDefinition> field = fieldValue.join("field");

        final Root<Acl> aclRoot = criteriaQuery.from(Acl.class);
        aclRoot.alias("acl");

        final Predicate joinWithAcl = criteriaBuilder.equal(recordRoot.get("uuid"), aclRoot.get("subjectUuid"));
        final Predicate pimGroup = aclRoot.get("group").in(getGroupsOfUser(pimUser));

        final Predicate hasAclForGroup = criteriaBuilder.and(
                joinWithAcl,
                pimGroup
        );

        criteriaQuery.where(
                criteriaBuilder.and(
                        criteriaBuilder.or(
                                criteriaBuilder.equal(recordRoot.get("creator"), pimUser),
                                hasAclForGroup
                        ), criteriaBuilder.equal(field.get("name"), "datasetUri"),
                        criteriaBuilder.equal(fieldValue.get("value"), dataset)
                )
        );

        criteriaQuery.distinct(true);
        criteriaQuery.select(recordRoot);


        return session.createQuery(criteriaQuery).stream();
    }

    public Optional<PimRecord> getByParentUriOfPrimaryGroup(Session session, String parent, PimUser pimUser) {
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

        CriteriaQuery<PimRecord> criteriaQuery = criteriaBuilder.createQuery(PimRecord.class);
        Root<PimRecord> pimRecordRoot = criteriaQuery.from(PimRecord.class);

        final Root<Acl> aclRoot = criteriaQuery.from(Acl.class);
        aclRoot.alias("acl");

        final Predicate joinWithAcl = criteriaBuilder.equal(pimRecordRoot.get("uuid"), aclRoot.get("subjectUuid"));
        final Predicate pimGroup = aclRoot.get("group").in(getGroupsOfUser(pimUser));

        final Predicate hasAclForGroup = criteriaBuilder.and(
                joinWithAcl,
                pimGroup
        );

        criteriaQuery.where(
                criteriaBuilder.and(
                        criteriaBuilder.equal(pimRecordRoot.get("parent"), parent),
                        criteriaBuilder.or(
                                criteriaBuilder.equal(pimRecordRoot.get("creator"), pimUser),
                                hasAclForGroup
                        )
                )
        );
        criteriaQuery.select(pimRecordRoot);
        Query<PimRecord> query = session.createQuery(criteriaQuery);

        return query.getResultStream().findAny();
    }

}
