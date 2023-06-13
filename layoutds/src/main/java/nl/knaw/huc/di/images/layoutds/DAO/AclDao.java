package nl.knaw.huc.di.images.layoutds.DAO;

import nl.knaw.huc.di.images.layoutds.models.pim.Acl;
import nl.knaw.huc.di.images.layoutds.models.pim.PimGroup;
import nl.knaw.huc.di.images.layoutds.models.pim.Role;
import org.hibernate.Session;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

public class AclDao extends GenericDAO<Acl> {
    public AclDao() {
        super(Acl.class);
    }

    public Stream<Acl> getBySubjectUuid(Session session, UUID subjectUuid) {
        final CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        final CriteriaQuery<Acl> criteriaQuery = criteriaBuilder.createQuery(Acl.class);
        final Root<Acl> from = criteriaQuery.from(Acl.class);

        criteriaQuery.where(criteriaBuilder.equal(from.get("subjectUuid"), subjectUuid));

        return session.createQuery(criteriaQuery).stream();
    }

    public void deleteForSubject(Session session, UUID subjectUuid) {
        final CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        final CriteriaDelete<Acl> criteriaQuery = criteriaBuilder.createCriteriaDelete(Acl.class);
        final Root<Acl> from = criteriaQuery.from(Acl.class);

        criteriaQuery.where(criteriaBuilder.equal(from.get("subjectUuid"), subjectUuid));

        session.createQuery(criteriaQuery).executeUpdate();
    }

    /**
     * @return The Acl of group or one of its supergroups
     */
    public Optional<Acl> getBySubjectUuidGroupPermissionAndRoles(Session session, UUID subjectUuid, PimGroup group, Acl.Permission permission, Set<Role> roles) {
        if (group == null) {
            return Optional.empty();
        }

        final CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        final CriteriaQuery<Acl> criteriaQuery = criteriaBuilder.createQuery(Acl.class);
        final Root<Acl> aclRoot = criteriaQuery.from(Acl.class);
        criteriaQuery.where(criteriaBuilder.and(
                criteriaBuilder.equal(aclRoot.get("subjectUuid"), subjectUuid),
                aclRoot.get("group").in(group.getSuperGroupsInHierarchy()),
                criteriaBuilder.equal(aclRoot.get("permission"), permission),
                aclRoot.get("role").in(roles)
        ));
        criteriaQuery.select(aclRoot);

        return session.createQuery(criteriaQuery).setMaxResults(1).stream().findAny();
    }


    public Stream<Acl> getByGroup(Session session, PimGroup pimGroup) {
        if (pimGroup == null) {
           return Stream.empty();
        }

        final CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        final CriteriaQuery<Acl> criteriaQuery = criteriaBuilder.createQuery(Acl.class);
        final Root<Acl> root = criteriaQuery.from(Acl.class);
        criteriaQuery.where(criteriaBuilder.equal(root.get("group"), pimGroup));
        criteriaQuery.select(root);

        return session.createQuery(criteriaQuery).stream();
    }
}
