package nl.knaw.huc.di.images.layoutds.services;

import nl.knaw.huc.di.images.layoutds.DAO.*;
import nl.knaw.huc.di.images.layoutds.SessionFactorySingleton;
import nl.knaw.huc.di.images.layoutds.StudentJpaConfig;
import nl.knaw.huc.di.images.layoutds.exceptions.NotFoundException;
import nl.knaw.huc.di.images.layoutds.exceptions.PimSecurityException;
import nl.knaw.huc.di.images.layoutds.exceptions.ValidationException;
import nl.knaw.huc.di.images.layoutds.models.Configuration;
import nl.knaw.huc.di.images.layoutds.models.DocumentImage;
import nl.knaw.huc.di.images.layoutds.models.DocumentImageSet;
import nl.knaw.huc.di.images.layoutds.models.MetaData;
import nl.knaw.huc.di.images.layoutds.models.pim.Acl;
import nl.knaw.huc.di.images.layoutds.models.pim.PimGroup;
import nl.knaw.huc.di.images.layoutds.models.pim.PimUser;
import nl.knaw.huc.di.images.layoutds.models.pim.Role;
import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import javax.persistence.NoResultException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static nl.knaw.huc.di.images.layoutds.models.pim.Acl.Permission.*;
import static nl.knaw.huc.di.images.layoutds.services.AclMatcher.acl;
import static nl.knaw.huc.di.images.layoutds.services.AclTestHelpers.adminUser;
import static nl.knaw.huc.di.images.layoutds.services.AclTestHelpers.userWithMembershipAndPrimaryGroup;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {StudentJpaConfig.class}, loader = AnnotationConfigContextLoader.class)
public class DocumentImageSetServiceTest {

    private final DocumentImageDAO documentImageDAO = new DocumentImageDAO();
    private DocumentImageSetService documentImageSetService;
    private DocumentImageSetDAO documentImageSetDAO;
    private PimUserDao pimUserDao;
    private UUID pimUserUuid;
    private UUID primaryGroupUuid;
    private PimGroupDAO pimGroupDAO;
    private ConfigurationDAO configurationDAO;

    @Before
    public void setUp() {
        documentImageSetService = new DocumentImageSetService((session, uuid) -> "remoteUri");
        pimUserDao = new PimUserDao();
        pimGroupDAO = new PimGroupDAO();

        final PimGroup primaryGroup = new PimGroup();
        primaryGroupUuid = primaryGroup.getUuid();
        pimGroupDAO.save(primaryGroup);

        PimUser pimUser = userWithMembershipAndPrimaryGroup(primaryGroup, Role.PI);
        pimUserDao.save(pimUser);
        pimUserUuid = pimUser.getUuid();

        documentImageSetDAO = new DocumentImageSetDAO();
        configurationDAO = new ConfigurationDAO();
        configurationDAO.save(new Configuration("useGroups", "true"));
    }

    @After
    public void tearDown() {
        // Start with a clean database with each test.
        SessionFactorySingleton.closeSessionFactory();
    }

    @Test
    public void saveSetsTheOwnerAndCreationDate() throws PimSecurityException, ValidationException {
        final DocumentImageSet documentImageSet = createDocumentImageSet("imageset", "http://example.org");

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, pimUserUuid);
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.save(session, documentImageSet, pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final DocumentImageSet byUUID = documentImageSetDAO.getByUUID(session, documentImageSet.getUuid());

            assertThat(byUUID, allOf(
                    hasProperty("owner", hasProperty("uuid", equalTo(pimUserUuid))),
                    hasProperty("created", not(equalTo(nullValue())))
            ));
        }
    }

    @Test
    public void saveAddsAclsForDocumentImageSet() throws PimSecurityException, ValidationException {
        final DocumentImageSet documentImageSet = createDocumentImageSet("imageset", "http://example.org");

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, pimUserUuid);
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.save(session, documentImageSet, pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {

            final AclDao aclDao = new AclDao();
            final Set<Acl> acls = aclDao.getBySubjectUuid(session, documentImageSet.getUuid()).collect(Collectors.toSet());

            assertThat(acls, containsInAnyOrder(
                    acl().forGroupWithUuid(primaryGroupUuid).forRole(Role.PI).withPermission(READ),
                    acl().forGroupWithUuid(primaryGroupUuid).forRole(Role.PI).withPermission(CREATE),
                    acl().forGroupWithUuid(primaryGroupUuid).forRole(Role.PI).withPermission(UPDATE),
                    acl().forGroupWithUuid(primaryGroupUuid).forRole(Role.PI).withPermission(DELETE),
                    acl().forGroupWithUuid(primaryGroupUuid).forRole(Role.RESEARCHER).withPermission(READ),
                    acl().forGroupWithUuid(primaryGroupUuid).forRole(Role.RESEARCHER).withPermission(CREATE),
                    acl().forGroupWithUuid(primaryGroupUuid).forRole(Role.RESEARCHER).withPermission(UPDATE),
                    acl().forGroupWithUuid(primaryGroupUuid).forRole(Role.ASSISTANT).withPermission(READ),
                    acl().forGroupWithUuid(primaryGroupUuid).forRole(Role.ASSISTANT).withPermission(UPDATE)
            ));
        }
    }

    @Test
    public void saveSetsUriIfItIsEmpty() throws PimSecurityException, ValidationException {
        final DocumentImageSet documentImageSet = new DocumentImageSet();
        documentImageSet.setImageset("imageset");

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, pimUserUuid);
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.save(session, documentImageSet, pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final DocumentImageSet byUUID = documentImageSetDAO.getByUUID(session, documentImageSet.getUuid());

            assertThat(byUUID, hasProperty("uri", allOf(
                    not(nullValue()),
                    not(isEmptyString())
            )));
        }
    }

    @Test
    public void saveDoesNotOverrideTheUri() throws PimSecurityException, ValidationException {
        final String uri = "http://example.org";
        final DocumentImageSet documentImageSet = createDocumentImageSet("imageset", uri);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, pimUserUuid);
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.save(session, documentImageSet, pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final DocumentImageSet byUUID = documentImageSetDAO.getByUUID(session, documentImageSet.getUuid());

            assertThat(byUUID, hasProperty("uri", equalTo(uri)));
        }
    }

    @Test
    public void saveSetsTheRemoteUri() throws PimSecurityException, ValidationException {
        final DocumentImageSet documentImageSet = createDocumentImageSet("imageset", "http://example.org");

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, pimUserUuid);
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.save(session, documentImageSet, pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final DocumentImageSet byUUID = documentImageSetDAO.getByUUID(session, documentImageSet.getUuid());

            assertThat(byUUID, hasProperty("remoteUri", allOf(
                    not(nullValue()),
                    not(isEmptyString())
            )));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void saveThrowsAnExceptionIfTheDocumentImageSetAlreadyExists() throws PimSecurityException, ValidationException {
        final DocumentImageSet documentImageSet = createDocumentImageSet("imageset", "http://example.org");

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, pimUserUuid);
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.save(session, documentImageSet, pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, pimUserUuid);
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.save(session, documentImageSet, pimUser);
            transaction.commit();
        }
    }

    @Test(expected = PimSecurityException.class)
    public void saveThrowsAPimSecurityExceptionIfUserOnlyHasTheRoleAuthenticatedWithinTheGroup() throws PimSecurityException, ValidationException {
        final DocumentImageSet documentImageSet = createDocumentImageSet("imageset", "http://example.org");
        final PimUser pimUser1 = userWithMembershipAndPrimaryGroup(pimGroupDAO.getByUUID(primaryGroupUuid), Role.AUTHENTICATED);
        pimUserDao.save(pimUser1);


        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser userWithNotEnoughPermissions = pimUserDao.getByUUID(session, pimUser1.getUuid());
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.save(session, documentImageSet, userWithNotEnoughPermissions);
            transaction.commit();
        }
    }

    @Test(expected = PimSecurityException.class)
    public void saveThrowsAPimSecurityExceptionIfUserOnlyHasTheRoleAssistantWithinTheGroup() throws PimSecurityException, ValidationException {
        final DocumentImageSet documentImageSet = createDocumentImageSet("imageset", "http://example.org");
        final PimUser pimUser1 = userWithMembershipAndPrimaryGroup(pimGroupDAO.getByUUID(primaryGroupUuid), Role.ASSISTANT);
        pimUserDao.save(pimUser1);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser userWithNotEnoughPermissions = pimUserDao.getByUUID(session, pimUser1.getUuid());
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.save(session, documentImageSet, userWithNotEnoughPermissions);
            transaction.commit();
        }
    }

    @Test(expected = PimSecurityException.class)
    public void saveThrowsAPimSecurityExceptionIfTheUserDoesNotHaveAPrimaryGroup() throws PimSecurityException, ValidationException {
        final DocumentImageSet documentImageSet = createDocumentImageSet("imageset", "http://example.org");
        final PimUser pimUser = new PimUser();
        pimUser.addMembership(pimGroupDAO.getByUUID(primaryGroupUuid), Role.PI);
        pimUserDao.save(pimUser);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser userWithNotEnoughPermissions = pimUserDao.getByUUID(session, pimUser.getUuid());
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.save(session, documentImageSet, userWithNotEnoughPermissions);
            transaction.commit();
        }
    }

    @Test(expected = PimSecurityException.class)
    public void saveThrowsAPimSecurityExceptionIfUserIsDisabled() throws PimSecurityException, ValidationException {
        final DocumentImageSet documentImageSet = createDocumentImageSet("imageset", "http://example.org");
        final PimUser pimUser1 = userWithMembershipAndPrimaryGroup(pimGroupDAO.getByUUID(primaryGroupUuid), Role.PI);
        pimUser1.setDisabled(true);
        pimUserDao.save(pimUser1);


        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser userWithNotEnoughPermissions = pimUserDao.getByUUID(session, pimUser1.getUuid());
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.save(session, documentImageSet, userWithNotEnoughPermissions);
            transaction.commit();
        }
    }

    @Test(expected = ValidationException.class)
    public void saveThrowsAValidationExceptionWhenTheDocumentImageSetIsNotvalid() throws PimSecurityException, ValidationException {
        final DocumentImageSet nonValidDocumentImageSet = new DocumentImageSet();

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, pimUserUuid);
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.save(session, nonValidDocumentImageSet, pimUser);
            transaction.commit();
        }
    }

    @Test(expected = ValidationException.class)
    public void saveThrowsAValidationExceptionWhenTheUserAllreadyHasASetWithTheSameName() throws PimSecurityException, ValidationException {
        final DocumentImageSet documentImageSet = createDocumentImageSet("imageset", "http://example.org");

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser userWithEnoughPermissions = pimUserDao.getByUUID(session, pimUserUuid);
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.save(session, documentImageSet, userWithEnoughPermissions);
            transaction.commit();
        }

        final DocumentImageSet documentImageSetWithSameNameAndUser = createDocumentImageSet("imageset", "http://example.org");

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser userWithEnoughPermissions = pimUserDao.getByUUID(session, pimUserUuid);
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.save(session, documentImageSetWithSameNameAndUser, userWithEnoughPermissions);
            transaction.commit();
        }
    }

    @Test
    public void streamAllForUserReturnsTheSetsOfTheUsersPrimaryGroup() throws PimSecurityException, ValidationException {
        final PimGroup pimGroup = new PimGroup();
        pimGroupDAO.save(pimGroup);

        PimUser pimUser2 = userWithMembershipAndPrimaryGroup(pimGroup, Role.PI);
        UUID pimUser2Uuid = pimUser2.getUuid();
        pimUserDao.save(pimUser2);

        final DocumentImageSet documentImageSet1 = createDocumentImageSet("imageset", "http://example.org");
        final DocumentImageSet documentImageSet2 = createDocumentImageSet("imageset2", "http://example.com");
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, pimUserUuid);
            pimUser2 = pimUserDao.getByUUID(session, pimUser2Uuid);
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.save(session, documentImageSet1, pimUser);
            documentImageSetService.save(session, documentImageSet2, pimUser2);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            pimUser2 = pimUserDao.getByUUID(session, pimUser2Uuid);
            Stream<DocumentImageSet> documentImageSetStream = documentImageSetService.streamAllForUser(session, pimUser2, false);

            final Set<DocumentImageSet> documentImageSets = documentImageSetStream.collect(Collectors.toSet());
            assertThat(documentImageSets, hasItem(hasProperty("uuid", equalTo(documentImageSet2.getUuid()))));
        }
    }

    @Test
    public void streamAllForUserReturnsThePublicSetsForNonAdmin() throws PimSecurityException, ValidationException {
        final PimGroup pimGroup = new PimGroup();
        pimGroupDAO.save(pimGroup);

        PimUser pimUser2 = userWithMembershipAndPrimaryGroup(pimGroup, Role.PI);
        UUID pimUser2Uuid = pimUser2.getUuid();
        pimUserDao.save(pimUser2);

        final DocumentImageSet publicSet = createDocumentImageSet("publicSet", "http://example.org");
        publicSet.setPublicDocumentImageSet(true);
        final DocumentImageSet privateSet = createDocumentImageSet("privateSet", "http://example.com");
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, pimUserUuid);
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.save(session, publicSet, pimUser);
            documentImageSetService.save(session, privateSet, pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            makePrivate(privateSet, session);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            pimUser2 = pimUserDao.getByUUID(session, pimUser2Uuid);
            Stream<DocumentImageSet> documentImageSetStream = documentImageSetService.streamAllForUser(session, pimUser2, false);

            final Set<DocumentImageSet> documentImageSets = documentImageSetStream.collect(Collectors.toSet());
            assertThat(documentImageSets, contains(hasProperty("uuid", equalTo(publicSet.getUuid()))));
        }
    }

    @Test
    public void streamAllForUserReturnsAllDataForUserWithAdminRole() throws PimSecurityException, ValidationException {
        PimUser admin = adminUser();
        UUID adminUuid = admin.getUuid();
        pimUserDao.save(admin);

        final DocumentImageSet documentImageSet1 = createDocumentImageSet("imageset", "http://example.org");
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, pimUserUuid);
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.save(session, documentImageSet1, pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            admin = pimUserDao.getByUUID(session, adminUuid);
            Stream<DocumentImageSet> documentImageSetStream = documentImageSetService.streamAllForUser(session, admin, false);

            final Set<DocumentImageSet> documentImageSets = documentImageSetStream.collect(Collectors.toSet());
            assertThat(documentImageSets, contains(hasProperty("uuid", equalTo(documentImageSet1.getUuid()))));
        }
    }

    @Test
    public void streamAllForUserReturnsEmptyStreamWhenUserIsDisabled() throws PimSecurityException, ValidationException {
        PimUser admin = adminUser();
        admin.setDisabled(true);
        UUID adminUuid = admin.getUuid();
        pimUserDao.save(admin);

        final DocumentImageSet documentImageSet1 = createDocumentImageSet("imageset", "http://example.org");
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, pimUserUuid);
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.save(session, documentImageSet1, pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            admin = pimUserDao.getByUUID(session, adminUuid);
            Stream<DocumentImageSet> documentImageSetStream = documentImageSetService.streamAllForUser(session, admin, false);

            assertThat(documentImageSetStream.findAny(), hasProperty("empty", is(true)));
        }
    }

    @Test
    public void streamAllForUserReturnsUsersOwnDataWhenNoGroupsAreUsed() throws PimSecurityException, ValidationException {
        PimUser pimUser2 = userWithMembershipAndPrimaryGroup(pimGroupDAO.getByUUID(primaryGroupUuid), Role.PI);
        UUID pimUser2Uuid = pimUser2.getUuid();
        pimUserDao.save(pimUser2);

        DocumentImageSet documentImageSet1 = createDocumentImageSet("imageset", "http://example.org");
        DocumentImageSet documentImageSet2 = createDocumentImageSet("imageset", "http://example.con");
        doNotUseGroups();
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, pimUserUuid);
            pimUser2 = pimUserDao.getByUUID(session, pimUser2Uuid);
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.save(session, documentImageSet1, pimUser);
            documentImageSetService.save(session, documentImageSet2, pimUser2);
            transaction.commit();
        }
        // Make data private, data is by default public
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            makePrivate(documentImageSet1, session);
            makePrivate(documentImageSet2, session);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            pimUser2 = pimUserDao.getByUUID(session, pimUser2Uuid);
            Stream<DocumentImageSet> documentImageSetStream = documentImageSetService.streamAllForUser(session, pimUser2, false);

            final Set<DocumentImageSet> documentImageSets = documentImageSetStream.collect(Collectors.toSet());
            assertThat(documentImageSets, contains(
                    hasProperty("uuid", equalTo(documentImageSet2.getUuid()))
            ));
        }
    }

    private void doNotUseGroups() {
        final Configuration useGroups = configurationDAO.getByKey("useGroups");
        useGroups.setValue("false");
        configurationDAO.save(useGroups);
    }

    private void makePrivate(DocumentImageSet documentImageSet, Session session) {
        documentImageSet = documentImageSetDAO.getByUUID(documentImageSet.getUuid());
        documentImageSet.setPublicDocumentImageSet(false);
        documentImageSetDAO.save(session, documentImageSet);
    }

    @Test
    public void streamAllForUserReturnsPublicDataWhenNoGroupsAreUsed() throws PimSecurityException, ValidationException {
        PimUser pimUser2 = userWithMembershipAndPrimaryGroup(pimGroupDAO.getByUUID(primaryGroupUuid), Role.PI);
        UUID pimUser2Uuid = pimUser2.getUuid();
        pimUserDao.save(pimUser2);

        final DocumentImageSet documentImageSet1 = createDocumentImageSet("imageset", "http://example.org");
        final DocumentImageSet publicSet = createDocumentImageSet("imageset2", "http://example.net");
        doNotUseGroups();
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, pimUserUuid);
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.save(session, documentImageSet1, pimUser);
            documentImageSetService.save(session, publicSet, pimUser);
            transaction.commit();
        }

        // Make data private, data is by default public
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            makePrivate(documentImageSet1, session);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            pimUser2 = pimUserDao.getByUUID(session, pimUser2Uuid);
            Stream<DocumentImageSet> documentImageSetStream = documentImageSetService.streamAllForUser(session, pimUser2, false);

            final Set<DocumentImageSet> documentImageSets = documentImageSetStream.collect(Collectors.toSet());
            assertThat(documentImageSets, contains(
                    hasProperty("uuid", equalTo(publicSet.getUuid()))
            ));
        }
    }

    @Test
    public void streamAllForUserReturnsTheSetsOfSuperGroups() throws PimSecurityException, ValidationException {
        final PimGroup pimGroup = new PimGroup();
        pimGroup.addSupergroup(pimGroupDAO.getByUUID(primaryGroupUuid));
        pimGroupDAO.save(pimGroup);

        PimUser pimUser2 = userWithMembershipAndPrimaryGroup(pimGroup, Role.PI);
        UUID pimUser2Uuid = pimUser2.getUuid();
        pimUserDao.save(pimUser2);

        final DocumentImageSet documentImageSet1 = createDocumentImageSet("imageset", "http://example.org");
        final DocumentImageSet documentImageSet2 = createDocumentImageSet("imageset2", "http://example.com");
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, pimUserUuid);
            pimUser2 = pimUserDao.getByUUID(session, pimUser2Uuid);
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.save(session, documentImageSet1, pimUser);
            documentImageSetService.save(session, documentImageSet2, pimUser2);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            pimUser2 = pimUserDao.getByUUID(session, pimUser2Uuid);
            Stream<DocumentImageSet> documentImageSetStream = documentImageSetService.streamAllForUser(session, pimUser2, false);

            final Set<DocumentImageSet> documentImageSets = documentImageSetStream.collect(Collectors.toSet());
            assertThat(documentImageSets, containsInAnyOrder(
                    hasProperty("uuid", equalTo(documentImageSet2.getUuid())),
                    hasProperty("uuid", equalTo(documentImageSet1.getUuid()))
            ));
        }
    }

    private DocumentImageSet createDocumentImageSet(String imageset, String uri) {
        final DocumentImageSet documentImageSet1 = new DocumentImageSet();
        documentImageSet1.setImageset(imageset);
        documentImageSet1.setUri(uri);
        return documentImageSet1;
    }

    @Test(expected = NoResultException.class)
    public void getByUuidThrowsNotResultException() throws PimSecurityException, ValidationException {
        final PimGroup pimGroup = new PimGroup();
        pimGroupDAO.save(pimGroup);

        PimUser pimUser2 = userWithMembershipAndPrimaryGroup(pimGroup, Role.ASSISTANT);
        pimUserDao.save(pimUser2);
        UUID pimUser2Uuid = pimUser2.getUuid();

        final DocumentImageSet documentImageSet1 = createDocumentImageSet("imageset", "http://example.org");
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, pimUserUuid);
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.save(session, documentImageSet1, pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            makePrivate(documentImageSet1, session);
            transaction.commit();
        }


        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            pimUser2 = pimUserDao.getByUUID(session, pimUser2Uuid);
            Optional<DocumentImageSet> documentImageSet = documentImageSetService.getByUuid(session, documentImageSet1.getUuid(), pimUser2);

//            assertThat(documentImageSet, hasProperty("empty", equalTo(true)));

        }
    }

    @Test
    public void getByUuidReturnsTheDocumentImageSetIfTheUserIsOwner() throws PimSecurityException, ValidationException {
        final DocumentImageSet documentImageSet1 = createDocumentImageSet("imageset", "http://example.org");
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, pimUserUuid);
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.save(session, documentImageSet1, pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, pimUserUuid);
            Optional<DocumentImageSet> documentImageSet = documentImageSetService.getByUuid(session, documentImageSet1.getUuid(), pimUser);

            assertThat(documentImageSet, hasProperty("empty", equalTo(false)));
        }
    }

    @Test
    public void getByUuidReturnsTheDocumentImageSetIfTheUserIsInSameGroupAsOwner() throws PimSecurityException, ValidationException {
        final PimUser pimUser2 = userWithMembershipAndPrimaryGroup(pimGroupDAO.getByUUID(primaryGroupUuid), Role.ASSISTANT);
        pimUserDao.save(pimUser2);
        final UUID pimUser2Uuid = pimUser2.getUuid();
        final DocumentImageSet documentImageSet1 = createDocumentImageSet("imageset", "http://example.org");
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, pimUserUuid);
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.save(session, documentImageSet1, pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, pimUser2Uuid);
            Optional<DocumentImageSet> documentImageSet = documentImageSetService.getByUuid(session, documentImageSet1.getUuid(), pimUser);

            assertThat(documentImageSet, hasProperty("empty", equalTo(false)));
        }
    }

    @Test
    public void getByUuidReturnsTheImageSetIfItIsOwnedByASupergroup() throws PimSecurityException, ValidationException {
        final PimGroup pimGroup = new PimGroup();
        pimGroup.addSupergroup(pimGroupDAO.getByUUID(primaryGroupUuid));
        pimGroupDAO.save(pimGroup);

        PimUser pimUser2 = userWithMembershipAndPrimaryGroup(pimGroup, Role.ASSISTANT);
        pimUserDao.save(pimUser2);
        UUID pimUser2Uuid = pimUser2.getUuid();

        final DocumentImageSet documentImageSet1 = createDocumentImageSet("imageset", "http://example.org");
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, pimUserUuid);
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.save(session, documentImageSet1, pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            pimUser2 = pimUserDao.getByUUID(session, pimUser2Uuid);
            Optional<DocumentImageSet> documentImageSet = documentImageSetService.getByUuid(session, documentImageSet1.getUuid(), pimUser2);

            assertThat(documentImageSet, hasProperty("empty", equalTo(false)));

        }
    }

    @Test
    public void getByUuidReturnsTheImageSetForAnAdmin() throws PimSecurityException, ValidationException {
        PimUser admin = adminUser();
        pimUserDao.save(admin);
        UUID adminUuid = admin.getUuid();


        final DocumentImageSet documentImageSet1 = createDocumentImageSet("imageset", "http://example.org");
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, pimUserUuid);
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.save(session, documentImageSet1, pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            makePrivate(documentImageSet1, session);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            admin = pimUserDao.getByUUID(session, adminUuid);
            Optional<DocumentImageSet> documentImageSet = documentImageSetService.getByUuid(session, documentImageSet1.getUuid(), admin);

            assertThat(documentImageSet, hasProperty("empty", equalTo(false)));

        }
    }

    // FIXME TI-351: create complete fix
    @Test
    public void getByUuidReturnsTheImageSetForASiameseNetworkMinion() throws PimSecurityException, ValidationException {
        PimUser minion = AclTestHelpers.userWithRoles(List.of(Role.SIAMESENETWORK_MINION));
        pimUserDao.save(minion);
        UUID minionUuid = minion.getUuid();


        final DocumentImageSet documentImageSet1 = createDocumentImageSet("imageset", "http://example.org");
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, pimUserUuid);
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.save(session, documentImageSet1, pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            makePrivate(documentImageSet1, session);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            minion = pimUserDao.getByUUID(session, minionUuid);
            Optional<DocumentImageSet> documentImageSet = documentImageSetService.getByUuid(session, documentImageSet1.getUuid(), minion);

            assertThat(documentImageSet, hasProperty("empty", equalTo(false)));

        }
    }

    @Test
    public void getByUuidReturnsThePublicDocumentImageSet() throws PimSecurityException, ValidationException {
        final PimGroup pimGroup = new PimGroup();
        pimGroupDAO.save(pimGroup);
        PimUser userOutsideOwningGroup = userWithMembershipAndPrimaryGroup(pimGroup, Role.PI);
        pimUserDao.save(userOutsideOwningGroup);
        UUID userUuid = userOutsideOwningGroup.getUuid();

        final DocumentImageSet publicImageSet = createDocumentImageSet("imageset", "http://example.org");
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, pimUserUuid);
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.save(session, publicImageSet, pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            userOutsideOwningGroup = pimUserDao.getByUUID(session, userUuid);
            Optional<DocumentImageSet> documentImageSet = documentImageSetService.getByUuid(session, publicImageSet.getUuid(), userOutsideOwningGroup);

            assertThat(documentImageSet, hasProperty("empty", equalTo(false)));

        }
    }

    @Test
    public void getByUuidReturnsThePublicDocumentImageSetWhenUserIsNull() throws PimSecurityException, ValidationException {
        final PimGroup pimGroup = new PimGroup();
        pimGroupDAO.save(pimGroup);

        final DocumentImageSet publicImageSet = createDocumentImageSet("imageset", "http://example.org");
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, pimUserUuid);
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.save(session, publicImageSet, pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            Optional<DocumentImageSet> documentImageSet = documentImageSetService.getByUuid(session, publicImageSet.getUuid(), null);

            assertThat(documentImageSet, hasProperty("empty", equalTo(false)));

        }
    }

    @Test
    public void getByUuidReturnsDocumentImageSetOwnedByUserWhenNotUsingGroups() throws PimSecurityException, ValidationException {
        PimUser pimUser2 = userWithMembershipAndPrimaryGroup(pimGroupDAO.getByUUID(primaryGroupUuid), Role.ASSISTANT);
        pimUserDao.save(pimUser2);

        doNotUseGroups();
        final DocumentImageSet documentImageSet1 = createDocumentImageSet("imageset", "http://example.org");
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, pimUserUuid);
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.save(session, documentImageSet1, pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            pimUser2 = pimUserDao.getByUUID(session, pimUserUuid);
            Optional<DocumentImageSet> documentImageSet = documentImageSetService.getByUuid(session, documentImageSet1.getUuid(), pimUser2);

            assertThat(documentImageSet, hasProperty("empty", equalTo(false)));

        }
    }


    @Test
    public void getByUuidReturnsPublicDocumentImageSetWhenNotUsingGroups() throws PimSecurityException, ValidationException {
        PimUser pimUser2 = userWithMembershipAndPrimaryGroup(pimGroupDAO.getByUUID(primaryGroupUuid), Role.ASSISTANT);
        pimUserDao.save(pimUser2);
        UUID pimUser2Uuid = pimUser2.getUuid();

        doNotUseGroups();
        final DocumentImageSet publicSet = createDocumentImageSet("imageset", "http://example.org");
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, pimUserUuid);
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.save(session, publicSet, pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            pimUser2 = pimUserDao.getByUUID(session, pimUser2Uuid);
            Optional<DocumentImageSet> documentImageSet = documentImageSetService.getByUuid(session, publicSet.getUuid(), pimUser2);

            assertThat(documentImageSet, hasProperty("empty", equalTo(false)));

        }
    }

    @Test(expected = NoResultException.class)
    public void getByUuidWillNotReturnNonPublicDocumentImageSetWhenNotUsingGroups() throws PimSecurityException, ValidationException {
        PimUser pimUser2 = userWithMembershipAndPrimaryGroup(pimGroupDAO.getByUUID(primaryGroupUuid), Role.ASSISTANT);
        pimUserDao.save(pimUser2);
        UUID pimUser2Uuid = pimUser2.getUuid();

        doNotUseGroups();
        final DocumentImageSet nonPublicSet = createDocumentImageSet("imageset", "http://example.org");
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, pimUserUuid);
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.save(session, nonPublicSet, pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            makePrivate(nonPublicSet, session);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            pimUser2 = pimUserDao.getByUUID(session, pimUser2Uuid);
            Optional<DocumentImageSet> documentImageSet = documentImageSetService.getByUuid(session, nonPublicSet.getUuid(), pimUser2);

//            assertThat(documentImageSet, hasProperty("empty", equalTo(true)));
        }
    }

    @Test(expected = NoResultException.class)
    public void getByUuidWillNotReturnNonPublicDocumentImageSetWhenTheUserIsDisabled() throws PimSecurityException, ValidationException {
        PimUser pimUser2 = userWithMembershipAndPrimaryGroup(pimGroupDAO.getByUUID(primaryGroupUuid), Role.ADMIN);
        pimUser2.setDisabled(true);
        pimUserDao.save(pimUser2);
        UUID pimUser2Uuid = pimUser2.getUuid();

        doNotUseGroups();
        final DocumentImageSet nonPublicSet = createDocumentImageSet("imageset", "http://example.org");
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, pimUserUuid);
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.save(session, nonPublicSet, pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            makePrivate(nonPublicSet, session);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            pimUser2 = pimUserDao.getByUUID(session, pimUser2Uuid);
            documentImageSetService.getByUuid(session, nonPublicSet.getUuid(), pimUser2);

        }
    }


    @Test(expected = PimSecurityException.class)
    public void updateThrowsAnExceptionIfThePrimaryGroupOfTheUserDoesNotHaveUpdateRights() throws PimSecurityException, ValidationException {
        final PimGroup otherGroup = new PimGroup();
        pimGroupDAO.save(otherGroup);
        PimUser pimUser2 = userWithMembershipAndPrimaryGroup(otherGroup, Role.ASSISTANT);
        pimUserDao.save(pimUser2);
        UUID pimUser2Uuid = pimUser2.getUuid();
        final DocumentImageSet documentImageSet1 = createDocumentImageSet("imageset", "http://example.org");
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, pimUserUuid);
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.save(session, documentImageSet1, pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, pimUser2Uuid);
            final DocumentImageSet setToUpdate = documentImageSetDAO.getByUUID(documentImageSet1.getUuid());
            setToUpdate.setImageset("otherImageSet");
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.update(session, setToUpdate, pimUser);
            transaction.commit();
        }
    }

    @Test(expected = PimSecurityException.class)
    public void updateThrowsAnExceptionIfTheUserIsNotAllowedToEditDataOfThePrimaryGroup() throws PimSecurityException, ValidationException {
        final PimUser pimUser1 = userWithMembershipAndPrimaryGroup(pimGroupDAO.getByUUID(primaryGroupUuid), Role.AUTHENTICATED);
        pimUserDao.save(pimUser1);
        final DocumentImageSet documentImageSet = createDocumentImageSet("imageset", "http://example.org");
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, pimUserUuid);
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.save(session, documentImageSet, pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser userWithNotEnoughRights = pimUserDao.getByUUID(session, pimUser1.getUuid());
            final DocumentImageSet setToUpdate = documentImageSetDAO.getByUUID(session, documentImageSet.getUuid());
            setToUpdate.setImageset("another");
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.update(session, setToUpdate, userWithNotEnoughRights);
            transaction.commit();
        }
    }

    @Test(expected = PimSecurityException.class)
    public void updateThrowsExceptionIfTheUserIsDisabled() throws PimSecurityException, ValidationException {
        PimUser pimUser2 = userWithMembershipAndPrimaryGroup(pimGroupDAO.getByUUID(primaryGroupUuid), Role.ASSISTANT);
        pimUser2.setDisabled(true);
        pimUserDao.save(pimUser2);
        UUID pimUser2Uuid = pimUser2.getUuid();
        final DocumentImageSet documentImageSet1 = createDocumentImageSet("imageset", "http://example.org");
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, pimUserUuid);
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.save(session, documentImageSet1, pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, pimUser2Uuid);
            final DocumentImageSet setToUpdate = documentImageSetDAO.getByUUID(documentImageSet1.getUuid());
            setToUpdate.setImageset("otherImageSet");
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.update(session, setToUpdate, pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final DocumentImageSet updatedSet = documentImageSetDAO.getByUUID(session, documentImageSet1.getUuid());

            assertThat(updatedSet, hasProperty("imageset", equalTo("otherImageSet")));
        }
    }

    @Test
    public void updateSetsTheUpdateDocumentImageSet() throws PimSecurityException, ValidationException {
        PimUser pimUser2 = userWithMembershipAndPrimaryGroup(pimGroupDAO.getByUUID(primaryGroupUuid), Role.ASSISTANT);
        pimUserDao.save(pimUser2);
        UUID pimUser2Uuid = pimUser2.getUuid();
        final DocumentImageSet documentImageSet1 = createDocumentImageSet("imageset", "http://example.org");
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, pimUserUuid);
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.save(session, documentImageSet1, pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, pimUser2Uuid);
            final DocumentImageSet setToUpdate = documentImageSetDAO.getByUUID(documentImageSet1.getUuid());
            setToUpdate.setImageset("otherImageSet");
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.update(session, setToUpdate, pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final DocumentImageSet updatedSet = documentImageSetDAO.getByUUID(session, documentImageSet1.getUuid());

            assertThat(updatedSet, hasProperty("imageset", equalTo("otherImageSet")));
        }
    }

    @Test
    public void updateIsAllowedWhenASuperGroupContainsPermissions() throws PimSecurityException, ValidationException {
        final PimGroup superSuperGroup = pimGroupDAO.getByUUID(primaryGroupUuid);
        final PimGroup superGroup = new PimGroup();
        superGroup.addSupergroup(superSuperGroup);
        pimGroupDAO.save(superGroup);
        final PimGroup pimGroup = new PimGroup();
        pimGroup.addSupergroup(superGroup);
        pimGroupDAO.save(pimGroup);
        PimUser pimUser2 = userWithMembershipAndPrimaryGroup(pimGroup, Role.ASSISTANT);
        pimUserDao.save(pimUser2);
        UUID pimUser2Uuid = pimUser2.getUuid();
        final DocumentImageSet documentImageSet1 = createDocumentImageSet("imageset", "http://example.org");
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, pimUserUuid);
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.save(session, documentImageSet1, pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, pimUser2Uuid);
            final DocumentImageSet setToUpdate = documentImageSetDAO.getByUUID(documentImageSet1.getUuid());
            setToUpdate.setImageset("otherImageSet");
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.update(session, setToUpdate, pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final DocumentImageSet updatedSet = documentImageSetDAO.getByUUID(session, documentImageSet1.getUuid());

            assertThat(updatedSet, hasProperty("imageset", equalTo("otherImageSet")));
        }
    }

    @Test
    public void updateIsAllowedWhenUserIsAnAdmin() throws PimSecurityException, ValidationException {
        PimUser admin = adminUser();
        pimUserDao.save(admin);
        UUID adminUuid = admin.getUuid();
        final DocumentImageSet documentImageSet1 = createDocumentImageSet("imageset", "http://example.org");
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, pimUserUuid);
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.save(session, documentImageSet1, pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            admin = pimUserDao.getByUUID(session, adminUuid);
            final DocumentImageSet setToUpdate = documentImageSetDAO.getByUUID(documentImageSet1.getUuid());
            setToUpdate.setImageset("otherImageSet");
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.update(session, setToUpdate, admin);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final DocumentImageSet updatedSet = documentImageSetDAO.getByUUID(session, documentImageSet1.getUuid());

            assertThat(updatedSet, hasProperty("imageset", equalTo("otherImageSet")));
        }
    }

    @Test
    public void updateIsAllowedForPublicDataWhenUserIsAssistantOrHigher() throws PimSecurityException, ValidationException {
        final PimUser pimUser1 = AclTestHelpers.userWithRoles(List.of(Role.ASSISTANT));
        pimUserDao.save(pimUser1);
        final DocumentImageSet documentImageSet = createDocumentImageSet("imageset", "http://example.org");
        doNotUseGroups();
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, pimUserUuid);
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.save(session, documentImageSet, pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser userWithNotEnoughRights = pimUserDao.getByUUID(session, pimUser1.getUuid());
            final DocumentImageSet setToUpdate = documentImageSetDAO.getByUUID(session, documentImageSet.getUuid());
            setToUpdate.setImageset("another");
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.update(session, setToUpdate, userWithNotEnoughRights);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final DocumentImageSet updatedSet = documentImageSetDAO.getByUUID(session, documentImageSet.getUuid());

            assertThat(updatedSet, hasProperty("imageset", equalTo("another")));
        }
    }

    @Test(expected = PimSecurityException.class)
    public void updateIsNotAllowedForPublicDataWhenUserIsOnlyAuthenticated() throws PimSecurityException, ValidationException {
        final PimUser pimUser1 = userWithMembershipAndPrimaryGroup(pimGroupDAO.getByUUID(primaryGroupUuid), Role.AUTHENTICATED);
        pimUserDao.save(pimUser1);
        final DocumentImageSet documentImageSet = createDocumentImageSet("imageset", "http://example.org");
        doNotUseGroups();
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, pimUserUuid);
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.save(session, documentImageSet, pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser userWithNotEnoughRights = pimUserDao.getByUUID(session, pimUser1.getUuid());
            final DocumentImageSet setToUpdate = documentImageSetDAO.getByUUID(session, documentImageSet.getUuid());
            setToUpdate.setImageset("another");
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.update(session, setToUpdate, userWithNotEnoughRights);
            transaction.commit();
        }

    }

    @Test(expected = PimSecurityException.class)
    public void updateIsNotAllowedForPrivateDataOfOtherUsers() throws PimSecurityException, ValidationException {
        final PimUser pimUser1 = userWithMembershipAndPrimaryGroup(pimGroupDAO.getByUUID(primaryGroupUuid), Role.ASSISTANT);
        pimUserDao.save(pimUser1);
        final DocumentImageSet documentImageSet = createDocumentImageSet("imageset", "http://example.org");
        doNotUseGroups();
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, pimUserUuid);
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.save(session, documentImageSet, pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            makePrivate(documentImageSet, session);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser userWithNotEnoughRights = pimUserDao.getByUUID(session, pimUser1.getUuid());
            final DocumentImageSet setToUpdate = documentImageSetDAO.getByUUID(session, documentImageSet.getUuid());
            setToUpdate.setImageset("another");
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.update(session, setToUpdate, userWithNotEnoughRights);
            transaction.commit();
        }
    }

    @Test
    public void isAllowedToUpdateReturnsTrueForUserWithRoleAdmin() throws ValidationException, PimSecurityException {
        PimUser adminUser = adminUser();
        pimUserDao.save(adminUser);
        DocumentImageSet documentImageSet = createDocumentImageSet("imageset", "http://example.org");
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, pimUserUuid);
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.save(session, documentImageSet, pimUser);
            transaction.commit();
        }
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            adminUser = pimUserDao.getByUUID(session, adminUser.getUuid());
            documentImageSet = documentImageSetDAO.getByUUID(session, documentImageSet.getUuid());
            assertThat(documentImageSetService.userIsAllowedToEdit(session, documentImageSet, adminUser), is(true));
        }
    }

    @Test
    public void isAllowedToUpdateReturnsTrueForAnAssistantInTheGroup() throws ValidationException, PimSecurityException {
        final PimGroup pimGroup = pimGroupDAO.getByUUID(primaryGroupUuid);
        PimUser assistant = userWithMembershipAndPrimaryGroup(pimGroup, Role.ASSISTANT);
        pimUserDao.save(assistant);
        DocumentImageSet documentImageSet = createDocumentImageSet("imageset", "http://example.org");
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, pimUserUuid);
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.save(session, documentImageSet, pimUser);
            transaction.commit();
        }
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            assistant = pimUserDao.getByUUID(session, assistant.getUuid());
            documentImageSet = documentImageSetDAO.getByUUID(session, documentImageSet.getUuid());
            assertThat(documentImageSetService.userIsAllowedToEdit(session, documentImageSet, assistant), is(true));
        }
    }

    @Test
    public void isAllowedToUpdateReturnsTrueForTheOwnerOfTheSetWhenNotUsingGroups() throws ValidationException, PimSecurityException {
        DocumentImageSet documentImageSet = createDocumentImageSet("imageset", "http://example.org");
        doNotUseGroups();
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, pimUserUuid);
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.save(session, documentImageSet, pimUser);

            transaction.commit();
        }
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            makePrivate(documentImageSet, session);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser owner = pimUserDao.getByUUID(session, pimUserUuid);
            documentImageSet = documentImageSetDAO.getByUUID(session, documentImageSet.getUuid());
            assertThat(documentImageSetService.userIsAllowedToEdit(session, documentImageSet, owner), is(true));
        }
    }

    @Test
    public void isAllowedToUpdateReturnsTrueForAnAssistantWhenNotUsingGroupsAndSetIsPublicWhenNotUserGroups() throws ValidationException, PimSecurityException {
        final PimGroup pimGroup = pimGroupDAO.getByUUID(primaryGroupUuid);
        PimUser assistant = userWithMembershipAndPrimaryGroup(pimGroup, Role.ASSISTANT);
        pimUserDao.save(assistant);
        DocumentImageSet documentImageSet = createDocumentImageSet("imageset", "http://example.org");
        doNotUseGroups();
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, pimUserUuid);
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.save(session, documentImageSet, pimUser);
            transaction.commit();
        }
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            assistant = pimUserDao.getByUUID(session, assistant.getUuid());
            documentImageSet = documentImageSetDAO.getByUUID(session, documentImageSet.getUuid());
            assertThat(documentImageSetService.userIsAllowedToEdit(session, documentImageSet, assistant), is(true));
        }
    }

    @Test
    public void isAllowedToUpdateReturnsFalseWhenUserIsDisabledSetIsPublicWhenNotUserGroups() throws ValidationException, PimSecurityException {
        doNotUseGroups();
        final PimGroup pimGroup = pimGroupDAO.getByUUID(primaryGroupUuid);
        PimUser assistant = userWithMembershipAndPrimaryGroup(pimGroup, Role.ADMIN);
        assistant.setDisabled(true);
        pimUserDao.save(assistant);
        DocumentImageSet documentImageSet = createDocumentImageSet("imageset", "http://example.org");
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, pimUserUuid);
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.save(session, documentImageSet, pimUser);
            transaction.commit();
        }
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            assistant = pimUserDao.getByUUID(session, assistant.getUuid());
            documentImageSet = documentImageSetDAO.getByUUID(session, documentImageSet.getUuid());
            assertThat(documentImageSetService.userIsAllowedToEdit(session, documentImageSet, assistant), is(false));
        }
    }

    @Test
    public void isAllowedToUpdateReturnsFalseIfUserIsNotAMemberOfTheGroup() throws ValidationException, PimSecurityException {
        final PimGroup pimGroup = new PimGroup();
        pimGroupDAO.save(pimGroup);
        PimUser notGroupMember = userWithMembershipAndPrimaryGroup(pimGroup, Role.ASSISTANT);
        pimUserDao.save(notGroupMember);
        DocumentImageSet documentImageSet = createDocumentImageSet("imageset", "http://example.org");
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, pimUserUuid);
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.save(session, documentImageSet, pimUser);
            transaction.commit();
        }
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            notGroupMember = pimUserDao.getByUUID(session, notGroupMember.getUuid());
            documentImageSet = documentImageSetDAO.getByUUID(session, documentImageSet.getUuid());
            assertThat(documentImageSetService.userIsAllowedToEdit(session, documentImageSet, notGroupMember), is(false));
        }
    }

    @Test
    public void isAllowedToUpdateReturnsFalseIfTheSetIsPrivateAndTheUserIsNotTheOwnerWhenNotUsingGroups() throws ValidationException, PimSecurityException {
        PimUser otherUser = new PimUser();
        pimUserDao.save(otherUser);
        DocumentImageSet documentImageSet = createDocumentImageSet("imageset", "http://example.org");
        doNotUseGroups();
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, pimUserUuid);
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.save(session, documentImageSet, pimUser);

            transaction.commit();
        }
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            makePrivate(documentImageSet, session);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            otherUser = pimUserDao.getByUUID(session, otherUser.getUuid());
            documentImageSet = documentImageSetDAO.getByUUID(session, documentImageSet.getUuid());
            assertThat(documentImageSetService.userIsAllowedToEdit(session, documentImageSet, otherUser), is(false));
        }
    }

    @Test
    public void deleteRemovesDocumentImageSet() throws PimSecurityException, ValidationException {
        final PimGroup pimGroup = new PimGroup();
        pimGroupDAO.save(pimGroup);
        final PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup, Role.PI);
        pimUserDao.save(pimUser);

        final DocumentImageSet documentImageSet = createDocumentImageSet("imageset", "http://example.org");
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.save(session, documentImageSet, pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.delete(session, documentImageSet.getUuid(), pimUser);
            transaction.commit();
        }

        assertThat(documentImageSetDAO.getByUUID(documentImageSet.getUuid()), is(nullValue()));
    }

    @Test
    public void deleteIsAllowedForAdmins() throws PimSecurityException, ValidationException {
        final PimGroup pimGroup = new PimGroup();
        pimGroupDAO.save(pimGroup);
        final PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup, Role.PI);
        pimUserDao.save(pimUser);
        final PimUser admin = adminUser();
        pimUserDao.save(admin);

        final DocumentImageSet documentImageSet = createDocumentImageSet("imageset", "http://example.org");
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.save(session, documentImageSet, pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.delete(session, documentImageSet.getUuid(), admin);
            transaction.commit();
        }

        assertThat(documentImageSetDAO.getByUUID(documentImageSet.getUuid()), is(nullValue()));
    }

    @Test
    public void deleteRemovesAcls() throws PimSecurityException, ValidationException {
        final PimGroup pimGroup = new PimGroup();
        pimGroupDAO.save(pimGroup);
        final PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup, Role.PI);
        pimUserDao.save(pimUser);

        final DocumentImageSet documentImageSet = createDocumentImageSet("imageset", "http://example.org");
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.save(session, documentImageSet, pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.delete(session, documentImageSet.getUuid(), pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final AclDao aclDao = new AclDao();
            final Set<Acl> acls = aclDao.getBySubjectUuid(session, documentImageSet.getUuid()).collect(Collectors.toSet());
            assertThat(acls, is(empty()));
        }

    }

    @Test(expected = PimSecurityException.class)
    public void deleteThrowsPimSecurityExceptionForPiOfOtherGroup() throws PimSecurityException, ValidationException {
        final PimGroup pimGroup = new PimGroup();
        pimGroupDAO.save(pimGroup);
        final PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup, Role.PI);
        pimUserDao.save(pimUser);

        final PimGroup otherGroup = new PimGroup();
        pimGroupDAO.save(otherGroup);
        final PimUser piOfOtherGroup = userWithMembershipAndPrimaryGroup(otherGroup, Role.PI);
        pimUserDao.save(piOfOtherGroup);

        final DocumentImageSet documentImageSet = createDocumentImageSet("imageset", "http://example.org");
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.save(session, documentImageSet, pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.delete(session, documentImageSet.getUuid(), piOfOtherGroup);
            transaction.commit();
        }

    }

    @Test(expected = PimSecurityException.class)
    public void deleteThrowsPimSecurityExceptionForResearchers() throws PimSecurityException, ValidationException {
        final PimGroup pimGroup = new PimGroup();
        pimGroupDAO.save(pimGroup);
        final PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup, Role.PI);
        pimUserDao.save(pimUser);

        final PimUser researcher = userWithMembershipAndPrimaryGroup(pimGroup, Role.RESEARCHER);
        pimUserDao.save(researcher);

        final DocumentImageSet documentImageSet = createDocumentImageSet("imageset", "http://example.org");
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.save(session, documentImageSet, pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.delete(session, documentImageSet.getUuid(), researcher);
            transaction.commit();
        }

    }

    @Test(expected = PimSecurityException.class)
    public void deleteThrowsPimSecurityExceptionForAssistants() throws PimSecurityException, ValidationException {
        final PimGroup pimGroup = new PimGroup();
        pimGroupDAO.save(pimGroup);
        final PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup, Role.PI);
        pimUserDao.save(pimUser);

        final PimUser assistant = userWithMembershipAndPrimaryGroup(pimGroup, Role.ASSISTANT);
        pimUserDao.save(assistant);

        final DocumentImageSet documentImageSet = createDocumentImageSet("imageset", "http://example.org");
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.save(session, documentImageSet, pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.delete(session, documentImageSet.getUuid(), assistant);
            transaction.commit();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteThrowsIllegalArgumentExceptionWhenTheDocumentImageSetToDeleteDoesNotExist() throws PimSecurityException {
        final PimGroup pimGroup = new PimGroup();
        pimGroupDAO.save(pimGroup);
        final PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup, Role.PI);
        pimUserDao.save(pimUser);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.delete(session, UUID.randomUUID(), pimUser);
            transaction.commit();
        }
    }

    @Test
    public void deleteIsAllowedForTheOwnerPIOfTheDocumentWhenNotUsingGroups() throws PimSecurityException, ValidationException {
        doNotUseGroups();
        final PimGroup pimGroup = new PimGroup();
        pimGroupDAO.save(pimGroup);
        final PimUser pimUser = AclTestHelpers.userWithRoles(List.of(Role.PI));
        pimUserDao.save(pimUser);

        final DocumentImageSet documentImageSet = createDocumentImageSet("imageset", "http://example.org");
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.save(session, documentImageSet, pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            pimUserDao.getByUUID(pimUser.getUuid());
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.delete(session, documentImageSet.getUuid(), pimUser);
            transaction.commit();
        }

        assertThat(documentImageSetDAO.getByUUID(documentImageSet.getUuid()), is(nullValue()));

    }

    @Test
    public void deleteIsAllowedForAdminsWhenNotUsingGroups() throws PimSecurityException, ValidationException {
        doNotUseGroups();
        final PimGroup pimGroup = new PimGroup();
        pimGroupDAO.save(pimGroup);
        final PimUser pimUser = AclTestHelpers.userWithRoles(List.of(Role.PI));
        pimUserDao.save(pimUser);
        final PimUser admin = adminUser();
        pimUserDao.save(admin);

        final DocumentImageSet documentImageSet = createDocumentImageSet("imageset", "http://example.org");
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.save(session, documentImageSet, pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.delete(session, documentImageSet.getUuid(), admin);
            transaction.commit();
        }

        assertThat(documentImageSetDAO.getByUUID(documentImageSet.getUuid()), is(nullValue()));
    }

    @Test(expected = PimSecurityException.class)
    public void deleteIsNotAllowedForDisabledUsersWhenNotUsingGroups() throws PimSecurityException, ValidationException {
        doNotUseGroups();
        final PimGroup pimGroup = new PimGroup();
        pimGroupDAO.save(pimGroup);
        final PimUser pimUser = AclTestHelpers.userWithRoles(List.of(Role.PI));
        pimUserDao.save(pimUser);
        final PimUser disabledAdmin = adminUser();
        disabledAdmin.setDisabled(true);
        pimUserDao.save(disabledAdmin);

        final DocumentImageSet documentImageSet = createDocumentImageSet("imageset", "http://example.org");
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.save(session, documentImageSet, pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.delete(session, documentImageSet.getUuid(), disabledAdmin);
            transaction.commit();
        }

    }

    @Test(expected = PimSecurityException.class)
    public void deleteIsNotAllowedForOtherPIsWhenNotUsingGroups() throws PimSecurityException, ValidationException {
        doNotUseGroups();
        final PimGroup pimGroup = new PimGroup();
        pimGroupDAO.save(pimGroup);
        final PimUser pimUser = AclTestHelpers.userWithRoles(List.of(Role.PI));
        pimUserDao.save(pimUser);
        final PimUser otherPI = AclTestHelpers.userWithRoles(List.of(Role.PI));
        pimUserDao.save(otherPI);

        final DocumentImageSet documentImageSet = createDocumentImageSet("imageset", "http://example.org");
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.save(session, documentImageSet, pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.delete(session, documentImageSet.getUuid(), otherPI);
            transaction.commit();
        }
    }

    @Test
    public void streamImagesOfDocumentImageSetReturnsAStreamOfTheImagesWhenTheUserIsAllowedToReadTheImageSet() throws Exception {
        final PimGroup pimGroup = new PimGroup();
        pimGroupDAO.save(pimGroup);
        PimUser userOutsideOwningGroup = userWithMembershipAndPrimaryGroup(pimGroup, Role.PI);
        pimUserDao.save(userOutsideOwningGroup);
        UUID userUuid = userOutsideOwningGroup.getUuid();

        final DocumentImageSet publicImageSet = createDocumentImageSet("imageset", "http://example.org");
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, pimUserUuid);
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.save(session, publicImageSet, pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            DocumentImage documentImage = new DocumentImage();
            documentImage.setUri("http://example.org/image.jpg");
            documentImage.setRemoteuri("http://example.org/image.jpg");
            documentImage.addDocumentImageSet(documentImageSetDAO.getByUUID(session, publicImageSet.getUuid()));
            documentImageDAO.save(session, documentImage);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            userOutsideOwningGroup = pimUserDao.getByUUID(session, userUuid);
            Stream<DocumentImage> images = documentImageSetService.streamImagesOfDocumentImageSet(session, publicImageSet.getUuid(), true, userOutsideOwningGroup);

            assertThat(images.findAny(), hasProperty("empty", equalTo(false)));
        }
    }

    @Test
    public void streamImagesOfDocumentImageSetReturnsAnEmptyStreamWhenTheImageSetDoesNotExist() {
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, pimUserUuid);
            Stream<DocumentImage> images = documentImageSetService.streamImagesOfDocumentImageSet(session, UUID.randomUUID(), true, pimUser);

            assertThat(images.findAny(), hasProperty("empty", equalTo(true)));
        }
    }

    @Test
    public void streamDocumentImageSetReturnsAnEmptyStreamWhenTheUserIsNotAllowedToSeeTheImageSet() throws Exception {
        final PimGroup pimGroup = new PimGroup();
        pimGroupDAO.save(pimGroup);
        PimUser userOutsideOwningGroup = userWithMembershipAndPrimaryGroup(pimGroup, Role.PI);
        pimUserDao.save(userOutsideOwningGroup);
        UUID userUuid = userOutsideOwningGroup.getUuid();

        final DocumentImageSet privateImageSet = createDocumentImageSet("imageset", "http://example.org");
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, pimUserUuid);
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.save(session, privateImageSet, pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            makePrivate(privateImageSet, session);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            DocumentImage documentImage = new DocumentImage();
            documentImage.setUri("http://example.org/image.jpg");
            documentImage.setRemoteuri("http://example.org/image.jpg");
            documentImage.addDocumentImageSet(documentImageSetDAO.getByUUID(session, privateImageSet.getUuid()));
            documentImageDAO.save(session, documentImage);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            userOutsideOwningGroup = pimUserDao.getByUUID(session, userUuid);
            Stream<DocumentImage> images = documentImageSetService.streamImagesOfDocumentImageSet(session, privateImageSet.getUuid(), true, userOutsideOwningGroup);

            assertThat(images.findAny(), hasProperty("empty", equalTo(true)));
        }
    }

    @Test
    public void getImagesOfSetByMetadataLabelReturnsAStreamOfTheImages() throws Exception {
        final DocumentImageSet privateImageSet = createDocumentImageSet("imageset", "http://example.org");
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, pimUserUuid);
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.save(session, privateImageSet, pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            makePrivate(privateImageSet, session);
            transaction.commit();
        }

        final String label = "label";
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            DocumentImage documentImage = new DocumentImage();
            final MetaData metaData = new MetaData(documentImage, label, "labelValue");
            documentImage.setMetaData(Set.of(metaData));
            documentImage.setUri("http://example.org/image.jpg");
            documentImage.setRemoteuri("http://example.org/image.jpg");
            documentImage.addDocumentImageSet(documentImageSetDAO.getByUUID(session, privateImageSet.getUuid()));
            documentImageDAO.save(session, documentImage);

            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, pimUserUuid);
            Stream<Pair<DocumentImage, String>> images = documentImageSetService.getImagesByMetadataLabel(session, privateImageSet.getUuid(), label, pimUser);

            assertThat(images.findAny(), hasProperty("empty", equalTo(false)));
        }

    }

    @Test
    public void getImagesOfSetByMetadataLabelReturnsIfTheUserIsNotAllowedToSeeTheSet() throws Exception {
        final PimGroup pimGroup = new PimGroup();
        pimGroupDAO.save(pimGroup);
        PimUser userOutsideOwningGroup = userWithMembershipAndPrimaryGroup(pimGroup, Role.PI);
        pimUserDao.save(userOutsideOwningGroup);
        UUID userUuid = userOutsideOwningGroup.getUuid();

        final DocumentImageSet privateImageSet = createDocumentImageSet("imageset", "http://example.org");
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(session, pimUserUuid);
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.save(session, privateImageSet, pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            makePrivate(privateImageSet, session);
            transaction.commit();
        }

        final String label = "label";
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            DocumentImage documentImage = new DocumentImage();
            final MetaData metaData = new MetaData(documentImage, label, "labelValue");
            documentImage.setMetaData(Set.of(metaData));
            documentImage.setUri("http://example.org/image.jpg");
            documentImage.setRemoteuri("http://example.org/image.jpg");
            documentImage.addDocumentImageSet(documentImageSetDAO.getByUUID(session, privateImageSet.getUuid()));
            documentImageDAO.save(session, documentImage);

            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            userOutsideOwningGroup = pimUserDao.getByUUID(session, userUuid);
            Stream<Pair<DocumentImage, String>> images = documentImageSetService.getImagesByMetadataLabel(session, privateImageSet.getUuid(), label, userOutsideOwningGroup);

            assertThat(images.findAny(), hasProperty("empty", equalTo(true)));
        }
    }

    @Test
    public void getImagesOfSetByMetadataLabelReturnsEmptyStreamIfTheSetDoesNotExist() {
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUser pimUser = pimUserDao.getByUUID(pimUserUuid);

            Stream<Pair<DocumentImage, String>> documentImageStream = documentImageSetService.getImagesByMetadataLabel(session, UUID.randomUUID(), "label", pimUser);

            assertThat(documentImageStream.findAny(), hasProperty("empty", equalTo(true)));
        }
    }

    @Test
    public void addSubsetAddsSubsetToAnImageSet() throws Exception {
        final PimGroup pimGroup = new PimGroup();
        pimGroupDAO.save(pimGroup);
        final PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup, Role.PI);
        pimUserDao.save(pimUser);

        final DocumentImageSet documentImageSet = createDocumentImageSet("imageset", "http://example.org");
        final DocumentImageSet subset = createDocumentImageSet("subset", "http://example2.org");
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.save(session, documentImageSet, pimUser);
            documentImageSetService.save(session, subset, pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.addSubSet(session, documentImageSet.getUuid(), subset.getUuid(), pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final DocumentImageSet dbImageset = documentImageSetService.getByUuid(session, documentImageSet.getUuid(), pimUser).get();
            assertThat(dbImageset.getSubSets(), contains(hasProperty("uuid", equalTo(subset.getUuid()))));

            final DocumentImageSet dbSubset = documentImageSetService.getByUuid(session, subset.getUuid(), pimUser).get();
            assertThat(dbSubset.getSuperSets(), contains(hasProperty("uuid", equalTo(dbImageset.getUuid()))));
        }

    }

    @Test(expected = PimSecurityException.class)
    public void addSubsetThrowsPimSecurityExceptionIfTheUserIsNotAllowedToEditTheImageset() throws Exception {
        final PimGroup pimGroup = new PimGroup();
        pimGroupDAO.save(pimGroup);
        final PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup, Role.PI);
        pimUserDao.save(pimUser);

        final PimGroup otherGroup = new PimGroup();
        pimGroupDAO.save(otherGroup);
        final PimUser otherUser = userWithMembershipAndPrimaryGroup(otherGroup, Role.PI);
        pimUserDao.save(otherUser);

        final DocumentImageSet documentImageSet = createDocumentImageSet("imageset", "http://example.org");
        documentImageSet.setPublicDocumentImageSet(true);
        final DocumentImageSet subset = createDocumentImageSet("subset", "http://example2.org");
        subset.setPublicDocumentImageSet(true);
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.save(session, documentImageSet, pimUser);
            documentImageSetService.save(session, subset, pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.addSubSet(session, documentImageSet.getUuid(), subset.getUuid(), otherUser);
            transaction.commit();
        }
    }

    @Test(expected = NotFoundException.class)
    public void addSubsetThrowsNotFoundExceptionWhenTheImagesetIsNotFound() throws Exception {
        final PimUser admin = adminUser();
        pimUserDao.save(admin);
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.addSubSet(session, UUID.randomUUID(), UUID.randomUUID(), admin);
            transaction.commit();
        }
    }

    @Test(expected = ValidationException.class)
    public void addSubsetThrowsValidationExceptionWhenTheSubsetIsNotFound() throws Exception {
        final PimGroup pimGroup = new PimGroup();
        pimGroupDAO.save(pimGroup);
        final PimUser pimUser = userWithMembershipAndPrimaryGroup(pimGroup, Role.PI);
        pimUserDao.save(pimUser);

        final DocumentImageSet documentImageSet = createDocumentImageSet("imageset", "http://example.org");
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.save(session, documentImageSet, pimUser);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            documentImageSetService.addSubSet(session, documentImageSet.getUuid(), UUID.randomUUID(), pimUser);
            transaction.commit();
        }
    }

}