//package nl.knaw.huc.di.images.layoutds.DAO;
//
//import nl.knaw.huc.di.images.layoutds.SessionFactorySingleton;
//import nl.knaw.huc.di.images.layoutds.models.pim.Dataset;
//import org.hibernate.Session;
//
//import javax.persistence.TypedQuery;
//import javax.persistence.criteria.CriteriaBuilder;
//import javax.persistence.criteria.CriteriaQuery;
//import javax.persistence.criteria.Root;
//import java.util.List;
//
//public class DataSetDao extends Generic<Dataset> {
//
//    public DataSetDao() {
//        super(Dataset.class);
//    }
//
//    public List<Dataset> getAllPublic(Session session) {
//        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
//
//        CriteriaQuery<Dataset> criteriaQuery = criteriaBuilder.createQuery(Dataset.class);
//        Root<Dataset> datasetRoot = criteriaQuery.from(Dataset.class);
//        criteriaQuery.select(datasetRoot).where(
//                criteriaBuilder.or(
//                        criteriaBuilder.isNull(datasetRoot.get("publicDataset")),
//                        criteriaBuilder.isTrue(datasetRoot.get("publicDataset"))
//                )
//        );
//        TypedQuery<Dataset> query = session.createQuery(criteriaQuery);
//        List<Dataset> results = query.getResultList();
//        return results;
//
//    }
//
//    public List<Dataset> getAllPublic() {
//        Session session = SessionFactorySingleton.getSessionFactory().openSession();
//        List<Dataset> results = getAllPublic(session);
//        session.close();
//        return results;
//    }
//
//
//}
