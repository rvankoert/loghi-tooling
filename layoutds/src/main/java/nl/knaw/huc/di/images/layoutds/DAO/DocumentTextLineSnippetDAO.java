package nl.knaw.huc.di.images.layoutds.DAO;

import nl.knaw.huc.di.images.layoutds.SessionFactorySingleton;
import nl.knaw.huc.di.images.layoutds.models.DocumentTextLineSnippet;
import org.hibernate.Session;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class DocumentTextLineSnippetDAO extends GenericDAO<DocumentTextLineSnippet> {

    public DocumentTextLineSnippetDAO() {
        super(DocumentTextLineSnippet.class);
    }


    // TODO RUTGERCHECK: find using cosine similarity
    public List<DocumentTextLineSnippet> find(DocumentTextLineSnippet target, int maxResults, float diff) {
        try (Session session = SessionFactorySingleton.getSessionFactory().openSession()) {

            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

            CriteriaQuery<DocumentTextLineSnippet> criteriaQuery = criteriaBuilder.createQuery(DocumentTextLineSnippet.class);
            Root<DocumentTextLineSnippet> documentTextLineSnippetRoot = criteriaQuery.from(DocumentTextLineSnippet.class);

            criteriaQuery.where(
                    criteriaBuilder.and(
                            criteriaBuilder.greaterThan(documentTextLineSnippetRoot.get("feature1"), target.getFeature1() - diff),
                            criteriaBuilder.greaterThan(documentTextLineSnippetRoot.get("feature2"), target.getFeature2() - diff),
                            criteriaBuilder.greaterThan(documentTextLineSnippetRoot.get("feature3"), target.getFeature3() - diff),
                            criteriaBuilder.greaterThan(documentTextLineSnippetRoot.get("feature4"), target.getFeature4() - diff),
                            criteriaBuilder.greaterThan(documentTextLineSnippetRoot.get("feature5"), target.getFeature5() - diff),
                            criteriaBuilder.greaterThan(documentTextLineSnippetRoot.get("feature6"), target.getFeature6() - diff),
                            criteriaBuilder.greaterThan(documentTextLineSnippetRoot.get("feature7"), target.getFeature7() - diff),
                            criteriaBuilder.greaterThan(documentTextLineSnippetRoot.get("feature8"), target.getFeature8() - diff),
                            criteriaBuilder.greaterThan(documentTextLineSnippetRoot.get("feature9"), target.getFeature9() - diff),
                            criteriaBuilder.greaterThan(documentTextLineSnippetRoot.get("feature10"), target.getFeature10() - diff),
                            criteriaBuilder.greaterThan(documentTextLineSnippetRoot.get("feature11"), target.getFeature11() - diff),
                            criteriaBuilder.greaterThan(documentTextLineSnippetRoot.get("feature12"), target.getFeature12() - diff),
                            criteriaBuilder.greaterThan(documentTextLineSnippetRoot.get("feature13"), target.getFeature13() - diff),
                            criteriaBuilder.greaterThan(documentTextLineSnippetRoot.get("feature14"), target.getFeature14() - diff),
                            criteriaBuilder.greaterThan(documentTextLineSnippetRoot.get("feature15"), target.getFeature15() - diff),
                            criteriaBuilder.greaterThan(documentTextLineSnippetRoot.get("feature16"), target.getFeature16() - diff),
                            criteriaBuilder.greaterThan(documentTextLineSnippetRoot.get("feature17"), target.getFeature17() - diff),
                            criteriaBuilder.greaterThan(documentTextLineSnippetRoot.get("feature18"), target.getFeature18() - diff),
                            criteriaBuilder.greaterThan(documentTextLineSnippetRoot.get("feature19"), target.getFeature19() - diff),
                            criteriaBuilder.greaterThan(documentTextLineSnippetRoot.get("feature20"), target.getFeature20() - diff),
                            criteriaBuilder.lessThan(documentTextLineSnippetRoot.get("feature1"), target.getFeature1() + diff),
                            criteriaBuilder.lessThan(documentTextLineSnippetRoot.get("feature2"), target.getFeature2() + diff),
                            criteriaBuilder.lessThan(documentTextLineSnippetRoot.get("feature3"), target.getFeature3() + diff),
                            criteriaBuilder.lessThan(documentTextLineSnippetRoot.get("feature4"), target.getFeature4() + diff),
                            criteriaBuilder.lessThan(documentTextLineSnippetRoot.get("feature5"), target.getFeature5() + diff),
                            criteriaBuilder.lessThan(documentTextLineSnippetRoot.get("feature6"), target.getFeature6() + diff),
                            criteriaBuilder.lessThan(documentTextLineSnippetRoot.get("feature7"), target.getFeature7() + diff),
                            criteriaBuilder.lessThan(documentTextLineSnippetRoot.get("feature8"), target.getFeature8() + diff),
                            criteriaBuilder.lessThan(documentTextLineSnippetRoot.get("feature9"), target.getFeature9() + diff),
                            criteriaBuilder.lessThan(documentTextLineSnippetRoot.get("feature10"), target.getFeature10() + diff),
                            criteriaBuilder.lessThan(documentTextLineSnippetRoot.get("feature11"), target.getFeature11() + diff),
                            criteriaBuilder.lessThan(documentTextLineSnippetRoot.get("feature12"), target.getFeature12() + diff),
                            criteriaBuilder.lessThan(documentTextLineSnippetRoot.get("feature13"), target.getFeature13() + diff),
                            criteriaBuilder.lessThan(documentTextLineSnippetRoot.get("feature14"), target.getFeature14() + diff),
                            criteriaBuilder.lessThan(documentTextLineSnippetRoot.get("feature15"), target.getFeature15() + diff),
                            criteriaBuilder.lessThan(documentTextLineSnippetRoot.get("feature16"), target.getFeature16() + diff),
                            criteriaBuilder.lessThan(documentTextLineSnippetRoot.get("feature17"), target.getFeature17() + diff),
                            criteriaBuilder.lessThan(documentTextLineSnippetRoot.get("feature18"), target.getFeature18() + diff),
                            criteriaBuilder.lessThan(documentTextLineSnippetRoot.get("feature19"), target.getFeature19() + diff),
                            criteriaBuilder.lessThan(documentTextLineSnippetRoot.get("feature20"), target.getFeature20() + diff)
                            , criteriaBuilder.equal(documentTextLineSnippetRoot.get("isValidation"), true)

                    )
            );

            //TODO RUTGERCHECK: there has to be an easier/prettier way to do this.....

//        criteriaQuery.orderBy(criteriaBuilder.asc(
//                criteriaBuilder.sqrt(
//                        criteriaBuilder.sum(
//                                criteriaBuilder.sum(
//                                        criteriaBuilder.sum(
//                                                criteriaBuilder.sum(
//                                                        criteriaBuilder.sum(
//                                                                criteriaBuilder.quot(criteriaBuilder.diff(documentTextLineSnippetRoot.get("feature1"), -target.getFeature1()), 2),
//                                                                criteriaBuilder.prod(criteriaBuilder.diff(documentTextLineSnippetRoot.get("feature2"), -target.getFeature2()), 2)
//                                                        ),
//                                                        criteriaBuilder.sum(
//                                                                criteriaBuilder.prod(criteriaBuilder.diff(documentTextLineSnippetRoot.get("feature3"), -target.getFeature3()), 2),
//                                                                criteriaBuilder.prod(criteriaBuilder.diff(documentTextLineSnippetRoot.get("feature4"), -target.getFeature4()), 2)
//                                                        )
//                                                ),
//                                                criteriaBuilder.sum(
//                                                        criteriaBuilder.sum(
//                                                                criteriaBuilder.prod(criteriaBuilder.diff(documentTextLineSnippetRoot.get("feature5"), -target.getFeature5()), 2),
//                                                                criteriaBuilder.prod(criteriaBuilder.diff(documentTextLineSnippetRoot.get("feature6"), -target.getFeature6()), 2)
//                                                        ),
//                                                        criteriaBuilder.sum(
//                                                                criteriaBuilder.prod(criteriaBuilder.diff(documentTextLineSnippetRoot.get("feature7"), -target.getFeature7()), 2),
//                                                                criteriaBuilder.prod(criteriaBuilder.diff(documentTextLineSnippetRoot.get("feature8"), -target.getFeature8()), 2)
//                                                        )
//                                                )
//
//                                        ),
//                                        criteriaBuilder.sum(
//                                                criteriaBuilder.sum(
//                                                        criteriaBuilder.sum(
//                                                                criteriaBuilder.prod(criteriaBuilder.diff(documentTextLineSnippetRoot.get("feature9"), -target.getFeature9()), 2),
//                                                                criteriaBuilder.prod(criteriaBuilder.diff(documentTextLineSnippetRoot.get("feature10"), -target.getFeature10()), 2)
//                                                        ),
//                                                        criteriaBuilder.sum(
//                                                                criteriaBuilder.prod(criteriaBuilder.diff(documentTextLineSnippetRoot.get("feature11"), -target.getFeature11()), 2),
//                                                                criteriaBuilder.prod(criteriaBuilder.diff(documentTextLineSnippetRoot.get("feature12"), -target.getFeature12()), 2)
//                                                        )
//                                                ),
//                                                criteriaBuilder.sum(
//                                                        criteriaBuilder.sum(
//                                                                criteriaBuilder.prod(criteriaBuilder.diff(documentTextLineSnippetRoot.get("feature13"), -target.getFeature13()), 2),
//                                                                criteriaBuilder.prod(criteriaBuilder.diff(documentTextLineSnippetRoot.get("feature14"), -target.getFeature14()), 2)
//                                                        ),
//                                                        criteriaBuilder.sum(
//                                                                criteriaBuilder.prod(criteriaBuilder.diff(documentTextLineSnippetRoot.get("feature15"), -target.getFeature15()), 2),
//                                                                criteriaBuilder.prod(criteriaBuilder.diff(documentTextLineSnippetRoot.get("feature16"), -target.getFeature16()), 2)
//                                                        )
//                                                )
//
//                                        )
//
//                                ),
//                                criteriaBuilder.sum(
//                                        criteriaBuilder.sum(
//                                                criteriaBuilder.prod(criteriaBuilder.diff(documentTextLineSnippetRoot.get("feature17"), -target.getFeature17()), 2),
//                                                criteriaBuilder.prod(criteriaBuilder.diff(documentTextLineSnippetRoot.get("feature18"), -target.getFeature18()), 2)
//                                        ),
//                                        criteriaBuilder.sum(
//                                                criteriaBuilder.prod(criteriaBuilder.diff(documentTextLineSnippetRoot.get("feature19"), -target.getFeature19()), 2),
//                                                criteriaBuilder.prod(criteriaBuilder.diff(documentTextLineSnippetRoot.get("feature20"), -target.getFeature20()), 2)
//                                        )
//                                )
//
//                        )
//                ))
//        );


            TypedQuery<DocumentTextLineSnippet> query = session.createQuery(criteriaQuery).setMaxResults(maxResults);
            List<DocumentTextLineSnippet> documentTextLineSnippets = query.getResultList();

            session.close();
            return documentTextLineSnippets;
        }
    }

    // TODO RUTGERCHECK (method is not used): find using cosine similarity
    public List<DocumentTextLineSnippet> findOld(DocumentTextLineSnippet target, int maxResults, float diff) {
        try (Session session = SessionFactorySingleton.getSessionFactory().openSession()) {


            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

            CriteriaQuery<DocumentTextLineSnippet> criteriaQuery = criteriaBuilder.createQuery(DocumentTextLineSnippet.class);
            Root<DocumentTextLineSnippet> documentTextLineSnippetRoot = criteriaQuery.from(DocumentTextLineSnippet.class);

            criteriaQuery.where(
                    criteriaBuilder.and(
                            criteriaBuilder.greaterThan(documentTextLineSnippetRoot.get("feature1"), target.getFeature1() - diff),
                            criteriaBuilder.greaterThan(documentTextLineSnippetRoot.get("feature2"), target.getFeature2() - diff),
                            criteriaBuilder.greaterThan(documentTextLineSnippetRoot.get("feature3"), target.getFeature3() - diff),
                            criteriaBuilder.greaterThan(documentTextLineSnippetRoot.get("feature4"), target.getFeature4() - diff),
                            criteriaBuilder.greaterThan(documentTextLineSnippetRoot.get("feature5"), target.getFeature5() - diff),
                            criteriaBuilder.greaterThan(documentTextLineSnippetRoot.get("feature6"), target.getFeature6() - diff),
                            criteriaBuilder.greaterThan(documentTextLineSnippetRoot.get("feature7"), target.getFeature7() - diff),
                            criteriaBuilder.greaterThan(documentTextLineSnippetRoot.get("feature8"), target.getFeature8() - diff),
                            criteriaBuilder.greaterThan(documentTextLineSnippetRoot.get("feature9"), target.getFeature9() - diff),
                            criteriaBuilder.greaterThan(documentTextLineSnippetRoot.get("feature10"), target.getFeature10() - diff),
                            criteriaBuilder.greaterThan(documentTextLineSnippetRoot.get("feature11"), target.getFeature11() - diff),
                            criteriaBuilder.greaterThan(documentTextLineSnippetRoot.get("feature12"), target.getFeature12() - diff),
                            criteriaBuilder.greaterThan(documentTextLineSnippetRoot.get("feature13"), target.getFeature13() - diff),
                            criteriaBuilder.greaterThan(documentTextLineSnippetRoot.get("feature14"), target.getFeature14() - diff),
                            criteriaBuilder.greaterThan(documentTextLineSnippetRoot.get("feature15"), target.getFeature15() - diff),
                            criteriaBuilder.greaterThan(documentTextLineSnippetRoot.get("feature16"), target.getFeature16() - diff),
                            criteriaBuilder.greaterThan(documentTextLineSnippetRoot.get("feature17"), target.getFeature17() - diff),
                            criteriaBuilder.greaterThan(documentTextLineSnippetRoot.get("feature18"), target.getFeature18() - diff),
                            criteriaBuilder.greaterThan(documentTextLineSnippetRoot.get("feature19"), target.getFeature19() - diff),
                            criteriaBuilder.greaterThan(documentTextLineSnippetRoot.get("feature20"), target.getFeature20() - diff),
                            criteriaBuilder.lessThan(documentTextLineSnippetRoot.get("feature1"), target.getFeature1() + diff),
                            criteriaBuilder.lessThan(documentTextLineSnippetRoot.get("feature2"), target.getFeature2() + diff),
                            criteriaBuilder.lessThan(documentTextLineSnippetRoot.get("feature3"), target.getFeature3() + diff),
                            criteriaBuilder.lessThan(documentTextLineSnippetRoot.get("feature4"), target.getFeature4() + diff),
                            criteriaBuilder.lessThan(documentTextLineSnippetRoot.get("feature5"), target.getFeature5() + diff),
                            criteriaBuilder.lessThan(documentTextLineSnippetRoot.get("feature6"), target.getFeature6() + diff),
                            criteriaBuilder.lessThan(documentTextLineSnippetRoot.get("feature7"), target.getFeature7() + diff),
                            criteriaBuilder.lessThan(documentTextLineSnippetRoot.get("feature8"), target.getFeature8() + diff),
                            criteriaBuilder.lessThan(documentTextLineSnippetRoot.get("feature9"), target.getFeature9() + diff),
                            criteriaBuilder.lessThan(documentTextLineSnippetRoot.get("feature10"), target.getFeature10() + diff),
                            criteriaBuilder.lessThan(documentTextLineSnippetRoot.get("feature11"), target.getFeature11() + diff),
                            criteriaBuilder.lessThan(documentTextLineSnippetRoot.get("feature12"), target.getFeature12() + diff),
                            criteriaBuilder.lessThan(documentTextLineSnippetRoot.get("feature13"), target.getFeature13() + diff),
                            criteriaBuilder.lessThan(documentTextLineSnippetRoot.get("feature14"), target.getFeature14() + diff),
                            criteriaBuilder.lessThan(documentTextLineSnippetRoot.get("feature15"), target.getFeature15() + diff),
                            criteriaBuilder.lessThan(documentTextLineSnippetRoot.get("feature16"), target.getFeature16() + diff),
                            criteriaBuilder.lessThan(documentTextLineSnippetRoot.get("feature17"), target.getFeature17() + diff),
                            criteriaBuilder.lessThan(documentTextLineSnippetRoot.get("feature18"), target.getFeature18() + diff),
                            criteriaBuilder.lessThan(documentTextLineSnippetRoot.get("feature19"), target.getFeature19() + diff),
                            criteriaBuilder.lessThan(documentTextLineSnippetRoot.get("feature20"), target.getFeature20() + diff)
                            , criteriaBuilder.equal(documentTextLineSnippetRoot.get("isValidation"), true)

                    )
            );

            TypedQuery<DocumentTextLineSnippet> query = session.createQuery(criteriaQuery).setMaxResults(maxResults);
            List<DocumentTextLineSnippet> documentTextLineSnippets = query.getResultList();

            session.close();
            return documentTextLineSnippets;
        }

    }


    public DocumentTextLineSnippet getByImageLocation(String imageLocation) {
        try (Session session = SessionFactorySingleton.getSessionFactory().openSession()) {

            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

            CriteriaQuery<DocumentTextLineSnippet> criteriaQuery = criteriaBuilder.createQuery(DocumentTextLineSnippet.class);
            Root<DocumentTextLineSnippet> documentTextLineSnippetRoot = criteriaQuery.from(DocumentTextLineSnippet.class);

            criteriaQuery.where(
                    criteriaBuilder.and(
                            criteriaBuilder.equal(documentTextLineSnippetRoot.get("imageLocation"), imageLocation)
                    )
            );
            TypedQuery<DocumentTextLineSnippet> query = session.createQuery(criteriaQuery);
            List<DocumentTextLineSnippet> documentTextLineSnippets = query.getResultList();
            if (documentTextLineSnippets.size() > 0) {
                session.close();
                return documentTextLineSnippets.get(0);
            }
            session.close();
            return null;
        }
    }

    public DocumentTextLineSnippet getByImageLocationAndModel(String imageLocation, String model) {
        try (Session session = SessionFactorySingleton.getSessionFactory().openSession()) {

            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

            CriteriaQuery<DocumentTextLineSnippet> criteriaQuery = criteriaBuilder.createQuery(DocumentTextLineSnippet.class);
            Root<DocumentTextLineSnippet> documentTextLineSnippetRoot = criteriaQuery.from(DocumentTextLineSnippet.class);

            criteriaQuery.where(
                    criteriaBuilder.and(
//                        criteriaBuilder.equal(documentTextLineSnippetRoot.get("isValidation"), true),
                            criteriaBuilder.equal(documentTextLineSnippetRoot.get("imageLocation"), imageLocation),
                            criteriaBuilder.equal(documentTextLineSnippetRoot.get("model"), model)
                    )
            );
            TypedQuery<DocumentTextLineSnippet> query = session.createQuery(criteriaQuery);
            List<DocumentTextLineSnippet> documentTextLineSnippets = query.getResultList();
            if (documentTextLineSnippets.size() > 0) {
                session.close();
                return documentTextLineSnippets.get(0);
            }
            session.close();
            return null;
        }
    }

    public List<DocumentTextLineSnippet> getValidationSet(int limit, String model) {
        try (Session session = SessionFactorySingleton.getSessionFactory().openSession()) {

            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

            CriteriaQuery<DocumentTextLineSnippet> criteriaQuery = criteriaBuilder.createQuery(DocumentTextLineSnippet.class);
            Root<DocumentTextLineSnippet> documentTextLineSnippetRoot = criteriaQuery.from(DocumentTextLineSnippet.class);

            criteriaQuery.where(
                    criteriaBuilder.and(
                            criteriaBuilder.equal(documentTextLineSnippetRoot.get("isValidation"), true),
                            criteriaBuilder.equal(documentTextLineSnippetRoot.get("model"), model)//,
//                        criteriaBuilder.notEqual(documentTextLineSnippetRoot.get("tag"), "unknown")
                    )
            );

            TypedQuery<DocumentTextLineSnippet> query = session.createQuery(criteriaQuery);
            query.setMaxResults(limit);
            List<DocumentTextLineSnippet> documentTextLineSnippets = query.getResultList();

            session.close();
            return documentTextLineSnippets;
        }
    }

    public List<DocumentTextLineSnippet> getValidationSetWithoutUnknown(int limit, String model) {
        try (Session session = SessionFactorySingleton.getSessionFactory().openSession()) {

            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

            CriteriaQuery<DocumentTextLineSnippet> criteriaQuery = criteriaBuilder.createQuery(DocumentTextLineSnippet.class);
            Root<DocumentTextLineSnippet> documentTextLineSnippetRoot = criteriaQuery.from(DocumentTextLineSnippet.class);

            criteriaQuery.where(
                    criteriaBuilder.and(
                            criteriaBuilder.equal(documentTextLineSnippetRoot.get("isValidation"), true),
                            criteriaBuilder.equal(documentTextLineSnippetRoot.get("model"), model),
                            criteriaBuilder.notEqual(documentTextLineSnippetRoot.get("tag"), "unknown")
                    )
            );

            TypedQuery<DocumentTextLineSnippet> query = session.createQuery(criteriaQuery);
            query.setMaxResults(limit);
            List<DocumentTextLineSnippet> documentTextLineSnippets = query.getResultList();

            session.close();
            return documentTextLineSnippets;
        }
    }

    public List<DocumentTextLineSnippet> getTrainSet(int limit) {
        try (Session session = SessionFactorySingleton.getSessionFactory().openSession()) {

            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

            CriteriaQuery<DocumentTextLineSnippet> criteriaQuery = criteriaBuilder.createQuery(DocumentTextLineSnippet.class);
            Root<DocumentTextLineSnippet> documentTextLineSnippetRoot = criteriaQuery.from(DocumentTextLineSnippet.class);

            criteriaQuery.where(
                    criteriaBuilder.and(
                            criteriaBuilder.equal(documentTextLineSnippetRoot.get("isValidation"), false)//,
//                        criteriaBuilder.notEqual(documentTextLineSnippetRoot.get("tag"), "14-2")
                    )
            );

            TypedQuery<DocumentTextLineSnippet> query = session.createQuery(criteriaQuery);
            query.setMaxResults(limit);
            List<DocumentTextLineSnippet> documentTextLineSnippets = query.getResultList();

            session.close();
            return documentTextLineSnippets;
        }
    }

    public List<DocumentTextLineSnippet> getByDocumentImageId(Integer documentImageId) {
        try (Session session = SessionFactorySingleton.getSessionFactory().openSession()) {

            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

            CriteriaQuery<DocumentTextLineSnippet> criteriaQuery = criteriaBuilder.createQuery(DocumentTextLineSnippet.class);
            Root<DocumentTextLineSnippet> documentTextLineSnippetRoot = criteriaQuery.from(DocumentTextLineSnippet.class);

            criteriaQuery.where(
                    criteriaBuilder.equal(documentTextLineSnippetRoot.get("documentImageId"), documentImageId)
            );

            TypedQuery<DocumentTextLineSnippet> query = session.createQuery(criteriaQuery);
            List<DocumentTextLineSnippet> documentTextLineSnippets = query.getResultList();

            session.close();
            return documentTextLineSnippets;
        }
    }

    public List<String> getDistinctModels() {
        try (Session session = SessionFactorySingleton.getSessionFactory().openSession()) {


            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

            CriteriaQuery<String> criteriaQuery = criteriaBuilder.createQuery(String.class);
            Root<DocumentTextLineSnippet> documentTextLineSnippetRoot = criteriaQuery.from(DocumentTextLineSnippet.class);

            criteriaQuery.select(documentTextLineSnippetRoot.get("model")).distinct(true);
            criteriaQuery.orderBy(criteriaBuilder.asc(documentTextLineSnippetRoot.get("model")));

            TypedQuery<String> query = session.createQuery(criteriaQuery);
            List<String> documentTextLineSnippets = query.getResultList();

            session.close();
            return documentTextLineSnippets;
        }
    }
}
