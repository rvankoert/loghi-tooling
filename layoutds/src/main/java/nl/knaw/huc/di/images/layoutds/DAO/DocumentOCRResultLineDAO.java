package nl.knaw.huc.di.images.layoutds.DAO;

import nl.knaw.huc.di.images.layoutds.models.DocumentOCRResult;
import nl.knaw.huc.di.images.layoutds.models.DocumentOCRResultLine;
import org.hibernate.Session;

import javax.persistence.criteria.*;
import java.util.List;
import java.util.UUID;

public class DocumentOCRResultLineDAO extends GenericDAO<DocumentOCRResultLine> {

    public DocumentOCRResultLineDAO() {
        super(DocumentOCRResultLine.class);
    }

    public DocumentOCRResultLine getByLineIdAndParentId(Session session, String lineId, UUID pageUUID) {
        final CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        final CriteriaQuery<DocumentOCRResultLine> criteriaQuery = criteriaBuilder.createQuery(DocumentOCRResultLine.class);
        final Root<DocumentOCRResultLine> from = criteriaQuery.from(DocumentOCRResultLine.class);
        final Join<DocumentOCRResultLine, DocumentOCRResult> ocrResult = from.join("parent", JoinType.INNER);
        criteriaQuery.where(criteriaBuilder.and(
                criteriaBuilder.equal(from.get("lineId"), lineId),
                criteriaBuilder.equal(ocrResult.get("uuid"), pageUUID)
        ));

        final List<DocumentOCRResultLine> resultList = session.createQuery(criteriaQuery).getResultList();
        if (resultList.size() == 1) {
            return resultList.get(0);
        }
        return null;
    }
}
