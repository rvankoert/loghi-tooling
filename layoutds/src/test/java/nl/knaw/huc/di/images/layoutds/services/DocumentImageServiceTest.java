package nl.knaw.huc.di.images.layoutds.services;

import nl.knaw.huc.di.images.layoutds.DAO.*;
import nl.knaw.huc.di.images.layoutds.SessionFactorySingleton;
import nl.knaw.huc.di.images.layoutds.models.DocumentImage;
import nl.knaw.huc.di.images.layoutds.models.DocumentImageSet;
import nl.knaw.huc.di.images.layoutds.models.pim.Acl;
import nl.knaw.huc.di.images.layoutds.models.pim.PimGroup;
import nl.knaw.huc.di.images.layoutds.models.pim.PimUser;
import nl.knaw.huc.di.images.layoutds.models.pim.Role;
import nl.knaw.huc.di.images.layoutds.security.PermissionHandler;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static nl.knaw.huc.di.images.layoutds.services.AclTestHelpers.userWithRoles;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DocumentImageServiceTest {

    private DocumentImageService documentImageService;
    private DocumentImageDAO documentImageDAO;
    private DocumentImageSetDAO documentImageSetDAO;
    private ConfigurationDAO configurationDAO;
    private PermissionHandler permissionHandlerMock;

    @Before
    public void setUp() throws Exception {
        permissionHandlerMock = mock(PermissionHandler.class);
        documentImageService = new DocumentImageService(permissionHandlerMock);
        documentImageSetDAO = new DocumentImageSetDAO();
        documentImageDAO = new DocumentImageDAO();
        when(permissionHandlerMock.useGroups()).thenReturn(true);
    }

    @After
    public void tearDown() {
        // Start with a clean database with each test.
        SessionFactorySingleton.closeSessionFactory();
    }

    @Test
    public void getByUuidReturnsAnEmptyOptionWhenTheDocumentImageDoesNotExist() {
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {

            Optional<DocumentImage> image = documentImageService.getByUuid(session, UUID.randomUUID(), new PimUser());

            assertThat(image, hasProperty("empty", equalTo(true)));
        }
    }

    @Test
    public void getByUuidReturnsTheImageWhenTheUserIsAllowedToSeeTheDocumentImageSet() {
        final DocumentImage documentImage;

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            documentImage = new DocumentImage();
            documentImage.setUri("http://example.org/image.jpg");
            documentImageDAO.save(session, documentImage);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            final DocumentImageSet documentImageSet = createDocumentImageSet("set", "http://example.org", false);
            documentImageSet.addDocumentImage(documentImage);
            documentImage.addDocumentImageSet(documentImageSet);
            documentImageSetDAO.save(session, documentImageSet);
            transaction.commit();

            when(permissionHandlerMock.isAllowedToRead(any(Session.class), Mockito.eq(documentImageSet.getUuid()), any(PimUser.class)))
                    .thenReturn(true);
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Optional<DocumentImage> image = documentImageService.getByUuid(session, documentImage.getUuid(), new PimUser());

            assertThat(image, hasProperty("empty", equalTo(false)));
        }
    }

    @Test
    public void getByUuidReturnsTheImageWhenImagesetIsPublic() {
        final DocumentImage documentImage;

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            documentImage = new DocumentImage();
            documentImage.setUri("http://example.org/image.jpg");
            documentImageDAO.save(session, documentImage);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            final DocumentImageSet documentImageSet = createDocumentImageSet("set", "http://example.org", true);
            documentImageSet.addDocumentImage(documentImage);
            documentImage.addDocumentImageSet(documentImageSet);
            documentImageSetDAO.save(session, documentImageSet);
            transaction.commit();

            when(permissionHandlerMock.isAllowedToRead(any(Session.class), Mockito.eq(documentImageSet.getUuid()), any(PimUser.class)))
                    .thenReturn(false);
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Optional<DocumentImage> image = documentImageService.getByUuid(session, documentImage.getUuid(), new PimUser());

            assertThat(image, hasProperty("empty", equalTo(false)));
        }
    }

    @Test
    public void getByUuidReturnsTheImageWhenImagesetIsPublicAndNoGroupsAreUsed() {
        final DocumentImage documentImage;

        doNotUseGroups();

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            documentImage = new DocumentImage();
            documentImage.setUri("http://example.org/image.jpg");
            documentImageDAO.save(session, documentImage);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            final DocumentImageSet documentImageSet = createDocumentImageSet("set", "http://example.org", true);
            documentImageSet.addDocumentImage(documentImage);
            documentImage.addDocumentImageSet(documentImageSet);
            documentImageSetDAO.save(session, documentImageSet);
            transaction.commit();

            when(permissionHandlerMock.isAllowedToRead(any(Session.class), Mockito.eq(documentImageSet.getUuid()), any(PimUser.class)))
                    .thenReturn(false);
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Optional<DocumentImage> image = documentImageService.getByUuid(session, documentImage.getUuid(), new PimUser());

            assertThat(image, hasProperty("empty", equalTo(false)));
        }
    }

    @Test
    public void getByUuidReturnsTheImageWhenWhenUserIsOwnerAndNoGroupsAreUsed() {
        final DocumentImage documentImage;
        final PimUser pimUser = userWithRoles(List.of(Role.PI));
        doNotUseGroups();

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final PimUserDao pimUserDao = new PimUserDao();
            pimUserDao.save(session, pimUser);
            final Transaction transaction = session.beginTransaction();
            documentImage = new DocumentImage();
            documentImage.setUri("http://example.org/image.jpg");
            documentImageDAO.save(session, documentImage);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            final DocumentImageSet documentImageSet = createDocumentImageSet("set", "http://example.org", false);
            documentImageSet.addDocumentImage(documentImage);
            documentImageSet.setOwner(pimUser);
            documentImage.addDocumentImageSet(documentImageSet);
            documentImageSetDAO.save(session, documentImageSet);
            transaction.commit();

            when(permissionHandlerMock.isAllowedToRead(any(Session.class), Mockito.eq(documentImageSet.getUuid()), any(PimUser.class)))
                    .thenReturn(false);
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Optional<DocumentImage> image = documentImageService.getByUuid(session, documentImage.getUuid(), pimUser);

            assertThat(image, hasProperty("empty", equalTo(false)));
        }
    }

    @Test
    public void getByUuidReturnsEmptyOptionalWhenTheUserIsNotAllowedToSeeTheDocumentImageSet() {
        final DocumentImage documentImage;

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            documentImage = new DocumentImage();
            documentImage.setUri("http://example.org/image.jpg");
            documentImageDAO.save(session, documentImage);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            final DocumentImageSet documentImageSet = createDocumentImageSet("set", "http://example.org", false);
            documentImageSet.addDocumentImage(documentImage);
            documentImage.addDocumentImageSet(documentImageSet);
            documentImageSetDAO.save(session, documentImageSet);
            transaction.commit();

            when(permissionHandlerMock.isAllowedToRead(any(Session.class), Mockito.eq(documentImageSet.getUuid()), any(PimUser.class)))
                    .thenReturn(false);
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Optional<DocumentImage> image = documentImageService.getByUuid(session, documentImage.getUuid(), new PimUser());

            assertThat(image, hasProperty("empty", equalTo(true)));
        }
    }

    @Test
    public void getByRemoteUriReturnsAnEmptyOptionWhenTheDocumentImageDoesNotExist() {
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {

            Optional<DocumentImage> image = documentImageService.getByRemoteUri(session, "http://example.org/image.jpg", new PimUser());

            assertThat(image, hasProperty("empty", equalTo(true)));
        }
    }

    @Test
    public void getByRemoteUriReturnsTheImageWhenTheUserIsAllowedToSeeTheDocumentImageSet() {
        final DocumentImage documentImage;

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            documentImage = new DocumentImage();
            documentImage.setUri("http://example.org/image.jpg");
            documentImage.setRemoteuri("http://example.org/image.jpg");
            documentImageDAO.save(session, documentImage);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            final DocumentImageSet documentImageSet = createDocumentImageSet("set", "http://example.org", false);
            documentImageSet.addDocumentImage(documentImage);
            documentImage.addDocumentImageSet(documentImageSet);
            documentImageSetDAO.save(session, documentImageSet);
            transaction.commit();

            when(permissionHandlerMock.isAllowedToRead(any(Session.class), Mockito.eq(documentImageSet.getUuid()), any(PimUser.class)))
                    .thenReturn(true);
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            Optional<DocumentImage> image = documentImageService.getByRemoteUri(session, "http://example.org/image.jpg", new PimUser());


            assertThat(image, hasProperty("empty", equalTo(false)));
        }
    }

    @Test
    public void getByRemoteUriReturnsTheImageWhenTheDocumentImageSetIsPublic() {
        final DocumentImage documentImage;

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            documentImage = new DocumentImage();
            documentImage.setUri("http://example.org/image.jpg");
            documentImage.setRemoteuri("http://example.org/image.jpg");
            documentImageDAO.save(session, documentImage);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            final DocumentImageSet documentImageSet = createDocumentImageSet("set", "http://example.org", true);
            documentImageSet.addDocumentImage(documentImage);
            documentImage.addDocumentImageSet(documentImageSet);
            documentImageSetDAO.save(session, documentImageSet);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            Optional<DocumentImage> image = documentImageService.getByRemoteUri(session, "http://example.org/image.jpg", new PimUser());


            assertThat(image, hasProperty("empty", equalTo(false)));
        }
    }

    @Test
    public void getByRemoteUriReturnsTheImageWhenTheDocumentImageSetIsPublicWithoutUsingGroups() {
        final DocumentImage documentImage;
        doNotUseGroups();

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            documentImage = new DocumentImage();
            documentImage.setUri("http://example.org/image.jpg");
            documentImage.setRemoteuri("http://example.org/image.jpg");
            documentImageDAO.save(session, documentImage);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            final DocumentImageSet documentImageSet = createDocumentImageSet("set", "http://example.org", true);
            documentImageSet.addDocumentImage(documentImage);
            documentImage.addDocumentImageSet(documentImageSet);
            documentImageSetDAO.save(session, documentImageSet);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            Optional<DocumentImage> image = documentImageService.getByRemoteUri(session, "http://example.org/image.jpg", new PimUser());


            assertThat(image, hasProperty("empty", equalTo(false)));
        }
    }

    @Test
    public void getByRemoteUriReturnsEmptyOptionalWhenTheUserIsNotAllowedToSeeTheDocumentImageSet() {
        final DocumentImage documentImage;

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            documentImage = new DocumentImage();
            documentImage.setUri("http://example.org/image.jpg");
            documentImage.setRemoteuri("http://example.org/image.jpg");
            documentImageDAO.save(session, documentImage);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            final DocumentImageSet documentImageSet = createDocumentImageSet("set", "http://example.org", false);
            documentImageSet.addDocumentImage(documentImage);
            documentImage.addDocumentImageSet(documentImageSet);
            documentImageSetDAO.save(session, documentImageSet);
            transaction.commit();

            when(permissionHandlerMock.isAllowedToRead(any(Session.class), Mockito.eq(documentImageSet.getUuid()), any(PimUser.class)))
                    .thenReturn(false);
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            Optional<DocumentImage> image = documentImageService.getByRemoteUri(session, "http://example.org/image.jpg", new PimUser());

            assertThat(image, hasProperty("empty", equalTo(true)));
        }
    }

    @Test
    public void getAllStreamingReturnsTheImagesTheIsAllowedToSee() {
        final DocumentImage documentImage;
        final PimGroup pimGroup = new PimGroup();
        new PimGroupDAO().save(pimGroup);
        final PimUser pimUser = AclTestHelpers.userWithMembershipAndPrimaryGroup(pimGroup, Role.ASSISTANT);
        new PimUserDao().save(pimUser);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            documentImage = new DocumentImage();
            documentImage.setUri("http://example.org/image.jpg");
            documentImage.setRemoteuri("http://example.org/image.jpg");
            documentImageDAO.save(session, documentImage);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            final DocumentImageSet documentImageSet = createDocumentImageSet("set", "http://example.org", false);
            documentImageSet.addDocumentImage(documentImage);
            documentImage.addDocumentImageSet(documentImageSet);
            documentImageSetDAO.save(session, documentImageSet);
            final AclDao aclDao = new AclDao();
            aclDao.save(Acl.readPermission(documentImageSet.getUuid(), pimGroup, Role.ASSISTANT));
            transaction.commit();

        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {

            Stream<DocumentImage> imageStream = documentImageService.getAllStreaming(session, pimUser);

            assertThat(imageStream.findAny(), hasProperty("empty", equalTo(false)));
        }
    }

    @Test
    public void getAllStreamingReturnsAnEmptyStreamWhenTheUserIsNotAllowedToSeeAnyDocumentImage() {
        final DocumentImage documentImage;
        final PimGroupDAO pimGroupDAO = new PimGroupDAO();
        final PimGroup pimGroup = new PimGroup();
        pimGroupDAO.save(pimGroup);
        final PimGroup otherGroup = new PimGroup();
        pimGroupDAO.save(otherGroup);
        final PimUser pimUser = AclTestHelpers.userWithMembershipAndPrimaryGroup(otherGroup, Role.ASSISTANT);
        new PimUserDao().save(pimUser);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            documentImage = new DocumentImage();
            documentImage.setUri("http://example.org/image.jpg");
            documentImage.setRemoteuri("http://example.org/image.jpg");
            documentImageDAO.save(session, documentImage);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            final DocumentImageSet documentImageSet = createDocumentImageSet("set", "http://example.org", false);
            documentImageSet.addDocumentImage(documentImage);
            documentImage.addDocumentImageSet(documentImageSet);
            documentImageSetDAO.save(session, documentImageSet);

            final AclDao aclDao = new AclDao();
            aclDao.save(Acl.readPermission(documentImageSet.getUuid(), pimGroup, Role.ASSISTANT));
            transaction.commit();

        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {

            Stream<DocumentImage> imageStream = documentImageService.getAllStreaming(session, pimUser);

            assertThat(imageStream.findAny(), hasProperty("empty", equalTo(true)));
        }
    }

    @Test
    public void getAllStreamingReturnsAllImagesWhenNotUsingGroups() {
        doNotUseGroups();
        final DocumentImage documentImage;
        final PimGroup pimGroup = new PimGroup();
        new PimGroupDAO().save(pimGroup);
        final PimUser pimUser = new PimUser();
        new PimUserDao().save(pimUser);

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            documentImage = new DocumentImage();
            documentImage.setUri("http://example.org/image.jpg");
            documentImage.setRemoteuri("http://example.org/image.jpg");
            documentImageDAO.save(session, documentImage);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            final DocumentImageSet documentImageSet = createDocumentImageSet("set", "http://example.org", false);
            documentImageSet.addDocumentImage(documentImage);
            documentImage.addDocumentImageSet(documentImageSet);
            documentImageSetDAO.save(session, documentImageSet);
            transaction.commit();

        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {

            Stream<DocumentImage> imageStream = documentImageService.getAllStreaming(session, pimUser);

            assertThat(imageStream.findAny(), hasProperty("empty", equalTo(false)));
        }
    }

    @Test
    public void getReturnsAnEmptyOptionWhenTheDocumentImageDoesNotExist() {
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {

            Optional<DocumentImage> image = documentImageService.get(session, 1, new PimUser());

            assertThat(image, hasProperty("empty", equalTo(true)));
        }
    }

    @Test
    public void getReturnsTheImageWhenTheUserIsAllowedToSeeTheDocumentImageSet() {
        final DocumentImage documentImage;

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            documentImage = new DocumentImage();
            documentImage.setUri("http://example.org/image.jpg");
            documentImageDAO.save(session, documentImage);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            final DocumentImageSet documentImageSet = createDocumentImageSet("set", "http://example.org", false);
            documentImageSet.addDocumentImage(documentImage);
            documentImage.addDocumentImageSet(documentImageSet);
            documentImageSetDAO.save(session, documentImageSet);
            transaction.commit();

            when(permissionHandlerMock.isAllowedToRead(any(Session.class), Mockito.eq(documentImageSet.getUuid()), any(PimUser.class)))
                    .thenReturn(true);
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final DocumentImage byUUID = documentImageDAO.getByUUID(documentImage.getUuid());
            final Optional<DocumentImage> image = documentImageService.get(session, byUUID.getId(), new PimUser());

            assertThat(image, hasProperty("empty", equalTo(false)));
        }
    }

    @Test
    public void getReturnsEmptyOptionalWhenTheUserIsNotAllowedToSeeTheDocumentImageSet() {
        final DocumentImage documentImage;

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            documentImage = new DocumentImage();
            documentImage.setUri("http://example.org/image.jpg");
            documentImageDAO.save(session, documentImage);
            transaction.commit();
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            final DocumentImageSet documentImageSet = createDocumentImageSet("set", "http://example.org", false);
            documentImageSet.addDocumentImage(documentImage);
            documentImage.addDocumentImageSet(documentImageSet);
            documentImageSetDAO.save(session, documentImageSet);
            transaction.commit();

            when(permissionHandlerMock.isAllowedToRead(any(Session.class), Mockito.eq(documentImageSet.getUuid()), any(PimUser.class)))
                    .thenReturn(false);
        }

        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final DocumentImage byUUID = documentImageDAO.getByUUID(documentImage.getUuid());
            final Optional<DocumentImage> image = documentImageService.get(session, byUUID.getId(), new PimUser());

            assertThat(image, hasProperty("empty", equalTo(true)));
        }
    }

    private void doNotUseGroups() {
        when(permissionHandlerMock.useGroups()).thenReturn(false);
    }

    private DocumentImageSet createDocumentImageSet(String imageset, String uri, boolean isPublic) {
        final DocumentImageSet documentImageSet = new DocumentImageSet();
        documentImageSet.setImageset(imageset);
        documentImageSet.setUri(uri);
        documentImageSet.setPublicDocumentImageSet(isPublic);
        return documentImageSet;
    }
}
