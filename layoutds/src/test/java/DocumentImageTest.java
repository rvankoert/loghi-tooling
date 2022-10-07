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
        Assert.assertEquals(count + 2, results.size());
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

}

