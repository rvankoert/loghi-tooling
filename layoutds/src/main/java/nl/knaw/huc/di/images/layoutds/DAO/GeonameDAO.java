package nl.knaw.huc.di.images.layoutds.DAO;

import nl.knaw.huc.di.images.layoutds.SessionFactorySingleton;
import nl.knaw.huc.di.images.layoutds.models.Geoname;
import org.hibernate.Session;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class GeonameDAO extends GenericDAO<Geoname> {

    public GeonameDAO() {
        super(Geoname.class);
    }

    public List<Geoname> getByName(String name) {
        try (Session session = SessionFactorySingleton.getSessionFactory().openSession()) {

            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

            CriteriaQuery<Geoname> criteriaQuery = criteriaBuilder.createQuery(Geoname.class);
            Root<Geoname> geonameRoot = criteriaQuery.from(Geoname.class);

            criteriaQuery.where(criteriaBuilder.equal(geonameRoot.get("name"), name));
            TypedQuery<Geoname> query = session.createQuery(criteriaQuery);
            List<Geoname> geonames = query.getResultList();

            session.close();
            return geonames;
        }
    }

    public List<Geoname> getByMetaphone(String metaphone) {
        try (Session session = SessionFactorySingleton.getSessionFactory().openSession()) {

            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

            CriteriaQuery<Geoname> criteriaQuery = criteriaBuilder.createQuery(Geoname.class);
            Root<Geoname> geonameRoot = criteriaQuery.from(Geoname.class);

            criteriaQuery.where(criteriaBuilder.equal(geonameRoot.get("metaphone"), metaphone));
            TypedQuery<Geoname> query = session.createQuery(criteriaQuery);
            List<Geoname> geonames = query.getResultList();

            session.close();
            return geonames;
        }
    }
}
