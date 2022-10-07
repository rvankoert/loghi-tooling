package nl.knaw.huc.di.images.layoutds.services;

import nl.knaw.huc.di.images.layoutds.models.pim.Membership;
import nl.knaw.huc.di.images.layoutds.models.pim.PimGroup;
import nl.knaw.huc.di.images.layoutds.models.pim.PimUser;
import nl.knaw.huc.di.images.layoutds.models.pim.Role;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static nl.knaw.huc.di.images.layoutds.services.SecurityUtils.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SecurityUtilsTest {
    @Test
    public void isAllowedToSeeUserWhenUserHasLowerRole() {
        final PimUser requestingUser = userWithRole(Role.ADMIN);
        final PimUser pimUser = userWithRole(Role.AUTHENTICATED);

        assertTrue(isAllowedToSeeUser(requestingUser, pimUser));
    }

    @Test
    public void adminIsAllowedToSeeOtherAdmin() {
        final PimUser requestingUser = userWithRole(Role.ADMIN);
        final PimUser pimUser = userWithRole(Role.ADMIN);

        assertTrue(isAllowedToSeeUser(requestingUser, pimUser));
    }

    private PimUser userWithRole(Role role) {
        final PimUser pimUser = new PimUser();
        pimUser.setRoles(List.of(role));

        return pimUser;
    }

    @Test
    @Ignore // we need to be a bit more strict
    public void isAllowedToSeeUserWhenUserHasSameRole() {
        final PimUser requestingUser = userWithRole(Role.PI);
        final PimUser pimUser = userWithRole(Role.PI);

        assertTrue(isAllowedToSeeUser(requestingUser, pimUser));
    }

    @Test
    public void isNotAllowedToSeeUserWhenUserHasHigherRole() {
        final PimUser requestingUser = userWithRole(Role.PI);
        final PimUser pimUser = userWithRole(Role.ADMIN);

        assertFalse(isAllowedToSeeUser(requestingUser, pimUser));
    }

    @Test
    public void isAllowedToSeeRoleWhenRoleIsLower() {
        final PimUser requestingUser = userWithRole(Role.ADMIN);

        assertTrue(isAllowedToSeeRole(requestingUser, Role.ASSISTANT));
    }

    @Test
    public void isAllowedToSeeRoleWhenRoleIsSame() {
        final PimUser requestingUser = userWithRole(Role.ASSISTANT);

        assertTrue(isAllowedToSeeRole(requestingUser, Role.ASSISTANT));
    }

    @Test
    public void isNotAllowedToSeeRoleWhenRoleIsHigher() {
        final PimUser requestingUser = userWithRole(Role.ASSISTANT);

        assertFalse(isAllowedToSeeRole(requestingUser, Role.ADMIN));
    }

    @Test
    public void isAllowedToSeeGroupIfUserIsMemberOfTheGroup() {
        final PimGroup pimGroup = new PimGroup();
        final PimUser pimUser = userWithMemberShip(pimGroup, Role.ASSISTANT);

        assertTrue(isAllowedToSeeGroup(pimUser, pimGroup));
    }

    private PimUser userWithMemberShip(PimGroup pimGroup, Role role) {
        final PimUser pimUser = new PimUser();
        pimUser.addMembership(pimGroup, role);
        return pimUser;
    }

    @Test
    public void isAllowedToSeeGroupIfUserIsAdmin() {
        final PimUser pimUser = userWithRole(Role.ADMIN);
        final PimGroup pimGroup = new PimGroup();

        assertTrue(isAllowedToSeeGroup(pimUser, pimGroup));
    }

    @Test
    public void isNotAllowedToSeeGroupIfUserIsNotAMember() {
        final PimUser pimUser = new PimUser();
        final PimGroup pimGroup = new PimGroup();

        assertFalse(isAllowedToSeeGroup(pimUser, pimGroup));
    }

    @Test
    public void isAllowedToAddMembershipWhenUserIsAdmin() {
        final PimGroup pimGroup = new PimGroup();
        final PimUser pimUser = userWithRole(Role.ADMIN);

        assertTrue(isAllowedToAddMemberShip(pimUser, pimGroup, Role.PI));
    }

    @Test
    public void isAllowedToAddMembershipWhenUserRoleIsEqualToTheRoleToAdd() {
        final PimGroup pimGroup = new PimGroup();
        final PimUser pimUser = userWithMemberShip(pimGroup, Role.ASSISTANT);

        assertTrue(isAllowedToAddMemberShip(pimUser, pimGroup, Role.ASSISTANT));
    }

    @Test
    public void isAllowedToAddMembershipWhenUserRoleIsHigherToTheRoleToAdd() {
        final PimGroup pimGroup = new PimGroup();
        final PimUser pimUser = userWithMemberShip(pimGroup, Role.RESEARCHER);

        assertTrue(isAllowedToAddMemberShip(pimUser, pimGroup, Role.ASSISTANT));
    }

    @Test
    public void isNotAllowedToAddMembershipWhenUserIsNotAMemberOfTheGroup() {
        final PimUser pimUser = new PimUser();
        final PimGroup pimGroup = new PimGroup();

        assertFalse(isAllowedToAddMemberShip(pimUser, pimGroup, Role.ASSISTANT));
    }

    @Test
    public void isNotAllowedToAddMembershipWhenUserRoleLowerTheRoleToAdd() {
        final PimGroup pimGroup = new PimGroup();
        final PimUser pimUser = userWithMemberShip(pimGroup, Role.ASSISTANT);

        assertFalse(isAllowedToAddMemberShip(pimUser, pimGroup, Role.RESEARCHER));
    }

    @Test
    public void isAllowedToRemoveMembershipWhenUserIsAdmin() {
        final PimGroup pimGroup = new PimGroup();
        final PimUser pimUser = userWithRole(Role.ADMIN);
        final Membership membership = membershipForGroupAndRole(pimGroup, Role.RESEARCHER);

        assertTrue(isAllowedToRemoveMembership(pimUser, membership));
    }

    @Test
    public void isAllowedToRemoveMembershipWhenUserIsPIAndMembershipHasLowerRole() {
        final PimGroup pimGroup = new PimGroup();
        final PimUser pimUser = userWithMemberShip(pimGroup, Role.PI);
        final Membership membership = membershipForGroupAndRole(pimGroup, Role.RESEARCHER);

        assertTrue(isAllowedToRemoveMembership(pimUser, membership));
    }

    @Test
    public void isNotAllowedToRemoveMembershipWhenUserIsPIAndMembershipHasEqualRole() {
        final PimGroup pimGroup = new PimGroup();
        final PimUser pimUser = userWithMemberShip(pimGroup, Role.PI);
        final Membership membership = membershipForGroupAndRole(pimGroup, Role.PI);

        assertFalse(isAllowedToRemoveMembership(pimUser, membership));
    }

    private Membership membershipForGroupAndRole(PimGroup pimGroup, Role role) {
        return new Membership(pimGroup, new PimUser(), role);
    }

    @Test
    public void isNotAllowedToRemoveMembershipWhenUserIsPIAndMembershipHasHigherRole() {
        final PimGroup pimGroup = new PimGroup();
        final PimUser pimUser = userWithMemberShip(pimGroup, Role.ADMIN);
        final Membership membership = membershipForGroupAndRole(pimGroup, Role.ASSISTANT);

        assertFalse(isAllowedToRemoveMembership(pimUser, membership));
    }

    @Test
    public void isNotAllowedToRemoveMembershipWhenUserIsNotPIMember() {
        final PimGroup pimGroup = new PimGroup();
        final PimUser pimUser = userWithMemberShip(pimGroup, Role.RESEARCHER);
        final Membership membership = membershipForGroupAndRole(pimGroup, Role.ASSISTANT);

        assertFalse(isAllowedToRemoveMembership(pimUser, membership));
    }

    @Test
    public void isAllowedToAddSubGroupWhenUserIsAdmin() {
        final PimGroup pimGroup = new PimGroup();
        final PimUser pimUser = userWithRole(Role.ADMIN);

        assertTrue(isAllowedToAddSubGroup(pimUser, pimGroup));
    }

    @Test
    public void isAllowedToAddSubGroupWhenUserIsPI() {
        final PimGroup pimGroup = new PimGroup();
        final PimUser pimUser = userWithMemberShip(pimGroup, Role.PI);

        assertTrue(isAllowedToAddSubGroup(pimUser, pimGroup));
    }

    @Test
    public void isAllowedToAddSubGroupWhenUserHasLowerMembershipThanPI() {
        final PimGroup pimGroup = new PimGroup();
        final PimUser pimUser = userWithRole(Role.RESEARCHER);

        assertFalse(isAllowedToAddSubGroup(pimUser, pimGroup));
    }

    @Test
    public void isAllowedToRemoveSubGroupWhenUserIsAdmin() {
        final PimGroup pimGroup = new PimGroup();
        final PimUser pimUser = userWithRole(Role.ADMIN);

        assertTrue(isAllowedToRemoveSubGroup(pimUser, pimGroup));
    }

    @Test
    public void isAllowedToRemoveSubGroupWhenUserIsPI() {
        final PimGroup pimGroup = new PimGroup();
        final PimUser pimUser = userWithMemberShip(pimGroup, Role.PI);

        assertTrue(isAllowedToRemoveSubGroup(pimUser, pimGroup));
    }

    @Test
    public void isNotAllowedToRemoveSubGroupWhenUserHasLowerMembershipThanPI() {
        final PimGroup pimGroup = new PimGroup();
        final PimUser pimUser = userWithRole(Role.RESEARCHER);

        assertFalse(isAllowedToRemoveSubGroup(pimUser, pimGroup));
    }
}