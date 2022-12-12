package nl.knaw.huc.di.images.layoutds.DAO;

import nl.knaw.huc.di.images.layoutds.models.DocumentOCRResult;
import nl.knaw.huc.di.images.layoutds.models.TranscriptionFormat;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

public class DocumentOCRResultDAO extends GenericDAO<DocumentOCRResult> {

    public DocumentOCRResultDAO() {
        super(DocumentOCRResult.class);
    }

    public List<DocumentOCRResult> getByDocumentImageId(Session session, Long documentImageId) {
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

        CriteriaQuery<DocumentOCRResult> criteriaQuery = criteriaBuilder.createQuery(DocumentOCRResult.class);
        Root<DocumentOCRResult> documentOCRResultRoot = criteriaQuery.from(DocumentOCRResult.class);

        criteriaQuery.where(criteriaBuilder.equal(documentOCRResultRoot.get("documentImageId"), documentImageId));
        criteriaQuery.orderBy(
                criteriaBuilder.desc(documentOCRResultRoot.get("analyzed")),
                criteriaBuilder.desc(documentOCRResultRoot.get("id"))
        );
        TypedQuery<DocumentOCRResult> query = session.createQuery(criteriaQuery);
        List<DocumentOCRResult> documentOCRResults = query.getResultList();
        return documentOCRResults;
    }

    public DocumentOCRResult getByRemoteUrl(Session session, String remoteUrl) {
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

        CriteriaQuery<DocumentOCRResult> criteriaQuery = criteriaBuilder.createQuery(DocumentOCRResult.class);
        Root<DocumentOCRResult> documentOCRResultRoot = criteriaQuery.from(DocumentOCRResult.class);

        criteriaQuery.where(criteriaBuilder.equal(documentOCRResultRoot.get("remoteURL"), remoteUrl));
        TypedQuery<DocumentOCRResult> query = session.createQuery(criteriaQuery);
        List<DocumentOCRResult> documentOCRResults = query.setMaxResults(1).getResultList();
        if (documentOCRResults.size() > 0) {
            return documentOCRResults.get(0);
        }
        return null;
    }

    public List<DocumentOCRResult> getAllNonEmptyBefore(Session session, int limit, int skip, Date beforeDate) {
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

        CriteriaQuery<DocumentOCRResult> criteriaQuery = criteriaBuilder.createQuery(DocumentOCRResult.class);
        Root<DocumentOCRResult> ocrResultRoot = criteriaQuery.from(DocumentOCRResult.class);
        criteriaQuery.where(criteriaBuilder.and(
                criteriaBuilder.isFalse(ocrResultRoot.get("emptyPage")),
                criteriaBuilder.lessThan(ocrResultRoot.get("analyzed"), beforeDate)
        ));


        criteriaQuery.select(ocrResultRoot);

        TypedQuery<DocumentOCRResult> query = session.createQuery(criteriaQuery);
        query.setFirstResult(skip);
        query.setMaxResults(limit);


        return query.getResultList();
    }

    public DocumentOCRResult getLatestOcrResultOfImage(Session session, Long imageId, TranscriptionFormat format) {
        final CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        final CriteriaQuery<DocumentOCRResult> criteriaQuery = criteriaBuilder.createQuery(DocumentOCRResult.class);
        final Root<DocumentOCRResult> from = criteriaQuery.from(DocumentOCRResult.class);

        criteriaQuery.where(criteriaBuilder.and(
                criteriaBuilder.equal(from.get("documentImageId"), imageId),
                criteriaBuilder.equal(from.get("format"), format)
        ));
        criteriaQuery.orderBy(criteriaBuilder.desc(from.get("analyzed")));

        final Query<DocumentOCRResult> query = session.createQuery(criteriaQuery);
        query.setMaxResults(1);

        return query.getSingleResult();
    }

    public Stream<DocumentOCRResult> getLatestPageResults(Session session) {
        final CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        final CriteriaQuery<DocumentOCRResult> criteriaQuery = criteriaBuilder.createQuery(DocumentOCRResult.class);
        final Root<DocumentOCRResult> queryRoot = criteriaQuery.from(DocumentOCRResult.class);

        final Subquery<Long> subQuery = criteriaQuery.subquery(Long.class);
        final Root<DocumentOCRResult> subQueryRoot = subQuery.from(DocumentOCRResult.class);
        subQuery.where(criteriaBuilder.equal(subQueryRoot.get("format"), TranscriptionFormat.Page));
        subQuery.groupBy(subQueryRoot.get("documentImageId"));
        subQuery.select(criteriaBuilder.max(subQueryRoot.get("id")));

        criteriaQuery.where(queryRoot.get("id").in(subQuery));

        final Query<DocumentOCRResult> query = session.createQuery(criteriaQuery);

        return query.stream();
    }
}
