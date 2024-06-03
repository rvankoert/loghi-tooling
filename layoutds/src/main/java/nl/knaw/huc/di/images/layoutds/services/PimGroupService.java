package nl.knaw.huc.di.images.layoutds.services;

import nl.knaw.huc.di.images.layoutds.DAO.AclDAO;
import nl.knaw.huc.di.images.layoutds.DAO.MembershipDAO;
import nl.knaw.huc.di.images.layoutds.DAO.PimGroupDAO;
import nl.knaw.huc.di.images.layoutds.DAO.PimUserDAO;
import nl.knaw.huc.di.images.layoutds.exceptions.PimSecurityException;
import nl.knaw.huc.di.images.layoutds.models.pim.*;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Date;
import java.util.Iterator;
import java.util.UUID;
import java.util.stream.Stream;

public class PimGroupService {

    private final PimGroupDAO pimGroupDAO = new PimGroupDAO();

    public void addMembership(Session session, UUID groupId, UUID userToAddId, Role role, PimUser user) throws PimSecurityException, GroupNotFoundException {
        final PimGroup group = pimGroupDAO.getByUUID(session, groupId);

        if (role == Role.ADMIN) {
            throw new IllegalArgumentException("Role \"" + role + "\" is not supported for a membership");
        }

        if (group == null) {
            throw new GroupNotFoundException();
        }
        final PimUserDAO pimUserDao = new PimUserDAO();
        final PimUser userToAdd = pimUserDao.getByUUID(session, userToAddId);

        if (userToAdd == null) {
            throw new IllegalArgumentException("User with UUID \"" + userToAddId + "\" cannot be found");
        }

        if (!SecurityUtils.isAllowedToAddMemberShip(user, group, role)) {
            throw new PimSecurityException();
        }
        group.addMembership(new Membership(group, userToAdd, role));

        if (userToAdd.getPrimaryGroup() == null) {
            userToAdd.setPrimaryGroup(group);
        }

        final Transaction transaction = session.beginTransaction();
        this.pimGroupDAO.save(session, group);
        transaction.commit();

    }

    public void removeMembershipFromGroup(Session session, UUID groupId, UUID membershipId, PimUser admin) throws PimSecurityException, GroupNotFoundException {
        final PimGroup pimGroup = pimGroupDAO.getByUUID(groupId);

        if (pimGroup == null) {
            throw new GroupNotFoundException();
        }

        final MembershipDAO membershipDao = new MembershipDAO();
        final Membership membership = membershipDao.getByUUID(session, membershipId);
        if (membership == null) {
            throw new IllegalArgumentException("Membership with UUID \"" + membershipId + "\" cannot be found");
        }

        if (!membership.getPimGroup().equals(pimGroup)) {
            throw new IllegalArgumentException("Group of membership is not equal to group.");
        }

        if (!SecurityUtils.isAllowedToRemoveMembership(admin, membership)) {
            throw new PimSecurityException();
        }
        final Transaction transaction = session.beginTransaction();

        final PimUser pimUser = membership.getPimUser();
        if (membership.getPimGroup().equals(pimUser.getPrimaryGroup())) {
            final PimUserDAO pimUserDao = new PimUserDAO();
            pimUser.setPrimaryGroup(null);
            pimUserDao.save(session, pimUser);
        }

        membershipDao.delete(session, membership);
        session.evict(membership);
        transaction.commit();
    }

    public void create(Session session, PimGroup pimGroup, PimUser pimUser) throws PimSecurityException {
        if (!pimUser.isAdmin()) {
            throw new PimSecurityException();
        }
        pimGroupDAO.save(session, pimGroup);
    }

    public void addSubgroup(Session session, UUID pimGroupId, UUID subgroupId, PimUser pimUser) throws PimSecurityException, GroupNotFoundException {
        final PimGroup pimGroup = pimGroupDAO.getByUUID(session, pimGroupId);
        if (pimGroup == null) {
            throw new GroupNotFoundException();
        }

        final PimGroup subGroup = pimGroupDAO.getByUUID(session, subgroupId);
        if (subGroup == null) {
            throw new IllegalArgumentException("Subgroup with UUID \"" + subgroupId + "\" cannot be found");
        }

        if (!SecurityUtils.isAllowedToAddSubGroup(pimUser, pimGroup)) {
            throw new PimSecurityException();
        }

        pimGroup.addSubgroup(subGroup);
        pimGroupDAO.save(session, pimGroup);
    }

    public void removeSubgroup(Session session, UUID groupId, UUID subgroupId, PimUser pimUser) throws PimSecurityException, GroupNotFoundException {
        final PimGroup pimGroup = pimGroupDAO.getByUUID(session, groupId);

        if (pimGroup == null) {
            throw new GroupNotFoundException();
        }

        if (!SecurityUtils.isAllowedToRemoveSubGroup(pimUser, pimGroup)) {
            throw new PimSecurityException();
        }

        final PimGroup subgroup = pimGroupDAO.getByUUID(session, subgroupId);

        if (subgroup == null) {
            throw new IllegalArgumentException("Subgroup with UUID \"" + subgroupId + "\" cannot be found");
        }

        pimGroup.removeSubgroup(subgroup);

        pimGroupDAO.save(session, pimGroup);

    }

    public Stream<PimGroup> getAutoComplete(Session session, PimUser pimUser, String filter, int limit, int skip) {
        if (pimUser.isAdmin()) {
            return pimGroupDAO.getAutoCompleteForAdmin(session, filter, limit, skip);
        }

        return pimGroupDAO.getAutoComplete(session, pimUser, filter, limit, skip);
    }

    /**
     * This is a soft delete method
     */
    public void delete(Session session, UUID groupUuid, PimUser pimUser) throws PimSecurityException, GroupNotFoundException {
        if (!pimUser.isAdmin() || pimUser.getDisabled()) {
            throw new PimSecurityException();
        }

        final PimGroup pimGroup = pimGroupDAO.getByUUID(session, groupUuid);

        if (pimGroup == null) {
            throw new GroupNotFoundException();
        }

        pimGroup.setDeleted(new Date());

        final Transaction transaction = session.beginTransaction();
        pimGroupDAO.save(session, pimGroup);
        final AclDAO aclDao = new AclDAO();
        final Stream<Acl> aclsOfGroup = aclDao.getByGroup(session, pimGroup);

        for (final Iterator<Acl> aclIterator = aclsOfGroup.iterator(); aclIterator.hasNext();) {
            final Acl acl = aclIterator.next();

            acl.setDeleted(new Date());

            aclDao.save(session, acl);
        }


        transaction.commit();

    }
}
