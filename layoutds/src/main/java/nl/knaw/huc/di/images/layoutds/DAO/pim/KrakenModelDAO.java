package nl.knaw.huc.di.images.layoutds.DAO.pim;

import nl.knaw.huc.di.images.layoutds.DAO.GenericDAO;
import nl.knaw.huc.di.images.layoutds.SessionFactorySingleton;
import nl.knaw.huc.di.images.layoutds.models.KrakenModel;
import nl.knaw.huc.di.images.layoutds.models.Language;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class KrakenModelDAO extends GenericDAO<KrakenModel> {
    public KrakenModelDAO() {
        super(KrakenModel.class);
    }

    public Map<String, byte[]> getBinaryModelById(long id) {
        try (Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            KrakenModel krakenModel = get(session, id);
            return krakenModel != null ? krakenModel.getModelData() : null;
        }
    }

    public KrakenModel getByUri(String uri) {
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

            CriteriaQuery<KrakenModel> criteriaQuery = criteriaBuilder.createQuery(KrakenModel.class);
            Root<KrakenModel> root = criteriaQuery.from(KrakenModel.class);

            criteriaQuery.where(criteriaBuilder.equal(root.get("uri"), uri));
            TypedQuery<KrakenModel> query = session.createQuery(criteriaQuery);
            List<KrakenModel> krakenModels = query.getResultList();

            if (krakenModels.size() == 1) {
                return krakenModels.get(0);
            } else {
                return null;
            }
        }
    }

    public Stream<KrakenModel> getAutocomplete(Session session, String filter, int limit, int skip) {
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        String likeFilter = "%" + filter.toLowerCase() + "%";

        CriteriaQuery<KrakenModel> criteriaQuery = criteriaBuilder.createQuery(KrakenModel.class);
        Root<KrakenModel> root = criteriaQuery.from(KrakenModel.class);
        final Join<KrakenModel, Language> language = root.join("language");
        criteriaQuery.where(criteriaBuilder.or(
                criteriaBuilder.like(criteriaBuilder.lower(root.get("uri")), likeFilter),
                criteriaBuilder.like(criteriaBuilder.lower(language.get("name")), likeFilter)
        ));

        final Query<KrakenModel> query = session.createQuery(criteriaQuery);

        return query.setMaxResults(limit).setFirstResult(skip).getResultStream();
    }
}
