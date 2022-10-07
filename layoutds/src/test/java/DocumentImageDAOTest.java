import nl.knaw.huc.di.images.layoutds.DAO.DocumentImageDAO;
import nl.knaw.huc.di.images.layoutds.DAO.DocumentImageSetDAO;
import nl.knaw.huc.di.images.layoutds.SessionFactorySingleton;
import nl.knaw.huc.di.images.layoutds.StudentJpaConfig;
import nl.knaw.huc.di.images.layoutds.models.DocumentImage;
import nl.knaw.huc.di.images.layoutds.models.DocumentImageSet;
import nl.knaw.huc.di.images.layoutds.models.MetaData;
import nl.knaw.huc.di.images.layoutds.models.pim.PimUser;
import org.apache.commons.lang3.tuple.Pair;
import org.hamcrest.CoreMatchers;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasProperty;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {StudentJpaConfig.class}, loader = AnnotationConfigContextLoader.class)
public class DocumentImageDAOTest {


    private DocumentImageDAO documentImageDAO;

    @Before
    public void setUp() throws Exception {
        documentImageDAO = new DocumentImageDAO();
    }

    @After
    public void tearDown() {
        // Start with a clean database with each test.
        SessionFactorySingleton.closeSessionFactory();
    }

    @Test
    public void getManyRandomTesseractV4HOCRBestEmptyTest() {
        DocumentImageDAO documentImageDAO = this.documentImageDAO;

        DocumentImage documentImage = new DocumentImage();
        documentImage.setUri("/unique");
        documentImageDAO.save(documentImage);
        documentImage = new DocumentImage();
        documentImage.setUri("/notunique");
        documentImageDAO.save(documentImage);

        List<DocumentImage> results = documentImageDAO.getManyRandomTesseractV4HOCRBestEmpty(10);
        Assert.assertEquals(0, results.size());
    }

    @Test
    public void setAllDocumentImagesNotBrokenTest() {
        DocumentImageDAO documentImageDAO = this.documentImageDAO;

        DocumentImage documentImage = new DocumentImage();
        documentImage.setUri("/unique2");
        documentImage.setBroken(true);
        documentImageDAO.save(documentImage);
        documentImage = new DocumentImage();
        documentImage.setUri("/notunique2");
        documentImage.setBroken(true);
        documentImageDAO.save(documentImage);

        assertThat(documentImageDAO.getAll().stream().anyMatch(image -> image.getBroken() != null && image.getBroken()), is(true));

        documentImageDAO.setAllDocumentImagesNotBroken();

        assertThat(documentImageDAO.getAll().stream().anyMatch(image -> image.getBroken() != null && image.getBroken()), is(false));
    }

    @Test
    public void getManifestsTest() {
        DocumentImageDAO documentImageDAO = this.documentImageDAO;

        DocumentImage documentImage = new DocumentImage();
        documentImage.setUri("/unique3");
        documentImageDAO.save(documentImage);
        documentImage = new DocumentImage();
        documentImage.setUri("/notunique3");
        documentImageDAO.save(documentImage);

        List<DocumentImageSet> results = documentImageDAO.getManifests();
        Assert.assertEquals(0, results.size());
    }

    @Test
    public void getImagesBySetAndMetadataLabelReturnsTheImagesOfASetAndLabel() {
        final DocumentImageSet privateImageSet = new DocumentImageSet();
        privateImageSet.setImageset("imageset");
        privateImageSet.setUri("http://example.org");
        final DocumentImageSetDAO documentImageSetDAO = new DocumentImageSetDAO();
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            documentImageSetDAO.save(session, privateImageSet);
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
            final DocumentImageSet documentImageSet = documentImageSetDAO.getByUUID(session, privateImageSet.getUuid());
            final Stream<Pair<DocumentImage, String>> images = documentImageDAO.getImagesBySetAndMetadataLabel(session, documentImageSet, label);

            final Set<Pair<DocumentImage, String>> imageSet = images.collect(Collectors.toSet());
            assertThat(imageSet, contains(allOf(
                    hasProperty("left", hasProperty("uri", equalTo("http://example.org/image.jpg"))),
                    hasProperty("right", equalTo("labelValue"))

            )));
        }
    }

    @Test
    public void getImagesBySetAndMetadataLabelReturnsEmptyStreamForOtherLabel() {
        final DocumentImageSet privateImageSet = new DocumentImageSet();
        privateImageSet.setImageset("imageset");
        privateImageSet.setUri("http://example.org");
        final DocumentImageSetDAO documentImageSetDAO = new DocumentImageSetDAO();
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            documentImageSetDAO.save(session, privateImageSet);
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
            final DocumentImageSet documentImageSet = documentImageSetDAO.getByUUID(session, privateImageSet.getUuid());
            final Stream<Pair<DocumentImage, String>> images = documentImageDAO.getImagesBySetAndMetadataLabel(session, documentImageSet, "otherLabel");

            assertThat(images.findAny(), hasProperty("empty", equalTo(true)));
        }
    }

    @Test
    public void getImagesBySetAndMetadataLabelReturnsEmptyStreamForOtherImageSet() {
        final DocumentImageSet privateImageSet = new DocumentImageSet();
        privateImageSet.setImageset("imageset");
        privateImageSet.setUri("http://example.org");

        final DocumentImageSet otherImageSet = new DocumentImageSet();
        otherImageSet.setImageset("imageset1");
        otherImageSet.setUri("http://example1.org");
        final DocumentImageSetDAO documentImageSetDAO = new DocumentImageSetDAO();
        try (final Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            documentImageSetDAO.save(session, privateImageSet);
            documentImageSetDAO.save(session, otherImageSet);
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
            final DocumentImageSet documentImageSet = documentImageSetDAO.getByUUID(session, otherImageSet.getUuid());
            final Stream<Pair<DocumentImage, String>> images = documentImageDAO.getImagesBySetAndMetadataLabel(session, documentImageSet, "otherLabel");

            assertThat(images.findAny(), hasProperty("empty", equalTo(true)));
        }
    }

}

