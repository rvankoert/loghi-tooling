package nl.knaw.huc.di.images.layoutds.services;

import nl.knaw.huc.di.images.layoutds.models.pim.PimGroup;
import nl.knaw.huc.di.images.layoutds.models.pim.PimUser;
import nl.knaw.huc.di.images.layoutds.models.pim.Role;

import java.util.List;

public class AclTestHelpers {
    public static PimUser userWithMembershipAndPrimaryGroup(PimGroup pimGroup, Role role) {
        PimUser pimUser = userWithRoles(List.of(role));
        pimUser.setPrimaryGroup(pimGroup);
        pimUser.addMembership(pimGroup, role);
        return pimUser;
    }

    public static PimUser adminUser() {
        final PimUser admin = new PimUser();
        admin.setRoles(List.of(Role.ADMIN));
        return admin;
    }

    public static PimUser userWithRoles(List<Role> roles) {
        final PimUser pimUser = new PimUser();
        pimUser.setRoles(roles);
        return pimUser;
    }
}
