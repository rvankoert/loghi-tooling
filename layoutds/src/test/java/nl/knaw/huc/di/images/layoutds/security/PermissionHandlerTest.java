package nl.knaw.huc.di.images.layoutds.security;

import nl.knaw.huc.di.images.layoutds.DAO.AclDao;
import nl.knaw.huc.di.images.layoutds.DAO.ConfigurationDAO;
import nl.knaw.huc.di.images.layoutds.DAO.PimGroupDAO;
import nl.knaw.huc.di.images.layoutds.DAO.PimUserDao;
import nl.knaw.huc.di.images.layoutds.SessionFactorySingleton;
import nl.knaw.huc.di.images.layoutds.models.Configuration;
import nl.knaw.huc.di.images.layoutds.models.pim.Acl;
import nl.knaw.huc.di.images.layoutds.models.pim.PimGroup;
import nl.knaw.huc.di.images.layoutds.models.pim.PimUser;
import nl.knaw.huc.di.images.layoutds.models.pim.Role;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static nl.knaw.huc.di.images.layoutds.models.pim.Acl.Permission.*;
import static nl.knaw.huc.di.images.layoutds.services.AclMatcher.acl;
import static nl.knaw.huc.di.images.layoutds.services.AclTestHelpers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.fail;

public class PermissionHandlerTest {

    public static final UUID SUBJECT_UUID = UUID.randomUUID();
    private ConfigurationDAO configurationDAO;
    private PimGroupDAO pimGroupDAO;
    private PimUserDao pimUserDao;
    private PermissionHandler permissionHandler;
    private AclDao aclDao;

    @Before
    public void setUp() throws Exception {
        configurationDAO = new ConfigurationDAO();
        configurationDAO.save(new Configuration("useGroups", "true"));
        pimGroupDAO = new PimGroupDAO();
        pimUserDao = new PimUserDao();
        permissionHandler = new PermissionHandler();
        pimUserDao = new PimUserDao();
        aclDao = new AclDao();
    }

    @After
    public void tearDown() {
        // Start with a clean database with each test.
        SessionFactorySingleton.closeSessionFactory();
    }

    @Test
    public void addAclsAddsAclsForACertainSubjectAndPrimaryGroupOfTheUser() {
        final PimGroup pimGroup = new PimGroup();
        final UUID groupUuid = pimGroup.getUuid();
        pimGroupDAO.save(pimGroup);
        final PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup, Role.PI);
        pimUserDao.save(pimUser);
        final UUID subjectUuid = SUBJECT_UUID;

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PermissionHandler permissionHandler = new PermissionHandler();
            final Transaction transaction = session.beginTransaction();
            final PimUser byUUID = pimUserDao.getByUUID(session, pimUser.getUuid());
            permissionHandler.addAcls(session, subjectUuid, byUUID);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {

            final AclDao aclDao = this.aclDao;
            final Set<Acl> acls = aclDao.getBySubjectUuid(session, subjectUuid).collect(Collectors.toSet());

            assertThat(acls, containsInAnyOrder(
                    acl().forGroupWithUuid(groupUuid).forRole(Role.PI).withPermission(READ),
                    acl().forGroupWithUuid(groupUuid).forRole(Role.PI).withPermission(CREATE),
                    acl().forGroupWithUuid(groupUuid).forRole(Role.PI).withPermission(UPDATE),
                    acl().forGroupWithUuid(groupUuid).forRole(Role.PI).withPermission(DELETE),
                    acl().forGroupWithUuid(groupUuid).forRole(Role.RESEARCHER).withPermission(READ),
                    acl().forGroupWithUuid(groupUuid).forRole(Role.RESEARCHER).withPermission(CREATE),
                    acl().forGroupWithUuid(groupUuid).forRole(Role.RESEARCHER).withPermission(UPDATE),
                    acl().forGroupWithUuid(groupUuid).forRole(Role.ASSISTANT).withPermission(READ),
                    acl().forGroupWithUuid(groupUuid).forRole(Role.ASSISTANT).withPermission(UPDATE)
            ));
        }
    }

    @Test
    public void addAclsDoesNothingWhenGroupsAreNotUsed() {
        doNotUseGroups();
        final PimGroup pimGroup = new PimGroup();
        pimGroupDAO.save(pimGroup);
        final PimUser pimUser = userWithRoles(List.of(Role.PI));
        pimUserDao.save(pimUser);
        final UUID subjectUuid = SUBJECT_UUID;

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            final PimUser byUUID = pimUserDao.getByUUID(session, pimUser.getUuid());
            permissionHandler.addAcls(session, subjectUuid, byUUID);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {

            final AclDao aclDao = this.aclDao;
            final boolean hasItems = aclDao.getBySubjectUuid(session, subjectUuid).findAny().isPresent();

            assertThat(hasItems, is(false));
        }
    }

    @Test
    public void isAllowedToCreateReturnsTrueForAdmins() {
        final PimUser pimUser = adminUser();
        pimUserDao.save(pimUser);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser byUUID = pimUserDao.getByUUID(session, pimUser.getUuid());

            assertThat(permissionHandler.isAllowedToCreate(session, byUUID), is(true));
        }
    }

    @Test
    public void isAllowedToCreateReturnsTrueForPIs() {
        final PimGroup pimGroup = new PimGroup();
        pimGroupDAO.save(pimGroup);
        final PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup, Role.PI);
        pimUserDao.save(pimUser);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser byUUID = pimUserDao.getByUUID(session, pimUser.getUuid());

            assertThat(permissionHandler.isAllowedToCreate(session, byUUID), is(true));
        }
    }

    @Test
    public void isAllowedToCreateReturnsTrueForResearchers() {
        final PimGroup pimGroup = new PimGroup();
        pimGroupDAO.save(pimGroup);
        final PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup, Role.RESEARCHER);
        pimUserDao.save(pimUser);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser byUUID = pimUserDao.getByUUID(session, pimUser.getUuid());

            assertThat(permissionHandler.isAllowedToCreate(session, byUUID), is(true));
        }
    }

    @Test
    public void isAllowedToCreateReturnsFalseForAssistants() {
        final PimGroup pimGroup = new PimGroup();
        pimGroupDAO.save(pimGroup);
        final PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup, Role.ASSISTANT);
        pimUserDao.save(pimUser);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser byUUID = pimUserDao.getByUUID(session, pimUser.getUuid());

            assertThat(permissionHandler.isAllowedToCreate(session, byUUID), is(false));
        }
    }

    @Test
    public void isAllowedToCreateReturnsFalseWhenTheUserIsDisabled() {
        final PimGroup pimGroup = new PimGroup();
        pimGroupDAO.save(pimGroup);
        final PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup, Role.RESEARCHER);
        pimUser.setDisabled(true);
        pimUserDao.save(pimUser);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser byUUID = pimUserDao.getByUUID(session, pimUser.getUuid());

            assertThat(permissionHandler.isAllowedToCreate(session, byUUID), is(false));
        }
    }

    @Test
    public void isAllowedToCreateReturnsTrueForAdminsWhenNotUsingGroups() {
        doNotUseGroups();
        final PimGroup pimGroup = new PimGroup();
        pimGroupDAO.save(pimGroup);
        final PimUser pimUser = adminUser();
        pimUserDao.save(pimUser);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser byUUID = pimUserDao.getByUUID(session, pimUser.getUuid());

            assertThat(permissionHandler.isAllowedToCreate(session, byUUID), is(true));
        }
    }

    @Test
    public void isAllowedToCreateReturnsTrueForPIsWhenNotUsingGroups() {
        doNotUseGroups();
        final PimGroup pimGroup = new PimGroup();
        pimGroupDAO.save(pimGroup);
        final PimUser pimUser = userWithRoles(List.of(Role.PI));
        pimUserDao.save(pimUser);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser byUUID = pimUserDao.getByUUID(session, pimUser.getUuid());

            assertThat(permissionHandler.isAllowedToCreate(session, byUUID), is(true));
        }
    }

    @Test
    public void isAllowedToCreateReturnsTrueForResearchersWhenNotUsingGroups() {
        doNotUseGroups();
        final PimGroup pimGroup = new PimGroup();
        pimGroupDAO.save(pimGroup);
        final PimUser pimUser = userWithRoles(List.of(Role.RESEARCHER));
        pimUserDao.save(pimUser);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser byUUID = pimUserDao.getByUUID(session, pimUser.getUuid());

            assertThat(permissionHandler.isAllowedToCreate(session, byUUID), is(true));
        }
    }

    @Test
    public void isAllowedToCreateReturnsFalseForAssistantsWhenNotUsingGroups() {
        final PimGroup pimGroup = new PimGroup();
        pimGroupDAO.save(pimGroup);
        final PimUser pimUser = userWithRoles(List.of(Role.ASSISTANT));
        pimUserDao.save(pimUser);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser byUUID = pimUserDao.getByUUID(session, pimUser.getUuid());

            assertThat(permissionHandler.isAllowedToCreate(session, byUUID), is(false));
        }
    }

    @Test
    public void isAllowedToCreateReturnsFalseIfTheUserIsDisabledAndNotUsingGroups() {
        final PimGroup pimGroup = new PimGroup();
        pimGroupDAO.save(pimGroup);
        final PimUser pimUser = userWithRoles(List.of(Role.ADMIN));
        pimUser.setDisabled(true);
        pimUserDao.save(pimUser);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser byUUID = pimUserDao.getByUUID(session, pimUser.getUuid());

            assertThat(permissionHandler.isAllowedToCreate(session, byUUID), is(false));
        }
    }

    @Test
    public void isAllowedToUpdateReturnsTrueForRoleWithUpdatePermission() {
        final PimGroup pimGroup = new PimGroup();
        pimGroupDAO.save(pimGroup);
        final AclDao aclDao = this.aclDao;
        final Acl acl = Acl.updatePermission(SUBJECT_UUID, pimGroup, Role.ASSISTANT);
        aclDao.save(acl);
        final PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup, Role.ASSISTANT);
        pimUserDao.save(pimUser);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser byUUID = pimUserDao.getByUUID(session, pimUser.getUuid());

            assertThat(permissionHandler.isAllowedToUpdate(session, byUUID, SUBJECT_UUID), is(true));
        }
    }

    @Test
    public void isAllowedToUpdateReturnsTrueForAdmin() {
        final PimUser pimUser = adminUser();
        pimUserDao.save(pimUser);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser byUUID = pimUserDao.getByUUID(session, pimUser.getUuid());

            assertThat(permissionHandler.isAllowedToUpdate(session, byUUID, SUBJECT_UUID), is(true));
        }
    }


    @Test
    public void isAllowedToUpdateReturnsFalseForRoleWithoutUpdatePermission() {
        final PimGroup pimGroup = new PimGroup();
        pimGroupDAO.save(pimGroup);
        final PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup, Role.ASSISTANT);
        pimUserDao.save(pimUser);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser byUUID = pimUserDao.getByUUID(session, pimUser.getUuid());

            assertThat(permissionHandler.isAllowedToUpdate(session, byUUID, SUBJECT_UUID), is(false));
        }
    }

    @Test
    public void isAllowedToUpdateReturnsFalseWhenTheUserIsDisabled() {
        final PimGroup pimGroup = new PimGroup();
        pimGroupDAO.save(pimGroup);
        final PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup, Role.RESEARCHER);
        pimUser.setDisabled(true);
        pimUserDao.save(pimUser);
        final AclDao aclDao = this.aclDao;
        final Acl acl = Acl.updatePermission(SUBJECT_UUID, pimGroup, Role.RESEARCHER);
        aclDao.save(acl);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser byUUID = pimUserDao.getByUUID(session, pimUser.getUuid());

            assertThat(permissionHandler.isAllowedToUpdate(session, byUUID, SUBJECT_UUID), is(false));
        }
    }

    @Test
    public void isAllowedToUpdateReturnsTrueForAdminWithoutUsingGroups() {
        doNotUseGroups();
        final PimUser pimUser = userWithRoles(List.of(Role.ADMIN));

        pimUserDao.save(pimUser);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser byUUID = pimUserDao.getByUUID(session, pimUser.getUuid());

            assertThat(permissionHandler.isAllowedToUpdate(session, byUUID, SUBJECT_UUID), is(true));
        }
    }

    @Test
    public void isAllowedToUpdateReturnsTrueForPIWithoutUsingGroups() {
        doNotUseGroups();
        final PimUser pimUser = userWithRoles(List.of(Role.PI));

        pimUserDao.save(pimUser);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser byUUID = pimUserDao.getByUUID(session, pimUser.getUuid());

            assertThat(permissionHandler.isAllowedToUpdate(session, byUUID, SUBJECT_UUID), is(true));
        }
    }

    @Test
    public void isAllowedToUpdateReturnsTrueForResearcherWithoutUsingGroups() {
        doNotUseGroups();
        final PimUser pimUser = userWithRoles(List.of(Role.RESEARCHER));

        pimUserDao.save(pimUser);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser byUUID = pimUserDao.getByUUID(session, pimUser.getUuid());

            assertThat(permissionHandler.isAllowedToUpdate(session, byUUID, SUBJECT_UUID), is(true));
        }
    }

    @Test
    public void isAllowedToUpdateReturnsTrueForAssistantWithoutUsingGroups() {
        doNotUseGroups();
        final PimUser pimUser = userWithRoles(List.of(Role.ASSISTANT));

        pimUserDao.save(pimUser);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser byUUID = pimUserDao.getByUUID(session, pimUser.getUuid());

            assertThat(permissionHandler.isAllowedToUpdate(session, byUUID, SUBJECT_UUID), is(true));
        }
    }

    @Test
    public void isAllowedToUpdateReturnsFalseWhenTheUserIsDisabledWithoutUsingGroups() {
        doNotUseGroups();
        final PimUser pimUser = userWithRoles(List.of(Role.ADMIN));
        pimUser.setDisabled(true);

        pimUserDao.save(pimUser);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser byUUID = pimUserDao.getByUUID(session, pimUser.getUuid());

            assertThat(permissionHandler.isAllowedToUpdate(session, byUUID, SUBJECT_UUID), is(false));
        }
    }


    @Test
    public void isAllowedToDeleteReturnsTrueForAdmin() {
        final PimUser pimUser = adminUser();
        pimUserDao.save(pimUser);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser byUUID = pimUserDao.getByUUID(session, pimUser.getUuid());

            assertThat(permissionHandler.isAllowedToDelete(session, byUUID, SUBJECT_UUID), is(true));
        }
    }

    @Test
    public void isAllowedToDeleteReturnsFalseForRoleWithoutUpdatePermission() {
        final PimGroup pimGroup = new PimGroup();
        pimGroupDAO.save(pimGroup);
        final PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup, Role.ASSISTANT);
        pimUserDao.save(pimUser);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser byUUID = pimUserDao.getByUUID(session, pimUser.getUuid());

            assertThat(permissionHandler.isAllowedToDelete(session, byUUID, SUBJECT_UUID), is(false));
        }
    }

    @Test
    public void isAllowedToDeleteReturnsWhenTheAdminUserIsDisabled() {
        final PimUser pimUser = adminUser();
        pimUser.setDisabled(true);
        pimUserDao.save(pimUser);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser byUUID = pimUserDao.getByUUID(session, pimUser.getUuid());

            assertThat(permissionHandler.isAllowedToDelete(session, byUUID, SUBJECT_UUID), is(false));
        }
    }

    @Test
    public void isAllowedToDeleteReturnsFalseWhenTheUserIsDisabled() {
        final PimGroup pimGroup = new PimGroup();
        pimGroupDAO.save(pimGroup);
        final PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup, Role.PI);
        pimUser.setDisabled(true);
        pimUserDao.save(pimUser);
        final Acl acl = Acl.deletePermission(SUBJECT_UUID, pimGroup, Role.PI);
        this.aclDao.save(acl);


        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser byUUID = pimUserDao.getByUUID(session, pimUser.getUuid());

            assertThat(permissionHandler.isAllowedToDelete(session, byUUID, SUBJECT_UUID), is(false));
        }
    }

    @Test
    public void isAllowedToDeleteReturnsTrueForAdminWithoutUsingGroups() {
        doNotUseGroups();
        final PimUser pimUser = userWithRoles(List.of(Role.ADMIN));

        pimUserDao.save(pimUser);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser byUUID = pimUserDao.getByUUID(session, pimUser.getUuid());

            assertThat(permissionHandler.isAllowedToDelete(session, byUUID, SUBJECT_UUID), is(true));
        }
    }

    @Test
    public void isAllowedToDeleteReturnsTrueForPIWithoutUsingGroups() {
        doNotUseGroups();
        final PimUser pimUser = userWithRoles(List.of(Role.PI));

        pimUserDao.save(pimUser);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser byUUID = pimUserDao.getByUUID(session, pimUser.getUuid());

            assertThat(permissionHandler.isAllowedToDelete(session, byUUID, SUBJECT_UUID), is(true));
        }
    }

    @Test
    public void isAllowedToDeleteReturnsFalseIfTheUserIsDisabledWithoutUsingGroups() {
        doNotUseGroups();
        final PimUser pimUser = userWithRoles(List.of(Role.PI));
        pimUser.setDisabled(true);

        pimUserDao.save(pimUser);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser byUUID = pimUserDao.getByUUID(session, pimUser.getUuid());

            assertThat(permissionHandler.isAllowedToDelete(session, byUUID, SUBJECT_UUID), is(false));
        }
    }

    @Test
    public void isAllowedToDeleteReturnsFalseForResearcherWithoutUsingGroups() {
        doNotUseGroups();
        final PimUser pimUser = userWithRoles(List.of(Role.RESEARCHER));

        pimUserDao.save(pimUser);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser byUUID = pimUserDao.getByUUID(session, pimUser.getUuid());

            assertThat(permissionHandler.isAllowedToDelete(session, byUUID, SUBJECT_UUID), is(false));
        }
    }

    @Test
    public void isAllowedToDeleteReturnsFalseForAssistantWithoutUsingGroups() {
        doNotUseGroups();
        final PimUser pimUser = userWithRoles(List.of(Role.ASSISTANT));

        pimUserDao.save(pimUser);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser byUUID = pimUserDao.getByUUID(session, pimUser.getUuid());

            assertThat(permissionHandler.isAllowedToDelete(session, byUUID, SUBJECT_UUID), is(false));
        }

    }

    @Test
    public void removeAllAclsForSubjectRemovesTheAclsOfAnEntity() {
        final PimGroup pimGroup = new PimGroup();
        pimGroupDAO.save(pimGroup);
        final PimGroup otherGroup = new PimGroup();
        pimGroupDAO.save(otherGroup);
        final PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup, Role.PI);
        pimUserDao.save(pimUser);
        final UUID subjectUuid = SUBJECT_UUID;

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            aclDao.save(Acl.readPermission(subjectUuid, pimGroup, Role.PI));
            aclDao.save(Acl.readPermission(subjectUuid, otherGroup, Role.PI));
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            permissionHandler.removeAllAclsForSubject(session, subjectUuid);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final boolean hasAnyAcl = aclDao.getBySubjectUuid(session, subjectUuid).findAny().isPresent();
            assertThat(hasAnyAcl, is(false));
        }
    }

    @Test
    public void removeAllAclsForSubjectDoesNothingWhenNotUsingGroups() {
        doNotUseGroups();
        final PimGroup pimGroup = new PimGroup();
        pimGroupDAO.save(pimGroup);
        final PimGroup otherGroup = new PimGroup();
        pimGroupDAO.save(otherGroup);
        final PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup, Role.PI);
        pimUserDao.save(pimUser);
        final UUID subjectUuid = SUBJECT_UUID;

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            aclDao.save(Acl.readPermission(subjectUuid, pimGroup, Role.PI));
            aclDao.save(Acl.readPermission(subjectUuid, otherGroup, Role.PI));
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            permissionHandler.removeAllAclsForSubject(session, subjectUuid);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final boolean hasAnyAcl = aclDao.getBySubjectUuid(session, subjectUuid).findAny().isPresent();
            assertThat(hasAnyAcl, is(true));
        }
    }

    @Test
    public void isAllowedToReadReturnsTrueIfTheUserHasARoleWithReadPermissionInGroupOfSubject() {
        final PimGroup pimGroup = new PimGroup();
        pimGroupDAO.save(pimGroup);
        final Acl readPermission = Acl.readPermission(SUBJECT_UUID, pimGroup, Role.ASSISTANT);
        this.aclDao.save(readPermission);
        final PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup, Role.ASSISTANT);
        pimUserDao.save(pimUser);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser byUUID = pimUserDao.getByUUID(session, pimUser.getUuid());

            assertThat(permissionHandler.isAllowedToRead(session, SUBJECT_UUID, byUUID), is(true));
        }
    }

    @Test
    public void isAllowedToReadReturnsTrueIfTheUsersWithTheReadRoleInASubgroup() {
        final PimGroup supergroup = new PimGroup();
        pimGroupDAO.save(supergroup);
        final PimGroup subgroup = new PimGroup();
        subgroup.addSupergroup(supergroup);
        pimGroupDAO.save(subgroup);
        try(final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            final Acl readPermission = Acl.readPermission(SUBJECT_UUID, pimGroupDAO.getByUUID(session, supergroup.getUuid()), Role.ASSISTANT);
            this.aclDao.save(session, readPermission);
            transaction.commit();
        }
        final PimUser pimUser = userWithMembershipAndPrimaryGroup(subgroup, Role.ASSISTANT);
        pimUserDao.save(pimUser);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser byUUID = pimUserDao.getByUUID(session, pimUser.getUuid());

            assertThat(permissionHandler.isAllowedToRead(session, SUBJECT_UUID, byUUID), is(true));
        }
    }

    @Test
    public void isAllowedToReadReturnsFalseIfTheUserHasNoRoleWithReadPermissionInGroupOfSubject() {
        final PimGroup pimGroup = new PimGroup();
        pimGroupDAO.save(pimGroup);
        final PimUser userWithoutPermission = userWithMembershipAndPrimaryGroup(pimGroup, Role.ASSISTANT);
        pimUserDao.save(userWithoutPermission);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser byUUID = pimUserDao.getByUUID(session, userWithoutPermission.getUuid());

            assertThat(permissionHandler.isAllowedToRead(session, SUBJECT_UUID, byUUID), is(false));
        }
    }

    @Test
    public void isAllowedToReadReturnsFalseWhenTheUserHasNoPrimaryGroup() {
        final PimUser pimUser = new PimUser();
        pimUserDao.save(pimUser);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser byUUID = pimUserDao.getByUUID(session, pimUser.getUuid());

            assertThat(permissionHandler.isAllowedToRead(session, SUBJECT_UUID, byUUID), is(false));
        }
    }

    @Test
    public void isAllowedToReadReturnsFalseIfTheUserIsDisabled() {
        final PimGroup supergroup = new PimGroup();
        pimGroupDAO.save(supergroup);
        final PimGroup subgroup = new PimGroup();
        subgroup.addSupergroup(supergroup);
        pimGroupDAO.save(subgroup);
        try(final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            final Acl readPermission = Acl.readPermission(SUBJECT_UUID, pimGroupDAO.getByUUID(session, supergroup.getUuid()), Role.ASSISTANT);
            this.aclDao.save(session, readPermission);
            transaction.commit();
        }
        final PimUser pimUser = userWithMembershipAndPrimaryGroup(subgroup, Role.ASSISTANT);
        pimUser.setDisabled(true);
        pimUserDao.save(pimUser);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser byUUID = pimUserDao.getByUUID(session, pimUser.getUuid());

            assertThat(permissionHandler.isAllowedToRead(session, SUBJECT_UUID, byUUID), is(false));
        }
    }

    @Test
    public void isAllowedToReadReturnsTrueForAdmins() {
        final PimUser admin = userWithRoles(List.of(Role.ADMIN));
        pimUserDao.save(admin);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final boolean allowedToRead = permissionHandler.isAllowedToRead(session, SUBJECT_UUID, admin);

            assertThat(allowedToRead, is(true));
        }
    }

    @Test
    public void isAllowedToReadReturnsTrueWhenNoGroupsAreUsed() {
        doNotUseGroups();
        final PimUser pimUser = userWithRoles(List.of(Role.AUTHENTICATED));
        pimUserDao.save(pimUser);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {

            assertThat(permissionHandler.isAllowedToRead(session, SUBJECT_UUID, pimUser), is(true));
        }
    }

    @Test
    public void isAllowedToReadReturnsFalseWhenNoGroupsAreUsedAndTheUserIsDisabled() {
        doNotUseGroups();
        final PimUser pimUser = userWithRoles(List.of(Role.AUTHENTICATED));
        pimUser.setDisabled(true);
        pimUserDao.save(pimUser);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {

            assertThat(permissionHandler.isAllowedToRead(session, SUBJECT_UUID, pimUser), is(false));
        }
    }

    private void doNotUseGroups() {
        final Configuration useGroups = configurationDAO.getByKey("useGroups");
        useGroups.setValue("false");
        configurationDAO.save(useGroups);
    }
}