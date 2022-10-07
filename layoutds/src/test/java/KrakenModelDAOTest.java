import nl.knaw.huc.di.images.layoutds.DAO.pim.KrakenModelDAO;
import nl.knaw.huc.di.images.layoutds.StudentJpaConfig;
import nl.knaw.huc.di.images.layoutds.models.KrakenModel;
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
public class KrakenModelDAOTest {

    @Test(expected = LazyInitializationException.class)
    public void actualModelIsLoadedLazily() {
        KrakenModelDAO krakenModelDAO = new KrakenModelDAO();
        KrakenModel krakenModel = new KrakenModel();
        krakenModel.setUri("http://example.org");
        krakenModel.setModelData("dit is een test".getBytes(), "filename");
        krakenModelDAO.save(krakenModel);

        KrakenModel model = krakenModelDAO.getByUri("http://example.org");

        model.getModelData();
    }

    @Test
    public void getBinaryModelByIdReturnsTheModelData() {
        KrakenModelDAO krakenModelDAO = new KrakenModelDAO();
        KrakenModel krakenModel = new KrakenModel();
        krakenModel.setUri("http://example.org");
        krakenModel.setModelData("dit is een test".getBytes(), "filename");
        krakenModel = krakenModelDAO.save(krakenModel);

        byte[] binaryModel = krakenModelDAO.getBinaryModelById(krakenModel.getId()).get("filename");

        assertThat(binaryModel, is("dit is een test".getBytes()));
    }

}