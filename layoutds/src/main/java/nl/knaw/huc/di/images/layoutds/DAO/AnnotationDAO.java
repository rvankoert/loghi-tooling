package nl.knaw.huc.di.images.layoutds.DAO;

import nl.knaw.huc.di.images.layoutds.models.Annotation.Annotation;
import nl.knaw.huc.di.images.layoutds.models.Annotation.On;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.List;

public class AnnotationDAO extends GenericDAO<Annotation> {

    public AnnotationDAO() {
        super(Annotation.class);
    }

    public List<Annotation> getByUri(Session session, String uri) {

        Transaction transaction = session.beginTransaction();
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

        CriteriaQuery<Annotation> criteriaQuery = criteriaBuilder.createQuery(Annotation.class);
        Root<Annotation> annotationRoot = criteriaQuery.from(Annotation.class);
        Join<Annotation, On> annotationOnJoin = annotationRoot.join("annotationOn", JoinType.INNER);

        criteriaQuery.where(criteriaBuilder.equal(annotationOnJoin.get("annotationFull"), uri));
        TypedQuery<Annotation> query = session.createQuery(criteriaQuery);

        List<Annotation> annotations = query.getResultList();

        transaction.commit();
        return annotations;
    }

}
