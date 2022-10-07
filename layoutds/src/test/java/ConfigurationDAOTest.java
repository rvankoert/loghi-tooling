import nl.knaw.huc.di.images.layoutds.DAO.ConfigurationDAO;
import nl.knaw.huc.di.images.layoutds.DAO.DuplicateDataException;
import nl.knaw.huc.di.images.layoutds.SessionFactorySingleton;
import nl.knaw.huc.di.images.layoutds.StudentJpaConfig;
import nl.knaw.huc.di.images.layoutds.models.Configuration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {StudentJpaConfig.class}, loader = AnnotationConfigContextLoader.class)
public class ConfigurationDAOTest {


    @Test
    public void getByKeyEmpty() throws DuplicateDataException {
        ConfigurationDAO dao = new ConfigurationDAO();
        Configuration result = dao.getByKey(SessionFactorySingleton.getSessionFactory().openSession(), "test");

        Assert.assertNull(result);
    }


}

