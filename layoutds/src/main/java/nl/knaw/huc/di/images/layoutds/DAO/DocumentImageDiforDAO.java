package nl.knaw.huc.di.images.layoutds.DAO;

import nl.knaw.huc.di.images.layoutds.SessionFactorySingleton;
import nl.knaw.huc.di.images.layoutds.models.DocumentImage;
import nl.knaw.huc.di.images.layoutds.models.DocumentImageDifor;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class DocumentImageDiforDAO extends GenericDAO<DocumentImageDifor> {

    public DocumentImageDiforDAO() {
        super(DocumentImageDifor.class);
    }

    public DocumentImageDifor getByDocumentImage(DocumentImage documentImage) {
        try (Session session = SessionFactorySingleton.getSessionFactory().openSession()) {

            Transaction transaction = session.beginTransaction();
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

            CriteriaQuery<DocumentImageDifor> criteriaQuery = criteriaBuilder.createQuery(DocumentImageDifor.class);
            Root<DocumentImageDifor> documentImageDiforRoot = criteriaQuery.from(DocumentImageDifor.class);

            criteriaQuery.where(criteriaBuilder.equal(documentImageDiforRoot.get("documentImage"), documentImage));
            TypedQuery<DocumentImageDifor> query = session.createQuery(criteriaQuery);
            List<DocumentImageDifor> documentImages = query.getResultList();

            transaction.commit();
            session.close();
            if (documentImages.size() == 1) {
                return documentImages.get(0);
            } else {
                return null;
            }
        }
    }
}
