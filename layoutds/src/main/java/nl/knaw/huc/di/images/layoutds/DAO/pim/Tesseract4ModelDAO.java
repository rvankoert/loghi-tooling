package nl.knaw.huc.di.images.layoutds.DAO.pim;

import com.google.common.io.Files;
import nl.knaw.huc.di.images.layoutds.DAO.GenericDAO;
import nl.knaw.huc.di.images.layoutds.SessionFactorySingleton;
import nl.knaw.huc.di.images.layoutds.models.Language;
import nl.knaw.huc.di.images.layoutds.models.pim.Tesseract4Model;
import org.hibernate.Session;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class Tesseract4ModelDAO extends GenericDAO<Tesseract4Model> {

    public Tesseract4ModelDAO() {
        super(Tesseract4Model.class);
    }

    public Tesseract4Model getByUri(String tesseract4modelName) {
        Session session = SessionFactorySingleton.getSessionFactory().openSession();
        Tesseract4Model tesseract4model = getByUri(session, tesseract4modelName);
        session.close();
        return tesseract4model;
    }

    public Tesseract4Model getByUri(Session session, String tesseract4modelName) {
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

        CriteriaQuery<Tesseract4Model> criteriaQuery = criteriaBuilder.createQuery(Tesseract4Model.class);
        Root<Tesseract4Model> tesseract4ModelRoot = criteriaQuery.from(Tesseract4Model.class);

        criteriaQuery.where(criteriaBuilder.equal(tesseract4ModelRoot.get("uri"), tesseract4modelName));
        TypedQuery<Tesseract4Model> query = session.createQuery(criteriaQuery);
        List<Tesseract4Model> tesseract4Models = query.getResultList();

        if (tesseract4Models.size() == 1) {
            return tesseract4Models.get(0);
        } else {
            return null;
        }
    }

    public Map<String, byte[]> getBinaryModelById(long id) {
        try (Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            Tesseract4Model tesseract4Model = get(session, id);
            return tesseract4Model != null ? tesseract4Model.getModelData() : null;
        }
    }

    public Tesseract4Model getByUriAndLanguage(Session session, String uri, Language language) {
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

        CriteriaQuery<Tesseract4Model> criteriaQuery = criteriaBuilder.createQuery(Tesseract4Model.class);
        Root<Tesseract4Model> tesseract4ModelRoot = criteriaQuery.from(Tesseract4Model.class);

        criteriaQuery.where(criteriaBuilder.and(
                criteriaBuilder.equal(tesseract4ModelRoot.get("uri"), uri),
                criteriaBuilder.equal(tesseract4ModelRoot.get("language"), language)
        ));
        TypedQuery<Tesseract4Model> query = session.createQuery(criteriaQuery);
        List<Tesseract4Model> tesseract4Models = query.getResultList();

        if (tesseract4Models.size() == 1) {
            return tesseract4Models.get(0);
        } else {
            return null;
        }
    }

    public Stream<Tesseract4Model> getAutocomplete(Session session, String filter, int limit, int skip) {
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

        CriteriaQuery<Tesseract4Model> criteriaQuery = criteriaBuilder.createQuery(Tesseract4Model.class);
        Root<Tesseract4Model> tesseract4ModelRoot = criteriaQuery.from(Tesseract4Model.class);
        final Join<Tesseract4Model, Language> language = tesseract4ModelRoot.join("language");

        final String likeFilter = "%" + filter.toLowerCase() + "%";
        criteriaQuery.where(criteriaBuilder.or(
                criteriaBuilder.like(criteriaBuilder.lower(tesseract4ModelRoot.get("uri")), likeFilter),
                criteriaBuilder.like(criteriaBuilder.lower(language.get("name")), likeFilter)
        ));
        TypedQuery<Tesseract4Model> query = session.createQuery(criteriaQuery);
        query.setMaxResults(limit);
        query.setFirstResult(skip);

        return query.getResultStream();
    }
}
