import nl.knaw.huc.di.images.layoutds.DAO.pim.Tesseract4ModelDAO;
import nl.knaw.huc.di.images.layoutds.StudentJpaConfig;
import nl.knaw.huc.di.images.layoutds.models.pim.Tesseract4Model;
import org.hibernate.LazyInitializationException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {StudentJpaConfig.class}, loader = AnnotationConfigContextLoader.class)
public class Tesseract4ModelDAOTest {

    @Test(expected = LazyInitializationException.class)
    public void actualModelIsLoadedLazily() {
        Tesseract4ModelDAO tesseract4ModelDAO = new Tesseract4ModelDAO();
        Tesseract4Model tesseract4Model = new Tesseract4Model();
        tesseract4Model.setUri("http://example.org");
        tesseract4Model.addModelData("dit is een test".getBytes(), "filename");
        tesseract4ModelDAO.save(tesseract4Model);

        Tesseract4Model model = tesseract4ModelDAO.getByUri("http://example.org");

        model.getModelData();
    }

    @Test
    public void getBinaryModelByIdReturnsTheModelData() {
        Tesseract4ModelDAO tesseract4ModelDAO = new Tesseract4ModelDAO();
        Tesseract4Model tesseract4Model = new Tesseract4Model();
        tesseract4Model.setUri("http://example.org");
        tesseract4Model.addModelData("dit is een test".getBytes(), "filename");
        tesseract4Model = tesseract4ModelDAO.save(tesseract4Model);

        byte[] binaryModel = tesseract4ModelDAO.getBinaryModelById(tesseract4Model.getId()).get("filename");

        assertThat(binaryModel, is("dit is een test".getBytes()));
    }

}