//package nl.knaw.huc.di.images.layoutds.DAO;
//
//import nl.knaw.huc.di.images.layoutds.SessionFactorySingleton;
//import nl.knaw.huc.di.images.layoutds.models.DocumentSeries;
//import org.hibernate.Session;
//import org.hibernate.Transaction;
//
//import javax.persistence.Tuple;
//import javax.persistence.TypedQuery;
//import javax.persistence.criteria.CriteriaBuilder;
//import javax.persistence.criteria.CriteriaQuery;
//import javax.persistence.criteria.Root;
//import java.util.List;
//import java.util.UUID;
//
//public class DocumentSeriesDAO extends GenericDAO<DocumentSeries> {
//
//    public DocumentSeriesDAO() {
//        super(DocumentSeries.class);
//    }
//
////    public DocumentSeries getDocumentSeries(String series) throws Exception {
////        Session session = SessionFactorySingleton.getSessionFactory().openSession();
////
////        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
////
////        CriteriaQuery<DocumentSeries> criteriaQuery = criteriaBuilder.createQuery(DocumentSeries.class);
////        Root<DocumentSeries> documentSeriesRoot = criteriaQuery.from(DocumentSeries.class);
////        criteriaQuery
////                .where(
////                        criteriaBuilder.and(
////                                criteriaBuilder.equal(documentSeriesRoot.get("series"),series)
////                        )
////                )
////        ;
////
////        TypedQuery<DocumentSeries> query = session.createQuery(criteriaQuery);
////        List<DocumentSeries> results = query.getResultList();
////
////        if (results.size() == 1) {
////            return results.get(0);
////        } else if (results.size() > 1) {
////            throw new Exception("duplicate data for series  "+ series );
////        } else {
////            return null;
////        }
////    }
//
//    public List<DocumentSeries> getPrettySeries() {
//        Session session = SessionFactorySingleton.getSessionFactory().openSession();
//
//        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
//
//        CriteriaQuery<DocumentSeries> criteriaQuery = criteriaBuilder.createQuery(DocumentSeries.class);
//        Root<DocumentSeries> documentSeriesRoot = criteriaQuery.from(DocumentSeries.class);
//        criteriaQuery
////                .multiselect(documentImageRoot.get("series"), documentImageRoot.get("prettyName"))
//                .distinct(true)
//                .orderBy(criteriaBuilder.asc(documentSeriesRoot.get("series")));
//
//        TypedQuery<DocumentSeries> query = session.createQuery(criteriaQuery);
//        return query.getResultList();
//    }
//
//
//    public List<DocumentSeries> getSeriesData() {
//        Session session = SessionFactorySingleton.getSessionFactory().openSession();
//
//        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
//
//        CriteriaQuery<DocumentSeries> criteriaQuery = criteriaBuilder.createQuery(DocumentSeries.class);
//        Root<DocumentSeries> documentSeriesRoot = criteriaQuery.from(DocumentSeries.class);
//        criteriaQuery
//                .where(
//                        criteriaBuilder.isNotNull(documentSeriesRoot.get("series"))
//                )
//
//        ;
//        criteriaQuery.orderBy(criteriaBuilder.asc(documentSeriesRoot.get("series")));
//
//        TypedQuery<DocumentSeries> query = session.createQuery(criteriaQuery);
//        return query.getResultList();
//    }
//
//    public DocumentSeries getDocumentSeries(Session session, String series) throws DuplicateDataException {
//
//        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
//
//        CriteriaQuery<DocumentSeries> criteriaQuery = criteriaBuilder.createQuery(DocumentSeries.class);
//        Root<DocumentSeries> documentSeriesRoot = criteriaQuery.from(DocumentSeries.class);
//        criteriaQuery
//                .where(
//                        criteriaBuilder.equal(documentSeriesRoot.get("series"), series)
//                )
//        ;
//
//        TypedQuery<DocumentSeries> query = session.createQuery(criteriaQuery);
//        List<DocumentSeries> results = query.getResultList();
//
//        if (results.size() == 1) {
//            return results.get(0);
//        } else if (results.size() > 1) {
//            throw new DuplicateDataException("duplicate data");
//        } else {
//            return null;
//        }
//    }
//
//    public DocumentSeries getDocumentSeriesByUri(Session session, String uri) throws DuplicateDataException {
//
//        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
//
//        CriteriaQuery<DocumentSeries> criteriaQuery = criteriaBuilder.createQuery(DocumentSeries.class);
//        Root<DocumentSeries> documentSeriesRoot = criteriaQuery.from(DocumentSeries.class);
//        criteriaQuery
//                .where(
//                        criteriaBuilder.equal(documentSeriesRoot.get("uri"), uri)
//                )
//        ;
//
//        TypedQuery<DocumentSeries> query = session.createQuery(criteriaQuery);
//        List<DocumentSeries> results = query.getResultList();
//
//        if (results.size() == 1) {
//            return results.get(0);
//        } else if (results.size() > 1) {
//            throw new DuplicateDataException("duplicate data");
//        } else {
//            return null;
//        }
//    }
//
//
//    public void setDescriptionForSeries(String series, String description) {
//        Session session = SessionFactorySingleton.getSessionFactory().openSession();
//
//        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
//
//        CriteriaQuery<DocumentSeries> cq = criteriaBuilder.createQuery(DocumentSeries.class);
//        Root<DocumentSeries> documentSeriesRoot = cq.from(DocumentSeries.class);
//        cq.where(
//                criteriaBuilder.and(
//                        criteriaBuilder.equal(documentSeriesRoot.get("series"), series)
//                )
//        );
//
//        TypedQuery<DocumentSeries> query = session.createQuery(cq);
//
//        Transaction transaction = session.beginTransaction();
//        DocumentSeries result = query.getSingleResult();
//        result.setSeriesDescription(description);
//        session.update(result);
//        transaction.commit();
//        session.close();
//    }
//
//    public DocumentSeries getByUUID(UUID uuid) {
//        Session session = SessionFactorySingleton.getSessionFactory().openSession();
//
//        Transaction transaction = session.beginTransaction();
//        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
//
//        CriteriaQuery<DocumentSeries> criteriaQuery = criteriaBuilder.createQuery(DocumentSeries.class);
//        Root<DocumentSeries> documentSeriesRoot = criteriaQuery.from(DocumentSeries.class);
//
//        criteriaQuery.where(criteriaBuilder.equal(documentSeriesRoot.get("uuid"), uuid));
//        TypedQuery<DocumentSeries> query = session.createQuery(criteriaQuery);
//        List<DocumentSeries> documentSeriesList = query.getResultList();
//
//        transaction.commit();
//        session.close();
//        if (documentSeriesList.size() == 1) {
//            return documentSeriesList.get(0);
//        } else {
//            return null;
//        }
//    }
//
//    public List<Tuple> getAllWithUUID() {
//        Session session = SessionFactorySingleton.getSessionFactory().openSession();
//
//        Transaction transaction = session.beginTransaction();
//        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
//
//        CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createQuery(Tuple.class);
//        Root<DocumentSeries> documentSeriesRoot = criteriaQuery.from(DocumentSeries.class);
//
//        criteriaQuery.multiselect(documentSeriesRoot.get("series"), documentSeriesRoot.get("uuid"));
//        TypedQuery<Tuple> query = session.createQuery(criteriaQuery);
//        List<Tuple> results = query.getResultList();
//        transaction.commit();
//        session.close();
//        return results;
//
//    }
//}
