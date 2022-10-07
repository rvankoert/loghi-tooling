package nl.knaw.huc.di.images.layoutds.DAO;

import nl.knaw.huc.di.images.layoutds.models.pim.PageImagePair;
import org.hibernate.Session;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class PageImagePairDAO extends GenericDAO<PageImagePair> {

    public PageImagePairDAO() {
        super(PageImagePair.class);
    }

    public List<PageImagePair> getAllUnProcessed(Session session) {
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

        CriteriaQuery<PageImagePair> criteriaQuery = criteriaBuilder.createQuery(PageImagePair.class);
        Root<PageImagePair> pageImagePairRoot = criteriaQuery.from(PageImagePair.class);
        criteriaQuery.where(
                criteriaBuilder.isTrue(pageImagePairRoot.get("readyForBaselineExtraction"))
        );
        criteriaQuery.select(pageImagePairRoot);

        TypedQuery<PageImagePair> query = session.createQuery(criteriaQuery);

        return query.getResultList();
    }
}
