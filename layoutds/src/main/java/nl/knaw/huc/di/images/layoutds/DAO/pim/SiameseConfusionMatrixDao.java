package nl.knaw.huc.di.images.layoutds.DAO.pim;

import nl.knaw.huc.di.images.layoutds.DAO.GenericDAO;
import nl.knaw.huc.di.images.layoutds.models.pim.SiameseConfusionMatrix;
import org.hibernate.Session;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.stream.Stream;

public class SiameseConfusionMatrixDao extends GenericDAO<SiameseConfusionMatrix> {
    public SiameseConfusionMatrixDao() {
        super(SiameseConfusionMatrix.class);
    }

    public Stream<SiameseConfusionMatrix> getAllStreamingOrderByDate(Session session) {
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<SiameseConfusionMatrix> criteriaQuery = criteriaBuilder.createQuery(SiameseConfusionMatrix.class);
        Root<SiameseConfusionMatrix> root = criteriaQuery.from(SiameseConfusionMatrix.class);
        criteriaQuery.orderBy(criteriaBuilder.desc(root.get("created")));
        TypedQuery<SiameseConfusionMatrix> query = session.createQuery(criteriaQuery);

        return query.getResultStream();
    }
}
