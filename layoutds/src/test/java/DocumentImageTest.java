import nl.knaw.huc.di.images.layoutds.DAO.DocumentImageDAO;
import nl.knaw.huc.di.images.layoutds.PublicAnnotation;
import nl.knaw.huc.di.images.layoutds.StudentJpaConfig;
import nl.knaw.huc.di.images.layoutds.models.DocumentImage;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {StudentJpaConfig.class}, loader = AnnotationConfigContextLoader.class)
public class DocumentImageTest {


    @Test
    public void minimalTest() {
        DocumentImageDAO documentImageDAO = new DocumentImageDAO();
        List<DocumentImage> results = documentImageDAO.getAll();
        int count = results.size();
        DocumentImage documentImage = new DocumentImage();
        documentImage.setUri("/unique" + UUID.randomUUID());
        documentImageDAO.save(documentImage);
        documentImage = new DocumentImage();
        documentImage.setUri("/unique" + UUID.randomUUID());
        documentImageDAO.save(documentImage);

        results = documentImageDAO.getAll();
        assertEquals(count + 2, results.size());
    }

    @Test
    public void hasAttributeTest() {
        DocumentImage documentImage = new DocumentImage();
        Class cls = documentImage.getClass();

        for (Field field : cls.getDeclaredFields()) {
            Class type = field.getType();
            String name = field.getName();
            PublicAnnotation annotation = field.getDeclaredAnnotation(PublicAnnotation.class);
            if (annotation != null) {
                System.out.println("FOUND IT");
                System.out.println(field.getName());
                System.out.println(field.getType().toString());
            }
        }
    }

    @Test
    public void getFileNameReturnsTheLastPartOfTheUri() {
        final DocumentImage documentImage = new DocumentImage();
        documentImage.setUri("/data/images/1.11.06.11_Cochin/1257B/NL-HaNA_1.11.06.11_1257B_0001.jpg");

        assertEquals("NL-HaNA_1.11.06.11_1257B_0001.jpg", documentImage.getFileName());
    }

    @Test
    public void getFileNameStripsTheIIIFPathToFindTheFileName() {
        final DocumentImage documentImage = new DocumentImage();
        documentImage.setUri("https://www.e-codices.unifr.ch/loris/kba/kba-MurQ0007/kba-MurQ0007_0000_0001v.jp2/full/full/0/default/jpg");

        assertEquals("kba-MurQ0007_0000_0001v.jp2", documentImage.getFileName());
    }

    @Test
    public void getFileNameStripsIIFPathToFindTheFileNameEvenWhenItEndsOnAFileName() {
        final DocumentImage documentImage = new DocumentImage();
        documentImage.setUri("http://www.e-codices.unifr.ch:80/loris/ubb/ubb-A-II-0029/ubb-A-II-0029_0012r.jp2/full/full/0/default.jpg");

        assertEquals("ubb-A-II-0029_0012r.jp2", documentImage.getFileName());
    }


    @Test
    public void getFileNameStripsTheIIIFPathToFindTheFileNameEvenWhenImageNameIsHidden() {
        final DocumentImage documentImage = new DocumentImage();
        documentImage.setUri("https://stacks.stanford.edu/image/iiif/fs743fm9703/046_145_R_TC_46/full/full/0/default.jpg");

        assertEquals("046_145_R_TC_46.jpg", documentImage.getFileName());
    }

}

