package nl.knaw.huc.di.images.layoutds.DAO;

import nl.knaw.huc.di.images.layoutds.SessionFactorySingleton;
import nl.knaw.huc.di.images.layoutds.models.DocumentImageSet;
import nl.knaw.huc.di.images.layoutds.models.pim.*;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.*;
import java.util.stream.Stream;

public class PimFieldSetDAO extends GenericDAO<PimFieldSet> {

    public PimFieldSetDAO() {
        super(PimFieldSet.class);
    }

    public PimFieldSet getByUUID(UUID uuid) {
        try (Session session = SessionFactorySingleton.getSessionFactory().openSession()) {

            Transaction transaction = session.beginTransaction();
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

            CriteriaQuery<PimFieldSet> criteriaQuery = criteriaBuilder.createQuery(PimFieldSet.class);
            Root<PimFieldSet> pimFieldSetRoot = criteriaQuery.from(PimFieldSet.class);

            criteriaQuery.where(criteriaBuilder.equal(pimFieldSetRoot.get("uuid"), uuid));
            TypedQuery<PimFieldSet> query = session.createQuery(criteriaQuery);
            List<PimFieldSet> pimFieldSets = query.getResultList();

            transaction.commit();
            session.close();
            if (pimFieldSets.size() == 1) {
                return pimFieldSets.get(0);
            } else {
                return null;
            }
        }
    }

    public Optional<PimFieldSet> getByUUID(Session session, UUID uuid, PimUser pimUser, boolean useGroups) {
        final CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        final CriteriaQuery<PimFieldSet> criteriaQuery = criteriaBuilder.createQuery(PimFieldSet.class);

        final Root<PimFieldSet> pimFieldSetRoot = criteriaQuery.from(PimFieldSet.class);


        final Predicate filterByUuid = criteriaBuilder.equal(pimFieldSetRoot.get("uuid"), uuid);
        pimFieldSetRoot.alias("dis");

        Predicate viewableWithoutAcl = criteriaBuilder.equal(pimFieldSetRoot.get("publicPimFieldSet"), true);

        if (useGroups) {
            final Root<Acl> aclRoot = criteriaQuery.from(Acl.class);
            aclRoot.alias("acl");
            Predicate aclPredicate = createAclFilter(pimUser, criteriaBuilder, criteriaQuery, pimFieldSetRoot);

            criteriaQuery.where(criteriaBuilder.and(criteriaBuilder.or(aclPredicate, viewableWithoutAcl), filterByUuid));
        } else {
            criteriaQuery.where(filterByUuid);
        }

        criteriaQuery.select(pimFieldSetRoot).groupBy(pimFieldSetRoot.get("id"));
        return Optional.ofNullable(session.createQuery(criteriaQuery).getSingleResult());
    }

    public List<PimFieldSet> getAll(Session session, PimUser pimUser, boolean onlyOwnData) {
        final Query<PimFieldSet> query = getAllFieldSetsQuery(session, pimUser, onlyOwnData);
        return query.getResultList();
    }

    private Query<PimFieldSet> getAllFieldSetsQuery(Session session, PimUser pimUser, boolean onlyOwnData) {
        final CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

        final CriteriaQuery<PimFieldSet> criteriaQuery = criteriaBuilder.createQuery(PimFieldSet.class);
        final Root<PimFieldSet> root = criteriaQuery.from(PimFieldSet.class);

        if (onlyOwnData && pimUser != null) {
            criteriaQuery.where(criteriaBuilder.equal(root.get("owner"), pimUser));
        } else if (pimUser != null) {
            if (!pimUser.isAdmin()) {
                criteriaQuery.where(criteriaBuilder.or(
                        criteriaBuilder.equal(root.get("publicPimFieldSet"), true),
                        criteriaBuilder.equal(root.get("owner"), pimUser)
                ));
            }
        } else {
            criteriaQuery.where(criteriaBuilder.equal(root.get("publicPimFieldSet"), true));
        }


        return session.createQuery(criteriaQuery);
    }

    public Stream<PimFieldSet> getAllStreaming(Session session, PimUser pimUser, boolean onlyOwnData) {
        return getAllFieldSetsQuery(session, pimUser, onlyOwnData).stream();
    }

    public Stream<PimFieldSet> getByRecord(Session session, PimRecord record, boolean onlyOwnData, PimUser pimUser) {
        final CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        final CriteriaQuery<PimFieldSet> criteria = criteriaBuilder.createQuery(PimFieldSet.class);
        final Root<PimFieldValue> fieldValue = criteria.from(PimFieldValue.class);


        final Join<PimFieldValue, PimFieldDefinition> field = fieldValue.join("field");
        final Join<PimFieldDefinition, PimFieldSet> pimFieldSet = field.join("pimFieldSet");

        criteria.where(criteriaBuilder.equal(fieldValue.get("pimRecord"), record));

        criteria.distinct(true);
        criteria.select(pimFieldSet);

        final Query<PimFieldSet> query = session.createQuery(criteria);

        return query.stream();
    }

    public Stream<PimFieldSet> streamAllForPrimaryGroupOfUser(Session session, PimUser pimUser, boolean onlyOwnData) {
        final CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        final CriteriaQuery<PimFieldSet> criteriaQuery = criteriaBuilder.createQuery(PimFieldSet.class);

        final Root<PimFieldSet> pimFieldSetRoot = criteriaQuery.from(PimFieldSet.class);
        pimFieldSetRoot.alias("dis");

        final Predicate hasAclForGroup = createAclFilter(pimUser, criteriaBuilder, criteriaQuery, pimFieldSetRoot);

        Predicate viewableWithoutAcl = criteriaBuilder.or(
                criteriaBuilder.equal(pimFieldSetRoot.get("owner"), pimUser),
                criteriaBuilder.equal(pimFieldSetRoot.get("publicPimFieldSet"), true)
        );

        if (viewableWithoutAcl != null) {
            criteriaQuery.where(
                    criteriaBuilder.or(
                            viewableWithoutAcl,
                            hasAclForGroup
                    )
            );
        } else {
            criteriaQuery.where(
                    hasAclForGroup
            );
        }

        criteriaQuery.select(pimFieldSetRoot).groupBy(pimFieldSetRoot.get("id"));

        return session.createQuery(criteriaQuery).stream();
    }

    public Stream<PimFieldSet> getPimFieldSetsByDocumentImageset(Session session, DocumentImageSet documentImageSet) {
        final CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        final CriteriaQuery<PimFieldSet> criteriaQuery = criteriaBuilder.createQuery(PimFieldSet.class);
        final Root<PimFieldValue> pimFieldValueRoot = criteriaQuery.from(PimFieldValue.class);
        final Join<PimFieldValue, PimFieldDefinition> field = pimFieldValueRoot.join("field");
        final Join<PimFieldDefinition, PimFieldSet> pimFieldSet = field.join("pimFieldSet");

        criteriaQuery.where(criteriaBuilder.equal(pimFieldValueRoot.get("value"), documentImageSet.getRemoteUri()));
        criteriaQuery.groupBy(pimFieldSet);
        criteriaQuery.select(pimFieldSet);

        final Query<PimFieldSet> query = session.createQuery(criteriaQuery);
        return query.stream();
    }

    public Stream<PimFieldSet> getAutocomplete(Session session, PimUser pimUser, boolean onlyOwnData, String filter, int limit, int skip) {
        final CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

        final CriteriaQuery<PimFieldSet> criteriaQuery = criteriaBuilder.createQuery(PimFieldSet.class);
        final Root<PimFieldSet> root = criteriaQuery.from(PimFieldSet.class);

        final Predicate autoCompleteFilter = criteriaBuilder.like(
                criteriaBuilder.lower(root.get("name")),
                "%" + filter.toLowerCase() + "%");
        if (onlyOwnData && pimUser != null) {
            criteriaQuery.where(criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("owner"), pimUser),
                    autoCompleteFilter
            ));
        } else {
            if (pimUser == null) {
                criteriaQuery.where(criteriaBuilder.and(
                        criteriaBuilder.equal(root.get("publicPimFieldSet"), true),
                        autoCompleteFilter
                ));
            } else {
                criteriaQuery.where(criteriaBuilder.and(
                        criteriaBuilder.or(
                                criteriaBuilder.equal(root.get("publicPimFieldSet"), true),
                                criteriaBuilder.equal(root.get("owner"), pimUser)
                        ),
                        autoCompleteFilter
                ));
            }
        }

        final Query<PimFieldSet> query = session.createQuery(criteriaQuery);
        query.setMaxResults(limit);
        query.setFirstResult(skip);

        return query.getResultStream();
    }

    public Stream<PimFieldSet> getAutocompleteForPrimaryGroupOfUser(Session session, PimUser pimUser, boolean onlyOwnData, String filter, int limit, int skip) {
        final CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

        final CriteriaQuery<PimFieldSet> criteriaQuery = criteriaBuilder.createQuery(PimFieldSet.class);
        final Root<PimFieldSet> pimFieldSetRoot = criteriaQuery.from(PimFieldSet.class);

        final Predicate autoCompleteFilter = criteriaBuilder.like(pimFieldSetRoot.get("name"), "%" + filter + "%");

        pimFieldSetRoot.alias("dis");

        final Predicate hasAclForGroup = createAclFilter(pimUser, criteriaBuilder, criteriaQuery, pimFieldSetRoot);

        Predicate viewableWithoutAcl;
        if (onlyOwnData) {
            viewableWithoutAcl = criteriaBuilder.equal(pimFieldSetRoot.get("owner"), pimUser);
        } else {
            viewableWithoutAcl = criteriaBuilder.equal(pimFieldSetRoot.get("publicPimFieldSet"), true);
        }

        if (viewableWithoutAcl != null) {
            criteriaQuery.where(
                    criteriaBuilder.and(
                            criteriaBuilder.or(
                                    viewableWithoutAcl,
                                    hasAclForGroup
                            ),
                            autoCompleteFilter
                    )
            );
        } else {
            criteriaQuery.where(
                    criteriaBuilder.and(
                            hasAclForGroup,
                            autoCompleteFilter
                    )
            );
        }

        criteriaQuery.select(pimFieldSetRoot).groupBy(pimFieldSetRoot.get("id"));

        final Query<PimFieldSet> query = session.createQuery(criteriaQuery);
        query.setMaxResults(limit);
        query.setFirstResult(skip);

        return query.getResultStream();
    }
}
