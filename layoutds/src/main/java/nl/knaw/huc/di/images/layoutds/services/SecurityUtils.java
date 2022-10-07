package nl.knaw.huc.di.images.layoutds.services;

import nl.knaw.huc.di.images.layoutds.models.pim.Membership;
import nl.knaw.huc.di.images.layoutds.models.pim.PimGroup;
import nl.knaw.huc.di.images.layoutds.models.pim.PimUser;
import nl.knaw.huc.di.images.layoutds.models.pim.Role;

public class SecurityUtils {

    public static boolean isAllowedToSeeUser(PimUser requestingUser, PimUser user) {
        // A Pim user has always roles
        final Role highestRequestingRole = requestingUser.getRoles().stream().sorted().findFirst().get();
        final Role userRole = user.getRoles().stream().sorted().findFirst().get();
        // allow only admins to see other users
        return highestRequestingRole == Role.ADMIN;//|| highestRequestingRole.compareTo(userRole) <= 0;
    }

    public static boolean isAllowedToSeeRole(PimUser requestingUser, Role role) {
        // A Pim user has always roles
        final Role highestRequestingRole = requestingUser.getRoles().stream().sorted().findFirst().get();

        return highestRequestingRole.compareTo(role) <= 0;
    }

    public static boolean isAllowedToSeeGroup(PimUser pimUser, PimGroup group) {
        return pimUser.isAdmin() ||
                pimUser.getMemberships().stream()
                        .anyMatch(membership -> membership.getPimGroup().equals(group));
    }

    public static boolean isAllowedToAddMemberShip(PimUser pimUser, PimGroup group, Role role) {
        return pimUser.isAdmin() || pimUser.getMemberships().stream()
                .anyMatch(membership -> membership.getPimGroup().equals(group) && membership.isAllowedToSeeRole(role));
    }

    public static boolean isAllowedToRemoveMembership(PimUser pimUser, Membership membershipToRemove) {
        return pimUser.isAdmin() || pimUser.getMemberships().stream()
                .anyMatch(membership ->
                        membership.getRole() == Role.PI &&
                                membership.getPimGroup().equals(membershipToRemove.getPimGroup()) &&
                                (membership.getRole().compareTo(membershipToRemove.getRole()) < 0)
                );
    }

    public static boolean isAllowedToAddSubGroup(PimUser pimUser, PimGroup group) {

        return pimUser.isAdmin() || pimUser.getMemberships().stream()
                .anyMatch(membership ->
                        membership.getRole() == Role.PI &&
                                membership.getPimGroup().equals(group)
                );
    }

    public static boolean isAllowedToRemoveSubGroup(PimUser pimUser, PimGroup group) {
        return pimUser.isAdmin() || pimUser.getMemberships().stream()
                .anyMatch(membership ->
                        membership.getRole() == Role.PI &&
                                membership.getPimGroup().equals(group)
                );
    }
}
