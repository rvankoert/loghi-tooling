package nl.knaw.huc.di.images.layoutds.services;

import nl.knaw.huc.di.images.layoutds.models.pim.Acl;
import nl.knaw.huc.di.images.layoutds.models.pim.Role;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.util.Objects;
import java.util.UUID;

public class AclMatcher extends TypeSafeMatcher<Acl> {
    private Acl.Permission permission;
    private UUID groupUuid;
    private Role role;

    public AclMatcher() {

    }

    public static AclMatcher acl() {
        return new AclMatcher();
    }

    public AclMatcher forGroupWithUuid(UUID groupUuid) {
        this.groupUuid = groupUuid;
        return this;
    }

    public AclMatcher withPermission(Acl.Permission permission) {
        this.permission = permission;
        return this;
    }

    public AclMatcher forRole(Role role) {
        this.role = role;
        return this;
    }

    @Override
    protected boolean matchesSafely(Acl acl) {

        return acl.getGroup().getUuid().equals(groupUuid) &&
                acl.getPermission().equals(permission) &&
                Objects.equals(acl.getRole(), role);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("Acl for group with uuid ").appendValue(groupUuid)
                .appendText(" and role ").appendValue(role)
                .appendText(" and with permission ").appendValue(permission);
    }
}
