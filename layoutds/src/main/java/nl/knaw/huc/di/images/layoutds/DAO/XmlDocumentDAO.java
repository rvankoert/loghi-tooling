package nl.knaw.huc.di.images.layoutds.DAO;

import nl.knaw.huc.di.images.layoutds.SessionFactorySingleton;
import nl.knaw.huc.di.images.layoutds.models.XmlDocument;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class XmlDocumentDAO extends GenericDAO<XmlDocument> {

    public XmlDocumentDAO() {
        super(XmlDocument.class);
    }


    public List<XmlDocument> getResources() {
        Session session = SessionFactorySingleton.getSessionFactory().openSession();

        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

        CriteriaQuery<XmlDocument> criteriaQuery = criteriaBuilder.createQuery(XmlDocument.class);
        Root<XmlDocument> documentImageRoot = criteriaQuery.from(XmlDocument.class);

        criteriaQuery.where(criteriaBuilder.like(documentImageRoot.get("uri"), "/data/resourcesfiles/%"));
        TypedQuery<XmlDocument> query = session.createQuery(criteriaQuery);
        List<XmlDocument> documentImages = query.getResultList();

        session.close();
        return documentImages;
    }

    public XmlDocument getByUri(String uri) throws DuplicateDataException {
        Session session = SessionFactorySingleton.getSessionFactory().openSession();

        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

        CriteriaQuery<XmlDocument> criteriaQuery = criteriaBuilder.createQuery(XmlDocument.class);
        Root<XmlDocument> documentImageRoot = criteriaQuery.from(XmlDocument.class);

        criteriaQuery.where(criteriaBuilder.equal(documentImageRoot.get("uri"), uri));
        TypedQuery<XmlDocument> query = session.createQuery(criteriaQuery);
        List<XmlDocument> documentImages = query.getResultList();

        session.close();
        if (documentImages.size() == 1) {
            return documentImages.get(0);
        } else if (documentImages.size() > 1) {
            throw new DuplicateDataException("duplicate data");
        } else {
            return null;
        }
    }


    public List<XmlDocument> getRandomLocalXmlDocumentWithoutImageSet(Session session, int limit) {
        final CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

        final CriteriaQuery<XmlDocument> criteriaQuery = criteriaBuilder.createQuery(XmlDocument.class);
        final Root<XmlDocument> xmlDocumentRoot = criteriaQuery.from(XmlDocument.class);
        criteriaQuery.select(xmlDocumentRoot).where(
                criteriaBuilder.or(
                        criteriaBuilder.isNull(xmlDocumentRoot.get("broken")),
                        criteriaBuilder.equal(xmlDocumentRoot.get("broken"), false)
                ),
                criteriaBuilder.isEmpty(xmlDocumentRoot.get("documentImageSets")),
                criteriaBuilder.isNotNull(xmlDocumentRoot.get("uri")),
                criteriaBuilder.like(xmlDocumentRoot.get("uri"), "/data/%")
        );

        final Query<XmlDocument> query = session.createQuery(criteriaQuery);

        return query.setMaxResults(limit).getResultList();
    }

    public List<XmlDocument> getDocsWithoutOriginalFileName(Session session) {
        final CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        final CriteriaQuery<XmlDocument> criteriaQuery = criteriaBuilder.createQuery(XmlDocument.class);
        final Root<XmlDocument> documentImageRoot = criteriaQuery.from(XmlDocument.class);
        criteriaQuery.select(documentImageRoot).where(
                criteriaBuilder.isNull(documentImageRoot.get("originalFileName"))
        );
        final Query<XmlDocument> query = session.createQuery(criteriaQuery);

        return query.getResultList();
    }


}
