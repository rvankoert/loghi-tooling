package nl.knaw.huc.di.images.layoutds.DAO;

import nl.knaw.huc.di.images.layoutds.SessionFactorySingleton;
import nl.knaw.huc.di.images.layoutds.models.pim.PimUser;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PimUserDAO extends GenericDAO<PimUser> {

    public static final Logger LOG = LoggerFactory.getLogger(PimUserDAO.class);

    public PimUserDAO() {
        super(PimUser.class);
    }

    public PimUser getByEmail(String email) {
        try (Session session = SessionFactorySingleton.getSessionFactory().openSession()) {

            Transaction transaction = session.beginTransaction();

            PimUser pimUser = getByEmail(session, email);

            transaction.commit();
            session.close();
            return pimUser;
        }
    }

    public PimUser getByEmail(Session session, String email) {
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

        CriteriaQuery<PimUser> criteriaQuery = criteriaBuilder.createQuery(PimUser.class);
        Root<PimUser> pimUserRoot = criteriaQuery.from(PimUser.class);

        criteriaQuery.where(criteriaBuilder.equal(pimUserRoot.get("email"), email));
        TypedQuery<PimUser> query = session.createQuery(criteriaQuery);
        List<PimUser> pimUsers = query.getResultList();

        if (pimUsers.size() == 1) {
            return pimUsers.get(0);
        } else if (pimUsers.size() > 1) {
            LOG.error("Duplicate user data for email '{}'", email);
            return pimUsers.get(0);
        } else {
            return null;
        }
    }


    public PimUser findOrSaveUser(PimUser userToFind) {
        PimUser user = getByEmail(userToFind.getEmail());
        if (user == null) {
            user = this.save(userToFind);
        }

        return user;
    }

    public PimUser getByUUID(Session session, UUID uuid) {
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

        CriteriaQuery<PimUser> criteriaQuery = criteriaBuilder.createQuery(PimUser.class);
        Root<PimUser> pimUserRoot = criteriaQuery.from(PimUser.class);

        criteriaQuery.where(criteriaBuilder.equal(pimUserRoot.get("uuid"), uuid));
        TypedQuery<PimUser> query = session.createQuery(criteriaQuery);
        List<PimUser> pimUserList = query.getResultList();

        if (pimUserList.size() == 1) {
            return pimUserList.get(0);
        } else {
            return null;
        }
    }

    public Optional<PimUser> getByName(Session session, String userName) {
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

        CriteriaQuery<PimUser> criteriaQuery = criteriaBuilder.createQuery(PimUser.class);
        Root<PimUser> pimUserRoot = criteriaQuery.from(PimUser.class);

        criteriaQuery.where(criteriaBuilder.equal(pimUserRoot.get("name"), userName));
        TypedQuery<PimUser> query = session.createQuery(criteriaQuery);

        return query.getResultStream().findFirst();
    }
}
