package nl.knaw.huc.di.images.layoutds.DAO;

import nl.knaw.huc.di.images.layoutds.SessionFactorySingleton;
import nl.knaw.huc.di.images.layoutds.models.DocumentManifest;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.UUID;

public class DocumentManifestDAO extends GenericDAO<DocumentManifest> {

    public DocumentManifestDAO() {
        super(DocumentManifest.class);
    }

    public DocumentManifest getBySeriesAndImageset(String series, String imageset) throws Exception {
        try (Session session = SessionFactorySingleton.getSessionFactory().openSession()) {

            Transaction transaction = session.beginTransaction();

            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

            CriteriaQuery<DocumentManifest> criteriaQuery = criteriaBuilder.createQuery(DocumentManifest.class);
            Root<DocumentManifest> documentImageRoot = criteriaQuery.from(DocumentManifest.class);

            criteriaQuery.where(
                    criteriaBuilder.and(
                            criteriaBuilder.equal(documentImageRoot.get("series"), series),
                            criteriaBuilder.equal(documentImageRoot.get("imageSet"), imageset)
                    )
            );
            TypedQuery<DocumentManifest> query = session.createQuery(criteriaQuery);
//        query.setParameter(p, uri);
            List<DocumentManifest> documentImages = query.getResultList();


            transaction.commit();
            session.close();
            if (documentImages.size() == 1) {
                return documentImages.get(0);
            } else if (documentImages.size() > 1) {
                throw new DuplicateDataException("duplicate data");
            } else {
                return null;
            }
        }
    }

    public DocumentManifest getByImageSetUUID(UUID imagesetUUID) throws DuplicateDataException {
        try (Session session = SessionFactorySingleton.getSessionFactory().openSession()) {

            Transaction transaction = session.beginTransaction();
            DocumentManifest documentManifest = getByImageSetUUID(session, imagesetUUID);
            transaction.commit();
            session.close();
            return documentManifest;
        }
    }

    public DocumentManifest getByImageSetUUID(Session session, UUID imagesetUUID) throws DuplicateDataException {

        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

        CriteriaQuery<DocumentManifest> criteriaQuery = criteriaBuilder.createQuery(DocumentManifest.class);
        Root<DocumentManifest> documentImageRoot = criteriaQuery.from(DocumentManifest.class);

        criteriaQuery.where(
                criteriaBuilder.equal(documentImageRoot.get("imagesetuuid"), imagesetUUID)
        );
        TypedQuery<DocumentManifest> query = session.createQuery(criteriaQuery);
        List<DocumentManifest> documentImages = query.getResultList();

        if (documentImages.size() == 1) {
            return documentImages.get(0);
        } else if (documentImages.size() > 1) {
            throw new DuplicateDataException("duplicate data");
        } else {
            return null;
        }
    }
}
