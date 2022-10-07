package nl.knaw.huc.di.images.layoutds.DAO;

import nl.knaw.huc.di.images.layoutds.models.prizepapers.HCADescription;
import org.hibernate.Session;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class HCADescriptionDAO extends GenericDAO<HCADescription> {

    public HCADescriptionDAO() {
        super(HCADescription.class);
    }

    public HCADescription getHCADescriptionByCode(Session session, String code) throws DuplicateDataException {

        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

        CriteriaQuery<HCADescription> criteriaQuery = criteriaBuilder.createQuery(HCADescription.class);
        Root<HCADescription> hcaDescriptionRoot = criteriaQuery.from(HCADescription.class);

        criteriaQuery.where(criteriaBuilder.equal(hcaDescriptionRoot.get("code"), code));
        TypedQuery<HCADescription> query = session.createQuery(criteriaQuery);
        List<HCADescription> hcaDescriptions = query.getResultList();

        if (hcaDescriptions.size() == 1) {
            return hcaDescriptions.get(0);
        } else if (hcaDescriptions.size() > 1) {
            throw new DuplicateDataException("duplicate data");
        } else {
            return null;
        }

    }
}