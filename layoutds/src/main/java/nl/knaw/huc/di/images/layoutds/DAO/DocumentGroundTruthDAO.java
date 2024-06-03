package nl.knaw.huc.di.images.layoutds.DAO;

import nl.knaw.huc.di.images.layoutds.SessionFactorySingleton;
import nl.knaw.huc.di.images.layoutds.models.DocumentGroundTruth;
import nl.knaw.huc.di.images.layoutds.models.DocumentGroundTruthStatus;
import nl.knaw.huc.di.images.layoutds.models.pim.PimUser;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.List;

public class DocumentGroundTruthDAO extends GenericDAO<DocumentGroundTruth> {

    public DocumentGroundTruthDAO() {
        super(DocumentGroundTruth.class);
    }

    public DocumentGroundTruth getLatestForUserByImageUri(Long userId, Long documentImageId) {
        try (Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<DocumentGroundTruth> criteriaQuery = criteriaBuilder.createQuery(DocumentGroundTruth.class);

            Root<DocumentGroundTruth> groundTruthRoot = criteriaQuery.from(DocumentGroundTruth.class);
            Predicate documentIdRestriction = criteriaBuilder.equal(groundTruthRoot.get("documentImageId"), documentImageId);

            Subquery<PimUser> userSubquery = criteriaQuery.subquery(PimUser.class);
            Root<PimUser> userRoot = userSubquery.from(PimUser.class);
            userSubquery.select(userRoot);
            Predicate userRestriction = criteriaBuilder.equal(userRoot.get("id"), userId);
            userSubquery.where(userRestriction);

            criteriaQuery.where(
                    criteriaBuilder.and(
                            documentIdRestriction,
                            criteriaBuilder.exists(userSubquery)
                    )
            );
            criteriaQuery.orderBy(criteriaBuilder.desc(groundTruthRoot.get("made")));

            Query<DocumentGroundTruth> query = session.createQuery(criteriaQuery);
            List<DocumentGroundTruth> resultList = query.getResultList();
            DocumentGroundTruth result = resultList.stream().findFirst().orElse(null);
            session.close();

            return result;
        }
    }

    public List<DocumentGroundTruth> getLatest(Session session, int userId) {
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

        CriteriaQuery<DocumentGroundTruth> criteriaQuery = criteriaBuilder.createQuery(typeParameterClass);
        Root<DocumentGroundTruth> documentImageRoot = criteriaQuery.from(typeParameterClass);
        criteriaQuery.select(documentImageRoot);

        criteriaQuery.where(criteriaBuilder.equal(documentImageRoot.get("groundTruthStatus"), DocumentGroundTruthStatus.GroundTruth));
        TypedQuery<DocumentGroundTruth> query = session.createQuery(criteriaQuery);

        List<DocumentGroundTruth> results = query.getResultList();

        return results;
    }
}
