package nl.knaw.huc.di.images.layoutds.models.pim;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


public class MembershipTest {
    @Test
    public void isAllowedToSeeRoleReturnsTrueForRolesWithLessOrEqualPermissions() {
        final Membership adminMembership = new Membership(null, null, Role.ADMIN);

        assertThat(adminMembership.isAllowedToSeeRole(Role.ADMIN), is(true));
        assertThat(adminMembership.isAllowedToSeeRole(Role.OCR_MINION), is(true));
        assertThat(adminMembership.isAllowedToSeeRole(Role.SIAMESENETWORK_MINION), is(true));
        assertThat(adminMembership.isAllowedToSeeRole(Role.JOBRUNNER_MINION), is(true));
        assertThat(adminMembership.isAllowedToSeeRole(Role.PI), is(true));
        assertThat(adminMembership.isAllowedToSeeRole(Role.RESEARCHER), is(true));
        assertThat(adminMembership.isAllowedToSeeRole(Role.ASSISTANT), is(true));

        final Membership piMembership = new Membership(null, null, Role.PI);
        assertThat(piMembership.isAllowedToSeeRole(Role.PI), is(true));
        assertThat(piMembership.isAllowedToSeeRole(Role.RESEARCHER), is(true));
        assertThat(piMembership.isAllowedToSeeRole(Role.ASSISTANT), is(true));

        final Membership researcherMembership = new Membership(null, null, Role.RESEARCHER);
        assertThat(researcherMembership.isAllowedToSeeRole(Role.RESEARCHER), is(true));
        assertThat(researcherMembership.isAllowedToSeeRole(Role.ASSISTANT), is(true));

        final Membership assistantMembership = new Membership(null, null, Role.ASSISTANT);
        assertThat(assistantMembership.isAllowedToSeeRole(Role.ASSISTANT), is(true));
    }

    @Test
    public void isAllowedToSeeRoleReturnsFalseForRolesWithMorePermissions() {
        final Membership piMembership = new Membership(null, null, Role.PI);
        assertThat(piMembership.isAllowedToSeeRole(Role.ADMIN), is(false));

        final Membership researcherMembership = new Membership(null, null, Role.RESEARCHER);
        assertThat(researcherMembership.isAllowedToSeeRole(Role.ADMIN), is(false));
        assertThat(researcherMembership.isAllowedToSeeRole(Role.PI), is(false));

        final Membership assistantMembership = new Membership(null, null, Role.ASSISTANT);
        assertThat(assistantMembership.isAllowedToSeeRole(Role.ADMIN), is(false));
        assertThat(assistantMembership.isAllowedToSeeRole(Role.PI), is(false));
        assertThat(assistantMembership.isAllowedToSeeRole(Role.RESEARCHER), is(false));
    }
}