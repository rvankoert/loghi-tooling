package nl.knaw.huc.di.images.layoutds.security;

import nl.knaw.huc.di.images.layoutds.DAO.AclDao;
import nl.knaw.huc.di.images.layoutds.DAO.ConfigurationDAO;
import nl.knaw.huc.di.images.layoutds.models.pim.*;
import org.hibernate.Session;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PermissionHandler {

    public static final List<Role> ROLES_ALLOWED_TO_UPDATE = List.of(Role.PI, Role.RESEARCHER, Role.ASSISTANT);
    public final List<Role> rolesAllowedToCreate;
    private static final List<Role> ROLES_ALLOWED_TO_DELETE = List.of(Role.PI);

    private final ConfigurationDAO configurationDAO;
    private final AclDao aclDao;

    public PermissionHandler() {
        this(List.of(Role.PI, Role.RESEARCHER));
    }

    public PermissionHandler(List<Role> rolesAllowedToCreate) {
        configurationDAO = new ConfigurationDAO();
        aclDao = new AclDao();
        this.rolesAllowedToCreate = rolesAllowedToCreate;
    }

    public void addAcls(Session session, UUID subjectUuid, PimUser pimUser) {
        if (useGroups()) {
            aclDao.save(session, Acl.readPermission(subjectUuid, pimUser.getPrimaryGroup(), Role.PI));
            aclDao.save(session, Acl.createPermission(subjectUuid, pimUser.getPrimaryGroup(), Role.PI));
            aclDao.save(session, Acl.updatePermission(subjectUuid, pimUser.getPrimaryGroup(), Role.PI));
            aclDao.save(session, Acl.deletePermission(subjectUuid, pimUser.getPrimaryGroup(), Role.PI));

            aclDao.save(session, Acl.readPermission(subjectUuid, pimUser.getPrimaryGroup(), Role.RESEARCHER));
            aclDao.save(session, Acl.createPermission(subjectUuid, pimUser.getPrimaryGroup(), Role.RESEARCHER));
            aclDao.save(session, Acl.updatePermission(subjectUuid, pimUser.getPrimaryGroup(), Role.RESEARCHER));

            aclDao.save(session, Acl.readPermission(subjectUuid, pimUser.getPrimaryGroup(), Role.ASSISTANT));
            aclDao.save(session, Acl.updatePermission(subjectUuid, pimUser.getPrimaryGroup(), Role.ASSISTANT));
        }
    }

    public boolean useGroups() {
        return configurationDAO.getBooleanByKey("useGroups", false);
    }

    public boolean isAllowedToCreate(Session session, PimUser pimUser) {
        if (pimUser.getDisabled()) {
            return false;
        }

        if (pimUser.isAdmin()) {
            return true;
        }

        if (useGroups()) {
            return userHasAnyRoleInPrimaryGroup(pimUser, rolesAllowedToCreate);
        }

        return userHasAnyRole(pimUser, rolesAllowedToCreate);
    }

    private boolean userHasAnyRole(PimUser pimUser, List<Role> rolesAllowedToCreate) {
        return pimUser.getRoles().stream().anyMatch(rolesAllowedToCreate::contains);
    }

    private boolean userHasAnyRoleInPrimaryGroup(PimUser pimUser, List<Role> roles) {
        return getMembershipsForPrimaryGroup(pimUser)
                .anyMatch(membership -> roles.contains(membership.getRole()));
    }

    private Stream<Membership> getMembershipsForPrimaryGroup(PimUser pimUser) {
        return pimUser.getMemberships().stream()
                .filter(membership -> membership.getPimGroup().equals(pimUser.getPrimaryGroup()));
    }

    public boolean isAllowedToUpdate(Session session, PimUser pimUser, UUID subjectId) {
        if (pimUser.getDisabled()) {
            return false;
        }
        if (pimUser.isAdmin()) {
            return true;
        }
        final Set<Role> roles = getRolesInPrimaryGroup(pimUser);
        if (useGroups()) {
            return hasEnoughPermissions(session, pimUser.getPrimaryGroup(), Acl.Permission.UPDATE, roles, subjectId);
        }

        return userHasAnyRole(pimUser, ROLES_ALLOWED_TO_UPDATE);
    }

    private Set<Role> getRolesInPrimaryGroup(PimUser pimUser) {
        return getMembershipsForPrimaryGroup(pimUser)
                .map(Membership::getRole)
                .collect(Collectors.toSet());
    }

    private boolean hasEnoughPermissions(Session session, PimGroup pimGroup, Acl.Permission permission, Set<Role> roles, UUID subjectUuid) {
        return aclDao.getBySubjectUuidGroupPermissionAndRoles(session, subjectUuid, pimGroup, permission, roles).isPresent();
    }

    public boolean isAllowedToDelete(Session session, PimUser pimUser, UUID subjectUuid) {
        if (pimUser.getDisabled()) {
            return false;
        }

        if (pimUser.isAdmin()) {
            return true;
        }

        final Set<Role> roles = getRolesInPrimaryGroup(pimUser);
        if (useGroups()) {
            return hasEnoughPermissions(session, pimUser.getPrimaryGroup(), Acl.Permission.DELETE, roles, subjectUuid);
        }

        return userHasAnyRole(pimUser, ROLES_ALLOWED_TO_DELETE);
    }

    public void removeAllAclsForSubject(Session session, UUID subjectUuid) {
        if (useGroups()) {
            aclDao.deleteForSubject(session, subjectUuid);
        }
    }

    public boolean isAllowedToRead(Session session, UUID subjectUuid, PimUser pimUser) {
        if (pimUser.getDisabled()) {
            return false;
        }

        final Set<Role> roles = getRolesInPrimaryGroup(pimUser);
        if (useGroups()) {
            return pimUser.isAdmin() || hasEnoughPermissions(session, pimUser.getPrimaryGroup(), Acl.Permission.READ, roles, subjectUuid);
        }
        return true;
    }
}
