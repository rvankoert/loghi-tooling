package nl.knaw.huc.di.images.layoutds.DAO;

import nl.knaw.huc.di.images.layoutds.models.ElasticSearchIndex;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class ElasticSearchIndexDAO extends GenericDAO<ElasticSearchIndex> {

    public ElasticSearchIndexDAO() {
        super(ElasticSearchIndex.class);
    }

    public ElasticSearchIndex getByName(Session session, String url) {
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

        CriteriaQuery<ElasticSearchIndex> criteriaQuery = criteriaBuilder.createQuery(ElasticSearchIndex.class);
        Root<ElasticSearchIndex> elasticSearchIndexRoot = criteriaQuery.from(ElasticSearchIndex.class);

        criteriaQuery.where(criteriaBuilder.equal(elasticSearchIndexRoot.get("name"), url));
        TypedQuery<ElasticSearchIndex> query = session.createQuery(criteriaQuery);
        List<ElasticSearchIndex> elasticSearchIndices = query.getResultList();

        if (elasticSearchIndices.size() == 1) {
            return elasticSearchIndices.get(0);
        } else {
            return null;
        }
    }

    public ElasticSearchIndex getByNameOrCreate(Session session, String url) {
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

        CriteriaQuery<ElasticSearchIndex> criteriaQuery = criteriaBuilder.createQuery(ElasticSearchIndex.class);
        Root<ElasticSearchIndex> elasticSearchIndexRoot = criteriaQuery.from(ElasticSearchIndex.class);

        criteriaQuery.where(criteriaBuilder.equal(elasticSearchIndexRoot.get("name"), url));
        TypedQuery<ElasticSearchIndex> query = session.createQuery(criteriaQuery);
        List<ElasticSearchIndex> elasticSearchIndices = query.getResultList();

        if (elasticSearchIndices.size() == 1) {
            return elasticSearchIndices.get(0);
        } else {
            ElasticSearchIndex elasticSearchIndex = new ElasticSearchIndex();
            elasticSearchIndex.setName(url);
            Transaction transaction = session.beginTransaction();
            save(session, elasticSearchIndex);
            transaction.commit();
            return elasticSearchIndex;
        }
    }

}
