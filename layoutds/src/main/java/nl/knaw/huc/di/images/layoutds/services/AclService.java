package nl.knaw.huc.di.images.layoutds.services;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import nl.knaw.huc.di.images.layoutds.DAO.AclDao;
import nl.knaw.huc.di.images.layoutds.DAO.PimGroupDAO;
import nl.knaw.huc.di.images.layoutds.exceptions.PimSecurityException;
import nl.knaw.huc.di.images.layoutds.models.pim.*;
import nl.knaw.huc.di.images.layoutds.models.pim.Acl.Permission;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AclService {

    private final AclDao aclDao = new AclDao();

    public Set<Acl> getAclsOfEnitity(Session session, UUID subjectId, PimUser pimUser) throws PimSecurityException {
        final Stream<Acl> aclStream = aclDao.getBySubjectUuid(session, subjectId);

        if (pimUser.isAdmin()) {
            return aclStream.collect(Collectors.toSet());
        } else if (getRolesInPrimaryGroup(pimUser).contains(Role.PI)) {
            final Set<PimGroup> hierarchy = pimUser.getSuperGroupsInHierarchyPrimaryGroup();
            return aclStream.filter(acl -> hierarchy.contains(acl.getGroup())).collect(Collectors.toSet());
        }

        throw new PimSecurityException();
    }

    private Stream<Membership> getMembershipsForPrimaryGroup(PimUser pimUser) {
        return pimUser.getMemberships().stream()
                .filter(membership -> membership.getPimGroup().equals(pimUser.getPrimaryGroup()));
    }

    private Set<Role> getRolesInPrimaryGroup(PimUser pimUser) {
        return getMembershipsForPrimaryGroup(pimUser)
                .map(Membership::getRole)
                .collect(Collectors.toSet());
    }

    public void removeAcl(Session session, UUID aclId, PimUser pimUser) throws PimSecurityException {
        final Transaction transaction = session.beginTransaction();
        final AclDao aclDao = this.aclDao;
        final Acl aclToDelete = aclDao.getByUUID(session, aclId);
        final Set<PimGroup> hierarchy = pimUser.getSuperGroupsInHierarchyPrimaryGroup();
        if (pimUser.isAdmin() || (getRolesInPrimaryGroup(pimUser).contains(Role.PI) && hierarchy.contains(aclToDelete.getGroup()))) {
            aclDao.delete(session, aclToDelete);

            transaction.commit();
        } else {
            throw new PimSecurityException();
        }
    }

    public void addAcl(Session session, UUID subjectId, AclToAdd aclToAdd, PimUser pimUser) throws PimSecurityException {
        final Set<PimGroup> hierarchy = pimUser.getSuperGroupsInHierarchyPrimaryGroup();

        final PimGroupDAO pimGroupDAO = new PimGroupDAO();
        final PimGroup group = pimGroupDAO.getByUUID(aclToAdd.group);

        if (group == null) {
            throw new IllegalArgumentException("Group does not exist");
        }

        final boolean allowedToAddAcl = pimUser.isAdmin() || aclDao.getBySubjectUuid(session, subjectId)
                .anyMatch(acl -> hierarchy.contains(acl.getGroup()) && Role.PI.equals(acl.getRole()));
        if (allowedToAddAcl) {
            final Acl acl = new Acl(subjectId, group, aclToAdd.permission, aclToAdd.role);
            aclDao.save(session, acl);
        } else {
            throw new PimSecurityException();
        }
    }

    public static class AclToAdd {
        @JsonProperty(required = true)
        private UUID group;
        @JsonProperty(required = true)
        private Role role;
        @JsonProperty(required = true)
        private Permission permission;

        @JsonCreator
        public AclToAdd(@JsonProperty("group") UUID group, @JsonProperty("role") Role role, @JsonProperty("permission") Permission permission) {
            this.group = group;
            this.role = role;
            this.permission = permission;
        }
    }
}
