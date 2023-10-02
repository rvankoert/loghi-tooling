import nl.knaw.huc.di.images.layoutds.models.Page.PcGts;
import nl.knaw.huc.di.images.pagexmlutils.PageUtils;
import nl.knaw.huc.di.images.pagexmlutils.PageValidator;
import nl.knaw.huc.di.images.stringtools.StringTools;
import org.junit.Assert;
import org.junit.Test;
import org.primaresearch.dla.page.io.xml.XmlPageReader;

import java.io.IOException;
import java.net.URL;
import java.util.Date;

public class PageValidatorTest {

    @Test
    public void pageValidatorRealWorld() throws IOException {
        URL url = Thread.currentThread().getContextClassLoader().getResource("NL-HlmNHA_1972_8_0022.xml");

        String path = url.getPath();
        String contents = StringTools.readFile(path);

        XmlPageReader reader = PageValidator.validate(contents);
        Assert.assertEquals(0, reader.getErrors().size());
        Assert.assertEquals(0, reader.getWarnings().size());

    }

    @Test
    public void pageValidatorSynthetic() throws IOException {
        String creator = "creator";
        Date created = new Date();
        Date lastChanged = new Date();
        String filename = "filename.jpg";
        int height = 100;
        int width = 200;
        PcGts page = new PcGts(creator, created, lastChanged, filename, height, width);
        page.getMetadata().setCreator("test");
        page.getMetadata().setCreated(new Date());
        page.getMetadata().setLastChange(new Date());
        page.getPage().setImageFilename("filename.jpg");
        page.getPage().setImageWidth(100);
        page.getPage().setImageHeight(200);
        String contents = PageUtils.convertPcGtsToString(page, PageUtils.NAMESPACE2019);

        XmlPageReader reader = PageValidator.validate(contents);
        Assert.assertEquals(0, reader.getErrors().size());
        Assert.assertEquals(0, reader.getWarnings().size());
    }

}
