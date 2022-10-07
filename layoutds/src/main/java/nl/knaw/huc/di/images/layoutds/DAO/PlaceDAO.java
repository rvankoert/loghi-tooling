package nl.knaw.huc.di.images.layoutds.DAO;

import nl.knaw.huc.di.images.layoutds.SessionFactorySingleton;
import nl.knaw.huc.di.images.layoutds.models.Place;
import org.hibernate.Session;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class PlaceDAO extends GenericDAO<Place> {

    public PlaceDAO() {
        super(Place.class);
    }

    public List<Place> getByName(String placeString) {
        Session session = SessionFactorySingleton.getSessionFactory().openSession();

        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

        CriteriaQuery<Place> criteriaQuery = criteriaBuilder.createQuery(Place.class);
        Root<Place> placeRoot = criteriaQuery.from(Place.class);

        criteriaQuery.where(criteriaBuilder.equal(placeRoot.get("name"), placeString));
        TypedQuery<Place> query = session.createQuery(criteriaQuery);
        List<Place> places = query.getResultList();

        session.close();
        return places;
    }

    public Place getRandomHocrPlaceNotChecked(Session session) {

        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

        CriteriaQuery<Place> criteriaQuery = criteriaBuilder.createQuery(Place.class);
        Root<Place> placeRoot = criteriaQuery.from(Place.class);
        criteriaQuery.select(placeRoot).where(
                criteriaBuilder.and(
                        criteriaBuilder.isNull(placeRoot.get("checked"))
                )
        );
        TypedQuery<Place> query = session.createQuery(criteriaQuery);
        List<Place> results = query.setMaxResults(1).getResultList();

        if (results.size() > 0) {
            return results.get(0);
        } else {
            return null;
        }
    }

    public List<Place> getByTag(String tag) {
        Session session = SessionFactorySingleton.getSessionFactory().openSession();

        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

        CriteriaQuery<Place> criteriaQuery = criteriaBuilder.createQuery(Place.class);
        Root<Place> placeRoot = criteriaQuery.from(Place.class);

        criteriaQuery.where(
                criteriaBuilder.and(
                        criteriaBuilder.isNotNull(placeRoot.get("longitude")),
                        criteriaBuilder.equal(placeRoot.get("tag"), tag)
                )
        );
        TypedQuery<Place> query = session.createQuery(criteriaQuery);
        List<Place> places = query.getResultList();

        session.close();
        return places;
    }
}
