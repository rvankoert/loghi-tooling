package nl.knaw.huc.di.images.layoutds.DAO;

import nl.knaw.huc.di.images.layoutds.models.DocumentOCRResult;
import nl.knaw.huc.di.images.layoutds.models.pim.PimSimpleTextLine;
import org.hibernate.Session;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class PimSimpleTextLineDAO extends GenericDAO<PimSimpleTextLine> {

    public PimSimpleTextLineDAO() {
        super(PimSimpleTextLine.class);
    }

    public PimSimpleTextLine getByKey(Session session, String pageXmlLocation, String lineId) {

        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

        CriteriaQuery<PimSimpleTextLine> criteriaQuery = criteriaBuilder.createQuery(PimSimpleTextLine.class);
        Root<PimSimpleTextLine> pimSimpleTextLineRoot = criteriaQuery.from(PimSimpleTextLine.class);

        criteriaQuery.where(
                criteriaBuilder.and(
                        criteriaBuilder.equal(pimSimpleTextLineRoot.get("pageXmlLocation"), pageXmlLocation),
                        criteriaBuilder.equal(pimSimpleTextLineRoot.get("lineId"), lineId)
                )
        );
        TypedQuery<PimSimpleTextLine> query = session.createQuery(criteriaQuery);
        List<PimSimpleTextLine> pimSimpleTextLines = query.getResultList();

        if (pimSimpleTextLines.size() == 0) {
            return null;
        }
        return pimSimpleTextLines.get(0);
    }

    public PimSimpleTextLine getRandomLine(Session session, String contains, double maxTotalConf, double maxLowestConf, DocumentOCRResult documentOCRResult) {
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

        CriteriaQuery<PimSimpleTextLine> criteriaQuery = criteriaBuilder.createQuery(PimSimpleTextLine.class);
        Root<PimSimpleTextLine> pimSimpleTextLineRoot = criteriaQuery.from(PimSimpleTextLine.class);
        List<Predicate> predList = new ArrayList<>();
        predList.add(criteriaBuilder.or(
                criteriaBuilder.isNull(pimSimpleTextLineRoot.get("skip")),
                criteriaBuilder.isFalse(pimSimpleTextLineRoot.get("skip"))
        ));

        predList.add(criteriaBuilder.or(
                criteriaBuilder.isNull(pimSimpleTextLineRoot.get("groundTruth")),
                criteriaBuilder.equal(pimSimpleTextLineRoot.get("groundTruth"), "")
        ));
        predList.add(criteriaBuilder.lessThan(pimSimpleTextLineRoot.get("confidence"), maxTotalConf));
        predList.add(criteriaBuilder.lessThan(pimSimpleTextLineRoot.get("lowestConf"), maxLowestConf));
        predList.add(criteriaBuilder.like(pimSimpleTextLineRoot.get("text"), "%" + contains + "%"));
        predList.add(criteriaBuilder.like(pimSimpleTextLineRoot.get("pageXmlLocation"), "/home/rutger/republic/%"));
        if (documentOCRResult != null) {
            predList.add(criteriaBuilder.equal(pimSimpleTextLineRoot.get("parent"), documentOCRResult));
        }
        Predicate[] predArray = new Predicate[predList.size()];
        predList.toArray(predArray);

        criteriaQuery.where(
                criteriaBuilder.and(
                        predArray
                )
        );
        criteriaQuery.orderBy(criteriaBuilder.desc(pimSimpleTextLineRoot.get("uuid")));

        TypedQuery<PimSimpleTextLine> query = session.createQuery(criteriaQuery).setMaxResults(1);
        List<PimSimpleTextLine> pimSimpleTextLines = query.getResultList();

        if (pimSimpleTextLines.size() == 0) {
            return null;
        }
        return pimSimpleTextLines.get(0);
    }

    public List<PimSimpleTextLine> getAllGroundTruth(Session session) {
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

        CriteriaQuery<PimSimpleTextLine> criteriaQuery = criteriaBuilder.createQuery(PimSimpleTextLine.class);
        Root<PimSimpleTextLine> pimSimpleTextLineRoot = criteriaQuery.from(PimSimpleTextLine.class);

        criteriaQuery.where(
                criteriaBuilder.and(
                        criteriaBuilder.or(
                                criteriaBuilder.isNull(pimSimpleTextLineRoot.get("skip")),
                                criteriaBuilder.isFalse(pimSimpleTextLineRoot.get("skip"))
                        ),
                        criteriaBuilder.isNotNull(pimSimpleTextLineRoot.get("groundTruth")),
                        criteriaBuilder.notEqual(pimSimpleTextLineRoot.get("groundTruth"), ""),
                        criteriaBuilder.like(pimSimpleTextLineRoot.get("pageXmlLocation"), "/home/rutger/republic/%")
                )
        );
        TypedQuery<PimSimpleTextLine> query = session.createQuery(criteriaQuery);
        List<PimSimpleTextLine> pimSimpleTextLines = query.getResultList();

        return pimSimpleTextLines;
    }

    public List<PimSimpleTextLine> getDifferentLines(Session session) {
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

        CriteriaQuery<PimSimpleTextLine> criteriaQuery = criteriaBuilder.createQuery(PimSimpleTextLine.class);
        Root<PimSimpleTextLine> pimSimpleTextLineRoot = criteriaQuery.from(PimSimpleTextLine.class);

        criteriaQuery.where(
                criteriaBuilder.and(
                        criteriaBuilder.isNotNull(pimSimpleTextLineRoot.get("groundTruth")),
                        criteriaBuilder.notEqual(pimSimpleTextLineRoot.get("groundTruth"), ""),
                        criteriaBuilder.notEqual(pimSimpleTextLineRoot.get("groundTruth"), pimSimpleTextLineRoot.get("text")),
                        criteriaBuilder.like(pimSimpleTextLineRoot.get("pageXmlLocation"), "/home/rutger/republic/%")
                )
        );
        TypedQuery<PimSimpleTextLine> query = session.createQuery(criteriaQuery);
        List<PimSimpleTextLine> pimSimpleTextLines = query.getResultList();

        return pimSimpleTextLines;
    }
}
