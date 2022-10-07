package nl.knaw.huc.di.images.layoutds.DAO;

import nl.knaw.huc.di.images.layoutds.SessionFactorySingleton;
import nl.knaw.huc.di.images.layoutds.models.IIIFDocument;
import nl.knaw.huc.di.images.layoutds.models.iiif.FlatManifest;
import nl.knaw.huc.di.images.layoutds.models.pim.PimUser;
import org.hibernate.Session;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class IIIFDocumentDAO extends GenericDAO<IIIFDocument> {

    public IIIFDocumentDAO() {
        super(IIIFDocument.class);
    }

    public IIIFDocument getByUri(String url) throws DuplicateDataException {
        Session session = SessionFactorySingleton.getSessionFactory().openSession();

        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

        CriteriaQuery<IIIFDocument> criteriaQuery = criteriaBuilder.createQuery(IIIFDocument.class);
        Root<IIIFDocument> documentImageRoot = criteriaQuery.from(IIIFDocument.class);

        criteriaQuery.where(criteriaBuilder.equal(documentImageRoot.get("uri"), url));
        TypedQuery<IIIFDocument> query = session.createQuery(criteriaQuery);
//        query.setParameter(p, uri);
        List<IIIFDocument> documentImages = query.getResultList();

        session.close();
        if (documentImages.size() == 1) {
            return documentImages.get(0);
        } else if (documentImages.size() > 1) {
            throw new DuplicateDataException("duplicate data");
        } else {
            return null;
        }
    }

    public List<FlatManifest> getAllFlatManifest(Session session, PimUser pimUser, boolean onlyOwnData) {
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

        CriteriaQuery<FlatManifest> criteriaQuery = criteriaBuilder.createQuery(FlatManifest.class);
        Root<IIIFDocument> iiifDocumentRoot = criteriaQuery.from(typeParameterClass);

        criteriaQuery.multiselect(
                iiifDocumentRoot.get("uri"),
                iiifDocumentRoot.get("IIIFId"),
                iiifDocumentRoot.get("type"),
                iiifDocumentRoot.get("label")
        );
        if (onlyOwnData && pimUser != null) {
            criteriaQuery.where(criteriaBuilder.equal(iiifDocumentRoot.get("owner"), pimUser));
        }

        TypedQuery<FlatManifest> query = session.createQuery(criteriaQuery);
        List<FlatManifest> results = query.getResultList();

        return results;
    }


}
