package nl.knaw.huc.di.images.layoutds.DAO;

import com.google.common.base.Predicates;
import nl.knaw.huc.di.images.layoutds.SessionFactorySingleton;
import nl.knaw.huc.di.images.layoutds.models.DocumentImage;
import nl.knaw.huc.di.images.layoutds.models.DocumentImageSet;
import nl.knaw.huc.di.images.layoutds.models.ElasticSearchIndex;
import nl.knaw.huc.di.images.layoutds.models.pim.Acl;
import nl.knaw.huc.di.images.layoutds.models.pim.PimGroup;
import nl.knaw.huc.di.images.layoutds.models.pim.PimUser;
import nl.knaw.huc.di.images.layoutds.models.pim.Role;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DocumentImageSetDAO extends GenericDAO<DocumentImageSet> {

    public DocumentImageSetDAO() {
        super(DocumentImageSet.class);
    }

    @Override
    public void delete(Session session, DocumentImageSet objectToDelete) {
        final Set<DocumentImage> documentImages = objectToDelete.getDocumentImages();
        session.delete(objectToDelete);

        documentImages.stream()
                .map(documentImage -> {
                    session.detach(documentImage);
                    return session.get(DocumentImage.class, documentImage.getId());
                })
                .filter(documentImage -> documentImage.getDocumentImageSets().stream().allMatch(documentImageSet -> documentImageSet.getId().equals(objectToDelete.getId())))
                .forEach(session::delete);
    }

//    public DocumentImageSet getDocumentImageSet(Session session, DocumentSeries series, String imageset, Boolean publishable) throws Exception {
//        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
//
//        CriteriaQuery<DocumentImageSet> criteriaQuery = criteriaBuilder.createQuery(DocumentImageSet.class);
//        Root<DocumentImageSet> documentImageSetRoot = criteriaQuery.from(DocumentImageSet.class);
//        if (publishable) {
//            criteriaQuery
//                    .where(
//                            criteriaBuilder.and(
//                                    criteriaBuilder.equal(documentImageSetRoot.get("documentSeries"), series),
//                                    criteriaBuilder.equal(documentImageSetRoot.get("imageset"), imageset),
//                                    criteriaBuilder.or(
//                                            criteriaBuilder.equal(documentImageSetRoot.get("publish"), true),
//                                            criteriaBuilder.isNull(documentImageSetRoot.get("publish"))
//                                    )
//                            )
//                    );
//        } else {
//            criteriaQuery
//                    .where(
//                            criteriaBuilder.and(
//                                    criteriaBuilder.equal(documentImageSetRoot.get("documentSeries"), series),
//                                    criteriaBuilder.equal(documentImageSetRoot.get("imageset"), imageset)
//                            )
//                    );
//        }
//
//
//        TypedQuery<DocumentImageSet> query = session.createQuery(criteriaQuery);
//        List<DocumentImageSet> results = query.getResultList();
//
//        if (results.size() == 1) {
//            return results.get(0);
//        } else if (results.size() > 1) {
//            throw new Exception("duplicate data for series and imageset " + series.getSeries() + " - " + imageset);
//        } else {
//            return null;
//        }
//    }

//    public String getPrettyNameForImageSet(String series, String imageSet) {
//        Session session = SessionFactorySingleton.getSessionFactory().openSession();
//
//        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
//
//        CriteriaQuery<DocumentImageSet> cq = criteriaBuilder.createQuery(DocumentImageSet.class);
//        Root<DocumentImageSet> documentImageSetRoot = cq.from(DocumentImageSet.class);
//        cq.where(
//                criteriaBuilder.and(
//                        criteriaBuilder.equal(documentImageSetRoot.get("series"), series),
//                        criteriaBuilder.equal(documentImageSetRoot.get("imageset"), imageSet)
//                )
//        );
//
//        TypedQuery<DocumentImageSet> query = session.createQuery(cq);
//        DocumentImageSet result;
//        try {
//            result = query.getSingleResult();
//        } catch (NoResultException nre) {
//            return imageSet;
//        } finally {
//            session.close();
//        }
//        return result.getPrettyName();
//    }

//    public void setPrettyNameForImageset(String series, String imageset, String prettyName) {
//        Session session = SessionFactorySingleton.getSessionFactory().openSession();
//
//        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
//
//        CriteriaQuery<DocumentImageSet> cq = criteriaBuilder.createQuery(DocumentImageSet.class);
//        Root<DocumentImageSet> documentImageSetRoot = cq.from(DocumentImageSet.class);
//        cq.where(
//                criteriaBuilder.and(
//                        criteriaBuilder.equal(documentImageSetRoot.get("series"), series),
//                        criteriaBuilder.equal(documentImageSetRoot.get("imageset"), imageset)
//                )
//        );
//
//        TypedQuery<DocumentImageSet> query = session.createQuery(cq);
//
//        Transaction transaction = session.beginTransaction();
//        DocumentImageSet result = query.getSingleResult();
//        result.setPrettyName(prettyName);
//        session.update(result);
//        transaction.commit();
//        session.close();
//    }

//    public List<DocumentImageSet> getPrettyImageSets(DocumentSeries documentSeries, int skip, int maxResults) {
//        Session session = SessionFactorySingleton.getSessionFactory().openSession();
//
//        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
//
//        CriteriaQuery<DocumentImageSet> criteriaQuery = criteriaBuilder.createQuery(DocumentImageSet.class);
//        Root<DocumentImageSet> documentImageSetRoot = criteriaQuery.from(DocumentImageSet.class);
//        criteriaQuery.where(
//                criteriaBuilder.equal(documentImageSetRoot.get("documentSeries"), documentSeries)
//        );
//
//        TypedQuery<DocumentImageSet> query = session.createQuery(criteriaQuery);
//        query.setMaxResults(maxResults);
//        List<DocumentImageSet> results = query.getResultList();
//        session.close();
//        return results;
//    }


    public List<DocumentImageSet> getPrettyImageSets(int skip, int maxResults) {
        try (Session session = SessionFactorySingleton.getSessionFactory().openSession()) {

            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

            CriteriaQuery<DocumentImageSet> criteriaQuery = criteriaBuilder.createQuery(DocumentImageSet.class);
            criteriaQuery.from(DocumentImageSet.class);

            TypedQuery<DocumentImageSet> query = session.createQuery(criteriaQuery);
            query.setFirstResult(skip).setMaxResults(maxResults);
            List<DocumentImageSet> results = query.getResultList();
            session.close();
            return results;
        }
    }

    public List<String> getAllIndices() {
        try (Session session = SessionFactorySingleton.getSessionFactory().openSession()) {

            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

            CriteriaQuery<String> criteriaQuery = criteriaBuilder.createQuery(String.class);
            Root<DocumentImageSet> documentImageSetRoot = criteriaQuery.from(DocumentImageSet.class);
            criteriaQuery
                    .select(documentImageSetRoot.get("elasticSearchIndex"))
                    .distinct(true)
                    .where(
                            criteriaBuilder.isNotNull(documentImageSetRoot.get("elasticSearchIndex"))
                    );

            TypedQuery<String> query = session.createQuery(criteriaQuery);
            List<String> results = query.getResultList();
            session.close();
            return results;
        }
    }

    public List<DocumentImageSet> getDocumentImageSetByElasticSearchIndex(ElasticSearchIndex index) {
        try (Session session = SessionFactorySingleton.getSessionFactory().openSession()) {

            List<DocumentImageSet> results = getDocumentImageSetByElasticSearchIndex(session, index).collect(Collectors.toList());
            session.close();

            return results;
        }
    }

    public Stream<DocumentImageSet> getDocumentImageSetByElasticSearchIndex(Session session, ElasticSearchIndex index) {

        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<DocumentImageSet> criteriaQuery = criteriaBuilder.createQuery(DocumentImageSet.class);
        Root<DocumentImageSet> documentImageSetRoot = criteriaQuery.from(DocumentImageSet.class);
        criteriaQuery.where(criteriaBuilder.equal(documentImageSetRoot.get("elasticSearchIndex"), index));
        TypedQuery<DocumentImageSet> query = session.createQuery(criteriaQuery);
        return query.getResultStream();
    }

    public DocumentImageSet getDocumentImageSetByUri(Session session, String uri) {

        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<DocumentImageSet> criteriaQuery = criteriaBuilder.createQuery(DocumentImageSet.class);
        Root<DocumentImageSet> documentImageSetRoot = criteriaQuery.from(DocumentImageSet.class);
        criteriaQuery.where(criteriaBuilder.equal(documentImageSetRoot.get("uri"), uri));

        return session.createQuery(criteriaQuery).uniqueResult();
    }

    public List<DocumentImageSet> getDocumentImageSetsByUri(Session session, String uri) {

        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<DocumentImageSet> criteriaQuery = criteriaBuilder.createQuery(DocumentImageSet.class);
        Root<DocumentImageSet> documentImageSetRoot = criteriaQuery.from(DocumentImageSet.class);
        criteriaQuery.where(criteriaBuilder.like(documentImageSetRoot.get("uri"), uri+"%"));

        return session.createQuery(criteriaQuery).getResultList();
    }

    public List<DocumentImageSet> getAllPublic(Session session) {
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

        CriteriaQuery<DocumentImageSet> criteriaQuery = criteriaBuilder.createQuery(DocumentImageSet.class);
        Root<DocumentImageSet> datasetRoot = criteriaQuery.from(DocumentImageSet.class);
        criteriaQuery.select(datasetRoot).where(
                criteriaBuilder.or(
                        criteriaBuilder.isNull(datasetRoot.get("publicDocumentImageSet")),
                        criteriaBuilder.isTrue(datasetRoot.get("publicDocumentImageSet"))
                )
        );
        TypedQuery<DocumentImageSet> query = session.createQuery(criteriaQuery);
        return query.getResultList();

    }

    public List<DocumentImageSet> getAllPublic() {
        try (Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            List<DocumentImageSet> results = getAllPublic(session);
            session.close();
            return results;
        }
    }

    public List<Tuple> getAllShortened(Session session) {
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

        CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createQuery(Tuple.class);
        Root<Tuple> datasetRoot = criteriaQuery.from(Tuple.class);

        criteriaQuery.select(datasetRoot).where(
                criteriaBuilder.or(
                        criteriaBuilder.isNull(datasetRoot.get("publicDocumentImageSet")),
                        criteriaBuilder.isTrue(datasetRoot.get("publicDocumentImageSet"))
                )
        );
        criteriaQuery.multiselect(datasetRoot.get("remoteuri"), datasetRoot.get("uuid"));
        TypedQuery<Tuple> query = session.createQuery(criteriaQuery);
        return query.getResultList();

    }

    private TypedQuery<DocumentImageSet> getAllQuery(Session session, PimUser pimUser, boolean onlyOwnData) {
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

        CriteriaQuery<DocumentImageSet> criteriaQuery = criteriaBuilder.createQuery(DocumentImageSet.class);
        Root<DocumentImageSet> datasetRoot = criteriaQuery.from(DocumentImageSet.class);
        Predicate predicate = null;
        if (onlyOwnData) {
            predicate = criteriaBuilder.equal(datasetRoot.get("owner"), pimUser);
        } else if (pimUser != null && !pimUser.getRoles().contains(Role.ADMIN)) {
            predicate = criteriaBuilder.or(
                    criteriaBuilder.isNull(datasetRoot.get("publicDocumentImageSet")),
                    criteriaBuilder.isTrue(datasetRoot.get("publicDocumentImageSet")),
                    criteriaBuilder.equal(datasetRoot.get("owner"), pimUser)
            );
        } else if (pimUser == null) {
            predicate = criteriaBuilder.or(
                    criteriaBuilder.isNull(datasetRoot.get("publicDocumentImageSet")),
                    criteriaBuilder.isTrue(datasetRoot.get("publicDocumentImageSet"))
            );
        }

        criteriaQuery.select(datasetRoot);
        if (predicate != null) {
            criteriaQuery.where(
                    predicate
            );
        }
        return session.createQuery(criteriaQuery);
    }

    public Stream<DocumentImageSet> getAllStreaming(Session session, PimUser pimUser, boolean onlyOwnData) {
        TypedQuery<DocumentImageSet> query = getAllQuery(session, pimUser, onlyOwnData);
        return query.getResultStream();
    }

//    public Stream<DocumentImageSet> getByDocumentImageSeries(Session session, DocumentSeries documentSeries) {
//
//        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
//        CriteriaQuery<DocumentImageSet> criteriaQuery = criteriaBuilder.createQuery(DocumentImageSet.class);
//
//        Root<DocumentSeries> documentSeriesRoot = criteriaQuery.from(DocumentSeries.class);
//        SetJoin<DocumentSeries, DocumentImageSet> documentImageSets = documentSeriesRoot.join(DocumentSeries_.documentImageSets);
//
//        criteriaQuery.where(
//                criteriaBuilder.and(
//                        criteriaBuilder.equal(documentSeriesRoot, documentSeries)
//                )
//        );
//        criteriaQuery.select(documentImageSets);
//
//        TypedQuery<DocumentImageSet> typedQuery = session.createQuery(criteriaQuery);
//        return typedQuery.getResultStream();
//    }


    public List<DocumentImageSet> getDocumentImageSetsWithoutRemoteUri(Session session) {
        final CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        final CriteriaQuery<DocumentImageSet> criteriaQuery = criteriaBuilder.createQuery(DocumentImageSet.class);

        final Root<DocumentImageSet> root = criteriaQuery.from(DocumentImageSet.class);

        criteriaQuery.where(criteriaBuilder.isNull(root.get("remoteUri")));

        final Query<DocumentImageSet> query = session.createQuery(criteriaQuery);

        return query.getResultList();
    }

    public Optional<DocumentImageSet> getSetByRemoteUri(Session session, String datasetUri) {
        final CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        final CriteriaQuery<DocumentImageSet> criteriaQuery = criteriaBuilder.createQuery(DocumentImageSet.class);
        final Root<DocumentImageSet> root = criteriaQuery.from(DocumentImageSet.class);

        criteriaQuery.where(criteriaBuilder.equal(root.get("remoteUri"), datasetUri));

        // session.createQuery(criteriaQuery).getSingleResult() gives problems when used in streams
        return session.createQuery(criteriaQuery).stream().findAny();
    }

    public Stream<DocumentImageSet> streamAllForPrimaryGroupOfUser(Session session, PimUser pimUser, boolean onlyOwnData) {
        final CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        final CriteriaQuery<DocumentImageSet> criteriaQuery = criteriaBuilder.createQuery(DocumentImageSet.class);

        final Root<DocumentImageSet> datasetRoot = criteriaQuery.from(DocumentImageSet.class);
        datasetRoot.alias("dis");

        final Predicate hasAclForGroup = createAclFilter(pimUser, criteriaBuilder, criteriaQuery, datasetRoot);

        Predicate viewableWithoutAcl;
        if (onlyOwnData) {
            viewableWithoutAcl = criteriaBuilder.equal(datasetRoot.get("owner"), pimUser);
        } else {
            viewableWithoutAcl = criteriaBuilder.or(
                    criteriaBuilder.isNull(datasetRoot.get("publicDocumentImageSet")),
                    criteriaBuilder.isTrue(datasetRoot.get("publicDocumentImageSet")),
                    criteriaBuilder.equal(datasetRoot.get("owner"), pimUser)
            );
        }


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

        criteriaQuery.select(datasetRoot).groupBy(datasetRoot.get("id"));

        return session.createQuery(criteriaQuery).stream();
    }


    public Optional<DocumentImageSet> getByUUID(Session session, UUID uuid, PimUser pimUser, boolean useGroups) {
        final CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        final CriteriaQuery<DocumentImageSet> criteriaQuery = criteriaBuilder.createQuery(DocumentImageSet.class);

        final Root<DocumentImageSet> datasetRoot = criteriaQuery.from(DocumentImageSet.class);

        final Predicate viewableWithoutAcl = criteriaBuilder.or(
                criteriaBuilder.isNull(datasetRoot.get("publicDocumentImageSet")),
                criteriaBuilder.isTrue(datasetRoot.get("publicDocumentImageSet")),
                criteriaBuilder.equal(datasetRoot.get("owner"), pimUser)
        );


        final Predicate filterByUuid = criteriaBuilder.equal(datasetRoot.get("uuid"), uuid);

        if (useGroups) {
            datasetRoot.alias("dis");
            final Root<Acl> aclRoot = criteriaQuery.from(Acl.class);
            aclRoot.alias("acl");
            Predicate aclPredicate = criteriaBuilder.and(
                    criteriaBuilder.equal(datasetRoot.get("uuid"), aclRoot.get("subjectUuid")),
                    aclRoot.get("group").in(getGroupsOfUser(pimUser)),
                    criteriaBuilder.isNull(aclRoot.get("deleted"))
            );

            criteriaQuery.where(criteriaBuilder.and(criteriaBuilder.or(viewableWithoutAcl, aclPredicate), filterByUuid));
        } else {
            criteriaQuery.where(viewableWithoutAcl, filterByUuid);
        }

        criteriaQuery.select(datasetRoot).groupBy(datasetRoot.get("id"));
        return Optional.ofNullable(session.createQuery(criteriaQuery).getSingleResult());
    }

    public Stream<DocumentImageSet> getAutocomplete(Session session, PimUser pimUser, boolean onlyOwnData, String filter, int limit, int skip) {
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

        CriteriaQuery<DocumentImageSet> criteriaQuery = criteriaBuilder.createQuery(DocumentImageSet.class);
        Root<DocumentImageSet> datasetRoot = criteriaQuery.from(DocumentImageSet.class);
        final Predicate filterOnName = criteriaBuilder.like(
                criteriaBuilder.lower(datasetRoot.get("imageset")),
                "%" + filter.toLowerCase() + "%"
        );
        Predicate predicate = null;
        if (onlyOwnData) {
            predicate = criteriaBuilder.equal(datasetRoot.get("owner"), pimUser);
        } else if (!pimUser.getRoles().contains(Role.ADMIN)) {
            predicate = criteriaBuilder.or(
                    criteriaBuilder.isNull(datasetRoot.get("publicDocumentImageSet")),
                    criteriaBuilder.isTrue(datasetRoot.get("publicDocumentImageSet")),
                    criteriaBuilder.equal(datasetRoot.get("owner"), pimUser)
            );
        }

        criteriaQuery.select(datasetRoot);
        if (predicate != null) {
            criteriaQuery.where(criteriaBuilder.and(predicate, filterOnName));
        } else {
            criteriaQuery.where(filterOnName);
        }
        final Query<DocumentImageSet> query = session.createQuery(criteriaQuery);
        query.setMaxResults(limit);
        query.setFirstResult(skip);
        return query.getResultStream();
    }

    public Stream<DocumentImageSet> getAutocompleteForPrimaryGroupOfUser(Session session, PimUser pimUser, boolean onlyOwnData, String filter, int limit, int skip) {
        final CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        final CriteriaQuery<DocumentImageSet> criteriaQuery = criteriaBuilder.createQuery(DocumentImageSet.class);

        final Root<DocumentImageSet> datasetRoot = criteriaQuery.from(DocumentImageSet.class);
        datasetRoot.alias("dis");
        final Predicate hasAclForGroup = createAclFilter(pimUser, criteriaBuilder, criteriaQuery, datasetRoot);

        final Predicate filterOnName = criteriaBuilder.like(datasetRoot.get("imageset"), "%" + filter + "%");

        Predicate viewableWithoutAcl;
        if (onlyOwnData) {
            viewableWithoutAcl = criteriaBuilder.equal(datasetRoot.get("owner"), pimUser);
        } else {
            viewableWithoutAcl = criteriaBuilder.or(
                    criteriaBuilder.isNull(datasetRoot.get("publicDocumentImageSet")),
                    criteriaBuilder.isTrue(datasetRoot.get("publicDocumentImageSet")),
                    criteriaBuilder.equal(datasetRoot.get("owner"), pimUser)
            );
        }


        if (viewableWithoutAcl != null) {
            criteriaQuery.where(
                    criteriaBuilder.and(
                            criteriaBuilder.or(
                                    viewableWithoutAcl,
                                    hasAclForGroup
                            ),
                            filterOnName
                    )
            );
        } else {
            criteriaQuery.where(
                    criteriaBuilder.and(
                            hasAclForGroup,
                            filterOnName
                    )
            );
        }

        criteriaQuery.select(datasetRoot).groupBy(datasetRoot.get("id"));

        final Query<DocumentImageSet> query = session.createQuery(criteriaQuery);
        query.setMaxResults(limit);
        query.setFirstResult(skip);

        return query.getResultStream();
    }



//    public List<DocumentImageSet> getByElasticSearchIndexOld(Session session, ElasticSearchIndex elasticSearchIndex, PimUser pimUser) {
//        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
//
//        CriteriaQuery<DocumentImageSet> criteriaQuery = criteriaBuilder.createQuery(DocumentImageSet.class);
//        Root<DocumentImageSet> datasetRoot = criteriaQuery.from(DocumentImageSet.class);
//        Predicate predicate = null;
//        if (pimUser != null && !pimUser.getRoles().contains(Role.ADMIN)) {
//            predicate = criteriaBuilder.or(
//                    criteriaBuilder.isNull(datasetRoot.get("publicDocumentImageSet")),
//                    criteriaBuilder.isTrue(datasetRoot.get("publicDocumentImageSet")),
//                    criteriaBuilder.equal(datasetRoot.get("owner"), pimUser)
//            );
//        }
//        if (pimUser == null) {
//            predicate = criteriaBuilder.or(
//                    criteriaBuilder.isNull(datasetRoot.get("publicDocumentImageSet")),
//                    criteriaBuilder.isTrue(datasetRoot.get("publicDocumentImageSet"))
//            );
//        }
//        Predicate filterOnElasticSearchIndex = criteriaBuilder.equal(datasetRoot.get("elasticSearchIndex"), elasticSearchIndex);
//        criteriaQuery.select(datasetRoot);
//        if (predicate != null) {
//            criteriaQuery.where(criteriaBuilder.and(predicate, filterOnElasticSearchIndex));
//        } else {
//            criteriaQuery.where(filterOnElasticSearchIndex);
//        }
//        final Query<DocumentImageSet> query = session.createQuery(criteriaQuery);
//        return query.getResultList();
//    }

    public List<DocumentImageSet> getByElasticSearchIndex(Session session, ElasticSearchIndex elasticSearchIndex, PimUser pimUser, boolean useAcls) {
        final CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        final CriteriaQuery<DocumentImageSet> criteriaQuery = criteriaBuilder.createQuery(DocumentImageSet.class);


        final Root<DocumentImageSet> datasetRoot = criteriaQuery.from(DocumentImageSet.class);
        datasetRoot.alias("dis");

        Predicate viewableWithoutAcl;

        Predicate filterOnElasticSearchIndex = criteriaBuilder.equal(datasetRoot.get("elasticSearchIndex"), elasticSearchIndex);
        criteriaQuery.select(datasetRoot);
        //unknown user
        if (pimUser == null) {
            viewableWithoutAcl = criteriaBuilder.or(
                    criteriaBuilder.isNull(datasetRoot.get("publicDocumentImageSet")),
                    criteriaBuilder.isTrue(datasetRoot.get("publicDocumentImageSet"))
            );
            criteriaQuery.where(
                    criteriaBuilder.and(
                            filterOnElasticSearchIndex,
                            criteriaBuilder.or(
                                    viewableWithoutAcl
                            )
                    )
            );

        } else {
            if (!pimUser.isAdmin()) {
                // not an admin
                viewableWithoutAcl = criteriaBuilder.or(
                        criteriaBuilder.isNull(datasetRoot.get("publicDocumentImageSet")),
                        criteriaBuilder.isTrue(datasetRoot.get("publicDocumentImageSet")),
                        criteriaBuilder.equal(datasetRoot.get("owner"), pimUser)
                );


                if (useAcls) {
                    final Predicate hasAclForGroup = createAclFilter(pimUser, criteriaBuilder, criteriaQuery, datasetRoot);

                    criteriaQuery.where(
                            criteriaBuilder.and(
                                    filterOnElasticSearchIndex,
                                    criteriaBuilder.or(
                                            viewableWithoutAcl,
                                            hasAclForGroup
                                    )
                            )
                    );
                }
                else {
                    criteriaQuery.where(criteriaBuilder.and(filterOnElasticSearchIndex, viewableWithoutAcl));
                }
            } else{
            //admin otherwise
                criteriaQuery.where(filterOnElasticSearchIndex);
            }
        }


        criteriaQuery.select(datasetRoot).groupBy(datasetRoot.get("id"));

        return session.createQuery(criteriaQuery).getResultList();
    }


    public DocumentImageSet getSetByImagesetForOwner(Session session, String imageset, PimUser pimUser) {
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

        CriteriaQuery<DocumentImageSet> criteriaQuery = criteriaBuilder.createQuery(DocumentImageSet.class);
        Root<DocumentImageSet> datasetRoot = criteriaQuery.from(DocumentImageSet.class);

        criteriaQuery.where(criteriaBuilder.and(
                criteriaBuilder.equal(datasetRoot.get("owner"), pimUser),
                criteriaBuilder.equal(datasetRoot.get("imageset"), imageset)
        ));

        final Query<DocumentImageSet> query = session.createQuery(criteriaQuery);

        return query.uniqueResult();
    }
}
