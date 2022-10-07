//package nl.knaw.huc.di.images.layoutds.DAO.Authorization;
//
//import nl.knaw.huc.di.images.layoutds.DAO.Generic;
//import nl.knaw.huc.di.images.layoutds.SessionFactorySingleton;
//import nl.knaw.huc.di.images.layoutds.models.authorization.AuthorizationGroup;
//import nl.knaw.huc.di.images.layoutds.models.authorization.AuthorizationUser;
//import org.hibernate.Session;
//
//import javax.persistence.TypedQuery;
//import javax.persistence.criteria.CriteriaBuilder;
//import javax.persistence.criteria.CriteriaQuery;
//import javax.persistence.criteria.Root;
//import java.util.List;
//
//public class AuthorizationGroupDAO extends Generic<AuthorizationGroup> {
//
//    public AuthorizationGroupDAO() {
//        super(AuthorizationGroup.class);
//    }
//
//
//    //FIXME RUTGERCHECK: remove file?
//    public List<AuthorizationGroup> getByUser(AuthorizationUser user) {
//        Session session = SessionFactorySingleton.getSessionFactory().openSession();
//
//        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
//
//        CriteriaQuery<AuthorizationGroup> criteriaQuery = criteriaBuilder.createQuery(AuthorizationGroup.class);
//        Root<AuthorizationGroup> authorizationGroupRoot = criteriaQuery.from(AuthorizationGroup.class);
//        criteriaQuery
//                .where(
//                        criteriaBuilder.equal(authorizationGroupRoot.get("documentSeries"), user)
//                );
//
//        TypedQuery<AuthorizationGroup> query = session.createQuery(criteriaQuery);
//        List<AuthorizationGroup> results = query.getResultList();
//        if (results.size() > 0) {
//            return results;
//        }
//        return null;
//    }
//}
