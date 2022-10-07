import nl.knaw.huc.di.images.layoutds.DAO.DocumentImageDAO;
import nl.knaw.huc.di.images.layoutds.DAO.DocumentImageSetDAO;
import nl.knaw.huc.di.images.layoutds.SessionFactorySingleton;
import nl.knaw.huc.di.images.layoutds.StudentJpaConfig;
import nl.knaw.huc.di.images.layoutds.models.DocumentImage;
import nl.knaw.huc.di.images.layoutds.models.DocumentImageSet;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.not;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {StudentJpaConfig.class}, loader = AnnotationConfigContextLoader.class)
public class DocumentImageSetStorageTest {

    public static final String IMG_URI = "/test/image.jpg";
    public static final String IMG_URI2 = "/test/image2.jpg";
    public static final String IMG_URI3 = "/test/image3.jpg";

    @After
    public void tearDown() {
        SessionFactorySingleton.closeSessionFactory();
    }

    @Test
    public void deletesDocumentImagesWhenDocumentImageSetIsDeleted() {
        final DocumentImageDAO documentImageDAO = new DocumentImageDAO();
        final DocumentImageSetDAO documentImageSetDAO = new DocumentImageSetDAO();

        try (Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            final DocumentImage savedImage = saveImageWithUri(documentImageDAO, session, "/test/image.jpg");

            final DocumentImageSet documentImageSet = new DocumentImageSet();
            documentImageSet.setUri("/test");
            documentImageSet.setImageset("/test");
            documentImageSet.addDocumentImage(savedImage);
            final DocumentImageSet savedSet = documentImageSetDAO.save(session, documentImageSet);

            transaction.commit();

            assertThat(documentImageDAO.get(savedImage.getId()), is(not(nullValue())));
            assertThat(documentImageSetDAO.get(savedSet.getId()), is(not(nullValue())));

            final Transaction deleteTransaction = session.beginTransaction();
            documentImageSetDAO.delete(session, savedSet);
            deleteTransaction.commit();

            assertThat(documentImageSetDAO.get(savedSet.getId()), is(nullValue()));
            assertThat(documentImageDAO.get(savedImage.getId()), is(nullValue()));
        }

    }

    @Test
    public void doesNotDeleteDocumentImagesConnectedToAnotherDocumentImageSet() {
        final DocumentImageDAO documentImageDAO = new DocumentImageDAO();
        final DocumentImageSetDAO documentImageSetDAO = new DocumentImageSetDAO();

        final DocumentImage savedImage;
        final DocumentImageSet savedSet;
        final DocumentImageSet savedSet2;
        try (Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            final DocumentImage image1 = new DocumentImage();
            image1.setUri("/test/image.jpg");
            final DocumentImage image = image1;
            savedImage = documentImageDAO.save(session, image);

            final DocumentImageSet documentImageSet = new DocumentImageSet();
            documentImageSet.setUri("/test");
            documentImageSet.setImageset("/test");
            documentImageSet.addDocumentImage(savedImage);
            savedSet = documentImageSetDAO.save(session, documentImageSet);

            final DocumentImageSet documentImageSet2 = new DocumentImageSet();
            documentImageSet2.setUri("/test2");
            documentImageSet2.setImageset("/test2");
            documentImageSet2.addDocumentImage(savedImage);
            savedSet2 = documentImageSetDAO.save(session, documentImageSet2);

            transaction.commit();
        }

        try (Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            assertThat(documentImageDAO.get(savedImage.getId()), is(not(nullValue())));
            assertThat(documentImageSetDAO.get(savedSet.getId()), is(not(nullValue())));
            assertThat(documentImageSetDAO.get(savedSet2.getId()), is(not(nullValue())));

            final Transaction deleteTransaction = session.beginTransaction();
            documentImageSetDAO.delete(session, savedSet);
            deleteTransaction.commit();

            assertThat(documentImageSetDAO.get(savedSet.getId()), is(nullValue()));
            assertThat(documentImageSetDAO.get(savedSet2.getId()), is(not(nullValue())));
            assertThat(documentImageDAO.get(savedImage.getId()), is(not(nullValue())));

        }
    }

    @Test
    public void testDeleteWithoutSession() {
        final DocumentImageDAO documentImageDAO = new DocumentImageDAO();
        final DocumentImageSetDAO documentImageSetDAO = new DocumentImageSetDAO();

        final DocumentImage savedImage;
        final DocumentImageSet savedSet;
        final DocumentImageSet savedSet2;
        try (Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            final DocumentImage image1 = new DocumentImage();
            image1.setUri("/test/image.jpg");
            final DocumentImage image = image1;
            savedImage = documentImageDAO.save(session, image);

            final DocumentImageSet documentImageSet = new DocumentImageSet();
            documentImageSet.setUri("/test");
            documentImageSet.setImageset("/test");
            documentImageSet.addDocumentImage(savedImage);
            savedSet = documentImageSetDAO.save(session, documentImageSet);

            final DocumentImageSet documentImageSet2 = new DocumentImageSet();
            documentImageSet2.setUri("/test2");
            documentImageSet2.setImageset("/test2");
            documentImageSet2.addDocumentImage(savedImage);
            savedSet2 = documentImageSetDAO.save(session, documentImageSet2);

            transaction.commit();
        }

        assertThat(documentImageDAO.get(savedImage.getId()), is(not(nullValue())));
        assertThat(documentImageSetDAO.get(savedSet.getId()), is(not(nullValue())));
        assertThat(documentImageSetDAO.get(savedSet2.getId()), is(not(nullValue())));

        documentImageSetDAO.delete(savedSet);

        assertThat(documentImageSetDAO.get(savedSet.getId()), is(nullValue()));
        assertThat(documentImageSetDAO.get(savedSet2.getId()), is(not(nullValue())));
        assertThat(documentImageDAO.get(savedImage.getId()), is(not(nullValue())));

    }

    @Test
    public void doNotRemoveSubsetsWhenRemovingDocumentImageSet() {
        final DocumentImageSetDAO documentImageSetDAO = new DocumentImageSetDAO();

        final DocumentImageSet savedSet;
        final DocumentImageSet savedSet2;
        try (Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();

            final DocumentImageSet documentImageSet = new DocumentImageSet();
            documentImageSet.setUri("/test");
            documentImageSet.setImageset("/test");
            savedSet = documentImageSetDAO.save(session, documentImageSet);

            final DocumentImageSet documentImageSet2 = new DocumentImageSet();
            documentImageSet2.setUri("/test2");
            documentImageSet2.setImageset("/test2");
            savedSet2 = documentImageSetDAO.save(session, documentImageSet2);

            savedSet.addSubSet(savedSet2);

            session.update(savedSet);

            transaction.commit();
        }

        try (Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            assertThat(documentImageSetDAO.get(savedSet.getId()), is(not(nullValue())));
            assertThat(documentImageSetDAO.get(savedSet2.getId()), is(not(nullValue())));

            final Transaction deleteTransaction = session.beginTransaction();
            documentImageSetDAO.delete(session, savedSet);
            deleteTransaction.commit();

            assertThat(documentImageSetDAO.get(savedSet.getId()), is(nullValue()));
            assertThat(documentImageSetDAO.get(savedSet2.getId()), is(not(nullValue())));
        }
    }

    @Test
    public void getAllImagesRetrievesAlsoRetrieveImageFromSubSets() {
        final DocumentImageDAO documentImageDAO = new DocumentImageDAO();
        final DocumentImageSetDAO documentImageSetDAO = new DocumentImageSetDAO();

        final DocumentImageSet savedSet3;
        try (Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();
            final DocumentImage savedImage = saveImageWithUri(documentImageDAO, session, IMG_URI);
            final DocumentImage savedImage2 = saveImageWithUri(documentImageDAO, session, IMG_URI2);
            final DocumentImage savedImage3 = saveImageWithUri(documentImageDAO, session, IMG_URI3);

            final DocumentImageSet documentImageSet = new DocumentImageSet();
            documentImageSet.setUri("/test");
            documentImageSet.setImageset("/test");
            documentImageSet.addDocumentImage(savedImage);
            final DocumentImageSet savedSet = documentImageSetDAO.save(session, documentImageSet);

            final DocumentImageSet documentImageSet2 = new DocumentImageSet();
            documentImageSet2.setUri("/test2");
            documentImageSet2.setImageset("/test2");
            documentImageSet2.addSubSet(savedSet);
            documentImageSet2.addDocumentImage(savedImage2);
            final DocumentImageSet savedSet2 = documentImageSetDAO.save(session, documentImageSet2);

            final DocumentImageSet documentImageSet3 = new DocumentImageSet();
            documentImageSet3.setUri("/test3");
            documentImageSet3.setImageset("/test3");
            documentImageSet3.addSubSet(savedSet2);
            documentImageSet3.addDocumentImage(savedImage3);
            savedSet3 = documentImageSetDAO.save(session, documentImageSet3);

            transaction.commit();
        }

        try (Session session = SessionFactorySingleton.getSessionFactory().openSession()) {
            final DocumentImageSet documentImageSet = documentImageSetDAO.get(session, savedSet3.getId());

            final Set<String> uris = documentImageSet.getAllImages().map(DocumentImage::getUri).collect(Collectors.toSet());

            assertThat(uris, containsInAnyOrder(IMG_URI, IMG_URI2, IMG_URI3));
        }

    }

    private DocumentImage saveImageWithUri(DocumentImageDAO documentImageDAO, Session session, String uri) {
        final DocumentImage image = new DocumentImage();
        image.setUri(uri);
        return documentImageDAO.save(session, image);
    }

}
