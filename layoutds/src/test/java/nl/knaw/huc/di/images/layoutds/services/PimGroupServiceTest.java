package nl.knaw.huc.di.images.layoutds.services;

import nl.knaw.huc.di.images.layoutds.DAO.AclDao;
import nl.knaw.huc.di.images.layoutds.DAO.MembershipDao;
import nl.knaw.huc.di.images.layoutds.DAO.PimGroupDAO;
import nl.knaw.huc.di.images.layoutds.DAO.PimUserDao;
import nl.knaw.huc.di.images.layoutds.SessionFactorySingleton;
import nl.knaw.huc.di.images.layoutds.StudentJpaConfig;
import nl.knaw.huc.di.images.layoutds.exceptions.PimSecurityException;
import nl.knaw.huc.di.images.layoutds.models.pim.*;
import org.hamcrest.Matcher;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static nl.knaw.huc.di.images.layoutds.services.AclTestHelpers.adminUser;
import static nl.knaw.huc.di.images.layoutds.services.AclTestHelpers.userWithMembershipAndPrimaryGroup;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * For the security tests of PimGroupService see: SecurityUtilsTest
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {StudentJpaConfig.class}, loader = AnnotationConfigContextLoader.class)
public class PimGroupServiceTest {

    private PimGroupDAO pimGroupDAO;
    private PimUserDao pimUserDao;
    private PimGroupService pimGroupService;

    @After
    public void tearDown() {
        // Start with a clean database with each test.
        SessionFactorySingleton.closeSessionFactory();
    }

    @Before
    public void setUp() {
        pimGroupDAO = new PimGroupDAO();
        pimUserDao = new PimUserDao();
        pimGroupService = new PimGroupService();
    }

    @Test
    public void addMembershipIsAllowedForAdmins() throws PimSecurityException, GroupNotFoundException {
        final PimUser admin = adminUser();
        final PimUser userToAdd = new PimUser();
        pimUserDao.save(userToAdd);
        final PimGroup pimGroup = new PimGroup();
        pimGroupDAO.save(pimGroup);
        final Role role = Role.PI;

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimGroup group = pimGroupDAO.getByUUID(session, pimGroup.getUuid());
            assertThat(group, hasProperty("memberships", is(empty())));
            final PimUser user = pimUserDao.getByUUID(session, userToAdd.getUuid());

            pimGroupService.addMembership(session, group.getUuid(), user.getUuid(), role, admin);
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser user = pimUserDao.getByUUID(session, userToAdd.getUuid());
            final PimGroup group = pimGroupDAO.getByUUID(session, pimGroup.getUuid());
            assertThat(group, hasProperty(
                    "memberships", contains(membershipFor(user, role))
            ));
        }
    }

    @Test
    public void addMembershipIsAllowedForPIOfTheGroup() throws PimSecurityException, GroupNotFoundException {
        final PimGroup pimGroup = new PimGroup();
        pimGroupDAO.save(pimGroup);
        final PimUser pi = userWithMembershipAndPrimaryGroup(pimGroup, Role.PI);
        pimUserDao.save(pi);
        final PimUser userToAdd = new PimUser();
        pimUserDao.save(userToAdd);
        final Role role = Role.PI;

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimGroup group = pimGroupDAO.getByUUID(session, pimGroup.getUuid());
            assertThat(group, hasProperty(
                    "memberships", not(hasItem(membershipFor(userToAdd, role)))
            ));
            final PimUser user = pimUserDao.getByUUID(session, userToAdd.getUuid());

            pimGroupService.addMembership(session, group.getUuid(), user.getUuid(), role, pi);
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser user = pimUserDao.getByUUID(session, userToAdd.getUuid());
            final PimGroup group = pimGroupDAO.getByUUID(session, pimGroup.getUuid());
            assertThat(group, hasProperty(
                    "memberships", hasItem(membershipFor(user, role))
            ));
        }
    }

    @Test(expected = PimSecurityException.class)
    public void addMembershipIsNotAllowedForPIOfOtherGroup() throws PimSecurityException, GroupNotFoundException {
        final PimGroup pimGroup = new PimGroup();
        pimGroupDAO.save(pimGroup);
        final PimGroup otherGroup = new PimGroup();
        pimGroupDAO.save(otherGroup);
        final PimUser piOfOtherGroup = userWithMembershipAndPrimaryGroup(otherGroup, Role.PI);
        pimUserDao.save(piOfOtherGroup);
        final PimUser userToAdd = new PimUser();
        pimUserDao.save(userToAdd);
        final Role role = Role.PI;

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimGroup group = pimGroupDAO.getByUUID(session, pimGroup.getUuid());
            final PimUser user = pimUserDao.getByUUID(session, userToAdd.getUuid());

            pimGroupService.addMembership(session, group.getUuid(), user.getUuid(), role, piOfOtherGroup);
        }
    }

    @Test(expected = GroupNotFoundException.class)
    public void addMembershipTrowsIllegalArgumentExceptionWhenTheUserDoesNotExist() throws PimSecurityException, GroupNotFoundException {
        final PimUser admin = adminUser();
        final PimUser userToAdd = new PimUser();
        pimUserDao.save(userToAdd);
        final Role role = Role.PI;

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {

            pimGroupService.addMembership(session, UUID.randomUUID(), userToAdd.getUuid(), role, admin);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void addMembershipTrowsIllegalArgumentExceptionWhenTheGroupDoesNotExist() throws PimSecurityException, GroupNotFoundException {
        final PimGroup pimGroup = new PimGroup();
        pimGroupDAO.save(pimGroup);
        final PimUser admin = adminUser();
        final Role role = Role.PI;

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {

            pimGroupService.addMembership(session, pimGroup.getUuid(), UUID.randomUUID(), role, admin);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void addMembershipThrowsIllegalArgumentExceptionIfRoleIsAdmin() throws PimSecurityException, GroupNotFoundException {
        final PimUser admin = adminUser();
        final PimUser pimUser = new PimUser();
        pimUserDao.save(pimUser);
        final PimGroup pimGroup = new PimGroup();
        pimGroupDAO.save(pimGroup);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            pimGroupService.addMembership(session, pimGroup.getUuid(), pimUser.getUuid(), Role.ADMIN, admin);
        }
    }


    @Test
    public void addMembershipAddsAMembershipSetsTheUsersPrimaryGroupWhenNull() throws PimSecurityException, GroupNotFoundException {
        final PimUser admin = adminUser();
        final PimUser pimUser = new PimUser();
        pimUserDao.save(pimUser);
        final PimGroup pimGroup = new PimGroup();
        pimGroupDAO.save(pimGroup);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser user = pimUserDao.getByUUID(session, pimUser.getUuid());
            assertThat(user, hasProperty("primaryGroup", is(nullValue())));
            final PimGroup group = pimGroupDAO.getByUUID(session, pimGroup.getUuid());

            pimGroupService.addMembership(session, group.getUuid(), user.getUuid(), Role.PI, admin);
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser user = pimUserDao.getByUUID(session, pimUser.getUuid());
            assertThat(user, hasProperty("primaryGroup", hasProperty("uuid", equalTo(pimGroup.getUuid()))));
        }
    }

    @Test
    public void addMembershipAddsAMembershipDoesNotOverwriteTheUsersPrimaryGroup() throws PimSecurityException, GroupNotFoundException {
        final PimUser admin = adminUser();
        final PimUser pimUser = new PimUser();
        pimUserDao.save(pimUser);
        final PimGroup primaryGroup = new PimGroup();
        pimGroupDAO.save(primaryGroup);
        final PimGroup secondaryGroup = new PimGroup();
        pimGroupDAO.save(secondaryGroup);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser user = pimUserDao.getByUUID(session, pimUser.getUuid());
            assertThat(user, hasProperty("primaryGroup", is(nullValue())));

            pimGroupService.addMembership(session, pimGroupDAO.getByUUID(session, primaryGroup.getUuid()).getUuid(), user.getUuid(), Role.PI, admin);

            assertThat(pimUserDao.getByUUID(session, pimUser.getUuid()), hasProperty("primaryGroup", hasProperty("uuid", equalTo(primaryGroup.getUuid()))));

            pimGroupService.addMembership(session, pimGroupDAO.getByUUID(session, secondaryGroup.getUuid()).getUuid(), user.getUuid(), Role.PI, admin);

        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser user = pimUserDao.getByUUID(session, pimUser.getUuid());
            assertThat(user, hasProperty("primaryGroup", hasProperty("uuid", equalTo(primaryGroup.getUuid()))));
        }
    }

    @Test
    public void removeMembershipIsAllowedForAdmin() throws PimSecurityException, GroupNotFoundException {
        final PimUser admin = adminUser();
        pimUserDao.save(admin);
        final PimUser pimUser = new PimUser();
        pimUserDao.save(pimUser);
        final PimGroup pimGroup = new PimGroup();
        pimGroupDAO.save(pimGroup);
        final Role role = Role.PI;
        final MembershipDao membershipDao = new MembershipDao();
        final Membership membership = new Membership(pimGroup, pimUser, role);
        membershipDao.save(membership);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimGroup group = pimGroupDAO.getByUUID(session, pimGroup.getUuid());
            assertThat(group, hasProperty("memberships", contains(membershipFor(pimUser, role))));
            final Membership membershipToDelete = membershipDao.getByUUID(membership.getUuid());

            pimGroupService.removeMembershipFromGroup(session, pimGroup.getUuid(), membershipToDelete.getUuid(), admin);
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            assertThat(pimGroupDAO.getByUUID(session, pimGroup.getUuid()), hasProperty("memberships", is(empty())));
        }
    }

    @Test
    public void removeMembershipIsAllowedForPiOfTheGroup() throws PimSecurityException, GroupNotFoundException {
        final PimGroup pimGroup = new PimGroup();
        pimGroupDAO.save(pimGroup);
        final PimUser pi = userWithMembershipAndPrimaryGroup(pimGroup, Role.PI);
        pimUserDao.save(pi);
        final PimUser pimUser = new PimUser();
        pimUserDao.save(pimUser);
        final Role role = Role.RESEARCHER;
        final MembershipDao membershipDao = new MembershipDao();
        final Membership membership = new Membership(pimGroup, pimUser, role);
        membershipDao.save(membership);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimGroup group = pimGroupDAO.getByUUID(session, pimGroup.getUuid());
            assertThat(group, hasProperty("memberships", hasItem(membershipFor(pimUser, role))));
            final Membership membershipToDelete = membershipDao.getByUUID(membership.getUuid());

            pimGroupService.removeMembershipFromGroup(session, pimGroup.getUuid(), membershipToDelete.getUuid(), pi);
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            assertThat(pimGroupDAO.getByUUID(session, pimGroup.getUuid()),
                    hasProperty("memberships", not(hasItem(membershipFor(pimUser, role))))
            );
        }
    }

    @Test(expected = PimSecurityException.class)
    public void removeMembershipIsNotAllowedForPiOfOtherGroup() throws PimSecurityException, GroupNotFoundException {
        final PimGroup pimGroup = new PimGroup();
        pimGroupDAO.save(pimGroup);
        final PimGroup otherGroup = new PimGroup();
        pimGroupDAO.save(otherGroup);
        final PimUser piOfOtherGroup = userWithMembershipAndPrimaryGroup(otherGroup, Role.PI);
        pimUserDao.save(piOfOtherGroup);
        final PimUser pimUser = new PimUser();
        pimUserDao.save(pimUser);
        final Role role = Role.PI;
        final MembershipDao membershipDao = new MembershipDao();
        final Membership membership = new Membership(pimGroup, pimUser, role);
        membershipDao.save(membership);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            pimGroupService.removeMembershipFromGroup(session, pimGroup.getUuid(), membership.getUuid(), piOfOtherGroup);
        }
    }

    @Test(expected = GroupNotFoundException.class)
    public void removeMembershipWillThrowGroupNotFoundExceptionWhenTheGroupCannotBeFound() throws PimSecurityException, GroupNotFoundException {
        final PimGroup pimGroup = new PimGroup();
        pimGroupDAO.save(pimGroup);
        final PimUser adminUser = adminUser();
        pimUserDao.save(adminUser);
        final PimUser pimUser = new PimUser();
        pimUserDao.save(pimUser);
        final Role role = Role.PI;
        final MembershipDao membershipDao = new MembershipDao();
        final Membership membership = new Membership(pimGroup, pimUser, role);
        membershipDao.save(membership);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            pimGroupService.removeMembershipFromGroup(session, UUID.randomUUID(), membership.getUuid(), adminUser);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeMembershipWillThrowIllegalArgumentExceptionWhenTheGroupAndTheGroupOfTheMembershipAreNotTheSame() throws PimSecurityException, GroupNotFoundException {
        final PimGroup pimGroup = new PimGroup();
        pimGroupDAO.save(pimGroup);
        final PimGroup otherGroup = new PimGroup();
        pimGroupDAO.save(otherGroup);
        final PimUser adminUser = adminUser();
        pimUserDao.save(adminUser);
        final PimUser pimUser = new PimUser();
        pimUserDao.save(pimUser);
        final Role role = Role.PI;
        final MembershipDao membershipDao = new MembershipDao();
        final Membership membership = new Membership(pimGroup, pimUser, role);
        membershipDao.save(membership);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            pimGroupService.removeMembershipFromGroup(session, otherGroup.getUuid(), membership.getUuid(), adminUser);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeMembershipWillThrowIllegalArgumentExceptionWhenTheMembershipDoesNotExist() throws PimSecurityException, GroupNotFoundException {
        final PimGroup pimGroup = new PimGroup();
        pimGroupDAO.save(pimGroup);
        final PimUser adminUser = adminUser();
        pimUserDao.save(adminUser);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            pimGroupService.removeMembershipFromGroup(session, pimGroup.getUuid(), UUID.randomUUID(), adminUser);
        }
    }

    @Test
    public void removeMembershipRemovesPrimaryGroupFromTheUserIfItIsEqualToTheGroup() throws PimSecurityException, GroupNotFoundException {
        final PimUser admin = adminUser();
        pimUserDao.save(admin);
        final PimGroup pimGroup = new PimGroup();
        pimGroupDAO.save(pimGroup);
        final PimUser pimUser = new PimUser();
        pimUser.setPrimaryGroup(pimGroup);
        pimUserDao.save(pimUser);
        final MembershipDao membershipDao = new MembershipDao();
        final Role role = Role.PI;
        final Membership membership = new Membership(pimGroup, pimUser, role);
        membershipDao.save(membership);


        final Membership membershipToDelete = membershipDao.getByUUID(membership.getUuid());
        assertThat(membershipToDelete.getPimUser(), hasProperty("primaryGroup", equalTo(pimGroup)));

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            pimGroupService.removeMembershipFromGroup(session, pimGroup.getUuid(), membershipToDelete.getUuid(), admin);
        }


        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            assertThat(pimUserDao.getByUUID(session, pimUser.getUuid()), hasProperty("primaryGroup", is(nullValue())));
        }
    }

    @Test
    public void removeMembershipDoesNotRemovePrimaryGroupWhenMembershipIsNotOfPrimaryGroup() throws PimSecurityException, GroupNotFoundException {
        final PimUser admin = adminUser();
        pimUserDao.save(admin);
        final PimGroup pimGroup = new PimGroup();
        pimGroupDAO.save(pimGroup);
        final PimGroup primaryGroup = new PimGroup();
        pimGroupDAO.save(primaryGroup);
        final PimUser pimUser = new PimUser();
        pimUser.setPrimaryGroup(primaryGroup);
        pimUserDao.save(pimUser);
        final MembershipDao membershipDao = new MembershipDao();
        final Role role = Role.PI;
        final Membership membership = new Membership(pimGroup, pimUser, role);
        membershipDao.save(membership);


        final Membership membershipToDelete = membershipDao.getByUUID(membership.getUuid());
        assertThat(membershipToDelete.getPimUser(), hasProperty("primaryGroup", equalTo(primaryGroup)));

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            pimGroupService.removeMembershipFromGroup(session, pimGroup.getUuid(), membershipToDelete.getUuid(), admin);
        }


        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            assertThat(pimUserDao.getByUUID(session, pimUser.getUuid()), hasProperty("primaryGroup", equalTo(primaryGroup)));
        }
    }

    @Test
    public void createIsAllowedForAdmins() throws PimSecurityException {
        final PimUser pimUser = adminUser();
        pimUserDao.save(pimUser);
        final PimGroup pimGroup = new PimGroup();

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            pimGroupService.create(session, pimGroup, pimUser);
            transaction.commit();
        }

        assertThat(pimGroupDAO.getByUUID(pimGroup.getUuid()), is(notNullValue()));
    }

    @Test(expected = PimSecurityException.class)
    public void createIsNotAllowedForNonAdmin() throws PimSecurityException {
        final PimGroup pimGroup = new PimGroup();
        pimGroupDAO.save(pimGroup);
        final PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup, Role.PI);
        pimUserDao.save(pimUser);
        final PimGroup groupToSave = new PimGroup();

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            pimGroupService.create(session, groupToSave, pimUser);
            transaction.commit();
        }
    }

    @Test
    public void addSubGroupIsAllowedForAdmins() throws PimSecurityException, GroupNotFoundException {
        final PimUser adminUser = adminUser();
        pimUserDao.save(adminUser);
        final PimGroup pimGroup = new PimGroup();
        pimGroupDAO.save(pimGroup);
        final PimGroup subGroup = new PimGroup();
        pimGroupDAO.save(subGroup);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            pimGroupService.addSubgroup(session, pimGroup.getUuid(), subGroup.getUuid(), adminUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            assertThat(pimGroupDAO.getByUUID(session, pimGroup.getUuid()).getSubgroups(), hasSize(1));
        }
    }

    @Test
    public void addSubGroupIsAllowedForPiOfGroup() throws PimSecurityException, GroupNotFoundException {
        final PimGroup pimGroup = new PimGroup();
        pimGroupDAO.save(pimGroup);
        final PimGroup subGroup = new PimGroup();
        pimGroupDAO.save(subGroup);
        final PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup, Role.PI);
        pimUserDao.save(pimUser);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            pimGroupService.addSubgroup(session, pimGroup.getUuid(), subGroup.getUuid(), pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            assertThat(pimGroupDAO.getByUUID(session, pimGroup.getUuid()).getSubgroups(), hasSize(1));
        }
    }

    @Test(expected = PimSecurityException.class)
    public void addSubGroupIsNotAllowedForPiOfSubGroup() throws PimSecurityException, GroupNotFoundException {
        final PimGroup pimGroup = new PimGroup();
        pimGroupDAO.save(pimGroup);
        final PimGroup subGroup = new PimGroup();
        pimGroupDAO.save(subGroup);
        final PimUser pimUser = userWithMembershipAndPrimaryGroup(subGroup, Role.PI);
        pimUserDao.save(pimUser);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            pimGroupService.addSubgroup(session, pimGroup.getUuid(), subGroup.getUuid(), pimUser);
            transaction.commit();
        }
    }

    @Test(expected = GroupNotFoundException.class)
    public void addSubGroupThrowsGroupNotFoundExceptionWhenGroupDoesNotExist() throws PimSecurityException, GroupNotFoundException {
        final PimGroup subGroup = new PimGroup();
        pimGroupDAO.save(subGroup);
        final PimUser pimUser = adminUser();
        pimUserDao.save(pimUser);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            pimGroupService.addSubgroup(session, UUID.randomUUID(), subGroup.getUuid(), pimUser);
            transaction.commit();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void addSubGroupThrowsIllegalArgumentExceptionWhenSubGroupDoesNotExist() throws PimSecurityException, GroupNotFoundException {
        final PimGroup pimGroup = new PimGroup();
        pimGroupDAO.save(pimGroup);
        final PimUser pimUser = adminUser();
        pimUserDao.save(pimUser);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            pimGroupService.addSubgroup(session, pimGroup.getUuid(), UUID.randomUUID(), pimUser);
            transaction.commit();
        }
    }

    @Test
    public void removeSubGroupIsAllowedForAdmins() throws PimSecurityException, GroupNotFoundException {
        final PimUser adminUser = adminUser();
        pimUserDao.save(adminUser);
        final PimGroup subGroup = new PimGroup();
        pimGroupDAO.save(subGroup);
        final PimGroup pimGroup = new PimGroup();
        pimGroup.addSubgroup(subGroup);
        pimGroupDAO.save(pimGroup);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            assertThat(pimGroupDAO.getByUUID(session, pimGroup.getUuid()).getSubgroups(), hasSize(1));
            final Transaction transaction = session.beginTransaction();
            pimGroupService.removeSubgroup(session, pimGroup.getUuid(), subGroup.getUuid(), adminUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            assertThat(pimGroupDAO.getByUUID(session, pimGroup.getUuid()).getSubgroups(), is(empty()));
        }
    }

    @Test
    public void removeSubGroupIsAllowedForPiOfGroup() throws PimSecurityException, GroupNotFoundException {
        final PimGroup subGroup = new PimGroup();
        pimGroupDAO.save(subGroup);
        final PimGroup pimGroup = new PimGroup();
        pimGroup.addSubgroup(subGroup);
        pimGroupDAO.save(pimGroup);
        final PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup, Role.PI);
        pimUserDao.save(pimUser);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            assertThat(pimGroupDAO.getByUUID(session, pimGroup.getUuid()).getSubgroups(), hasSize(1));
            final Transaction transaction = session.beginTransaction();
            pimGroupService.removeSubgroup(session, pimGroup.getUuid(), subGroup.getUuid(), pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            assertThat(pimGroupDAO.getByUUID(session, pimGroup.getUuid()).getSubgroups(), is(empty()));
        }
    }

    @Test(expected = PimSecurityException.class)
    public void removeSubGroupIsNotAllowedForPiOfSubgroup() throws PimSecurityException, GroupNotFoundException {
        final PimGroup subGroup = new PimGroup();
        pimGroupDAO.save(subGroup);
        final PimGroup pimGroup = new PimGroup();
        pimGroup.addSubgroup(subGroup);
        pimGroupDAO.save(pimGroup);
        final PimUser pimUser = userWithMembershipAndPrimaryGroup(subGroup, Role.PI);
        pimUserDao.save(pimUser);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            assertThat(pimGroupDAO.getByUUID(session, pimGroup.getUuid()).getSubgroups(), hasSize(1));
            final Transaction transaction = session.beginTransaction();
            pimGroupService.removeSubgroup(session, pimGroup.getUuid(), subGroup.getUuid(), pimUser);
            transaction.commit();
        }
    }

    @Test(expected = GroupNotFoundException.class)
    public void removeSubGroupThrowsGroupNotFoundExceptionWhenGroupDoesNotExist() throws PimSecurityException, GroupNotFoundException {
        final PimGroup subGroup = new PimGroup();
        pimGroupDAO.save(subGroup);
        final PimUser pimUser = adminUser();
        pimUserDao.save(pimUser);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            pimGroupService.removeSubgroup(session, UUID.randomUUID(), subGroup.getUuid(), pimUser);
            transaction.commit();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeSubGroupThrowsIllegalArgumentExceptionWhenSubGroupDoesNotExist() throws PimSecurityException, GroupNotFoundException {
        final PimGroup pimGroup = new PimGroup();
        pimGroupDAO.save(pimGroup);
        final PimUser pimUser = adminUser();
        pimUserDao.save(pimUser);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            pimGroupService.removeSubgroup(session, pimGroup.getUuid(), UUID.randomUUID(), pimUser);
            transaction.commit();
        }
    }

    @Test
    public void getAutoCompleteReturnsAllGroupsWithNamesLikeFilterForAdmin() {
        final PimUser pimUser = adminUser();
        pimUserDao.save(pimUser);
        final PimGroup pimGroup1 = new PimGroup();
        pimGroup1.setName("group1");
        pimGroupDAO.save(pimGroup1);
        final PimGroup pimGroup2 = new PimGroup();
        pimGroup2.setName("test123");
        pimGroupDAO.save(pimGroup2);


        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            final Stream<PimGroup> autoComplete = pimGroupService.getAutoComplete(session, pimUser, "test", 100, 0);

            assertThat(autoComplete.collect(Collectors.toList()), contains(hasProperty("name", equalTo("test123"))));

            transaction.commit();
        }
    }

    @Test
    public void getAutoCompleteSkipsTheFirstResultWhenSkipIsOne() {
        final PimUser pimUser = adminUser();
        pimUserDao.save(pimUser);
        final PimGroup pimGroup1 = new PimGroup();
        pimGroup1.setName("group1");
        pimGroupDAO.save(pimGroup1);
        final PimGroup pimGroup2 = new PimGroup();
        pimGroup2.setName("test123");
        pimGroupDAO.save(pimGroup2);


        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            final int skip = 1;
            final Stream<PimGroup> autoComplete = pimGroupService.getAutoComplete(session, pimUser, "", 100, skip);

            assertThat(autoComplete.collect(Collectors.toList()), hasSize(1));

            transaction.commit();
        }
    }

    @Test
    public void getAutocompleteReturnsTheGroupsForTheUsersMemberships() {
        final PimGroup pimGroup1 = new PimGroup();
        pimGroup1.setName("group1");
        pimGroupDAO.save(pimGroup1);
        final PimGroup pimGroup2 = new PimGroup();
        pimGroup2.setName("test123");
        pimGroupDAO.save(pimGroup2);
        final PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup2, Role.PI);
        pimUserDao.save(pimUser);


        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            final Stream<PimGroup> autoComplete = pimGroupService.getAutoComplete(session, pimUser, "", 100, 0);

            assertThat(autoComplete.collect(Collectors.toList()), contains(hasProperty("name", equalTo("test123"))));

            transaction.commit();
        }
    }

    @Test
    public void getAutocompleteReturnsTheGroupsForTheUsersMembershipsFiltersTheGroups() {
        final PimGroup pimGroup1 = new PimGroup();
        pimGroup1.setName("group1");
        pimGroupDAO.save(pimGroup1);
        final PimGroup pimGroup2 = new PimGroup();
        pimGroup2.setName("test123");
        pimGroupDAO.save(pimGroup2);
        final PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup2, Role.PI);
        pimUserDao.save(pimUser);


        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            final Stream<PimGroup> autoComplete = pimGroupService.getAutoComplete(session, pimUser, "group", 100, 0);

            assertThat(autoComplete.collect(Collectors.toList()), is(empty()));

            transaction.commit();
        }
    }

    @Test
    public void deleteSetsTheDeletedPropertyOfTheGroup() throws PimSecurityException, GroupNotFoundException {
        final PimGroup pimGroup1 = new PimGroup();
        pimGroup1.setName("group1");
        pimGroupDAO.save(pimGroup1);
        final PimUser pimUser = adminUser();
        pimUserDao.save(pimUser);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            pimGroupService.delete(session, pimGroup1.getUuid(), pimUser);
        }

        assertThat(pimGroupDAO.getByUUID(pimGroup1.getUuid()), hasProperty("deleted", is(not(nullValue()))));
    }

    @Test
    public void deleteSetsTheDeletedPropertyOfTheAclsOfTheGroup() throws PimSecurityException, GroupNotFoundException {
        final PimGroup pimGroup1 = new PimGroup();
        pimGroup1.setName("group1");
        pimGroupDAO.save(pimGroup1);
        final PimUser pimUser = adminUser();
        pimUserDao.save(pimUser);
        final AclDao aclDao = new AclDao();
        final Acl readPermission = Acl.readPermission(UUID.randomUUID(), pimGroup1, Role.RESEARCHER);
        aclDao.save(readPermission);
        final Acl updatePermission = Acl.updatePermission(UUID.randomUUID(), pimGroup1, Role.RESEARCHER);
        aclDao.save(updatePermission);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            pimGroupService.delete(session, pimGroup1.getUuid(), pimUser);
        }

        assertThat(aclDao.getByUUID(readPermission.getUuid()), hasProperty("deleted", is(not(nullValue()))));
        assertThat(aclDao.getByUUID(updatePermission.getUuid()), hasProperty("deleted", is(not(nullValue()))));
    }


    @Test(expected = PimSecurityException.class)
    public void deleteIsNotAllowedForPI() throws PimSecurityException, GroupNotFoundException {
        final PimGroup pimGroup1 = new PimGroup();
        pimGroup1.setName("group1");
        pimGroupDAO.save(pimGroup1);
        final PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup1, Role.PI);
        pimUserDao.save(pimUser);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            pimGroupService.delete(session, pimGroup1.getUuid(), pimUser);
        }
    }

    @Test(expected = PimSecurityException.class)
    public void deleteIsNotAllowedForResearcher() throws PimSecurityException, GroupNotFoundException {
        final PimGroup pimGroup1 = new PimGroup();
        pimGroup1.setName("group1");
        pimGroupDAO.save(pimGroup1);
        final PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup1, Role.RESEARCHER);
        pimUserDao.save(pimUser);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            pimGroupService.delete(session, pimGroup1.getUuid(), pimUser);
        }
    }

    @Test(expected = PimSecurityException.class)
    public void deleteIsNotAllowedForAssistant() throws PimSecurityException, GroupNotFoundException {
        final PimGroup pimGroup1 = new PimGroup();
        pimGroup1.setName("group1");
        pimGroupDAO.save(pimGroup1);
        final PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup1, Role.ASSISTANT);
        pimUserDao.save(pimUser);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            pimGroupService.delete(session, pimGroup1.getUuid(), pimUser);
        }
    }

    @Test(expected = PimSecurityException.class)
    public void deleteIsNotAllowedForAuthenticated() throws PimSecurityException, GroupNotFoundException {
        final PimGroup pimGroup1 = new PimGroup();
        pimGroup1.setName("group1");
        pimGroupDAO.save(pimGroup1);
        final PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup1, Role.AUTHENTICATED);
        pimUserDao.save(pimUser);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            pimGroupService.delete(session, pimGroup1.getUuid(), pimUser);
        }
    }


    @Test(expected = PimSecurityException.class)
    public void deleteIsNotAllowedForDisabledUser() throws PimSecurityException, GroupNotFoundException {
        final PimGroup pimGroup1 = new PimGroup();
        pimGroup1.setName("group1");
        pimGroupDAO.save(pimGroup1);
        final PimUser pimUser = adminUser();
        pimUser.setDisabled(true);
        pimUserDao.save(pimUser);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            pimGroupService.delete(session, pimGroup1.getUuid(), pimUser);
        }
    }

    @Test (expected = GroupNotFoundException.class)
    public void deleteThrowsGroupNotFoundExceptionWhenGroupDoesNotExist() throws PimSecurityException, GroupNotFoundException {
        final PimUser pimUser = adminUser();

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            pimGroupService.delete(session, UUID.randomUUID(), pimUser);
        }
    }

    private Matcher<Membership> membershipFor(PimUser user, Role role) {
        return allOf(
                hasProperty("pimUser", hasProperty("uuid", equalTo(user.getUuid()))),
                hasProperty("role", equalTo(role))
        );
    }
}